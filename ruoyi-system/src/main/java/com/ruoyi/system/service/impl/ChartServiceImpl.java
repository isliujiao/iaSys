package com.ruoyi.system.service.impl;

import cn.hutool.core.io.FileUtil;
import com.ruoyi.common.config.RedisLimiterManager;
import com.ruoyi.common.config.YcmAiManager;
import com.ruoyi.common.core.domain.ErrorCode;
import com.ruoyi.common.core.domain.entity.Chart;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.ChartStatusEnum;
import com.ruoyi.common.exception.base.BusinessException;
import com.ruoyi.common.utils.ExcelUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ThrowUtils;
import com.ruoyi.system.domain.dto.chart.ChartQueryRequest;
import com.ruoyi.system.domain.dto.chart.GenChartByAiRequest;
import com.ruoyi.system.domain.vo.BiResponseVO;
import com.ruoyi.system.mapper.ChartMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mq.BiMessageProducer;
import com.ruoyi.system.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 图表实现类，用于生成图表数据
 */
@Service
@Slf4j
public class ChartServiceImpl implements ChartService {

    @Autowired
    private YcmAiManager ycmAiManager;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private RedisLimiterManager redisLimiterManager;

    @Autowired
    private BiMessageProducer biMessageProducer;

    @Autowired
    private ChartMapper chartMapper;

    @Autowired
    private SysUserMapper userMapper;

    private final Long BI_MODEL_ID = 1720659664934776834L;

    private final Long STIPULATE_SIZE = 1024 * 1024L; // 规定文件大小

    public static final Boolean TEST_FLAG = false; // 测试返回数据，不执行调用ai操作

    //文件规定后缀
    private final List<String> VALID_FILE_SUFFIX_LIST = Arrays.asList("xlsx", "xls");

    @Override
    public BiResponseVO genChartByAi(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        // 非管理员用户需要校验次数
        if (!SecurityUtils.isAdmin(userId)) {
            ThrowUtils.throwIf(loginUser.getUser().getCurrency() <= 0, ErrorCode.NO_AUTH_ERROR, "调用次数不足");
        }

        if (TEST_FLAG) {
            // 返回生成值
            BiResponseVO biResponseVO = getBiResponseVO();
            return biResponseVO;
        }

        String chartName = genChartByAiRequest.getChartName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        verify(multipartFile, chartName, goal);

        // 使用 Redisson 限流（针对每个用户请求当前方法时限流）
        redisLimiterManager.setRedisCurrentLimit("genChartByAi_" + userId);

        /**
         * 用户输入-参考：
         * 分析需求：
         * {分析网站用户的增长情况}
         * 原始数据：
         * {日期,用户数
         * 1号,10
         * 2号,20
         * 3号,30}
         */
        StringBuilder userInput = new StringBuilder();

        //拼接“分析需求”
        userInput.append("分析需求：").append("\n");
        String userGoal = goal;
        // 如果图表类型不为空
        if (StringUtils.isNotBlank(chartType)) {
            // 就将分析目标拼接上“请使用”+图表类型
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");

        //拼接“原始数据”
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据（把multipartFile传进来，其他的东西先注释）
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

        log.info("【向AI发送请求userInput：{}】", userInput);

        //发送请求，得到响应数据
        String result = ycmAiManager.doChat(BI_MODEL_ID, userInput.toString());
        log.info("【AI-result返回结果：{}】", result);
        if (StringUtils.isEmpty(result)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        // 对返回结果做拆分,按照5个中括号进行拆分
        String[] splits = result.split("【【【【【");
        // 拆分之后还要进行校验
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();//拿到ChartCode图标编码
        String genResult = splits[2].trim();//拿到返回结果值

        //todo 插入数据库
        Chart chart = new Chart();
        Long chartId = Math.abs(RandomUtils.nextLong());
        chart.setId(chartId);
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(userId);
        boolean saveResult = chartMapper.insertChart(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        CompletableFuture.runAsync(() -> {
            userMapper.updateCurrency(userId);
            log.info("用户 {} 消耗 {} 通行货币积分", userId, "1");
        });

        // 返回生成值
        BiResponseVO biResponseVO = new BiResponseVO();
        biResponseVO.setGenChart(genChart);
        biResponseVO.setGenResult(genResult);
        biResponseVO.setChartId(chart.getId());

        return biResponseVO;
    }

    //弃用
    @Override
    @Deprecated
    public Boolean genChartByAiAsync(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String chartName = genChartByAiRequest.getChartName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        verify(multipartFile, chartName, goal);

        Long userId = SecurityUtils.getUserId();
        // 使用 Redisson 限流（针对每个用户请求当前方法时限流）
        redisLimiterManager.setRedisCurrentLimit("genChartByAi_" + userId);

        /**
         * 用户输入-参考：
         * 分析需求：
         * {分析网站用户的增长情况}
         * 原始数据：
         * {日期,用户数
         * 1号,10
         * 2号,20
         * 3号,30}
         */
        StringBuilder userInput = new StringBuilder();

        //拼接“分析需求”
        userInput.append("分析需求：").append("\n");
        String userGoal = goal;
        // 如果图表类型不为空
        if (StringUtils.isNotBlank(chartType)) {
            // 就将分析目标拼接上“请使用”+图表类型
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");

        //拼接“原始数据”
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据（把multipartFile传进来，其他的东西先注释）
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");
        log.info("【向AI发送请求userInput：{}】", userInput);

        // 先把图表 非生成数据 保存到数据库中
        Chart chart = new Chart();
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);

        // 设置任务状态为排队中
        chart.setUserId(userId);
        chart.setStatus("wait");
        boolean saveResult = chartMapper.insertChart(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // 在最终的返回结果前提交一个任务
        // todo 异步执行任务（建议处理任务队列满了抛异常情况）
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            // 先修改图表任务状态为 "执行中" 等执行成功后，修改为 "已完成" 保存执行结果；执行失败后，状态修改为 "失败"，记录任务失败信息。(为了防止同一个任务被多次执行)
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus(ChartStatusEnum.RUNNING.getStatusCode()); //执行中
            boolean isUp = chartMapper.updateChartById(updateChart);

            // 如果提交失败(一般情况下,更新失败可能意味着你的数据库出问题了)
            if (!isUp) {
                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
                return;
            }

            // 调用 AI (【鱼聪明模型ID】,【输入拼接内容】)
            String result = ycmAiManager.doChat(BI_MODEL_ID, userInput.toString());
            log.info("【AI-result返回结果：{}】", result);
            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
                handleChartUpdateError(chart.getId(), "AI 生成错误");
                return;
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();

            // 调用AI得到结果之后,再更新一次数据库（状态为已成功）
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getStatusCode());
            boolean updateResult = chartMapper.updateChartById(updateChartResult);
            ThrowUtils.throwIf(!updateResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        }, threadPoolExecutor);
        return true;
    }

    @Override
    public Boolean genChartByAiAsyncMq(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        // 非管理员用户需要校验次数
        if (!SecurityUtils.isAdmin(userId)) {
            ThrowUtils.throwIf(loginUser.getUser().getCurrency() <= 0, ErrorCode.NO_AUTH_ERROR, "调用次数不足");
        }

        if (TEST_FLAG) {
            return true;
        }
        // region ---- 校验数据、限流 ----
        String chartName = genChartByAiRequest.getChartName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        // 如果分析目标为空，就抛出请求参数错误异常，并给出提示
        verify(multipartFile, chartName, goal);

        // 使用 Redisson 限流（针对每个用户请求当前方法时限流）
        redisLimiterManager.setRedisCurrentLimit("genChartByAi_" + userId);

        // endregion

        // 压缩后的数据（把multipartFile传进来，其他的东西先注释）
        String csvData = ExcelUtils.excelToCsv(multipartFile);

        // 先把图表 非生成数据 保存到数据库中，设置任务状态为“排队中”
        Chart chart = new Chart();
        Long chartId = Math.abs(RandomUtils.nextLong());
        chart.setId(chartId);
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setUserId(userId);
        chart.setStatus("wait");
        boolean saveResult = chartMapper.insertChart(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // 在最终的返回结果前提交一个任务
        // todo 当前为生产者，向消息队列中发送需要处理的 唯一ID
        biMessageProducer.sendMessage(String.valueOf(chartId));

        CompletableFuture.runAsync(() -> {
            userMapper.updateCurrency(userId);
            log.info("用户 {} 消耗 {} 通行货币积分", userId, "1");
        });

        return saveResult;
    }

    @Override
    public Chart getChartById(long chartId) {
        return chartMapper.selectById(chartId);
    }

    @Override
    public Boolean updateChartById(Chart updateChartResult) {
        return chartMapper.updateChartById(updateChartResult);
    }

    @Override
    public List<Chart> listMyChartByPage(ChartQueryRequest chartQueryRequest, HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = SecurityUtils.getUserId();
        chartQueryRequest.setUserId(userId);
        // 限制爬虫
        ThrowUtils.throwIf(chartQueryRequest.getPageSize() > 20, ErrorCode.PARAMS_ERROR);
        return chartMapper.listMyChartByPage(chartQueryRequest);
    }


    /**
     * 发生 异常 情况，根据图标ID更新数据量并记录
     */
    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(ChartStatusEnum.FAILED.getStatusCode());
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = chartMapper.updateChartById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

    /**
     * 前置校验
     *
     * @param multipartFile 需要校验的文件
     * @param chartName     校验图表名
     * @param goal          校验的目标需求（帮我分析网站的增长趋势）
     */
    private void verify(MultipartFile multipartFile, String chartName, String goal) {
        // 如果分析目标为空，就抛出请求参数错误异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");

        // 如果名称不为空，并且名称长度大于100，就抛出异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName) && chartName.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        //文件校验
        //1、校验文件大小
        long fileSize = multipartFile.getSize();
        String filename = multipartFile.getOriginalFilename();
        ThrowUtils.throwIf(fileSize > STIPULATE_SIZE, ErrorCode.PARAMS_ERROR, "超出文件最大限制 1MB");
        //2、校验文件后缀名
        String fileSuffix = FileUtil.getSuffix(filename);
        ThrowUtils.throwIf(!VALID_FILE_SUFFIX_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式不符合要求");
    }

    /**
     * 模拟返回对象
     */
    private BiResponseVO getBiResponseVO() {
        BiResponseVO biResponseVO = new BiResponseVO();
        biResponseVO.setGenChart("{\n" +
                "  \"title\": {\n" +
                "    \"text\": \"网站增长趋势\",\n" +
                "    \"subtext\": \"用户数\"\n" +
                "  },\n" +
                "  \"tooltip\": {\n" +
                "    \"trigger\": \"axis\"\n" +
                "  },\n" +
                "  \"xAxis\": {\n" +
                "    \"type\": \"category\",\n" +
                "    \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\"]\n" +
                "  },\n" +
                "  \"yAxis\": {\n" +
                "    \"type\": \"value\"\n" +
                "  },\n" +
                "  \"series\": [\n" +
                "    {\n" +
                "      \"data\": [10, 20, 90, 20, 50],\n" +
                "      \"type\": \"line\",\n" +
                "      \"smooth\": true\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        biResponseVO.setGenResult("根据给定的原始数据，可以看到网站的用户数在不同日期上有所变化。根据柱状图可以得出以下结论：\n" +
                "- 在3号上网站的用户数达到最高点，为90人。\n" +
                "- 在2号、4号以及5号上网站的用户数波动较小，保持在20至50人之间。\n" +
                "- 在1号上，网站的用户数为10人，是整个时间段内的最小值。");
        biResponseVO.setChartId(UUID.randomUUID().getLeastSignificantBits());
        try{ Thread.sleep(3000); } catch (Exception e) { e.printStackTrace(); }
        log.info("------模拟生成数据，耗时3秒-----------");
        return biResponseVO;
    }

    /**
     * 模拟生成异步数据
     */
    private Chart getChart() {
        Chart chart = new Chart();
        chart.setChartName(UUID.randomUUID().toString().substring(5));
        chart.setGoal("分析目标");
        chart.setChartData("原始数据");
        chart.setGenResult("返回结果");
        chart.setChartData("{\n" +
                "  \"title\": {\n" +
                "    \"text\": \"网站增长趋势\",\n" +
                "    \"subtext\": \"用户数\"\n" +
                "  },\n" +
                "  \"tooltip\": {\n" +
                "    \"trigger\": \"axis\"\n" +
                "  },\n" +
                "  \"xAxis\": {\n" +
                "    \"type\": \"category\",\n" +
                "    \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\"]\n" +
                "  },\n" +
                "  \"yAxis\": {\n" +
                "    \"type\": \"value\"\n" +
                "  },\n" +
                "  \"series\": [\n" +
                "    {\n" +
                "      \"data\": [10, 20, 90, 20, 50],\n" +
                "      \"type\": \"line\",\n" +
                "      \"smooth\": true\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        chart.setChartType("图表类型");
        return chart;
    }


}





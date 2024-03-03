<template>
  <div class="fenxi">
    <el-row :gutter="24">
      <!-- 智能分析 填表数据 -->
      <el-col :span="12">
        <el-card header="智能分析">
          <el-form ref="addChart" :model="form" label-width="100px" @submit.prevent="onFinish" :rules="rules">
            <el-form-item label="分析目标" prop="goal">
              <el-input type="textarea" placeholder="请输入你的分析需求，比如：分析网站用户的增长情况" v-model="form.goal"></el-input>
            </el-form-item>
            <el-form-item label="图表名称" prop="chartName">
              <el-input placeholder="请输入图表名称" v-model="form.chartName"></el-input>
            </el-form-item>

            <el-form-item label="图表类型" prop="chartType">
              <el-select v-model="form.chartType" placeholder="请选择">
                <el-option v-for="item in chartTypes" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="原始数据" prop="file">

              <el-upload action="" :file-list="fileList" :on-exceed="handleExceed" :before-upload="beforeUpload">
                <el-button slot="trigger" type="primary" icon="el-icon-upload">
                  上传 EXCEL 文件
                </el-button>
              </el-upload>

            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="onFinish">
                提交
              </el-button>
              <el-button @click="resetForm('addChart')">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 可视化图表 分析结果 -->
      <el-col :span="12" class="jieguo" style="margin-bottom: 20px;">
        <el-card header="分析结论">
          <div v-show="!this.submitting && !chart">请先在左侧进行提交</div>
          <el-spinner v-show="this.submitting" :spinning="this.submitting"></el-spinner>
          <div>
            <el-table-column prop="">
              <template slot-scope="">
                {{ conclusion }}
              </template>
            </el-table-column>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="jieguo" style="margin-bottom: 20px;">
        <el-card header="可视化图表">
          <div v-show="!this.submitting && !chart">请先在左侧进行提交</div>
          <el-spinner v-show="this.submitting" :spinning="this.submitting"></el-spinner>
          <div v-show="chart" id="eChartsData" style="width: 600px; height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { genChartByAiUsingPOST } from "@/api/bi/bi.js";
import * as echarts from 'echarts'; // 导入 ECharts 库

export default {
  data() {
    return {
      form: {
        goal: '',
        chartName: '',
        chartType: '',
        // file: null,
      },
      fileList: [],
      submitting: false,
      chart: null,
      option: null,
      conclusion: null,
      chartTypes: [
        { value: '折线图', label: '折线图' },
        { value: '柱状图', label: '柱状图' },
        { value: '堆叠图', label: '堆叠图' },
        { value: '饼图', label: '饼图' },
        { value: '雷达图', label: '雷达图' },
      ],
      rules: {
        goal: [{ required: true, message: '请输入分析目标', trigger: 'blur' }],
        chartName: [{ required: true, message: '请输入图表名称', trigger: 'blur' }],
        file: [{ required: true, message: '请上传文件', trigger: 'change' }],
      },
    };
  },
  methods: {
    async onFinish() {
      this.submitting = true;
      if (this.form.goal == null || this.form.goal === ''){
        this.$message.error('请输入分析目标');
        return;
      }
      if(this.form.chartName == null || this.form.chartName === ''){
        this.$message.error('请输入图表名称');
        return;
      }
      console.log("上传文件：", this.form.file);
      if(this.form.file == null){
        this.$message.error('请选择文件');
        return;
      }

      /*开始提交前将内容置空*/
      this.chart = null;
      this.conclusion = null;

      const formData = new FormData();
      formData.append('file', this.form.file);
      formData.append('goal', this.form.goal);
      formData.append('chartName', this.form.chartName);
      formData.append('chartType', this.form.chartType);
      this.resetForm('addChart');
      try {
        const result = await genChartByAiUsingPOST(formData);
        console.log(" result.data: ", result.data);
        if (result.data.code != '200') {
          this.$message.error('分析失败,' + result.data.msg);
        }else {
          this.$message.success('分析成功');
          console.log("result.data.data: ", result.data.data)
          const chartOption = JSON.parse(result.data.data.genChart ?? '');

          var myChart = echarts.init(document.getElementById("eChartsData"));
          myChart.setOption(chartOption);
          console.log("chartOption: ", chartOption);

          if (!chartOption) {
            throw new Error('图表代码解析错误');
          } else {
            // chart.setOption(chartOption);
            this.chart = chartOption;
            this.conclusion = result.data.data.genResult;
          }
        }
      } catch (e) {
        this.$message.error('分析失败，' + e.message);
      }finally {
        this.submitting = false; // 在提交结束时设置为 false
      }
    },
    handleExceed() {
      this.$message.warning('文件超出限制大小！');
    },
    beforeUpload(file) {
      this.form.file = file;
      this.fileList = [file];
      return false;
    },
    resetForm(formRefName) {
      this.$refs[formRefName].resetFields();
      this.fileList = [];
      this.chart = null;
      this.conclusion = null;
    },
  },
};
</script>

<style>
.fenxi {
  margin: 20px 30px 20px 30px;
  /* 调整边距（上右下左） */
}

.jieguo {
  margin: 0px 0px 0px 0px;
}
</style>

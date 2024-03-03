<template>
  <div class="fenxi">

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
                <el-option
                  v-for="item in chartTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="原始数据" prop="file">
              
              <el-upload 
                action="" 
                :file-list="fileList" 
                :on-exceed="handleExceed" 
                :before-upload="beforeUpload">
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
  </div>
</template>

<script>
import { genChartByAiAsync } from "@/api/bi/bi.js";

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
      if (this.submitting) {
        return;
      }
      this.submitting = true;
      this.chart = null;
      this.option = null;

      const formData = new FormData();
      formData.append('file', this.form.file);
      formData.append('goal', this.form.goal);
      formData.append('chartName', this.form.chartName);
      formData.append('chartType', this.form.chartType);
      console.log("=========" + this.form.file);
      try {
        const response = await genChartByAiAsync(formData);
        console.log(response)
        if (response.data.code != 200) {
          this.$message.error('分析失败-' + response.data.msg);
          this.resetForm('addChart');
        } else {
          this.$message.success('分析成功');
          this.resetForm('addChart');
        }
      } catch (e) {
        this.resetForm('addChart');
        this.$message.error('分析失败，' + e.message);
      }
      // 重置表单
      this.submitting = false;
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
      this.option = null;
    },
  },
};
</script>

<style>
.fenxi {
  margin: 20px 30px 20px 30px;
  /* 调整边距（上右下左） */
}

.jieguo{
  margin: 0px 0px 0px 0px;
}
</style>
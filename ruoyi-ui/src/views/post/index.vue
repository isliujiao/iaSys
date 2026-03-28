<template>
  <div id="main">
    <el-form ref="form" :model="formData">
      <el-form-item prop="title" :rules="[{ required: true, message: '标题不能为空' },{ min: 5, max: 100, message: '标题长度5-100个字符' }]">
        <el-input
          v-model="formData.title"
          placeholder="请输入文章标题"
          clearable
        />
      </el-form-item>

      <el-form-item prop="tags" label="文章标签">
        <el-select
          v-model="formData.tags"
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="请选择或输入标签"
        />
      </el-form-item>

      <mavon-editor
        v-model="formData.content"
        class="markdown"
        :toolbars="markdownOption"
        @save="handleSave"
      />

      <el-form-item>
        <el-button type="primary" @click="submitForm">立即发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { addArticle } from '@/api/system/articleManager';
export default {
  data() {
    return {
      formData: {
        title: '',
        tags: [],
        content: ''
      },
      markdownOption: {
        bold: true,
        italic: true,
        header: true
        //...其他编辑器配置
      }
    }
  },
  methods: {
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.handleSave()
        } else {
          this.$message.error('请完善表单内容')
          return false
        }
      })
    },


    // 在methods中修改调用方式
    handleSave() {
      const submitData = {
        ...this.formData,
        tags: this.formData.tags.join(','),
      }
      addArticle(submitData)
        .then(() => {
          this.$message.success('发布成功')
          this.resetForm()
        })
        .catch(error => {
          this.$message.error('发布失败：' + error.response.data.msg)
        })
    },
    resetForm() {
      this.formData = {
        title: '',
        tags: [],
        content: ''
      }
    }
  }
}
</script>

<style scoped>
/* 样式调整，可以根据需要添加更多的CSS来美化界面 */
#main {
  margin: 20px 30px 20px 30px;
  /* 调整边距（上右下左） */
}
/* 设置Markdown编辑器的高度 */
.markdown {
  height: 500px; /* 您可以根据需要调整这个值 */
}
</style>

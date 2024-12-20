<template>
  <div id="main">
    <el-row type="flex">
      <el-col :span="22">
        <el-input
          v-model="title"
          placeholder="请输入文章标题 (5~100个字)"
          :maxlength="100"
          @input="checkTitleLength"
        ></el-input>
      </el-col>
      <el-col :span="2">
        <el-button type="primary" @click="publishArticle">发布文章</el-button>
      </el-col>
    </el-row>
    <!-- Markdown编辑器 -->
    <mavon-editor class="markdown" v-model="content" @save="handleSave" />

  </div>
</template>

<script>
export default {
  name: "index",
  data() {
    return {
      title: '', // 标题
      tagList: ['标签1', '标签2', '标签3', '标签4'], // 所有标签列表
      selectedTags: [], // 选中的标签
      content: '' // Markdown内容
    };
  },
  methods: {
    handleSave() {
      // 构造要发送的数据对象
      const data = {
        title: this.title,
        tags: this.selectedTags,
        content: this.content
      };

      // 调用API接口，这里使用axios作为HTTP客户端
      this.axios.post('/api/save', data)
        .then(response => {
          // 处理响应
          console.log(response);
          alert('保存成功');
        })
        .catch(error => {
          // 处理错误
          console.error(error);
          alert('保存失败');
        });
    }
  }
};
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

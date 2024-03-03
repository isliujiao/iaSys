<template>
  <div class="container">
    <el-input v-model="searchText" placeholder="请输入搜索关键词">
      <el-button slot="append" icon="el-icon-search" @click="onSearch">搜索</el-button>
    </el-input>

    <div class="space"></div>

    <el-tabs v-model="activeTab" @tab-click="onTabChange">
      <el-tab-pane label="文章" name="post">
        <PostList :post-list="postList" />
      </el-tab-pane>

      <el-tab-pane label="图片" name="picture">
        <div v-if="searchText != ''">
          <PictureList :picture-list="pictureList" />
        </div>
        <div v-else>
          请输入搜索内容……
        </div>
      </el-tab-pane>

      <el-tab-pane label="音乐" name="music">
        <!--        <UserList :user-list="userList" />-->
        暂未开放……
      </el-tab-pane>

      <el-tab-pane label="视频" name="video">
        暂未开放……
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import PostList from "@/views/search/components/PostList.vue";
import PictureList from "@/views/search/components/PictureList.vue";
import UserList from "@/views/search/components/UserList.vue";
import { searchAll } from "@/api/search/search.js";

export default {

  components: {
    PostList,
    PictureList,
    UserList,
  },

  data() {
    return {
      postList: '',
      pictureList: '',
      userList: '',
      searchText: '', // 搜索框中的文本
      activeTab: 'post', // 默认标签页为文章
      // 可以添加其他需要的数据
    };
  },

  methods: {
    onSearch() {
      this.fetchData();
    },
    onTabChange(tab) {
      // 在标签页切换时触发的方法
      this.activeTab = tab.name; // 更新活动标签页为点击的标签页的名称
      this.fetchData()
    },
    fetchData() {
      const queryParams = {
        type: this.activeTab, // 使用当前活动的标签作为搜索类型
        searchText: this.searchText,
        pageSize: 10,
        pageNum: 1,
      };
      if(this.searchText != ''){
        searchAll(queryParams).then(response => {
          if (queryParams.type === "post") {
            this.postList = response.data.dataList.rows;
          } else if (queryParams.type === "picture") {
            this.pictureList = response.data.dataList.rows;
          } else if (queryParams.type === "user") {
            this.userList = response.data.dataList.rows;
          }
        });
      }
    },
  }
};
</script>


<style>
.container {
  margin: 20px 70px 20px 70px;
  /* 调整边距（上右下左） */
}

.space {
  height: 20px;
  /* 设置间距高度 */
}
</style>

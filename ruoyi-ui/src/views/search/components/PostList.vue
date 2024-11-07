<template>
  <div>
    <div class="hot-search">
      <span> <i class="top-5-label"> Top 5：</i>
      </span>
      <span v-for="(item, index) in postHotList" :key="index" class="hot-search-term">
        <span class="full-hot-term" @mouseenter="setFullHotTerm(index, true)"
        @mouseleave="setFullHotTerm(index, false)">
          {{ getHotTerm(index) }}
        </span>
      </span>
    </div>

    <hr class="separator">

    <div class="article-list">
      <!-- Display articles with title and content -->
      <div v-for="(post, index) in postList" :key="index" class="article" @click="showArticleDetails(post)">
        <h3 class="article-title">{{ index + 1 }}、{{ post.title }}</h3>
        <div class="article-content">{{ truncatedContent(post) }}</div>
        <div class="article-icon">👍</div>
      </div>
    </div>

    <article-modal :showModal="isModalVisible" :selectedArticle="selectedArticle" @close="closeModal" />

  </div>
</template>

<script>
import { searchHot } from "@/api/search/search.js";
import ArticleModal from "@/views/search/components/ArticleModal.vue";


export default {
  components: {
    ArticleModal,
  },
  props: {
    postList: {
      type: Array,
      default: () => [],
    }
    // 可以在这里添加其他的 props
  },
  data() {
    return {
      postList: [], // 这里存放你的数据
      postHotList: [],
      showFullHotTerm: Array(5).fill(false), // 初始化数组用于控制显示热门搜索词的状态
      isModalVisible: false,
      selectedArticle: null,
    };
  },
  mounted() {
    // 初始化加载前5搜索词
    this.searchHotTop5();
  },
  methods: {
    truncatedContent(post) {
      // 截取前100个字符
      return post.content ? post.content.slice(0, 100)+'……' : '';
    },
    searchHotTop5() {
      searchHot().then(response => {
        console.log("response-->", response)
        this.postHotList = response.data;
        console.log("postHotList-->", this.postHotList)
      });
    },

    setFullHotTerm(index, value) {
      this.$set(this.showFullHotTerm, index, value);
    },

    getHotTerm(index) {
      const item = this.postHotList[index];
      if (this.showFullHotTerm[index]) {
        return `${index + 1}. ${item.title}`;
      } else {
        if (item.title.length <= 10) {
          return `${index + 1}. ${item.title}`;
        } else {
          return `${index + 1}. ${item.title.slice(0, 10)}...`;
        }
      }
    },
    // 展示详情
    showArticleDetails(article) {
      this.selectedArticle = article;
      this.isModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
      this.selectedArticle = null;
    },
  }
};
</script>

<style scoped>
/* 分隔符 */
.separator {
  margin: 20px 0px;
  /* Adjust margin as needed */
  border: 2px solid #ddd;
  /* Border color and style */
}

/* Your scoped styles */
.hot-search {
  margin-bottom: 20px;
}

.hot-search-term {
  display: inline-block;
  margin-right: 10px;
  font-size: 14px;
}

.article-list {
  margin-top: 20px;
}

.short-hot-term,
.full-hot-term {
  background-color: #f2f2f2;
  padding: 5px 10px;
  border-radius: 5px;
  display: inline-block;
}

.article {
  margin-bottom: 20px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease-in-out;
}

.hot-search-term:hover .full-hot-term {
  background: linear-gradient(45deg, #ff9900bc, #ff2d55);
  color: rgb(255, 255, 255);
}
.article:hover {
  box-shadow: 0px 8px 16px rgba(0, 0, 0, 0.2);
}

.article-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.article-content {
  font-size: 16px;
}
.top-5-label {
  font-size: 15px;
  font-weight: bold;
  background: linear-gradient(45deg, #ff9900bc, #ff2d55);
  padding: 5px 10px;
  border-radius: 5px;
  display: inline-block;
  -webkit-background-clip: text;
  color: transparent;
}


</style>

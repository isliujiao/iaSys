<template>
    <transition name="fade">
      <div v-if="showModal" class="modal" @click="closeModal">
        <div class="modal-content" @click.stop>
          <button class="close-button" @click="closeModal">×</button>
          <h3 class="modal-title">{{ selectedArticle.title }}</h3>
          <div class="content-container">
            <vue-markdown :source="selectedArticle.content" />
          </div>
        </div>
      </div>
    </transition>
  </template>
  
  <script>
  import VueMarkdown from 'vue-markdown';
  
  export default {
    components: {
      VueMarkdown,
    },
    props: {
      showModal: Boolean,
      selectedArticle: Object,
    },
    methods: {
      closeModal() {
        this.$emit('close');
      },
    },
  };
  </script>
  
  <style scoped>
  /* 在这里添加模态框样式 */
  .fade-enter-active, .fade-leave-active {
    transition: opacity 0.3s;
  }
  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
  
  .modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .modal-content {
    background: #fff;
    padding: 30px;
    border-radius: 12px;
    max-width: 800px;
    width: 90%;
    overflow-y: auto;
    z-index: 1001;
    position: relative;
  }
  
  .modal-title {
    font-size: 26px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
  }
  
  .content-container {
    max-height: 400px;
    font-size: large;
  }
  
  .close-button {
    position: absolute;
    top: 10px;
    right: 10px;
    font-size: 20px;
    background: none;
    border: none;
    color: #666;
    cursor: pointer;
  }
  
  .close-button:hover {
    color: #333;
  }
  
  /* 样式可以根据需要进行调整 */
  </style>
  
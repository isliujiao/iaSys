<template>
  <el-row :data="pictureList" :gutter="16">
    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" :xxl="3" v-for="(item, index) in pictureList" :key="index">
      <el-card shadow="hover">
        <img :src="item.url" alt="example" style="width: 100%" />
        <div slot="header" class="clearfix">
          <span>{{ item.title }}</span>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
export default {
  props: {
    pictureList: {
      type: Array,
      default: () =>[],
    }
    // 可以在这里添加其他的 props
  },
  data() {
    return {
      dataList: [] // 这里存放你的数据
    };
  },
}
</script>

<style scoped>
.box-card {
  width: 100%;
  margin-bottom: 20px;
}
</style>

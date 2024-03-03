<!--

  用户查询对于本系统在这里查询无用处，有专门的用户查询。待改成其他

-->
<template>
  <el-row :data="userList">
    <el-col v-for="(item, index) in userList" :key="index" :span="24">
      <el-card shadow="hover" style="width: 240px">
        <div slot="header" class="clearfix">
          <span>{{ item.nickName }}</span>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
export default {
  props: {
    userList: {
      type: Array,
      default: () =>[],
    }
    // 可以在这里添加其他的 props
  },
  data() {
    return {
      userList: [] // 这里存放你的数据
    };
  }
}
</script>

<style scoped>
/* Your scoped styles */
.gege {
  width: 200px;
}
</style>

<style scoped>
/* Your scoped styles */
.gege {
  width: 200px;
}
</style>


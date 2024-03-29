<template>
  <div class="my-chart-page">
    <!-- Search input -->
    <div>
      <el-input placeholder="请输入图表名称" v-model="searchValue" suffix-icon="el-icon-search" @search="handleSearch"
        :loading="loading">
        <el-button slot="append" icon="el-icon-search" @click="getList">搜索</el-button>
      </el-input>
    </div>

    <!-- List of charts -->
    <el-row :gutter="24" class="grid-charts">
      <el-col :span="colSpan" class="custom-chart-col" v-for="(chart, index) in chartList" :key="chart.id">
        <el-card class="chart-card" :body-style="{ padding: '20px' }">
          <div>
            <!-- <el-avatar :src="currentUser && currentUser.userAvatar"></el-avatar> -->
            <h2>{{ chart.chartName }}</h2>
                        <p>创建时间：{{ formatTime(chart.updateTime) }}</p>
          </div>
          <p v-if="chart.chartType">图表类型：{{ chart.chartType }}</p>
          <p>分析目标：{{ chart.goal }}</p>
          <el-divider></el-divider>
          <el-scrollbar wrap-class="chart-scroll">
            <div :id="`chartContainer_${index}`" ref="Echarts" style="width: 450px; height: 320px"></div>

          </el-scrollbar>
        </el-card>
      </el-col>

    </el-row>

    <!-- Pagination -->
    <el-pagination class="pagination" @current-change="handlePageChange" :current-page="searchParams.pageNum"
      :page-size="searchParams.pageSize" :total="total"></el-pagination>
  </div>
</template>

<script>
import { listMyChartByPage } from '@/api/bi/bi.js';
import { mapState } from 'vuex';
import * as echarts from 'echarts'; // 导入 ECharts 库

export default {
  data() {
    return {
      searchParams: {
        pageNum: 1,
        pageSize: 4,
        chartName: '',
      },
      searchValue: '',
      chartList: [],
      echartsInstances: [],
      total: 0,
      loading: true,
    };
  },
  // todo 这俩(created、mounted)必须同时都在……why？
  created() {
    setTimeout(() => {
      this.getList();
    }, 1000);
  },
  mounted() {
    this.$nextTick(() => {
      this.getList();
    });
  },
  computed: {
    ...mapState(['currentUser']),
    colSpan() {
      const screenWidth = window.innerWidth;
      if (screenWidth < 576) return 1;
      else if (screenWidth < 768) return 1;
      else if (screenWidth < 992) return 1;
      else if (screenWidth < 1200) return 2;
      else if (screenWidth < 1600) return 2;
      else return 2;
    },
  },

  methods: {
    getList() {
      this.loading = true;
      try {
        listMyChartByPage(this.searchParams).then((response) => {
          if (response) {
            this.chartList = response.rows;
            this.total = response.total;
            this.chartList.forEach((data, index) => {
              const chartOption = JSON.parse(data.genChart || '{}');
              const chartContainerId = `chartContainer_${index}`;
              // 等待一段时间再初始化 ECharts 实例
              setTimeout(() => {
                const chartInstance = echarts.init(document.getElementById(chartContainerId));
                chartInstance.setOption(chartOption);
                chartOption.title = undefined;
                this.echartsInstances.push(chartInstance);
              }, 100);
            });
          } else {
            this.$message.error('获取我的图表失败');
          }
        }).finally(() => {
          this.loading = false;
        });
      } catch (e) {
        this.$message.error('获取我的图表失败，' + e.message);
        this.loading = false;
      }
    },

    handleSearch(value) {
      this.searchParams = {
        ...this.searchParams,
        pageNum: 1,
        chartName: value,
      };
    },
    handlePageChange(page) {
      this.searchParams = {
        ...this.searchParams,
        pageNum: page,
      };
      this.$nextTick(() => {
        // 在切换页面前清理之前的 ECharts 实例
        this.echartsInstances.forEach(instance => {
          instance.dispose();
        });
        this.echartsInstances = []; // 清空实例数组
        this.getList();
      });
    },
    watch: {
      'searchParams.pageNum': {
        handler(newValue, oldValue) {
          // 如果分页数据发生变化，则异步执行 getList() 方法
          this.$nextTick(() => {
            setTimeout(() => {
              this.getList();
            }, 1000);
          });
        },
        immediate: true, // 立即执行一次
        deep: true // 深度监视
      }
    },

    parseChartOptions(genChart) {
      return genChart && JSON.parse(genChart);
    },
    formatTime(time) {
      const date = new Date(time);
      let originalDate = new Date(date);
      // 获取日期部分
      let year = originalDate.getFullYear();
      let month = ('0' + (originalDate.getMonth() + 1)).slice(-2); // 注意月份是从0开始计数的，需要加1
      let day = ('0' + originalDate.getDate()).slice(-2);
      // 获取时间部分
      let hours = ('0' + originalDate.getHours()).slice(-2);
      let minutes = ('0' + originalDate.getMinutes()).slice(-2);
      let seconds = ('0' + originalDate.getSeconds()).slice(-2);
      // 构建新的日期时间格式
      let formattedDateTimeString = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      // 输出格式化后的日期时间字符串
      return formattedDateTimeString;
    },
  },

};
</script>

<style>
.chart-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.chart-scroll {
  max-height: 300px;
  /* Set your desired max height */
  overflow-y: auto;
}

.grid-charts {
  margin-top: 20px;
}

.my-chart-page {
  margin-left: 50px;
  margin-right: 50px;
}

.custom-chart-col {
  width: 500px;
  /* 设置你想要的宽度 */
  height: 500px;
}
</style>

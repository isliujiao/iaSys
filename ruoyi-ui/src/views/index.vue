<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24">
        <blockquote class="text-warning" style="font-size: 14px">

        </blockquote>
        <hr />
      </el-col>
    </el-row>
    <el-row :gutter="10">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2>IA-智能分析平台</h2>
        <p>
          <b>当前版本:</b> <span>v{{ version }}</span>
        </p>
        <p>
          <el-button type="primary" size="mini" icon="el-icon-cloudy" plain
            @click="goTarget('https://gitee.com/isliujiao')">访问码云</el-button>
          <el-button type="primary" size="mini" icon="el-icon-cloudy" plain
            @click="goTarget('https://github.com/isliujiao')">访问GitHub</el-button>
          <el-button size="mini" icon="el-icon-s-home" plain
            @click="goTarget('https://gitee.com/isliujiao')">访问主页</el-button>
        </p>

        <el-row>
          <el-col :span="12">
            <h2>技术选型</h2>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <h4>后端技术</h4>
            <ul>
              <li>SpringBoot</li>
              <li>Spring Security</li>
              <li>JWT</li>
              <li>MyBatis</li>
              <li>Druid</li>
              <li>Fastjson</li>
              <li>...</li>
            </ul>
          </el-col>
          <el-col :span="6">
            <h4>前端技术</h4>
            <ul>
              <li>Vue</li>
              <li>Vuex</li>
              <li>Element-ui</li>
              <li>Axios</li>
              <li>Sass</li>
              <li>Quill</li>
              <li>...</li>
            </ul>
          </el-col>
        </el-row>
      </el-col>

      <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <h3>公告：</h3>
        <el-carousel indicator-position="outside">
          <el-carousel-item v-for="item in noticeList" :key="item.title">
            <h3>{{ item.noticeTitle }}</h3>
            <p v-html="item.noticeContent"></p>
          </el-carousel-item>
        </el-carousel>
      </el-col>
    </el-row>
    <el-divider />
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>联系信息</span>
          </div>
          <div class="body">
            <p>
            </p>
            <p>
              <i class="el-icon-chat-dot-round"></i> 微信：<a href="javascript:;">15666007723</a>
            </p>
            <p>
              <i class="el-icon-money"></i> 支付宝：<a href="javascript:;" class="支付宝信息">15666007723</a>
            </p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>更新日志</span>
          </div>
          <el-collapse accordion>
            <el-collapse-item title="v1.2.0 - 2024-01-15">
              <ol>
                <li>整合AIGC</li>
              </ol>
            </el-collapse-item>
            <el-collapse-item title="v1.1.0 - 2023-12-28">
              <ol>
                <li>整合BI智能分析</li>
              </ol>
            </el-collapse-item>
            <el-collapse-item title="v1.0.0 - 2023-11-24">
              <ol>
                <li>整合聚合搜索平台</li>
              </ol>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>捐赠支持</span>
          </div>
          <div class="body">
            <!-- <img
              src="@/assets/images/pay.png"
              alt="donate"
              width="100%"
            /> -->
            <span style="display: inline-block; height: 30px; line-height: 30px">你可以请作者喝杯咖啡表示鼓励</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div class="footer-copyright">
      <p>版权所有：© <a href="https://github.com/isliujiao" target="_blank"> isliujiao@github.com </a> 备案号：<a href="https://beian.miit.gov.cn/" target="_blank"> 鲁ICP备2024061781号-1</a></p>
    </div>
  </div>
</template>

<script>
import { listNotice } from "@/api/system/notice";
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.8.6",
      noticeIndex: 0,
      noticeList: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询公告列表 */
    getList() {
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows;
        this.total = response.total;
      });
      console.log(this.noticeList);
    },
    goTarget(href) {
      window.open(href, "_blank");
    }
  }
};
</script>

<style scoped lang="scss">
.footer-copyright {
  text-align: center;
  margin-top: 20px;
  padding: 10px 0;
  font-size: 12px;
  color: #999;
  border-top: 1px solid #eee;
}

.el-carousel__item h3 {
  /* 文字颜色 */
  color: #fff;
  /* 字体大小 */
  font-size: 18px;
  /* 行高 */
  // line-height: 10px;
  margin: 0;
  padding: 10px;
}

.el-carousel__item p {
  /* 文字颜色 */
  color: #000000;
  /* 字体大小 */
  font-size: 16px;
  /* 行高 */
  line-height: 24px;
  margin: 0;
  padding: 10px;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: #6ab9ed7c;
  /* 偶数项背景颜色 */
}

.el-carousel__item:nth-child(2n) {
  background-color: rgba(238, 130, 238, 0.451);
  /* 奇数项背景颜色 */
}

.el-carousel__indicators {
  bottom: 10px;
  /* 指示器距离底部的位置 */
}

.notice-container {
  overflow: hidden;
  height: 80px;
  /* 调整轮播容器的高度 */
}

.el-carousel__item {
  border-radius: 8px;
  /* 圆角 */
  overflow: hidden;
}

.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans",
  "Helvetica Neue",
  Helvetica,
  Arial,
  sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>


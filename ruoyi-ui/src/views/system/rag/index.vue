<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="文档名称" prop="fileName">
        <el-input
          v-model="queryParams.fileName"
          placeholder="请输入文档名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文档状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="就绪" value="ready" />
          <el-option label="处理中" value="processing" />
          <el-option label="失败" value="error" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :before-upload="beforeUpload"
          accept=".pdf,.docx,.xlsx,.txt"
        >
          <el-button type="primary" plain icon="el-icon-upload" size="mini" v-hasPermi="['rag:document:add']">
            上传文档
          </el-button>
        </el-upload>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 文档列表 -->
    <el-table v-loading="loading" :data="documentList">
      <el-table-column label="文档名称" prop="fileName" :show-overflow-tooltip="true" width="250" />
      <el-table-column label="文档类型" prop="fileType" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.fileType">{{ scope.row.fileType.toUpperCase() }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="文件大小" prop="fileSize" width="120" align="center">
        <template slot-scope="scope">
          {{ formatFileSize(scope.row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column label="分块数量" prop="chunkCount" width="100" align="center" />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 'ready'" type="success">就绪</el-tag>
          <el-tag v-else-if="scope.row.status === 'processing'" type="warning">处理中</el-tag>
          <el-tag v-else-if="scope.row.status === 'error'" type="danger">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上传人" prop="createBy" width="120" align="center" />
      <el-table-column label="上传时间" prop="createTime" width="180" align="center" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleReprocess(scope.row)"
            v-hasPermi="['rag:document:edit']"
            :disabled="scope.row.status === 'processing'"
          >重新处理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['rag:document:remove']"
            style="color: #f56c6c"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 上传说明对话框 -->
    <el-dialog title="上传说明" :visible.sync="uploadHelpVisible" width="500px" append-to-body>
      <el-alert type="info" :closable="false" show-icon>
        <template slot="title">
          <span>支持的文档格式：PDF、DOCX、XLSX、TXT</span>
        </template>
      </el-alert>
      <div style="margin-top: 20px">
        <h4>上传后会发生什么？</h4>
        <ol style="line-height: 2">
          <li>文档会被上传到服务器</li>
          <li>系统会自动提取文档内容</li>
          <li>文档会被分割成小块（每块约500字符）</li>
          <li>每个文本块会被转换为向量并存储</li>
          <li>用户@AI提问时，系统会检索相关内容辅助回答</li>
        </ol>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="uploadHelpVisible = false">我知道了</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDocument, delDocument, reprocessDocument } from '@/api/rag/document'
import { getToken } from '@/utils/auth'

export default {
  name: 'RagDocument',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      documentList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fileName: undefined,
        status: undefined
      },
      uploadUrl: process.env.VUE_APP_BASE_API + '/rag/document/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + getToken()
      },
      uploadHelpVisible: false
    }
  },
  created() {
    this.getList()
    // 首次访问显示上传说明
    if (!localStorage.getItem('ragUploadHelpShown')) {
      this.uploadHelpVisible = true
      localStorage.setItem('ragUploadHelpShown', 'true')
    }
  },
  methods: {
    /** 查询文档列表 */
    getList() {
      this.loading = true
      listDocument(this.queryParams).then(response => {
        this.documentList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 搜索按钮 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 上传前检查 */
    beforeUpload(file) {
      const isValidType = ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'text/plain'].includes(file.type)
      const isLt50M = file.size / 1024 / 1024 < 50

      if (!isValidType && !file.name.match(/\.(pdf|docx|xlsx|txt)$/i)) {
        this.$message.error('只能上传 PDF、DOCX、XLSX、TXT 格式的文件！')
        return false
      }
      if (!isLt50M) {
        this.$message.error('文件大小不能超过 50MB！')
        return false
      }

      this.$message.info('正在上传文档，请稍候...')
      return true
    },
    /** 上传成功 */
    handleUploadSuccess(response, file) {
      this.$message.success('文档上传成功，正在后台处理中...')
      this.getList()
    },
    /** 上传失败 */
    handleUploadError() {
      this.$message.error('文档上传失败，请重试！')
    },
    /** 重新处理 */
    handleReprocess(row) {
      this.$confirm('确定要重新处理文档 "' + row.fileName + '" 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        reprocessDocument(row.id).then(() => {
          this.$message.success('文档重新处理任务已提交')
          this.getList()
        })
      }).catch(() => {})
    },
    /** 删除按钮 */
    handleDelete(row) {
      this.$confirm('确定要删除文档 "' + row.fileName + '" 吗？删除后将同时删除所有关联的索引数据。', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delDocument(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    /** 格式化文件大小 */
    formatFileSize(size) {
      if (!size) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(size) / Math.log(k))
      return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }
  }
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style>

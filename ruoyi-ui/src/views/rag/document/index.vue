<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="文件名称" prop="fileName">
        <el-input v-model="queryParams.fileName" placeholder="请输入文件名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="处理中" value="processing" />
          <el-option label="就绪" value="ready" />
          <el-option label="失败" value="error" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-upload :action="uploadUrl" :headers="uploadHeaders" :file-list="fileList"
          :before-upload="handleBeforeUpload" :on-success="handleUploadSuccess"
          :accept="'.pdf,.docx,.xlsx,.txt'" :show-file-list="false">
          <el-button type="primary" icon="el-icon-upload">上传文档</el-button>
        </el-upload>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="documentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column label="文件名称" prop="fileName" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="文件类型" prop="fileType" width="100" align="center">
        <template slot-scope="scope">
          <el-tag size="small">{{ scope.row.fileType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="文件大小" prop="fileSize" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ formatFileSize(scope.row.fileSize) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="分块数" prop="chunkCount" width="100" align="center" />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 'ready'" type="success">就绪</el-tag>
          <el-tag v-else-if="scope.row.status === 'processing'" type="warning">处理中</el-tag>
          <el-tag v-else-if="scope.row.status === 'error'" type="danger">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上传人" prop="createBy" width="120" align="center" />
      <el-table-column label="上传时间" prop="createTime" width="180" align="center" />
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-refresh"
            @click="handleReprocess(scope.row)" :loading="scope.row.reprocessing">重新处理</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color: #F56C6C"
            @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 上传进度对话框 -->
    <el-dialog title="上传文档" :visible.sync="uploadDialogVisible" width="500px">
      <el-upload ref="upload" :action="uploadUrl" :headers="uploadHeaders"
        :before-upload="handleBeforeUpload" :on-success="handleUploadSuccess"
        :on-error="handleUploadError" :accept="'.pdf,.docx,.xlsx,.txt'" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          支持 PDF、Word(.docx)、Excel(.xlsx)、TXT 格式，单文件不超过 10MB
        </div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import { listDocument, getDocument, delDocument, reprocessDocument } from '@/api/rag/document'
import { getToken } from '@/utils/auth'

export default {
  name: 'RagDocument',
  data() {
    return {
      loading: false,
      showSearch: true,
      ids: [],
      documentList: [],
      total: 0,
      uploadDialogVisible: false,
      fileList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fileName: null,
        status: null
      }
    }
  },
  computed: {
    uploadUrl() {
      return process.env.VUE_APP_BASE_API + '/rag/document/upload'
    },
    uploadHeaders() {
      return { Authorization: 'Bearer ' + getToken() }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDocument(this.queryParams).then(response => {
        this.documentList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
    },
    handleBeforeUpload(file) {
      const allowed = ['pdf', 'docx', 'xlsx', 'txt']
      const ext = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase()
      if (!allowed.includes(ext)) {
        this.$message.error('仅支持 PDF、Word、Excel、TXT 格式')
        return false
      }
      if (file.size > 10 * 1024 * 1024) {
        this.$message.error('文件大小不能超过 10MB')
        return false
      }
      this.loading = true
      return true
    },
    handleUploadSuccess(res, file) {
      this.loading = false
      if (res.code === 200) {
        this.$message.success('上传成功，正在处理文档...')
        this.getList()
      } else {
        this.$message.error(res.msg || '上传失败')
      }
    },
    handleUploadError(err) {
      this.loading = false
      this.$message.error('上传失败')
    },
    handleReprocess(row) {
      this.$confirm('确认重新处理该文档吗？这将重新分块和向量化。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$set(row, 'reprocessing', true)
        reprocessDocument(row.id).then(() => {
          this.$message.success('已开始重新处理')
          this.getList()
        }).finally(() => {
          this.$set(row, 'reprocessing', false)
        })
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除文档 "' + row.fileName + '" 吗？这将同时删除向量数据。', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delDocument(row.id)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    formatFileSize(bytes) {
      if (!bytes) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }
  }
}
</script>

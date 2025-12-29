<template>
  <div class="upload-container">
    <h2>数据导入管理</h2>

    <!-- 上传区域 -->
    <el-card class="upload-card">
      <template #header>
        <span>上传环境监测数据</span>
      </template>
      
      <el-upload
        class="upload-demo"
        drag
        :http-request="handleCustomUpload"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :file-list="fileList"
        :disabled="uploading"
        accept=".xlsx,.xls">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text" v-if="!uploading">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <div class="el-upload__text" v-else>
          正在上传，请稍候...
        </div>
        <template #tip>
          <div class="el-upload__tip">只能上传 Excel 文件（.xlsx 或 .xls），且不超过 10MB</div>
        </template>
      </el-upload>

      <el-alert
        title="Excel 格式说明"
        type="info"
        :closable="false"
        style="margin-top: 20px;">
        <p>Excel 文件应包含以下列（表头必须完全匹配）：</p>
        <ul>
          <li><strong>监测点</strong>（必填）：如"1号猪舍"</li>
          <li><strong>监测位置</strong>（选填）：如"江西省南昌市南昌县"</li>
          <li><strong>温度</strong>（选填）：单位℃，如 25.5</li>
          <li><strong>湿度</strong>（选填）：单位%，如 65.0</li>
          <li><strong>CO₂浓度</strong>（选填）：单位ppm，如 450</li>
          <li><strong>氨气浓度</strong>（选填）：单位ppm，如 15</li>
          <li><strong>光照强度</strong>（选填）：单位lux，如 300</li>
          <li><strong>采集时间</strong>（选填）：格式 yyyy-MM-dd HH:mm:ss</li>
        </ul>
        <p style="margin-top: 10px;">
          <el-button link @click="downloadTemplate">下载模板文件</el-button>
        </p>
      </el-alert>
    </el-card>

    <!-- 上传记录 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>上传记录</span>
        <el-button style="float: right; padding: 3px 0" link @click="refreshRecords">刷新</el-button>
      </template>

      <el-table :data="recordList" border stripe>
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="originalName" label="文件名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="100">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="dataType" label="数据类型" width="100"></el-table-column>
        <el-table-column prop="uploadUsername" label="上传人" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="导入结果" width="150">
          <template #default="scope">
            <span v-if="scope.row.status === 'SUCCESS'">
              成功：{{ scope.row.successCount }} / 失败：{{ scope.row.failedCount }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="160"></el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" link @click="handleViewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="上传详情" v-model="detailDialogVisible" width="600px">
      <el-descriptions :column="1" border v-if="currentRecord">
        <el-descriptions-item label="文件名">{{ currentRecord.originalName }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ formatFileSize(currentRecord.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="数据类型">{{ currentRecord.dataType }}</el-descriptions-item>
        <el-descriptions-item label="上传人">{{ currentRecord.uploadUsername }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRecord.status)">
            {{ getStatusText(currentRecord.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="成功数量">{{ currentRecord.successCount }}</el-descriptions-item>
        <el-descriptions-item label="失败数量">{{ currentRecord.failedCount }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ currentRecord.createTime }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" v-if="currentRecord.errorMessage">
          <span style="color: red;">{{ currentRecord.errorMessage }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getUploadRecords, uploadEnvironmentData } from '@/api/upload'
import { ElMessage } from 'element-plus'

export default {
  name: 'DataUpload',
  data() {
    return {
      fileList: [],
      recordList: [],
      detailDialogVisible: false,
      currentRecord: null,
      uploading: false
    }
  },
  created() {
    this.loadRecords()
  },
  methods: {
    // 自定义上传方法，确保 JWT token 被正确传递
    async handleCustomUpload(options) {
      const file = options.file
      this.uploading = true
      
      try {
        // 获取当前用户信息
        const userInfo = localStorage.getItem('my-login-user')
        let userId = 1
        let username = 'admin'
        
        if (userInfo) {
          try {
            const user = JSON.parse(userInfo)
            userId = user.id || 1
            username = user.name || user.username || 'admin'
          } catch (e) {
            console.error('解析用户信息失败:', e)
          }
        }
        
        // 使用 API 方法上传，会自动添加 token
        const response = await uploadEnvironmentData(file, userId, username)
        const resp = response.data
        
        if (resp.flag || resp.success) {
          ElMessage.success('上传成功！')
          this.loadRecords()
          this.fileList = []
          // 调用成功回调
          if (options.onSuccess) {
            options.onSuccess(resp, file, this.fileList)
          }
        } else {
          ElMessage.error(resp.message || '上传失败')
          // 调用失败回调
          if (options.onError) {
            options.onError(new Error(resp.message || '上传失败'), file, this.fileList)
          }
        }
      } catch (error) {
        console.error('上传失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '上传失败'
        ElMessage.error('上传失败：' + errorMsg)
        // 调用失败回调
        if (options.onError) {
          options.onError(error, options.file, this.fileList)
        }
      } finally {
        this.uploading = false
      }
    },
    loadRecords() {
      getUploadRecords().then(response => {
        const resp = response.data
        if (resp.flag) {
          this.recordList = resp.data || []
        } else {
          if (process.env.NODE_ENV === 'development') {
            console.warn('获取上传记录失败:', resp.message)
          }
        }
      }).catch(error => {
        console.error('获取上传记录失败:', error)
        if (error.response?.status === 403) {
          ElMessage.warning('权限不足，无法查看上传记录')
        } else if (error.response?.status === 401) {
          ElMessage.error('登录已过期，请重新登录')
        } else {
          // 静默处理，不影响页面显示
          if (process.env.NODE_ENV === 'development') {
            console.error('获取上传记录失败:', error)
          }
        }
        this.recordList = [] // 确保有默认值
      })
    },
    refreshRecords() {
      this.loadRecords()
      ElMessage.success('刷新成功')
    },
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
                   || file.type === 'application/vnd.ms-excel'
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isExcel) {
        ElMessage.error('只能上传 Excel 文件！')
      }
      if (!isLt10M) {
        ElMessage.error('上传文件大小不能超过 10MB！')
      }
      return isExcel && isLt10M
    },
    handleUploadSuccess(response, file, fileList) {
      // 成功提示已在 handleCustomUpload 中处理
      // 这里可以添加其他成功后的处理逻辑
      this.fileList = []
    },
    handleUploadError(err, file, fileList) {
      // 错误提示已在 handleCustomUpload 中处理
      // 这里可以添加其他错误处理逻辑
      console.error('上传错误:', err)
    },
    handleViewDetail(row) {
      this.currentRecord = row
      this.detailDialogVisible = true
    },
    formatFileSize(bytes) {
      if (!bytes) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
    },
    getStatusType(status) {
      const map = {
        'PROCESSING': 'warning',
        'SUCCESS': 'success',
        'FAILED': 'danger'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'PROCESSING': '处理中',
        'SUCCESS': '成功',
        'FAILED': '失败'
      }
      return map[status] || status
    },
    downloadTemplate() {
      ElMessage.info('模板下载功能开发中...')
      // 实际应用中可以提供一个模板文件下载链接
    }
  }
}
</script>

<style scoped>
.upload-container {
  padding: 20px;
}
.upload-card {
  margin-bottom: 20px;
}
.upload-demo {
  text-align: center;
}
</style>



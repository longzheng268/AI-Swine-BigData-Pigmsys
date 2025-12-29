<template>
  <div class="prediction-container">
    <h2>预测分析</h2>

    <el-row :gutter="20">
      <!-- 左侧：模型选择和参数配置 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>模型选择</span>
          </template>
          
          <el-form :model="predictionForm" label-width="120px">
            <el-form-item label="模型类型">
              <el-select v-model="modelType" placeholder="请选择模型类型" @change="handleModelTypeChange">
                <el-option label="生猪生长预测" value="GROWTH"></el-option>
                <el-option label="环境质量评价" value="ENVIRONMENT"></el-option>
                <el-option label="疾病风险预测" value="DISEASE"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="选择模型">
              <el-select v-model="selectedModelId" placeholder="请选择模型" @change="handleModelChange">
                <el-option
                  v-for="model in filteredModels"
                  :key="model.id"
                  :label="model.modelName"
                  :value="model.id">
                  <span style="float: left">{{ model.modelName }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">
                    精度: {{ (model.accuracy * 100).toFixed(2) }}%
                  </span>
                </el-option>
              </el-select>
            </el-form-item>

            <el-divider></el-divider>

            <!-- 生猪生长预测参数 -->
            <div v-if="modelType === 'GROWTH'">
              <h4>输入参数</h4>
              <el-form-item label="日龄">
                <el-input-number v-model="predictionForm.age" :min="1" :max="365"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">天</span>
              </el-form-item>
              <el-form-item label="饲料摄入量">
                <el-input-number v-model="predictionForm.feed" :min="0" :max="10" :precision="2"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">kg/天</span>
              </el-form-item>
              <el-form-item label="品种">
                <el-select v-model="predictionForm.breed">
                  <el-option label="白猪" value="白猪"></el-option>
                  <el-option label="黑猪" value="黑猪"></el-option>
                  <el-option label="花猪" value="花猪"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="predictionForm.sex">
                  <el-radio value="公">公</el-radio>
                  <el-radio value="母">母</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>

            <!-- 环境质量评价参数 -->
            <div v-if="modelType === 'ENVIRONMENT'">
              <h4>输入参数</h4>
              <el-form-item label="温度">
                <el-input-number v-model="predictionForm.temperature" :min="0" :max="50" :precision="1"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">℃</span>
              </el-form-item>
              <el-form-item label="湿度">
                <el-input-number v-model="predictionForm.humidity" :min="0" :max="100" :precision="1"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">%</span>
              </el-form-item>
              <el-form-item label="CO₂浓度">
                <el-input-number v-model="predictionForm.co2" :min="0" :max="2000" :precision="0"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">ppm</span>
              </el-form-item>
              <el-form-item label="氨气浓度">
                <el-input-number v-model="predictionForm.nh3" :min="0" :max="100" :precision="1"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">ppm</span>
              </el-form-item>
              <el-form-item label="光照强度">
                <el-input-number v-model="predictionForm.light" :min="0" :max="1000" :precision="0"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">lux</span>
              </el-form-item>
            </div>

            <!-- 疾病风险预测参数 -->
            <div v-if="modelType === 'DISEASE'">
              <h4>输入参数</h4>
              <el-form-item label="温度">
                <el-input-number v-model="predictionForm.temperature" :min="0" :max="50" :precision="1"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">℃</span>
              </el-form-item>
              <el-form-item label="湿度">
                <el-input-number v-model="predictionForm.humidity" :min="0" :max="100" :precision="1"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">%</span>
              </el-form-item>
              <el-form-item label="养殖密度">
                <el-input-number v-model="predictionForm.density" :min="1" :max="50" :precision="0"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">头/m²</span>
              </el-form-item>
              <el-form-item label="日龄">
                <el-input-number v-model="predictionForm.age" :min="1" :max="365"></el-input-number>
                <span style="margin-left: 10px; color: #909399;">天</span>
              </el-form-item>
              <el-form-item label="疫苗接种">
                <el-switch v-model="predictionForm.vaccinated" active-text="已接种" inactive-text="未接种"></el-switch>
              </el-form-item>
            </div>

            <el-form-item>
              <el-button type="primary" @click="handlePredict" :loading="predicting">开始预测</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：预测结果展示 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <span>预测结果</span>
          </template>
          
          <div v-if="!predictionResult" class="empty-result">
            <i class="el-icon-data-analysis" style="font-size: 80px; color: #DCDFE6;"></i>
            <p>请选择模型并输入参数后开始预测</p>
          </div>

          <div v-else class="result-content">
            <!-- 生猪生长预测结果 -->
            <div v-if="modelType === 'GROWTH'">
              <el-result icon="success" title="预测完成">
                <template #subTitle>
                  <p style="font-size: 16px;">
                    预测体重: <strong style="font-size: 24px; color: #409EFF;">{{ predictionResult.predicted_weight }}</strong> kg
                  </p>
                  <p style="font-size: 16px;">
                    生长速度: <strong>{{ predictionResult.growth_rate }}</strong> kg/天
                  </p>
                </template>
                <template #extra>
                  <el-divider></el-divider>
                  <el-descriptions :column="2" border>
                    <el-descriptions-item label="模型精度">{{ (predictionResult.accuracy * 100).toFixed(2) }}%</el-descriptions-item>
                    <el-descriptions-item label="置信度">{{ (predictionResult.confidence * 100).toFixed(2) }}%</el-descriptions-item>
                    <el-descriptions-item label="建议" :span="2">{{ predictionResult.suggestions }}</el-descriptions-item>
                  </el-descriptions>
                </template>
              </el-result>
            </div>

            <!-- 环境质量评价结果 -->
            <div v-if="modelType === 'ENVIRONMENT'">
              <el-result icon="success" title="评价完成">
                <template #subTitle>
                  <p style="font-size: 16px;">
                    环境等级: <el-tag :type="getQualityType(predictionResult.quality_level)" size="large">
                      {{ predictionResult.quality_level }} 类 - {{ predictionResult.quality }}
                    </el-tag>
                  </p>
                  <p style="font-size: 16px;">
                    综合评分: <strong style="font-size: 24px; color: #67C23A;">{{ predictionResult.score }}</strong> 分
                  </p>
                </template>
                <template #extra>
                  <el-divider></el-divider>
                  <el-descriptions :column="2" border>
                    <el-descriptions-item label="温度状态">
                      <el-tag :type="predictionResult.temperature_status === '正常' ? 'success' : 'danger'">
                        {{ predictionResult.temperature_status }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="湿度状态">
                      <el-tag :type="predictionResult.humidity_status === '正常' ? 'success' : 'danger'">
                        {{ predictionResult.humidity_status }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="CO₂状态">
                      <el-tag :type="predictionResult.co2_status === '正常' ? 'success' : 'danger'">
                        {{ predictionResult.co2_status }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="氨气状态">
                      <el-tag :type="predictionResult.nh3_status === '正常' ? 'success' : 'danger'">
                        {{ predictionResult.nh3_status }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="改进建议" :span="2">{{ predictionResult.suggestions }}</el-descriptions-item>
                  </el-descriptions>
                </template>
              </el-result>
            </div>

            <!-- 疾病风险预测结果 -->
            <div v-if="modelType === 'DISEASE'">
              <el-result icon="success" title="预测完成">
                <template #subTitle>
                  <p style="font-size: 16px;">
                    风险等级: <el-tag :type="getRiskType(predictionResult.risk_level)" size="large">
                      {{ predictionResult.risk_level }}
                    </el-tag>
                  </p>
                  <p style="font-size: 16px;">
                    风险概率: <strong style="font-size: 24px; color: #E6A23C;">{{ (predictionResult.risk_probability * 100).toFixed(1) }}%</strong>
                  </p>
                </template>
                <template #extra>
                  <el-divider></el-divider>
                  <el-descriptions :column="1" border>
                    <el-descriptions-item label="风险评分">{{ predictionResult.risk_score }} 分</el-descriptions-item>
                    <el-descriptions-item label="预防建议">{{ predictionResult.prevention_advice }}</el-descriptions-item>
                  </el-descriptions>
                </template>
              </el-result>
            </div>

            <div style="margin-top: 20px; text-align: center;">
              <el-button type="primary" @click="handleExport">导出报告</el-button>
              <el-button @click="handleSave">保存结果</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getAllModels, getModelsByType, predict } from '@/api/prediction'

export default {
  name: 'PredictionAnalysis',
  data() {
    return {
      modelType: 'GROWTH',
      selectedModelId: null,
      allModels: [],
      filteredModels: [],
      predicting: false,
      predictionResult: null,
      predictionForm: {
        // 生猪生长预测
        age: 100,
        feed: 2.5,
        breed: '白猪',
        sex: '公',
        // 环境质量评价
        temperature: 25,
        humidity: 65,
        co2: 500,
        nh3: 20,
        light: 300,
        // 疾病风险预测
        density: 10,
        vaccinated: true
      }
    }
  },
  created() {
    this.loadModels()
  },
  methods: {
    loadModels() {
      getAllModels().then(response => {
        const resp = response.data
        if (resp.flag) {
          this.allModels = resp.data || []
          this.filterModels()
        } else {
          this.$message.warning(resp.message || '获取模型列表失败')
        }
      }).catch(error => {
        console.error('获取模型列表失败:', error)
        this.$message.error('获取模型列表失败：' + (error.response?.data?.message || error.message))
      })
    },
    filterModels() {
      this.filteredModels = this.allModels.filter(m => m.modelType === this.modelType)
      if (this.filteredModels.length > 0) {
        this.selectedModelId = this.filteredModels[0].id
      }
    },
    handleModelTypeChange() {
      this.filterModels()
      this.predictionResult = null
    },
    handleModelChange() {
      this.predictionResult = null
    },
    handlePredict() {
      if (!this.selectedModelId) {
        this.$message.warning('请选择模型')
        return
      }

      this.predicting = true
      predict(this.selectedModelId, this.predictionForm).then(response => {
        const resp = response.data
        if (resp.flag) {
          this.predictionResult = JSON.parse(resp.data.outputData)
          this.$message.success('预测完成')
        } else {
          this.$message.error(resp.message || '预测失败')
        }
      }).catch(error => {
        this.$message.error('预测失败：' + error.message)
      }).finally(() => {
        this.predicting = false
      })
    },
    handleReset() {
      this.predictionForm = {
        age: 100,
        feed: 2.5,
        breed: '白猪',
        sex: '公',
        temperature: 25,
        humidity: 65,
        co2: 500,
        nh3: 20,
        light: 300,
        density: 10,
        vaccinated: true
      }
      this.predictionResult = null
    },
    handleExport() {
      this.$message.info('导出功能开发中...')
    },
    handleSave() {
      this.$message.success('结果已保存')
    },
    getQualityType(level) {
      const map = {
        'I': 'success',
        'II': '',
        'III': 'warning',
        'IV': 'danger'
      }
      return map[level] || 'info'
    },
    getRiskType(level) {
      const map = {
        '低': 'success',
        '中': 'warning',
        '较高': 'warning',
        '高': 'danger'
      }
      return map[level] || 'info'
    }
  }
}
</script>

<style scoped>
.prediction-container {
  padding: 20px;
}
.empty-result {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}
.result-content {
  padding: 20px;
}
</style>



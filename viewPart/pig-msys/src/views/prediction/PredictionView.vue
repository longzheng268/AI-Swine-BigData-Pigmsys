<template>
  <div class="prediction-container">
    <el-card class="prediction-card">
      <template #header>
        <div class="card-header">
          <h2>智能预测</h2>
          <p>AI-Powered Prediction System</p>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="prediction-tabs">
        <!-- 生长预测 -->
        <el-tab-pane label="生长预测" name="growth">
          <el-form :model="growthForm" label-width="120px">
            <el-form-item label="日龄（天）">
              <el-input-number v-model="growthForm.age" :min="1" :max="365" />
            </el-form-item>
            <el-form-item label="饲料摄入量（kg）">
              <el-input-number v-model="growthForm.feed" :min="0" :max="10" :step="0.1" />
            </el-form-item>
            <el-form-item label="品种">
              <el-select v-model="growthForm.breed">
                <el-option label="白猪" value="白猪" />
                <el-option label="黑猪" value="黑猪" />
                <el-option label="杜洛克" value="杜洛克" />
              </el-select>
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="growthForm.sex">
                <el-option label="公" value="公" />
                <el-option label="母" value="母" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="predictGrowth">预测生长</el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div v-if="growthResult" class="result-box">
            <h3>预测结果</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="预测体重">{{ growthResult.predicted_weight }} kg</el-descriptions-item>
              <el-descriptions-item label="生长速度">{{ growthResult.growth_rate }} kg/天</el-descriptions-item>
              <el-descriptions-item label="准确率">{{ (growthResult.accuracy * 100).toFixed(1) }}%</el-descriptions-item>
              <el-descriptions-item label="置信度">{{ (growthResult.confidence * 100).toFixed(1) }}%</el-descriptions-item>
              <el-descriptions-item label="建议" :span="2">{{ growthResult.suggestions }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-tab-pane>

        <!-- 环境评价 -->
        <el-tab-pane label="环境评价" name="environment">
          <el-form :model="environmentForm" label-width="150px">
            <el-form-item label="温度（℃）">
              <el-input-number v-model="environmentForm.temperature" :min="0" :max="50" />
            </el-form-item>
            <el-form-item label="湿度（%）">
              <el-input-number v-model="environmentForm.humidity" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="CO2浓度（ppm）">
              <el-input-number v-model="environmentForm.co2" :min="0" :max="2000" />
            </el-form-item>
            <el-form-item label="氨气浓度（ppm）">
              <el-input-number v-model="environmentForm.nh3" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="光照强度（lux）">
              <el-input-number v-model="environmentForm.light" :min="0" :max="1000" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="predictEnvironment">评价环境</el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div v-if="environmentResult" class="result-box">
            <h3>评价结果</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="环境等级">
                <el-tag :type="getEnvironmentTagType(environmentResult.quality_level)">
                  {{ environmentResult.quality_level }} - {{ environmentResult.quality }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="综合评分">{{ environmentResult.score }} 分</el-descriptions-item>
              <el-descriptions-item label="温度状态">
                <el-tag :type="environmentResult.temperature_status === '正常' ? 'success' : 'danger'">
                  {{ environmentResult.temperature_status }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="湿度状态">
                <el-tag :type="environmentResult.humidity_status === '正常' ? 'success' : 'danger'">
                  {{ environmentResult.humidity_status }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="CO2状态">
                <el-tag :type="environmentResult.co2_status === '正常' ? 'success' : 'danger'">
                  {{ environmentResult.co2_status }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="氨气状态">
                <el-tag :type="environmentResult.nh3_status === '正常' ? 'success' : 'danger'">
                  {{ environmentResult.nh3_status }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="光照状态">
                <el-tag :type="environmentResult.light_status === '正常' ? 'success' : 'danger'">
                  {{ environmentResult.light_status }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="准确率">{{ (environmentResult.accuracy * 100).toFixed(1) }}%</el-descriptions-item>
              <el-descriptions-item label="改进建议" :span="2">{{ environmentResult.suggestions }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-tab-pane>

        <!-- 疾病风险 -->
        <el-tab-pane label="疾病风险预测" name="disease">
          <el-form :model="diseaseForm" label-width="150px">
            <el-form-item label="温度（℃）">
              <el-input-number v-model="diseaseForm.temperature" :min="0" :max="50" />
            </el-form-item>
            <el-form-item label="湿度（%）">
              <el-input-number v-model="diseaseForm.humidity" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="养殖密度（头/m²）">
              <el-input-number v-model="diseaseForm.density" :min="0" :max="50" />
            </el-form-item>
            <el-form-item label="日龄（天）">
              <el-input-number v-model="diseaseForm.age" :min="1" :max="365" />
            </el-form-item>
            <el-form-item label="疫苗接种">
              <el-switch v-model="diseaseForm.vaccinated" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="predictDisease">预测风险</el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div v-if="diseaseResult" class="result-box">
            <h3>预测结果</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="风险等级">
                <el-tag :type="getDiseaseTagType(diseaseResult.risk_level)" size="large">
                  {{ diseaseResult.risk_level }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="风险概率">{{ (diseaseResult.risk_probability * 100).toFixed(1) }}%</el-descriptions-item>
              <el-descriptions-item label="风险评分">{{ diseaseResult.risk_score }} 分</el-descriptions-item>
              <el-descriptions-item label="准确率">{{ (diseaseResult.accuracy * 100).toFixed(1) }}%</el-descriptions-item>
              <el-descriptions-item label="预防建议" :span="2">{{ diseaseResult.prevention_advice }}</el-descriptions-item>
              <el-descriptions-item label="改进措施" :span="2">
                <el-tag v-for="(suggestion, index) in filteredDiseaseSuggestions" :key="index" style="margin-right: 5px;">
                  {{ suggestion }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  predictGrowth as predictGrowthFn, 
  predictEnvironment as predictEnvironmentFn, 
  predictDisease as predictDiseaseFn,
  type GrowthResult,
  type EnvironmentResult,
  type DiseaseResult
} from '@/utils/prediction'

const activeTab = ref('growth')

// 生长预测表单
const growthForm = reactive({
  age: 100,
  feed: 2.5,
  breed: '白猪',
  sex: '公'
})

// 环境评价表单
const environmentForm = reactive({
  temperature: 25,
  humidity: 65,
  co2: 500,
  nh3: 20,
  light: 300
})

// 疾病风险表单
const diseaseForm = reactive({
  temperature: 25,
  humidity: 65,
  density: 10,
  age: 60,
  vaccinated: true
})

// 预测结果
const growthResult = ref<GrowthResult | null>(null)
const environmentResult = ref<EnvironmentResult | null>(null)
const diseaseResult = ref<DiseaseResult | null>(null)

// 过滤掉空字符串的建议
const filteredDiseaseSuggestions = computed(() => {
  return diseaseResult.value?.suggestions.filter((s: string) => s) || []
})

// 生长预测
const predictGrowth = () => {
  try {
    growthResult.value = predictGrowthFn(growthForm)
    ElMessage.success('生长预测完成')
  } catch (error: any) {
    ElMessage.error('预测失败: ' + error.message)
  }
}

// 环境评价
const predictEnvironment = () => {
  try {
    environmentResult.value = predictEnvironmentFn(environmentForm)
    ElMessage.success('环境评价完成')
  } catch (error: any) {
    ElMessage.error('评价失败: ' + error.message)
  }
}

// 疾病风险预测
const predictDisease = () => {
  try {
    diseaseResult.value = predictDiseaseFn(diseaseForm)
    ElMessage.success('风险预测完成')
  } catch (error: any) {
    ElMessage.error('预测失败: ' + error.message)
  }
}

// 获取环境等级标签类型
const getEnvironmentTagType = (level: string) => {
  const types: Record<string, string> = {
    'I': 'success',
    'II': 'info',
    'III': 'warning',
    'IV': 'danger'
  }
  return types[level] || 'info'
}

// 获取疾病风险标签类型
const getDiseaseTagType = (level: string) => {
  const types: Record<string, string> = {
    '低': 'success',
    '中': 'warning',
    '较高': 'danger',
    '高': 'danger'
  }
  return types[level] || 'info'
}
</script>

<style scoped>
.prediction-container {
  padding: 20px;
}

.prediction-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: #333;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.prediction-tabs {
  margin-top: 20px;
}

.result-box {
  margin-top: 20px;
}

.result-box h3 {
  margin-bottom: 15px;
  color: #333;
}
</style>

/**
 * 生猪智慧养殖预测算法
 * 将 Python Flask 预测服务的算法逻辑迁移到前端 JavaScript
 */

// 类型定义
export interface GrowthInput {
  age?: number
  feed?: number
  breed?: string
  sex?: string
}

export interface GrowthResult {
  predicted_weight: number
  growth_rate: number
  accuracy: number
  confidence: number
  suggestions: string
}

export interface EnvironmentInput {
  temperature?: number
  humidity?: number
  co2?: number
  nh3?: number
  light?: number
}

export interface EnvironmentResult {
  quality_level: string
  quality: string
  score: number
  temperature_status: string
  humidity_status: string
  co2_status: string
  nh3_status: string
  light_status: string
  suggestions: string
  accuracy: number
}

export interface DiseaseInput {
  temperature?: number
  humidity?: number
  density?: number
  age?: number
  vaccinated?: boolean
}

export interface DiseaseResult {
  risk_level: string
  risk_probability: number
  risk_score: number
  prevention_advice: string
  suggestions: string[]
  accuracy: number
}

export interface PredictionResponse<T> {
  code: number
  message: string
  data: T | null
}

/**
 * 生猪生长预测
 * @param inputData - 输入数据：{ age, feed, breed, sex }
 * @returns 预测结果
 */
export function predictGrowth(inputData: GrowthInput): GrowthResult {
  const age = Number(inputData.age || 100)
  const feed = Number(inputData.feed || 2.5)
  const breed = inputData.breed || '白猪'
  const sex = inputData.sex || '公'

  // 简单的预测模型
  const baseWeight = 30 + age * 0.5 + feed * 10
  const breedFactor = breed === '白猪' ? 1.1 : 1.0
  const sexFactor = sex === '公' ? 1.05 : 1.0

  const predictedWeight = baseWeight * breedFactor * sexFactor
  const growthRate = age > 0 ? predictedWeight / age : 0

  return {
    predicted_weight: Math.round(predictedWeight * 100) / 100,
    growth_rate: Math.round(growthRate * 1000) / 1000,
    accuracy: 0.925,
    confidence: 0.85 + Math.random() * 0.1,
    suggestions: '生长状况良好，建议继续保持当前饲养方式'
  }
}

/**
 * 环境质量评价
 * @param inputData - 输入数据：{ temperature, humidity, co2, nh3, light }
 * @returns 评价结果
 */
export function predictEnvironment(inputData: EnvironmentInput): EnvironmentResult {
  const temperature = Number(inputData.temperature || 25)
  const humidity = Number(inputData.humidity || 65)
  const co2 = Number(inputData.co2 || 500)
  const nh3 = Number(inputData.nh3 || 20)
  const light = Number(inputData.light || 300)

  // 评分计算
  let score = 100
  const issues: string[] = []

  if (temperature < 18 || temperature > 28) {
    score -= 15
    issues.push('温度超标')
  }
  if (humidity < 50 || humidity > 70) {
    score -= 10
    issues.push('湿度超标')
  }
  if (co2 > 600) {
    score -= 15
    issues.push('CO2浓度过高')
  }
  if (nh3 > 25) {
    score -= 20
    issues.push('氨气浓度过高')
  }
  if (light < 200 || light > 500) {
    score -= 10
    issues.push('光照不适宜')
  }

  // 确定等级
  let level: string
  let quality: string

  if (score >= 90) {
    level = 'I'
    quality = '优秀'
  } else if (score >= 75) {
    level = 'II'
    quality = '良好'
  } else if (score >= 60) {
    level = 'III'
    quality = '一般'
  } else {
    level = 'IV'
    quality = '较差'
  }

  const suggestions = issues.length > 0 ? issues.join('、') : '环境良好，建议保持'

  return {
    quality_level: level,
    quality: quality,
    score: score,
    temperature_status: temperature >= 18 && temperature <= 28 ? '正常' : '异常',
    humidity_status: humidity >= 50 && humidity <= 70 ? '正常' : '异常',
    co2_status: co2 <= 600 ? '正常' : '超标',
    nh3_status: nh3 <= 25 ? '正常' : '超标',
    light_status: light >= 200 && light <= 500 ? '正常' : '异常',
    suggestions: suggestions,
    accuracy: 0.88
  }
}

/**
 * 疾病风险预测
 * @param inputData - 输入数据：{ temperature, humidity, density, age, vaccinated }
 * @returns 风险预测结果
 */
export function predictDisease(inputData: DiseaseInput): DiseaseResult {
  const temperature = Number(inputData.temperature || 25)
  const humidity = Number(inputData.humidity || 65)
  const density = Number(inputData.density || 10)
  const age = Number(inputData.age || 60)
  const vaccinated = inputData.vaccinated !== undefined ? inputData.vaccinated : true

  // 风险计算
  let riskScore = 0

  if (temperature < 18 || temperature > 28) {
    riskScore += 20
  }
  if (humidity > 70) {
    riskScore += 15
  }
  if (density > 15) {
    riskScore += 25
  }
  if (age < 30) {
    riskScore += 10
  }
  if (!vaccinated) {
    riskScore += 30
  }

  // 确定风险等级
  let level: string
  let probability: number
  let advice: string

  if (riskScore <= 20) {
    level = '低'
    probability = 0.1 + Math.random() * 0.1
    advice = '风险较低，注意日常卫生和防疫'
  } else if (riskScore <= 40) {
    level = '中'
    probability = 0.3 + Math.random() * 0.2
    advice = '存在一定风险，建议加强通风和消毒'
  } else if (riskScore <= 60) {
    level = '较高'
    probability = 0.5 + Math.random() * 0.2
    advice = '风险较高，建议立即改善环境并加强监控'
  } else {
    level = '高'
    probability = 0.7 + Math.random() * 0.2
    advice = '风险很高，建议立即采取防疫措施并咨询兽医'
  }

  const suggestions: string[] = []
  if (density > 10) suggestions.push('定期消毒')
  if (humidity > 70) suggestions.push('改善通风')
  if (temperature < 18 || temperature > 28) suggestions.push('调节温度')
  if (!vaccinated) suggestions.push('及时疫苗接种')

  return {
    risk_level: level,
    risk_probability: Math.round(probability * 1000) / 1000,
    risk_score: riskScore,
    prevention_advice: advice,
    suggestions: suggestions,
    accuracy: 0.865
  }
}

/**
 * 统一的预测接口
 * @param modelType - 模型类型：GROWTH / ENVIRONMENT / DISEASE
 * @param inputData - 输入数据
 * @returns 预测结果
 */
export function predict(
  modelType: 'GROWTH',
  inputData: GrowthInput
): PredictionResponse<GrowthResult>
export function predict(
  modelType: 'ENVIRONMENT',
  inputData: EnvironmentInput
): PredictionResponse<EnvironmentResult>
export function predict(
  modelType: 'DISEASE',
  inputData: DiseaseInput
): PredictionResponse<DiseaseResult>
export function predict(
  modelType: string,
  inputData: GrowthInput | EnvironmentInput | DiseaseInput
): PredictionResponse<GrowthResult | EnvironmentResult | DiseaseResult> {
  try {
    let result: GrowthResult | EnvironmentResult | DiseaseResult

    switch (modelType) {
      case 'GROWTH':
        result = predictGrowth(inputData as GrowthInput)
        break
      case 'ENVIRONMENT':
        result = predictEnvironment(inputData as EnvironmentInput)
        break
      case 'DISEASE':
        result = predictDisease(inputData as DiseaseInput)
        break
      default:
        throw new Error('不支持的模型类型')
    }

    return {
      code: 200,
      message: '预测成功',
      data: result
    }
  } catch (error: any) {
    return {
      code: 500,
      message: `预测失败: ${error.message}`,
      data: null
    }
  }
}

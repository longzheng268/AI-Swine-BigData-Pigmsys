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
          <!-- 模式切换 -->
          <el-radio-group v-model="growthMode" class="mode-selector" size="large">
            <el-radio-button label="single">单组预测</el-radio-button>
            <el-radio-button label="comparison">对比沙盒</el-radio-button>
          </el-radio-group>

          <!-- 单组预测模式 -->
          <div v-if="growthMode === 'single'">
            <el-form :model="growthForm" label-width="150px" label-position="left">
              <el-form-item label="日龄（天）">
                <el-input-number 
                  v-model="growthForm.age" 
                  :min="1" 
                  :max="365"
                  placeholder="请输入日龄"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="饲料摄入量（kg）">
                <el-input-number 
                  v-model="growthForm.feed" 
                  :min="0" 
                  :max="10" 
                  :step="0.1"
                  placeholder="请输入饲料摄入量"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="品种">
                <el-select v-model="growthForm.breed" placeholder="请选择品种" style="width: 100%;">
                  <el-option label="白猪" value="白猪" />
                  <el-option label="黑猪" value="黑猪" />
                  <el-option label="杜洛克" value="杜洛克" />
                </el-select>
              </el-form-item>
              <el-form-item label="性别">
                <el-select v-model="growthForm.sex" placeholder="请选择性别" style="width: 100%;">
                  <el-option label="公" value="公" />
                  <el-option label="母" value="母" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="predictGrowth" size="large">
                  <el-icon style="margin-right: 5px;"><TrendCharts /></el-icon>
                  开始预测
                </el-button>
              </el-form-item>
            </el-form>

            <el-divider />

            <div v-if="growthResult" class="result-box">
              <h3>预测结果</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="预测体重">
                  <el-tag type="primary" size="large">{{ growthResult.predicted_weight }} kg</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="生长速度">
                  <el-tag type="success" size="large">{{ growthResult.growth_rate }} kg/天</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="准确率">{{ (growthResult.accuracy * 100).toFixed(1) }}%</el-descriptions-item>
                <el-descriptions-item label="置信度">{{ (growthResult.confidence * 100).toFixed(1) }}%</el-descriptions-item>
                <el-descriptions-item label="建议" :span="2">{{ growthResult.suggestions }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>

          <!-- 对比沙盒模式 -->
          <div v-else>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-card class="comparison-card group-a-card">
                  <template #header>
                    <div class="comparison-header">
                      <span>方案 A 组</span>
                    </div>
                  </template>
                  <el-form :model="growthFormA" label-width="120px" label-position="left" size="small">
                    <el-form-item label="日龄（天）">
                      <el-input-number 
                        v-model="growthFormA.age" 
                        :min="1" 
                        :max="365"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="饲料（kg）">
                      <el-input-number 
                        v-model="growthFormA.feed" 
                        :min="0" 
                        :max="10" 
                        :step="0.1"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="品种">
                      <el-select v-model="growthFormA.breed" style="width: 100%;">
                        <el-option label="白猪" value="白猪" />
                        <el-option label="黑猪" value="黑猪" />
                        <el-option label="杜洛克" value="杜洛克" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="性别">
                      <el-select v-model="growthFormA.sex" style="width: 100%;">
                        <el-option label="公" value="公" />
                        <el-option label="母" value="母" />
                      </el-select>
                    </el-form-item>
                  </el-form>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-card class="comparison-card group-b-card">
                  <template #header>
                    <div class="comparison-header">
                      <span>方案 B 组</span>
                    </div>
                  </template>
                  <el-form :model="growthFormB" label-width="120px" label-position="left" size="small">
                    <el-form-item label="日龄（天）">
                      <el-input-number 
                        v-model="growthFormB.age" 
                        :min="1" 
                        :max="365"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="饲料（kg）">
                      <el-input-number 
                        v-model="growthFormB.feed" 
                        :min="0" 
                        :max="10" 
                        :step="0.1"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="品种">
                      <el-select v-model="growthFormB.breed" style="width: 100%;">
                        <el-option label="白猪" value="白猪" />
                        <el-option label="黑猪" value="黑猪" />
                        <el-option label="杜洛克" value="杜洛克" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="性别">
                      <el-select v-model="growthFormB.sex" style="width: 100%;">
                        <el-option label="公" value="公" />
                        <el-option label="母" value="母" />
                      </el-select>
                    </el-form-item>
                  </el-form>
                </el-card>
              </el-col>
            </el-row>

            <div style="text-align: center; margin-top: 20px;">
              <el-button type="primary" @click="compareGrowth" size="large">
                <el-icon style="margin-right: 5px;"><TrendCharts /></el-icon>
                开始对比分析
              </el-button>
            </div>

            <el-divider />

            <div v-if="comparisonResult" class="result-box">
              <h3>对比结果</h3>
              
              <!-- ECharts 图表 -->
              <div ref="comparisonChart" class="comparison-chart"></div>

              <!-- 专家结论 -->
              <el-card class="expert-conclusion">
                <template #header>
                  <div class="conclusion-header">
                    <el-icon><Document /></el-icon>
                    <span style="margin-left: 8px;">专家结论</span>
                  </div>
                </template>
                <el-alert 
                  :title="comparisonResult.conclusion" 
                  :type="comparisonResult.betterGroup === 'A' ? 'success' : 'warning'"
                  :description="comparisonResult.analysis"
                  show-icon
                  :closable="false"
                />
                <div class="metrics-comparison">
                  <el-row :gutter="20" style="margin-top: 15px;">
                    <el-col :span="12">
                      <el-statistic title="A组 - 饲料转化率" :value="comparisonResult.groupA.conversionRate">
                        <template #suffix>kg饲料/kg体重</template>
                      </el-statistic>
                    </el-col>
                    <el-col :span="12">
                      <el-statistic title="B组 - 饲料转化率" :value="comparisonResult.groupB.conversionRate">
                        <template #suffix>kg饲料/kg体重</template>
                      </el-statistic>
                    </el-col>
                  </el-row>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <!-- 环境评价 -->
        <el-tab-pane label="环境评价" name="environment">
          <!-- 模式切换 -->
          <el-radio-group v-model="environmentMode" class="mode-selector" size="large">
            <el-radio-button label="single">单组评价</el-radio-button>
            <el-radio-button label="comparison">对比沙盒</el-radio-button>
          </el-radio-group>

          <!-- 单组评价模式 -->
          <div v-if="environmentMode === 'single'">
            <el-form :model="environmentForm" label-width="150px" label-position="left">
              <el-form-item label="温度（℃）">
                <el-input-number 
                  v-model="environmentForm.temperature" 
                  :min="0" 
                  :max="50"
                  placeholder="请输入温度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="湿度（%）">
                <el-input-number 
                  v-model="environmentForm.humidity" 
                  :min="0" 
                  :max="100"
                  placeholder="请输入湿度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="CO2浓度（ppm）">
                <el-input-number 
                  v-model="environmentForm.co2" 
                  :min="0" 
                  :max="2000"
                  placeholder="请输入CO2浓度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="氨气浓度（ppm）">
                <el-input-number 
                  v-model="environmentForm.nh3" 
                  :min="0" 
                  :max="100"
                  placeholder="请输入氨气浓度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="光照强度（lux）">
                <el-input-number 
                  v-model="environmentForm.light" 
                  :min="0" 
                  :max="1000"
                  placeholder="请输入光照强度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="success" @click="predictEnvironment" size="large">
                  <el-icon style="margin-right: 5px;"><Histogram /></el-icon>
                  开始评价
                </el-button>
              </el-form-item>
            </el-form>

            <el-divider />

            <div v-if="environmentResult" class="result-box">
              <h3>评价结果</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="环境等级">
                  <el-tag :type="getEnvironmentTagType(environmentResult.quality_level)" size="large">
                    {{ environmentResult.quality_level }} - {{ environmentResult.quality }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="综合评分">
                  <el-tag type="warning" size="large">{{ environmentResult.score }} 分</el-tag>
                </el-descriptions-item>
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
          </div>

          <!-- 对比沙盒模式 -->
          <div v-else>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-card class="comparison-card group-a-card">
                  <template #header>
                    <div class="comparison-header">
                      <span>方案 A 组</span>
                    </div>
                  </template>
                  <el-form :model="environmentFormA" label-width="120px" label-position="left" size="small">
                    <el-form-item label="温度（℃）">
                      <el-input-number 
                        v-model="environmentFormA.temperature" 
                        :min="0" 
                        :max="50"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="湿度（%）">
                      <el-input-number 
                        v-model="environmentFormA.humidity" 
                        :min="0" 
                        :max="100"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="CO2（ppm）">
                      <el-input-number 
                        v-model="environmentFormA.co2" 
                        :min="0" 
                        :max="2000"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="氨气（ppm）">
                      <el-input-number 
                        v-model="environmentFormA.nh3" 
                        :min="0" 
                        :max="100"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="光照（lux）">
                      <el-input-number 
                        v-model="environmentFormA.light" 
                        :min="0" 
                        :max="1000"
                        style="width: 100%;"
                      />
                    </el-form-item>
                  </el-form>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-card class="comparison-card group-b-card">
                  <template #header>
                    <div class="comparison-header">
                      <span>方案 B 组</span>
                    </div>
                  </template>
                  <el-form :model="environmentFormB" label-width="120px" label-position="left" size="small">
                    <el-form-item label="温度（℃）">
                      <el-input-number 
                        v-model="environmentFormB.temperature" 
                        :min="0" 
                        :max="50"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="湿度（%）">
                      <el-input-number 
                        v-model="environmentFormB.humidity" 
                        :min="0" 
                        :max="100"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="CO2（ppm）">
                      <el-input-number 
                        v-model="environmentFormB.co2" 
                        :min="0" 
                        :max="2000"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="氨气（ppm）">
                      <el-input-number 
                        v-model="environmentFormB.nh3" 
                        :min="0" 
                        :max="100"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="光照（lux）">
                      <el-input-number 
                        v-model="environmentFormB.light" 
                        :min="0" 
                        :max="1000"
                        style="width: 100%;"
                      />
                    </el-form-item>
                  </el-form>
                </el-card>
              </el-col>
            </el-row>

            <div style="text-align: center; margin-top: 20px;">
              <el-button type="success" @click="compareEnvironment" size="large">
                <el-icon style="margin-right: 5px;"><Histogram /></el-icon>
                开始对比分析
              </el-button>
            </div>

            <el-divider />

            <div v-if="environmentComparisonResult" class="result-box">
              <h3>对比结果</h3>
              
              <!-- ECharts 图表 -->
              <div ref="environmentComparisonChart" class="comparison-chart"></div>

              <!-- 专家结论 -->
              <el-card class="expert-conclusion">
                <template #header>
                  <div class="conclusion-header">
                    <el-icon><Document /></el-icon>
                    <span style="margin-left: 8px;">专家结论</span>
                  </div>
                </template>
                <el-alert 
                  :title="environmentComparisonResult.conclusion" 
                  :type="environmentComparisonResult.betterGroup === 'A' ? 'success' : 'warning'"
                  :description="environmentComparisonResult.analysis"
                  show-icon
                  :closable="false"
                />
                <div class="metrics-comparison">
                  <el-row :gutter="20" style="margin-top: 15px;">
                    <el-col :span="12">
                      <el-statistic title="A组 - 环境评分" :value="environmentComparisonResult.groupA.score">
                        <template #suffix>分 ({{ environmentComparisonResult.groupA.quality }})</template>
                      </el-statistic>
                    </el-col>
                    <el-col :span="12">
                      <el-statistic title="B组 - 环境评分" :value="environmentComparisonResult.groupB.score">
                        <template #suffix>分 ({{ environmentComparisonResult.groupB.quality }})</template>
                      </el-statistic>
                    </el-col>
                  </el-row>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <!-- 疾病风险 -->
        <el-tab-pane label="疾病风险预测" name="disease">
          <!-- 模式切换 -->
          <el-radio-group v-model="diseaseMode" class="mode-selector" size="large">
            <el-radio-button label="single">单组预测</el-radio-button>
            <el-radio-button label="comparison">对比沙盒</el-radio-button>
          </el-radio-group>

          <!-- 单组预测模式 -->
          <div v-if="diseaseMode === 'single'">
            <el-form :model="diseaseForm" label-width="150px" label-position="left">
              <el-form-item label="温度（℃）">
                <el-input-number 
                  v-model="diseaseForm.temperature" 
                  :min="0" 
                  :max="50"
                  placeholder="请输入温度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="湿度（%）">
                <el-input-number 
                  v-model="diseaseForm.humidity" 
                  :min="0" 
                  :max="100"
                  placeholder="请输入湿度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="养殖密度（头/m²）">
                <el-input-number 
                  v-model="diseaseForm.density" 
                  :min="0" 
                  :max="50"
                  placeholder="请输入养殖密度"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="日龄（天）">
                <el-input-number 
                  v-model="diseaseForm.age" 
                  :min="1" 
                  :max="365"
                  placeholder="请输入日龄"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="疫苗接种">
                <el-switch 
                  v-model="diseaseForm.vaccinated"
                  active-text="已接种"
                  inactive-text="未接种"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="danger" @click="predictDisease" size="large">
                  <el-icon style="margin-right: 5px;"><Warning /></el-icon>
                  开始预测
                </el-button>
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
                <el-descriptions-item label="风险概率">
                  <el-tag type="warning" size="large">{{ (diseaseResult.risk_probability * 100).toFixed(1) }}%</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="风险评分">{{ diseaseResult.risk_score }} 分</el-descriptions-item>
                <el-descriptions-item label="准确率">{{ (diseaseResult.accuracy * 100).toFixed(1) }}%</el-descriptions-item>
                <el-descriptions-item label="预防建议" :span="2">{{ diseaseResult.prevention_advice }}</el-descriptions-item>
                <el-descriptions-item label="改进措施" :span="2">
                  <el-tag v-for="(suggestion, index) in filteredDiseaseSuggestions" :key="index" style="margin-right: 5px; margin-bottom: 5px;">
                    {{ suggestion }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>

          <!-- 对比沙盒模式 -->
          <div v-else>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-card class="comparison-card group-a-card">
                  <template #header>
                    <div class="comparison-header">
                      <span>方案 A 组</span>
                    </div>
                  </template>
                  <el-form :model="diseaseFormA" label-width="120px" label-position="left" size="small">
                    <el-form-item label="温度（℃）">
                      <el-input-number 
                        v-model="diseaseFormA.temperature" 
                        :min="0" 
                        :max="50"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="湿度（%）">
                      <el-input-number 
                        v-model="diseaseFormA.humidity" 
                        :min="0" 
                        :max="100"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="密度（头/m²）">
                      <el-input-number 
                        v-model="diseaseFormA.density" 
                        :min="0" 
                        :max="50"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="日龄（天）">
                      <el-input-number 
                        v-model="diseaseFormA.age" 
                        :min="1" 
                        :max="365"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="疫苗接种">
                      <el-switch 
                        v-model="diseaseFormA.vaccinated"
                        active-text="已接种"
                        inactive-text="未接种"
                      />
                    </el-form-item>
                  </el-form>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-card class="comparison-card group-b-card">
                  <template #header>
                    <div class="comparison-header">
                      <span>方案 B 组</span>
                    </div>
                  </template>
                  <el-form :model="diseaseFormB" label-width="120px" label-position="left" size="small">
                    <el-form-item label="温度（℃）">
                      <el-input-number 
                        v-model="diseaseFormB.temperature" 
                        :min="0" 
                        :max="50"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="湿度（%）">
                      <el-input-number 
                        v-model="diseaseFormB.humidity" 
                        :min="0" 
                        :max="100"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="密度（头/m²）">
                      <el-input-number 
                        v-model="diseaseFormB.density" 
                        :min="0" 
                        :max="50"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="日龄（天）">
                      <el-input-number 
                        v-model="diseaseFormB.age" 
                        :min="1" 
                        :max="365"
                        style="width: 100%;"
                      />
                    </el-form-item>
                    <el-form-item label="疫苗接种">
                      <el-switch 
                        v-model="diseaseFormB.vaccinated"
                        active-text="已接种"
                        inactive-text="未接种"
                      />
                    </el-form-item>
                  </el-form>
                </el-card>
              </el-col>
            </el-row>

            <div style="text-align: center; margin-top: 20px;">
              <el-button type="danger" @click="compareDisease" size="large">
                <el-icon style="margin-right: 5px;"><Warning /></el-icon>
                开始对比分析
              </el-button>
            </div>

            <el-divider />

            <div v-if="diseaseComparisonResult" class="result-box">
              <h3>对比结果</h3>
              
              <!-- ECharts 图表 -->
              <div ref="diseaseComparisonChart" class="comparison-chart"></div>

              <!-- 专家结论 -->
              <el-card class="expert-conclusion">
                <template #header>
                  <div class="conclusion-header">
                    <el-icon><Document /></el-icon>
                    <span style="margin-left: 8px;">专家结论</span>
                  </div>
                </template>
                <el-alert 
                  :title="diseaseComparisonResult.conclusion" 
                  :type="diseaseComparisonResult.betterGroup === 'A' ? 'success' : 'warning'"
                  :description="diseaseComparisonResult.analysis"
                  show-icon
                  :closable="false"
                />
                <div class="metrics-comparison">
                  <el-row :gutter="20" style="margin-top: 15px;">
                    <el-col :span="12">
                      <el-statistic title="A组 - 风险评分" :value="diseaseComparisonResult.groupA.risk_score">
                        <template #suffix>分 ({{ diseaseComparisonResult.groupA.risk_level }})</template>
                      </el-statistic>
                    </el-col>
                    <el-col :span="12">
                      <el-statistic title="B组 - 风险评分" :value="diseaseComparisonResult.groupB.risk_score">
                        <template #suffix>分 ({{ diseaseComparisonResult.groupB.risk_level }})</template>
                      </el-statistic>
                    </el-col>
                  </el-row>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { TrendCharts, Histogram, Warning, Document } from '@element-plus/icons-vue'
import { 
  predictGrowth as predictGrowthFn, 
  predictEnvironment as predictEnvironmentFn, 
  predictDisease as predictDiseaseFn,
  type GrowthResult,
  type EnvironmentResult,
  type DiseaseResult
} from '@/utils/prediction'
import * as echarts from 'echarts'

const activeTab = ref('growth')

// 生长预测模式：single 单组预测，comparison 对比沙盒
const growthMode = ref('single')

// 环境评价模式
const environmentMode = ref('single')

// 疾病风险预测模式
const diseaseMode = ref('single')

// 生长预测表单
const growthForm = reactive({
  age: 100,
  feed: 2.5,
  breed: '白猪',
  sex: '公'
})

// 对比沙盒表单 - A组
const growthFormA = reactive({
  age: 100,
  feed: 2.5,
  breed: '白猪',
  sex: '公'
})

// 对比沙盒表单 - B组
const growthFormB = reactive({
  age: 120,
  feed: 3.0,
  breed: '杜洛克',
  sex: '公'
})

// 环境评价对比表单 - A组
const environmentFormA = reactive({
  temperature: 25,
  humidity: 65,
  co2: 500,
  nh3: 20,
  light: 300
})

// 环境评价对比表单 - B组
const environmentFormB = reactive({
  temperature: 22,
  humidity: 60,
  co2: 450,
  nh3: 15,
  light: 350
})

// 疾病风险对比表单 - A组
const diseaseFormA = reactive({
  temperature: 25,
  humidity: 65,
  density: 10,
  age: 60,
  vaccinated: true
})

// 疾病风险对比表单 - B组
const diseaseFormB = reactive({
  temperature: 23,
  humidity: 62,
  density: 8,
  age: 60,
  vaccinated: true
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

// 对比结果
interface ComparisonResult {
  groupA: {
    predicted_weight: number
    growth_rate: number
    conversionRate: number
  }
  groupB: {
    predicted_weight: number
    growth_rate: number
    conversionRate: number
  }
  betterGroup: 'A' | 'B'
  conclusion: string
  analysis: string
}
const comparisonResult = ref<ComparisonResult | null>(null)
const comparisonChart = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null
let resizeHandler: (() => void) | null = null

// 环境对比结果
interface EnvironmentComparisonResult {
  groupA: {
    score: number
    quality_level: string
    quality: string
  }
  groupB: {
    score: number
    quality_level: string
    quality: string
  }
  betterGroup: 'A' | 'B'
  conclusion: string
  analysis: string
}
const environmentComparisonResult = ref<EnvironmentComparisonResult | null>(null)
const environmentComparisonChart = ref<HTMLElement | null>(null)
let environmentChartInstance: echarts.ECharts | null = null

// 疾病风险对比结果
interface DiseaseComparisonResult {
  groupA: {
    risk_score: number
    risk_probability: number
    risk_level: string
  }
  groupB: {
    risk_score: number
    risk_probability: number
    risk_level: string
  }
  betterGroup: 'A' | 'B'
  conclusion: string
  analysis: string
}
const diseaseComparisonResult = ref<DiseaseComparisonResult | null>(null)
const diseaseComparisonChart = ref<HTMLElement | null>(null)
let diseaseChartInstance: echarts.ECharts | null = null
let environmentResizeHandler: (() => void) | null = null
let diseaseResizeHandler: (() => void) | null = null


// 过滤掉空字符串的建议
const filteredDiseaseSuggestions = computed(() => {
  return diseaseResult.value?.suggestions.filter((s: string) => s) || []
})

// 对比分析
const compareGrowth = async () => {
  try {
    // 计算A组预测
    const resultA = predictGrowthFn(growthFormA)
    // 计算B组预测
    const resultB = predictGrowthFn(growthFormB)

    // 计算饲料转化率 (饲料量 / 预测体重) - 越低越好
    const conversionRateA = resultA.predicted_weight > 0 ? growthFormA.feed / resultA.predicted_weight : 0
    const conversionRateB = resultB.predicted_weight > 0 ? growthFormB.feed / resultB.predicted_weight : 0

    // 判断哪组更优 (FCR越低越好)
    const betterGroup = conversionRateA < conversionRateB ? 'A' : 'B'
    const difference = Math.abs(conversionRateA - conversionRateB)
    const improvementPercent = ((difference / Math.max(conversionRateA, conversionRateB)) * 100).toFixed(1)

    comparisonResult.value = {
      groupA: {
        predicted_weight: resultA.predicted_weight,
        growth_rate: resultA.growth_rate,
        conversionRate: Math.round(conversionRateA * 1000) / 1000
      },
      groupB: {
        predicted_weight: resultB.predicted_weight,
        growth_rate: resultB.growth_rate,
        conversionRate: Math.round(conversionRateB * 1000) / 1000
      },
      betterGroup,
      conclusion: `方案${betterGroup}组的饲料转化率更优，效率更高`,
      analysis: `方案${betterGroup}组每公斤体重增长仅需${betterGroup === 'A' ? conversionRateA.toFixed(3) : conversionRateB.toFixed(3)}kg饲料，比另一组节省${improvementPercent}%。采用方案${betterGroup}组的饲养策略可以有效降低饲料成本，提高养殖效益。`
    }

    // 等待DOM更新后渲染图表
    await nextTick()
    renderComparisonChart()
    
    ElMessage.success('对比分析完成')
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    ElMessage.error(`对比分析失败: ${errorMsg}`)
  }
}

// 渲染对比图表
const renderComparisonChart = () => {
  if (!comparisonChart.value || !comparisonResult.value) return

  // 如果已有实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(comparisonChart.value)
  
  const option = {
    title: {
      text: '生长数据对比分析',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 16,
        fontWeight: 600
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['方案A组', '方案B组'],
      top: 40,
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: 80,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['预测体重 (kg)', '生长速度 (kg/天)', '饲料转化率 (kg/kg)'],
      axisLabel: {
        interval: 0,
        rotate: 0,
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      name: '数值',
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '方案A组',
        type: 'bar',
        data: [
          comparisonResult.value.groupA.predicted_weight,
          comparisonResult.value.groupA.growth_rate,
          comparisonResult.value.groupA.conversionRate
        ],
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      },
      {
        name: '方案B组',
        type: 'bar',
        data: [
          comparisonResult.value.groupB.predicted_weight,
          comparisonResult.value.groupB.growth_rate,
          comparisonResult.value.groupB.conversionRate
        ],
        itemStyle: {
          color: '#67C23A'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      }
    ]
  }

  chartInstance.setOption(option)

  // 响应式调整 - 先移除旧的监听器
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  resizeHandler = () => {
    chartInstance?.resize()
  }
  window.addEventListener('resize', resizeHandler)
}

// 清理资源
onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
    resizeHandler = null
  }
  if (environmentResizeHandler) {
    window.removeEventListener('resize', environmentResizeHandler)
    environmentResizeHandler = null
  }
  if (diseaseResizeHandler) {
    window.removeEventListener('resize', diseaseResizeHandler)
    diseaseResizeHandler = null
  }
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  if (environmentChartInstance) {
    environmentChartInstance.dispose()
    environmentChartInstance = null
  }
  if (diseaseChartInstance) {
    diseaseChartInstance.dispose()
    diseaseChartInstance = null
  }
})

// 生长预测
const predictGrowth = () => {
  try {
    growthResult.value = predictGrowthFn(growthForm)
    ElMessage.success('生长预测完成')
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    ElMessage.error(`预测失败: ${errorMsg}`)
  }
}

// 环境评价
const predictEnvironment = () => {
  try {
    environmentResult.value = predictEnvironmentFn(environmentForm)
    ElMessage.success('环境评价完成')
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    ElMessage.error(`评价失败: ${errorMsg}`)
  }
}

// 环境对比分析
const compareEnvironment = async () => {
  try {
    // 计算A组预测
    const resultA = predictEnvironmentFn(environmentFormA)
    // 计算B组预测
    const resultB = predictEnvironmentFn(environmentFormB)

    // 判断哪组更优 (分数越高越好)
    const betterGroup = resultA.score > resultB.score ? 'A' : 'B'
    const difference = Math.abs(resultA.score - resultB.score)
    const improvementPercent = ((difference / Math.min(resultA.score, resultB.score)) * 100).toFixed(1)

    environmentComparisonResult.value = {
      groupA: {
        score: resultA.score,
        quality_level: resultA.quality_level,
        quality: resultA.quality
      },
      groupB: {
        score: resultB.score,
        quality_level: resultB.quality_level,
        quality: resultB.quality
      },
      betterGroup,
      conclusion: `方案${betterGroup}组的环境质量更优，评级为${betterGroup === 'A' ? resultA.quality : resultB.quality}`,
      analysis: `方案${betterGroup}组的环境评分为${betterGroup === 'A' ? resultA.score : resultB.score}分，比另一组高${improvementPercent}%。该方案的环境条件更适合生猪生长，有助于提升养殖效益和动物福利水平。建议优先采用方案${betterGroup}组的环境配置。`
    }

    // 等待DOM更新后渲染图表
    await nextTick()
    renderEnvironmentComparisonChart()
    
    ElMessage.success('环境对比分析完成')
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    ElMessage.error(`对比分析失败: ${errorMsg}`)
  }
}

// 渲染环境对比图表
const renderEnvironmentComparisonChart = () => {
  if (!environmentComparisonChart.value || !environmentComparisonResult.value) return

  // 如果已有实例，先销毁
  if (environmentChartInstance) {
    environmentChartInstance.dispose()
  }

  environmentChartInstance = echarts.init(environmentComparisonChart.value)
  
  const option = {
    title: {
      text: '环境质量对比分析',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 16,
        fontWeight: 600
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['方案A组', '方案B组'],
      top: 40,
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: 80,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['环境评分'],
      axisLabel: {
        interval: 0,
        rotate: 0,
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      name: '分数',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '方案A组',
        type: 'bar',
        data: [environmentComparisonResult.value.groupA.score],
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      },
      {
        name: '方案B组',
        type: 'bar',
        data: [environmentComparisonResult.value.groupB.score],
        itemStyle: {
          color: '#67C23A'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      }
    ]
  }

  environmentChartInstance.setOption(option)

  // 响应式调整 - 先移除旧的监听器
  if (environmentResizeHandler) {
    window.removeEventListener('resize', environmentResizeHandler)
  }
  environmentResizeHandler = () => {
    environmentChartInstance?.resize()
  }
  window.addEventListener('resize', environmentResizeHandler)
}

// 疾病风险预测
const predictDisease = () => {
  try {
    diseaseResult.value = predictDiseaseFn(diseaseForm)
    ElMessage.success('风险预测完成')
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    ElMessage.error(`预测失败: ${errorMsg}`)
  }
}

// 疾病风险对比分析
const compareDisease = async () => {
  try {
    // 计算A组预测
    const resultA = predictDiseaseFn(diseaseFormA)
    // 计算B组预测
    const resultB = predictDiseaseFn(diseaseFormB)

    // 判断哪组更优 (风险分数越低越好)
    const betterGroup = resultA.risk_score < resultB.risk_score ? 'A' : 'B'
    const difference = Math.abs(resultA.risk_score - resultB.risk_score)
    const improvementPercent = ((difference / Math.max(resultA.risk_score, resultB.risk_score)) * 100).toFixed(1)

    diseaseComparisonResult.value = {
      groupA: {
        risk_score: resultA.risk_score,
        risk_probability: resultA.risk_probability,
        risk_level: resultA.risk_level
      },
      groupB: {
        risk_score: resultB.risk_score,
        risk_probability: resultB.risk_probability,
        risk_level: resultB.risk_level
      },
      betterGroup,
      conclusion: `方案${betterGroup}组的疾病风险更低，风险等级为${betterGroup === 'A' ? resultA.risk_level : resultB.risk_level}`,
      analysis: `方案${betterGroup}组的风险评分为${betterGroup === 'A' ? resultA.risk_score : resultB.risk_score}分，风险概率为${((betterGroup === 'A' ? resultA.risk_probability : resultB.risk_probability) * 100).toFixed(1)}%，比另一组降低${improvementPercent}%。该方案能够更有效地控制疾病风险，减少经济损失。建议采用方案${betterGroup}组的防疫策略。`
    }

    // 等待DOM更新后渲染图表
    await nextTick()
    renderDiseaseComparisonChart()
    
    ElMessage.success('疾病风险对比分析完成')
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    ElMessage.error(`对比分析失败: ${errorMsg}`)
  }
}

// 渲染疾病风险对比图表
const renderDiseaseComparisonChart = () => {
  if (!diseaseComparisonChart.value || !diseaseComparisonResult.value) return

  // 如果已有实例，先销毁
  if (diseaseChartInstance) {
    diseaseChartInstance.dispose()
  }

  diseaseChartInstance = echarts.init(diseaseComparisonChart.value)
  
  const option = {
    title: {
      text: '疾病风险对比分析',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 16,
        fontWeight: 600
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['方案A组', '方案B组'],
      top: 40,
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: 80,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['风险评分', '风险概率 (%)'],
      axisLabel: {
        interval: 0,
        rotate: 0,
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      name: '数值',
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '方案A组',
        type: 'bar',
        data: [
          diseaseComparisonResult.value.groupA.risk_score,
          diseaseComparisonResult.value.groupA.risk_probability * 100
        ],
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      },
      {
        name: '方案B组',
        type: 'bar',
        data: [
          diseaseComparisonResult.value.groupB.risk_score,
          diseaseComparisonResult.value.groupB.risk_probability * 100
        ],
        itemStyle: {
          color: '#67C23A'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      }
    ]
  }

  diseaseChartInstance.setOption(option)

  // 响应式调整 - 先移除旧的监听器
  if (diseaseResizeHandler) {
    window.removeEventListener('resize', diseaseResizeHandler)
  }
  diseaseResizeHandler = () => {
    diseaseChartInstance?.resize()
  }
  window.addEventListener('resize', diseaseResizeHandler)
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
  padding: 0;
  max-width: 100%;
}

.prediction-card {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #999;
  font-style: italic;
}

.prediction-tabs {
  margin-top: 20px;
}

.mode-selector {
  margin-bottom: 20px;
  display: block;
  text-align: center;
}

.comparison-card {
  margin-bottom: 20px;
}

.group-a-card {
  border-left: 3px solid #409EFF;
}

.group-b-card {
  border-left: 3px solid #67C23A;
}

.comparison-header {
  font-weight: 600;
  font-size: 16px;
  text-align: center;
}

.comparison-chart {
  width: 100%;
  height: 400px;
  margin: 20px 0;
}

.expert-conclusion {
  margin-top: 20px;
  border: 2px solid #E6A23C;
}

.conclusion-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.metrics-comparison {
  margin-top: 10px;
}

.result-box {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.result-box h3 {
  margin-bottom: 15px;
  color: #333;
  font-weight: 600;
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header h2 {
    font-size: 20px;
  }

  .card-header p {
    font-size: 12px;
  }

  :deep(.el-form-item__label) {
    font-size: 13px;
  }

  .result-box {
    padding: 15px;
  }

  :deep(.el-descriptions) {
    font-size: 13px;
  }

  .comparison-chart {
    height: 300px;
  }
}

@media (max-width: 480px) {
  :deep(.el-form .el-form-item__label) {
    width: 120px;
  }

  .result-box h3 {
    font-size: 16px;
  }

  .comparison-chart {
    height: 250px;
  }
}
</style>

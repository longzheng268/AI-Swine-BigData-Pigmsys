<template>
  <div class="comparison-container">
    <h2>生长预测方案对比</h2>
    <p class="description">
      输入两组不同的饲养参数，直观对比不同品种或饲养方式下的预期生长效果
    </p>

    <el-row :gutter="20">
      <!-- 方案一 -->
      <el-col :xs="24" :sm="12" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>方案一</span>
              <el-tag type="primary">对照组</el-tag>
            </div>
          </template>

          <el-form :model="plan1" label-width="100px" size="default">
            <el-form-item label="方案名称">
              <el-input
                v-model="plan1.name"
                placeholder="例如：白猪标准方案"
              ></el-input>
            </el-form-item>
            <el-form-item label="日龄">
              <el-input-number
                v-model="plan1.age"
                :min="1"
                :max="365"
                style="width: 100%"
              ></el-input-number>
              <span class="unit">天</span>
            </el-form-item>
            <el-form-item label="饲料摄入量">
              <el-input-number
                v-model="plan1.feed"
                :min="0"
                :max="10"
                :precision="2"
                style="width: 100%"
              ></el-input-number>
              <span class="unit">kg/天</span>
            </el-form-item>
            <el-form-item label="品种">
              <el-select v-model="plan1.breed" style="width: 100%">
                <el-option label="白猪" value="白猪"></el-option>
                <el-option label="黑猪" value="黑猪"></el-option>
                <el-option label="花猪" value="花猪"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="plan1.sex">
                <el-radio value="公">公</el-radio>
                <el-radio value="母">母</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 方案二 -->
      <el-col :xs="24" :sm="12" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>方案二</span>
              <el-tag type="success">实验组</el-tag>
            </div>
          </template>

          <el-form :model="plan2" label-width="100px" size="default">
            <el-form-item label="方案名称">
              <el-input
                v-model="plan2.name"
                placeholder="例如：黑猪优化方案"
              ></el-input>
            </el-form-item>
            <el-form-item label="日龄">
              <el-input-number
                v-model="plan2.age"
                :min="1"
                :max="365"
                style="width: 100%"
              ></el-input-number>
              <span class="unit">天</span>
            </el-form-item>
            <el-form-item label="饲料摄入量">
              <el-input-number
                v-model="plan2.feed"
                :min="0"
                :max="10"
                :precision="2"
                style="width: 100%"
              ></el-input-number>
              <span class="unit">kg/天</span>
            </el-form-item>
            <el-form-item label="品种">
              <el-select v-model="plan2.breed" style="width: 100%">
                <el-option label="白猪" value="白猪"></el-option>
                <el-option label="黑猪" value="黑猪"></el-option>
                <el-option label="花猪" value="花猪"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="plan2.sex">
                <el-radio value="公">公</el-radio>
                <el-radio value="母">母</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button
        type="primary"
        size="large"
        @click="handleCompare"
        :loading="comparing"
      >
        <el-icon style="margin-right: 5px"><DataAnalysis /></el-icon>
        开始对比分析
      </el-button>
      <el-button size="large" @click="handleReset">
        <el-icon style="margin-right: 5px"><Refresh /></el-icon>
        重置参数
      </el-button>
    </div>

    <!-- 对比结果 -->
    <div v-if="comparisonResult" class="comparison-result">
      <el-card>
        <template #header>
          <span>对比分析结果</span>
        </template>

        <!-- 数据对比表格 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>数据对比表</h3>
            <el-table :data="comparisonTable" stripe border style="width: 100%">
              <el-table-column
                prop="metric"
                label="指标"
                width="180"
                align="center"
              ></el-table-column>
              <el-table-column
                prop="plan1"
                :label="plan1.name || '方案一'"
                align="center"
              >
                <template #default="scope">
                  <span :class="{ 'better-value': scope.row.better === 1 }">{{
                    scope.row.plan1
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="plan2"
                :label="plan2.name || '方案二'"
                align="center"
              >
                <template #default="scope">
                  <span :class="{ 'better-value': scope.row.better === 2 }">{{
                    scope.row.plan2
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="difference" label="差异" align="center">
                <template #default="scope">
                  <el-tag
                    v-if="scope.row.difference"
                    :type="scope.row.diffType"
                  >
                    {{ scope.row.difference }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>

        <!-- ECharts 对比图表 -->
        <el-row :gutter="20" style="margin-top: 30px">
          <el-col :span="24">
            <h3>可视化对比</h3>
            <div ref="chartContainer" style="width: 100%; height: 400px"></div>
          </el-col>
        </el-row>

        <!-- 分析建议 -->
        <el-row :gutter="20" style="margin-top: 30px">
          <el-col :span="24">
            <h3>综合分析</h3>
            <el-alert
              :title="analysisConclusion"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <ul style="margin: 10px 0; padding-left: 20px">
                  <li
                    v-for="(suggestion, index) in analysisSuggestions"
                    :key="index"
                  >
                    {{ suggestion }}
                  </li>
                </ul>
              </template>
            </el-alert>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import { predictGrowth } from "@/utils/prediction";

export default {
  name: "ComparisonAnalysis",
  data() {
    return {
      comparing: false,
      plan1: {
        name: "白猪标准方案",
        age: 100,
        feed: 2.5,
        breed: "白猪",
        sex: "公",
      },
      plan2: {
        name: "黑猪优化方案",
        age: 100,
        feed: 3.0,
        breed: "黑猪",
        sex: "公",
      },
      comparisonResult: null,
      chartInstance: null,
      comparisonTable: [],
      analysisConclusion: "",
      analysisSuggestions: [],
    };
  },
  beforeUnmount() {
    if (this.chartInstance) {
      this.chartInstance.dispose();
    }
  },
  methods: {
    handleCompare() {
      this.comparing = true;

      // 使用前端算法进行预测
      setTimeout(() => {
        const result1 = predictGrowth(this.plan1);
        const result2 = predictGrowth(this.plan2);

        this.comparisonResult = {
          plan1: result1,
          plan2: result2,
        };

        // 生成对比表格数据
        this.generateComparisonTable(result1, result2);

        // 生成分析建议
        this.generateAnalysis(result1, result2);

        // 绘制图表
        this.$nextTick(() => {
          this.renderChart(result1, result2);
        });

        this.comparing = false;
        this.$message.success("对比分析完成");
      }, 500);
    },

    generateComparisonTable(result1, result2) {
      const weight1 = result1.predicted_weight;
      const weight2 = result2.predicted_weight;
      const rate1 = result1.growth_rate;
      const rate2 = result2.growth_rate;
      const confidence1 = result1.confidence * 100;
      const confidence2 = result2.confidence * 100;

      this.comparisonTable = [
        {
          metric: "预测体重 (kg)",
          plan1: weight1.toFixed(2),
          plan2: weight2.toFixed(2),
          difference: `${weight2 > weight1 ? "+" : ""}${(weight2 - weight1).toFixed(2)} kg`,
          diffType:
            weight2 > weight1
              ? "success"
              : weight2 < weight1
                ? "danger"
                : "info",
          better: weight2 > weight1 ? 2 : weight2 < weight1 ? 1 : 0,
        },
        {
          metric: "生长速度 (kg/天)",
          plan1: rate1.toFixed(3),
          plan2: rate2.toFixed(3),
          difference: `${rate2 > rate1 ? "+" : ""}${(rate2 - rate1).toFixed(3)} kg/天`,
          diffType:
            rate2 > rate1 ? "success" : rate2 < rate1 ? "danger" : "info",
          better: rate2 > rate1 ? 2 : rate2 < rate1 ? 1 : 0,
        },
        {
          metric: "置信度 (%)",
          plan1: confidence1.toFixed(1) + "%",
          plan2: confidence2.toFixed(1) + "%",
          difference: `${confidence2 > confidence1 ? "+" : ""}${(confidence2 - confidence1).toFixed(1)}%`,
          diffType:
            confidence2 > confidence1
              ? "success"
              : confidence2 < confidence1
                ? "danger"
                : "info",
          better:
            confidence2 > confidence1 ? 2 : confidence2 < confidence1 ? 1 : 0,
        },
        {
          metric: "品种",
          plan1: this.plan1.breed,
          plan2: this.plan2.breed,
          difference: "",
          diffType: "info",
          better: 0,
        },
        {
          metric: "饲料摄入 (kg/天)",
          plan1: this.plan1.feed.toFixed(2),
          plan2: this.plan2.feed.toFixed(2),
          difference:
            this.plan2.feed !== this.plan1.feed
              ? `${this.plan2.feed > this.plan1.feed ? "+" : ""}${(this.plan2.feed - this.plan1.feed).toFixed(2)} kg/天`
              : "",
          diffType: "info",
          better: 0,
        },
      ];
    },

    generateAnalysis(result1, result2) {
      const weight1 = result1.predicted_weight;
      const weight2 = result2.predicted_weight;
      const rate1 = result1.growth_rate;
      const rate2 = result2.growth_rate;

      const weightDiff = weight2 - weight1;
      const rateDiff = rate2 - rate1;
      const feedDiff = this.plan2.feed - this.plan1.feed;

      // 结论
      if (Math.abs(weightDiff) < 1) {
        this.analysisConclusion = "两个方案预期效果相近，差异不明显";
      } else if (weightDiff > 0) {
        this.analysisConclusion = `方案二（${this.plan2.name}）预期效果更好，预测体重高出 ${weightDiff.toFixed(2)} kg`;
      } else {
        this.analysisConclusion = `方案一（${this.plan1.name}）预期效果更好，预测体重高出 ${Math.abs(weightDiff).toFixed(2)} kg`;
      }

      // 建议
      this.analysisSuggestions = [];

      if (rateDiff > 0.1) {
        this.analysisSuggestions.push(
          `方案二的生长速度更快（${rate2.toFixed(3)} kg/天），建议采用方案二的饲养参数`,
        );
      } else if (rateDiff < -0.1) {
        this.analysisSuggestions.push(
          `方案一的生长速度更快（${rate1.toFixed(3)} kg/天），建议采用方案一的饲养参数`,
        );
      }

      if (feedDiff > 0 && weightDiff > 0) {
        const efficiency = weightDiff / feedDiff;
        this.analysisSuggestions.push(
          `增加饲料投入 ${feedDiff.toFixed(2)} kg/天，可获得 ${weightDiff.toFixed(2)} kg 的增重效果，饲料转化效率为 ${efficiency.toFixed(2)}`,
        );
      }

      if (this.plan1.breed !== this.plan2.breed) {
        if (weightDiff > 5) {
          this.analysisSuggestions.push(
            `${this.plan2.breed}在当前饲养条件下表现更优，建议优先选择该品种`,
          );
        } else if (weightDiff < -5) {
          this.analysisSuggestions.push(
            `${this.plan1.breed}在当前饲养条件下表现更优，建议优先选择该品种`,
          );
        }
      }

      if (this.analysisSuggestions.length === 0) {
        this.analysisSuggestions.push(
          "两个方案各有优势，建议根据实际成本和市场需求选择合适的方案",
        );
      }
    },

    renderChart(result1, result2) {
      if (!this.$refs.chartContainer) return;

      if (this.chartInstance) {
        this.chartInstance.dispose();
      }

      this.chartInstance = echarts.init(this.$refs.chartContainer);

      const option = {
        title: {
          text: "方案对比柱状图",
          left: "center",
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          data: [this.plan1.name || "方案一", this.plan2.name || "方案二"],
          top: 30,
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          data: ["预测体重 (kg)", "生长速度 (kg/天)", "置信度 (%)"],
          axisLabel: {
            interval: 0,
            rotate: 0,
          },
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: this.plan1.name || "方案一",
            type: "bar",
            data: [
              result1.predicted_weight.toFixed(2),
              (result1.growth_rate * 100).toFixed(2),
              (result1.confidence * 100).toFixed(2),
            ],
            itemStyle: {
              color: "#409EFF",
            },
            label: {
              show: true,
              position: "top",
              formatter: "{c}",
            },
          },
          {
            name: this.plan2.name || "方案二",
            type: "bar",
            data: [
              result2.predicted_weight.toFixed(2),
              (result2.growth_rate * 100).toFixed(2),
              (result2.confidence * 100).toFixed(2),
            ],
            itemStyle: {
              color: "#67C23A",
            },
            label: {
              show: true,
              position: "top",
              formatter: "{c}",
            },
          },
        ],
      };

      this.chartInstance.setOption(option);

      // 响应式调整
      window.addEventListener("resize", () => {
        if (this.chartInstance) {
          this.chartInstance.resize();
        }
      });
    },

    handleReset() {
      this.plan1 = {
        name: "白猪标准方案",
        age: 100,
        feed: 2.5,
        breed: "白猪",
        sex: "公",
      };
      this.plan2 = {
        name: "黑猪优化方案",
        age: 100,
        feed: 3.0,
        breed: "黑猪",
        sex: "公",
      };
      this.comparisonResult = null;
      this.comparisonTable = [];
      this.analysisSuggestions = [];
      if (this.chartInstance) {
        this.chartInstance.dispose();
        this.chartInstance = null;
      }
      this.$message.success("参数已重置");
    },
  },
};
</script>

<style scoped>
.comparison-container {
  padding: 20px;
}

.description {
  color: #606266;
  margin-bottom: 20px;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.unit {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

.action-buttons {
  text-align: center;
  margin: 30px 0;
}

.comparison-result {
  margin-top: 30px;
}

.better-value {
  color: #67c23a;
  font-weight: bold;
}

h3 {
  margin-bottom: 15px;
  color: #303133;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .comparison-container {
    padding: 10px;
  }

  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>

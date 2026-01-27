<template>
  <div class="big-screen-container" :style="{ backgroundColor: bgColor }">
    <!-- 动态背景效果 -->
    <div class="bg-decoration">
      <div class="bg-grid"></div>
      <div class="bg-particles">
        <div
          class="particle"
          v-for="i in 30"
          :key="i"
          :style="getParticleStyle(i)"
        ></div>
      </div>
      <div class="bg-gradient-1"></div>
      <div class="bg-gradient-2"></div>
      <div class="bg-gradient-3"></div>
      <!-- 光束效果 -->
      <div class="light-beam light-beam-1"></div>
      <div class="light-beam light-beam-2"></div>
      <div class="light-beam light-beam-3"></div>
      <!-- 扫描线效果 -->
      <div class="scan-line"></div>
    </div>

    <!-- 顶部标题栏 -->
    <div class="screen-header">
      <div class="header-left">
        <div class="logo-area">
          <img :src="logoImage" alt="logo" class="logo" />
        </div>
        <el-button
          type="primary"
          icon="el-icon-back"
          size="small"
          @click="goBack"
          class="back-button"
        >
          返回主页
        </el-button>
        <el-button
          type="success"
          icon="el-icon-refresh"
          size="small"
          @click="loadData(true)"
          class="refresh-button"
        >
          刷新数据
        </el-button>
      </div>
      <div class="header-center">
        <h1 class="main-title">智慧养猪大数据可视化平台</h1>
        <p class="sub-title">
          SMART PIG FARMING BIG DATA VISUALIZATION PLATFORM
        </p>
      </div>
      <div class="header-right">
        <div class="time-display">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
        </div>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="screen-body">
      <!-- 左侧区域 -->
      <div class="left-panel">
        <!-- 实时数据概览 -->
        <div class="panel-item overview-panel">
          <div class="panel-title">
            <span class="title-text">实时数据概览</span>
            <span class="update-time">{{ overviewData.updateTime }}</span>
          </div>
          <div class="panel-content">
            <div
              class="stat-card"
              v-for="(item, index) in overviewStats"
              :key="index"
            >
              <div class="stat-icon" :style="{ backgroundColor: item.color }">
                <i :class="item.icon"></i>
              </div>
              <div class="stat-info">
                <div class="stat-label">{{ item.label }}</div>
                <div class="stat-value">{{ item.value }}</div>
                <div class="stat-unit">{{ item.unit }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 猪类型分布 -->
        <div class="panel-item">
          <div class="panel-title">
            <span class="title-text">猪类型分布</span>
          </div>
          <div class="panel-content">
            <div ref="pigTypeChart" class="chart-container"></div>
          </div>
        </div>

        <!-- 环境质量评价 -->
        <div class="panel-item">
          <div class="panel-title">
            <span class="title-text">环境质量评价</span>
          </div>
          <div class="panel-content env-quality">
            <div class="quality-score">
              <div
                class="score-circle"
                :class="'level-' + environmentQuality.qualityLevel"
              >
                <span class="score-number">{{ environmentQuality.score }}</span>
                <span class="score-label">分</span>
              </div>
              <div class="quality-info">
                <div class="quality-level">
                  {{ environmentQuality.quality }}
                </div>
                <div class="quality-grade">
                  等级：{{ environmentQuality.qualityLevel }}
                </div>
              </div>
            </div>
            <div class="quality-suggestions">
              <i class="el-icon-warning"></i>
              {{ environmentQuality.suggestions }}
            </div>
          </div>
        </div>
      </div>

      <!-- 中间区域 -->
      <div class="center-panel">
        <!-- 环境指标趋势 -->
        <div class="panel-item large-panel">
          <div class="panel-title">
            <span class="title-text">环境指标实时趋势</span>
            <el-radio-group
              v-model="trendType"
              size="small"
              @change="renderTrendChart"
            >
              <el-radio-button value="temperature">温度</el-radio-button>
              <el-radio-button value="humidity">湿度</el-radio-button>
            </el-radio-group>
          </div>
          <div class="panel-content">
            <div ref="trendChart" class="chart-container"></div>
          </div>
        </div>

        <!-- 监测点分布地图 -->
        <div class="panel-item large-panel">
          <div class="panel-title">
            <span class="title-text">监测点实时状态</span>
          </div>
          <div class="panel-content">
            <div class="monitor-points-grid">
              <div
                v-for="(point, index) in monitorPoints"
                :key="index"
                class="monitor-point-card"
                :class="point.isAbnormal ? 'abnormal' : 'normal'"
              >
                <div class="point-header">
                  <span class="point-name">{{ point.name }}</span>
                  <el-tag
                    :type="point.isAbnormal ? 'danger' : 'success'"
                    size="small"
                  >
                    {{ point.status }}
                  </el-tag>
                </div>
                <div class="point-location">{{ point.location }}</div>
                <div class="point-metrics">
                  <div class="metric-item">
                    <span class="metric-label">温度</span>
                    <span class="metric-value">{{ point.temperature }}℃</span>
                  </div>
                  <div class="metric-item">
                    <span class="metric-label">湿度</span>
                    <span class="metric-value">{{ point.humidity }}%</span>
                  </div>
                  <div class="metric-item">
                    <span class="metric-label">CO₂</span>
                    <span class="metric-value">{{ point.co2 }}ppm</span>
                  </div>
                  <div class="metric-item">
                    <span class="metric-label">氨气</span>
                    <span class="metric-value">{{ point.nh3 }}ppm</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="right-panel">
        <!-- 生长预测 -->
        <div class="panel-item">
          <div class="panel-title">
            <span class="title-text">生长趋势预测</span>
          </div>
          <div class="panel-content growth-prediction">
            <div class="prediction-item">
              <div class="prediction-label">预测体重</div>
              <div class="prediction-value">
                {{ growthPrediction.predictedWeight }}
                <span class="unit">kg</span>
              </div>
            </div>
            <div class="prediction-item">
              <div class="prediction-label">生长速度</div>
              <div class="prediction-value">
                {{ growthPrediction.growthRate }}
                <span class="unit">kg/天</span>
              </div>
            </div>
            <div class="prediction-item">
              <div class="prediction-label">预测准确率</div>
              <div class="prediction-value">
                {{ (growthPrediction.accuracy * 100).toFixed(1) }}
                <span class="unit">%</span>
              </div>
            </div>
            <div class="trend-indicator" :class="growthPrediction.trend">
              <i class="el-icon-top"></i>
              趋势：{{ growthPrediction.trend }}
            </div>
          </div>
        </div>

        <!-- 环境指标统计 -->
        <div class="panel-item">
          <div class="panel-title">
            <span class="title-text">环境指标统计</span>
          </div>
          <div class="panel-content">
            <div ref="radarChart" class="chart-container"></div>
          </div>
        </div>

        <!-- 疾病风险预警 -->
        <div class="panel-item">
          <div class="panel-title">
            <span class="title-text">疾病风险预警</span>
          </div>
          <div class="panel-content risk-warning">
            <div class="risk-level" :class="'risk-' + diseaseRisk.riskLevel">
              <div class="risk-icon">
                <i class="el-icon-warning-outline"></i>
              </div>
              <div class="risk-info">
                <div class="risk-title">
                  风险等级：{{ diseaseRisk.riskLevel }}
                </div>
                <div class="risk-probability">
                  风险概率：{{
                    (diseaseRisk.riskProbability * 100).toFixed(1)
                  }}%
                </div>
              </div>
            </div>
            <div class="risk-advice">
              <div class="advice-title">
                <i class="el-icon-info"></i> 防疫建议
              </div>
              <div class="advice-content">
                {{ diseaseRisk.preventionAdvice }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import { getDashboardData, submitHadoopJob } from "@/api/dashboard";
import logoImage from "@/assets/logo.jpg";

export default {
  name: "BigScreen",
  data() {
    return {
      logoImage,
      bgColor: "#0A0E27",
      currentDate: "",
      currentTime: "",
      refreshInterval: 30,
      autoRefreshTimer: null,
      trendType: "temperature",
      isDestroyed: false, // 添加销毁标志

      // 数据
      overviewData: {
        updateTime: "",
      },
      overviewStats: [],
      pigTypeDistribution: {},
      environmentQuality: {
        qualityLevel: "II",
        quality: "良好",
        score: 0,
        suggestions: "",
      },
      growthPrediction: {
        predictedWeight: 0,
        growthRate: 0,
        accuracy: 0,
        trend: "上升",
      },
      diseaseRisk: {
        riskLevel: "低",
        riskProbability: 0,
        preventionAdvice: "",
      },
      environmentMetrics: {
        temperature: { standardRate: 0 },
        humidity: { standardRate: 0 },
        co2: { standardRate: 0 },
        ammonia: { standardRate: 0 },
      },
      trendData: {
        temperature: [],
        humidity: [],
      },
      monitorPoints: [],

      // 图表实例
      pigTypeChart: null,
      radarChart: null,
      trendChart: null,
    };
  },
  mounted() {
    // 🔍 诊断：检查token状态（仅在开发环境）
    if (process.env.NODE_ENV === "development") {
      const token = localStorage.getItem("my-login-token");
      console.log("🔍 Token诊断:", {
        存在: token ? "是" : "否",
        长度: token ? token.length : 0,
        前20个字符: token ? token.substring(0, 20) : "无",
        是否为有效JWT: token && token.length > 100 ? "可能是" : "❌ 不是",
      });
    }

    const token = localStorage.getItem("my-login-token");

    // 如果token无效（长度太短），提示用户重新登录
    if (!token || token.length < 50) {
      this.$confirm("登录状态无效或已过期，请重新登录", "提示", {
        confirmButtonText: "重新登录",
        cancelButtonText: "取消",
        type: "warning",
        showClose: false,
      })
        .then(() => {
          localStorage.removeItem("my-login-token");
          localStorage.removeItem("my-login-user");
          this.$router.push("/login");
        })
        .catch(() => {
          this.$router.push("/home");
        });
      return;
    }

    // 等待DOM完全渲染后再初始化图表
    this.$nextTick(() => {
      this.initCharts();
      // 打开大屏时提交 Hadoop 任务（fire-and-forget）
      this.submitHadoopJobFire();
      this.loadData();
      this.updateTime();
      // 已禁用自动刷新功能
      // this.startAutoRefresh()
    });

    window.addEventListener("resize", this.handleResize);
  },
  beforeUnmount() {
    // Vue 3 生命周期钩子，组件卸载前清理
    // 设置销毁标志
    this.isDestroyed = true;

    // 停止自动刷新
    this.stopAutoRefresh();

    // 移除事件监听
    window.removeEventListener("resize", this.handleResize);

    // 销毁图表
    this.destroyCharts();
  },
  methods: {
    /**
     * 初始化图表
     */
    initCharts() {
      // 如果组件已销毁，不执行初始化
      if (this.isDestroyed) {
        console.warn("组件已销毁，取消图表初始化");
        return;
      }

      try {
        // 检查DOM节点是否存在
        if (
          !this.$refs.pigTypeChart ||
          !this.$refs.radarChart ||
          !this.$refs.trendChart
        ) {
          console.warn("图表容器DOM节点未就绪，延迟初始化");
          setTimeout(() => {
            if (!this.isDestroyed) {
              this.initCharts();
            }
          }, 100);
          return;
        }

        // 销毁可能存在的旧实例
        this.destroyCharts();

        // 初始化新实例
        this.pigTypeChart = echarts.init(this.$refs.pigTypeChart);
        this.radarChart = echarts.init(this.$refs.radarChart);
        this.trendChart = echarts.init(this.$refs.trendChart);

        if (process.env.NODE_ENV === "development") {
          console.log("✅ ECharts图表实例初始化成功");
        }
      } catch (error) {
        console.error("❌ ECharts初始化失败:", error);
      }
    },

    /**
     * 销毁图表
     */
    destroyCharts() {
      try {
        if (this.pigTypeChart && !this.pigTypeChart.isDisposed()) {
          this.pigTypeChart.dispose();
        }
        if (this.radarChart && !this.radarChart.isDisposed()) {
          this.radarChart.dispose();
        }
        if (this.trendChart && !this.trendChart.isDisposed()) {
          this.trendChart.dispose();
        }

        // 重置为null
        this.pigTypeChart = null;
        this.radarChart = null;
        this.trendChart = null;
      } catch (error) {
        console.error("销毁图表时出错:", error);
      }
    },

    /**
     * 图表自适应
     */
    handleResize() {
      // 如果组件已销毁，不执行调整
      if (this.isDestroyed) {
        return;
      }

      // 为每个图表单独处理 resize，确保错误不会影响其他图表
      this.resizeChart("pigTypeChart", this.pigTypeChart);
      this.resizeChart("radarChart", this.radarChart);
      this.resizeChart("trendChart", this.trendChart);
    },

    /**
     * 安全的图表 resize 方法
     */
    resizeChart(chartName, chartInstance) {
      if (!chartInstance || chartInstance.isDisposed()) {
        return;
      }

      try {
        // 直接尝试 resize，如果失败则重新渲染
        chartInstance.resize();
      } catch (error) {
        console.error(`${chartName} 调整大小失败:`, error);
        // 如果 resize 失败，尝试修复配置或重新渲染
        try {
          // 先尝试修复配置
          const currentOption = chartInstance.getOption();
          if (currentOption && currentOption.series) {
            // 处理嵌套数组格式
            let allSeries = [];
            if (Array.isArray(currentOption.series[0])) {
              allSeries = currentOption.series[0];
            } else {
              allSeries = currentOption.series;
            }

            // 根据图表类型确定默认的 type
            let defaultType = "line"; // 默认类型
            if (chartName === "pigTypeChart") {
              defaultType = "pie";
            } else if (chartName === "radarChart") {
              defaultType = "radar";
            } else if (chartName === "trendChart") {
              defaultType = "line";
            }

            // 过滤并修复无效的 series 项
            const validSeries = allSeries
              .filter(
                (item) =>
                  item &&
                  item !== null &&
                  item !== undefined &&
                  item.type &&
                  typeof item.type === "string",
              )
              .map((item) => ({
                ...item,
                type: item.type || defaultType, // 确保 type 总是存在
              }));

            // 如果有有效数据，重新设置配置并再次尝试 resize
            if (validSeries.length > 0) {
              try {
                chartInstance.setOption(
                  {
                    series: validSeries,
                  },
                  false,
                ); // 使用 merge 模式，避免丢失其他配置
                chartInstance.resize();
                return; // 修复成功，退出
              } catch (setOptionError) {
                console.error(`${chartName} 修复配置失败:`, setOptionError);
              }
            }
          }

          // 如果修复配置失败，重新渲染图表
          if (chartName === "pigTypeChart") {
            this.renderPigTypeChart();
          } else if (chartName === "radarChart") {
            this.renderRadarChart();
          } else if (chartName === "trendChart") {
            this.renderTrendChart();
          }
        } catch (renderError) {
          console.error(`${chartName} 重新渲染失败:`, renderError);
        }
      }
    },

    /**
     * 加载数据
     * @param {boolean} showMessage - 是否显示加载消息
     */
    async loadData(showMessage = false) {
      // 如果组件已销毁，不执行加载
      if (this.isDestroyed) {
        console.warn("组件已销毁，取消数据加载");
        return;
      }

      // 如果是手动刷新，先提交 Hadoop 任务（fire-and-forget）
      if (showMessage) {
        this.submitHadoopJobFire();
      }

      // 显示加载提示（仅在手动刷新时）
      const loadingMsg = showMessage
        ? this.$message({
            message: "正在加载数据...",
            type: "info",
            duration: 0, // 不自动关闭
            iconClass: "el-icon-loading",
          })
        : null;

      try {
        // 添加 10 秒超时保护
        const response = await Promise.race([
          getDashboardData(),
          new Promise((_, reject) => 
            setTimeout(() => reject(new Error('请求超时')), 10000)
          )
        ]);

        // 再次检查组件是否已销毁（异步操作完成时）
        if (this.isDestroyed) {
          console.warn("数据加载完成时组件已销毁，取消后续操作");
          return;
        }

        const data = response.data.data;

        if (process.env.NODE_ENV === "development") {
          console.log("📊 大屏数据加载成功:", {
            hasOverview: !!data.realTimeOverview,
            hasHadoop: !!data.hadoopAnalysis,
            hasPrediction: !!data.predictionData,
            hasEnvironment: !!data.environmentData,
          });
        }

        // 处理实时概览数据
        this.overviewData = data.realTimeOverview || {};
        this.overviewStats = [
          {
            label: "总猪数",
            value: this.overviewData.totalPigs || 0,
            unit: "头",
            icon: "el-icon-medal",
            color: "#409EFF",
          },
          {
            label: "健康猪数",
            value: this.overviewData.healthyPigs || 0,
            unit: "头",
            icon: "el-icon-success",
            color: "#67C23A",
          },
          {
            label: "异常环境",
            value: this.overviewData.abnormalEnvironments || 0,
            unit: "处",
            icon: "el-icon-warning",
            color: "#F56C6C",
          },
          {
            label: "平均温度",
            value: this.overviewData.avgTemperature || 0,
            unit: "℃",
            icon: "el-icon-sunny",
            color: "#E6A23C",
          },
        ];

        // 处理Hadoop分析数据
        if (data.hadoopAnalysis) {
          this.pigTypeDistribution =
            data.hadoopAnalysis.pigTypeDistribution || {};
          // 确保 environmentMetrics 有默认结构
          this.environmentMetrics = data.hadoopAnalysis.environmentMetrics || {
            temperature: { standardRate: 0 },
            humidity: { standardRate: 0 },
            co2: { standardRate: 0 },
            ammonia: { standardRate: 0 },
          };

          if (data.hadoopAnalysis.trendAnalysis) {
            this.trendData.temperature = (
              data.hadoopAnalysis.trendAnalysis.temperatureTrend || []
            ).filter((item) => item != null);
            this.trendData.humidity = (
              data.hadoopAnalysis.trendAnalysis.humidityTrend || []
            ).filter((item) => item != null);
          }
        }

        // 处理Python预测数据
        if (data.predictionData) {
          this.environmentQuality =
            data.predictionData.environmentQuality || this.environmentQuality;
          this.growthPrediction =
            data.predictionData.growthPrediction || this.growthPrediction;
          this.diseaseRisk =
            data.predictionData.diseaseRisk || this.diseaseRisk;
        }

        // 处理环境监测数据
        if (data.environmentData) {
          this.monitorPoints = (data.environmentData.monitorPoints || []).slice(
            0,
            6,
          );
        }

        // 再次检查组件状态后渲染图表
        // 确保图表实例已初始化后再渲染
        if (!this.isDestroyed) {
          this.$nextTick(() => {
            if (!this.isDestroyed) {
              try {
                if (this.pigTypeChart && !this.pigTypeChart.isDisposed()) {
                  this.renderPigTypeChart();
                }
                if (this.radarChart && !this.radarChart.isDisposed()) {
                  this.renderRadarChart();
                }
                if (this.trendChart && !this.trendChart.isDisposed()) {
                  this.renderTrendChart();
                }
              } catch (error) {
                console.error("渲染图表时出错:", error);
              }
            }
          });
        }

        // 关闭加载提示，显示成功消息（仅在手动刷新时）
        if (loadingMsg) {
          loadingMsg.close();
          this.$message.success("数据加载成功");
        }
      } catch (error) {
        // 关闭加载提示
        if (loadingMsg) {
          loadingMsg.close();
        }

        // 如果组件已销毁，忽略错误
        if (this.isDestroyed) {
          return;
        }

        console.error("❌ 加载数据失败 - 详细错误:", {
          message: error.message,
          response: error.response,
          status: error.response?.status,
          data: error.response?.data,
          config: {
            url: error.config?.url,
            method: error.config?.method,
            headers: error.config?.headers,
          },
        });

        // 处理超时错误
        if (error.message && (error.message.includes("timeout") || error.message.includes("超时"))) {
          console.warn("⚠️  数据加载超时，使用默认数据");
          if (showMessage) {
            this.$message.warning({
              message: "数据加载超时，使用默认数据展示",
              duration: 3000,
            });
          }
          // 使用默认数据继续显示，而不是完全失败
          this.loadDefaultData();
          return;
        }

        if (error.response?.status === 403) {
          this.$message.error("权限不足，请重新登录");
        } else if (error.response?.status === 401) {
          this.$message.error("登录已过期，请重新登录");
        } else {
          this.$message.error(
            "数据加载失败：" + (error.response?.data?.message || error.message),
          );
        }
      }
    },

    /**
     * 加载默认数据（超时或错误时使用）
     */
    loadDefaultData() {
      // 使用默认值，确保图表能够正常显示
      if (!this.overviewData || Object.keys(this.overviewData).length === 0) {
        this.overviewData = {
          updateTime: new Date().toLocaleString("zh-CN"),
        };
        this.overviewStats = [
          {
            label: "总猪数",
            value: 0,
            unit: "头",
            icon: "el-icon-medal",
            color: "#409EFF",
          },
          {
            label: "健康猪数",
            value: 0,
            unit: "头",
            icon: "el-icon-success",
            color: "#67C23A",
          },
          {
            label: "异常环境",
            value: 0,
            unit: "处",
            icon: "el-icon-warning",
            color: "#F56C6C",
          },
          {
            label: "平均温度",
            value: 0,
            unit: "℃",
            icon: "el-icon-sunny",
            color: "#E6A23C",
          },
        ];
      }

      // 确保有默认的猪类型分布数据
      if (
        !this.pigTypeDistribution ||
        Object.keys(this.pigTypeDistribution).length === 0
      ) {
        this.pigTypeDistribution = {
          白猪: 0,
          黑猪: 0,
          花猪: 0,
        };
      }

      // 确保有默认的环境指标
      if (!this.environmentMetrics) {
        this.environmentMetrics = {
          temperature: { standardRate: 0 },
          humidity: { standardRate: 0 },
          co2: { standardRate: 0 },
          ammonia: { standardRate: 0 },
        };
      }

      // 确保有默认的趋势数据
      if (
        !this.trendData.temperature ||
        this.trendData.temperature.length === 0
      ) {
        this.trendData.temperature = [];
      }
      if (!this.trendData.humidity || this.trendData.humidity.length === 0) {
        this.trendData.humidity = [];
      }

      // 确保有默认的监测点数据
      if (!this.monitorPoints || this.monitorPoints.length === 0) {
        this.monitorPoints = [];
      }

      // 渲染图表（即使使用默认数据）
      if (!this.isDestroyed) {
        this.$nextTick(() => {
          if (!this.isDestroyed) {
            try {
              if (this.pigTypeChart && !this.pigTypeChart.isDisposed()) {
                this.renderPigTypeChart();
              }
              if (this.radarChart && !this.radarChart.isDisposed()) {
                this.renderRadarChart();
              }
              if (this.trendChart && !this.trendChart.isDisposed()) {
                this.renderTrendChart();
              }
            } catch (error) {
              console.error("渲染默认数据图表时出错:", error);
            }
          }
        });
      }
    },

    /**
     * 渲染猪类型分布图
     */
    renderPigTypeChart() {
      // 检查组件是否已销毁
      if (this.isDestroyed) {
        return;
      }

      // 检查图表实例和DOM节点
      if (
        !this.pigTypeChart ||
        this.pigTypeChart.isDisposed() ||
        !this.$refs.pigTypeChart
      ) {
        console.warn("猪类型图表未初始化或已销毁");
        return;
      }

      let data = Object.entries(this.pigTypeDistribution).map(
        ([name, value]) => ({
          name,
          value,
        }),
      );

      // 如果没有数据，显示默认提示
      if (data.length === 0 || data.every((item) => item.value === 0)) {
        data = [
          {
            name: "暂无数据",
            value: 1,
            itemStyle: {
              color: "#909399",
            },
          },
        ];
      }

      if (process.env.NODE_ENV === "development") {
        console.log("猪类型分布数据:", data);
      }

      // 更美观的配色方案
      const colors = ["#409EFF", "#67C23A", "#E6A23C", "#F56C6C", "#909399"];

      const option = {
        backgroundColor: "transparent",
        color: colors,
        tooltip: {
          trigger: "item",
          formatter: "{b}: {c}头 ({d}%)",
          backgroundColor: "rgba(0, 0, 0, 0.8)",
          borderColor: "#409EFF",
          borderWidth: 1,
          textStyle: {
            color: "#fff",
            fontSize: 14,
          },
        },
        legend: {
          orient: "horizontal",
          bottom: "2%",
          left: "center",
          textStyle: {
            color: "#fff",
            fontSize: 11,
          },
          itemWidth: 10,
          itemHeight: 10,
          itemGap: 12,
          padding: [5, 10],
        },
        series: [
          {
            type: "pie",
            radius: ["30%", "50%"], // 环形饼图，调小一些留出更多空间
            center: ["50%", "45%"], // 居中位置
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 8,
              borderColor: "rgba(11, 42, 110, 0.8)",
              borderWidth: 2,
            },
            data: data,
            emphasis: {
              itemStyle: {
                shadowBlur: 15,
                shadowOffsetX: 0,
                shadowColor: "rgba(64, 158, 255, 0.5)",
              },
              label: {
                show: true,
                fontSize: 16,
                fontWeight: "bold",
              },
            },
            label: {
              show: true,
              position: "outside",
              color: "#fff",
              fontSize: 12,
              formatter: "{b}\n{c}头",
              lineHeight: 18,
              padding: [0, 5],
            },
            labelLine: {
              show: true,
              length: 20, // 增加第一段引导线长度
              length2: 15, // 增加第二段引导线长度
              lineStyle: {
                color: "rgba(255, 255, 255, 0.5)",
                width: 1,
              },
            },
          },
        ],
      };

      try {
        // 过滤掉无效的 series 项（防止 undefined），并确保 type 是有效的字符串
        option.series = (option.series || [])
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "pie", // 确保 type 总是存在
          }));
        if (option.series.length === 0) {
          console.warn("猪类型图表没有有效的 series");
          return;
        }
        this.pigTypeChart.setOption(option, true);
      } catch (error) {
        console.error("渲染猪类型图表失败:", error);
      }
    },

    /**
     * 渲染雷达图
     */
    renderRadarChart() {
      // 检查组件是否已销毁
      if (this.isDestroyed) {
        return;
      }

      // 检查图表实例和DOM节点
      if (
        !this.radarChart ||
        this.radarChart.isDisposed() ||
        !this.$refs.radarChart
      ) {
        console.warn("雷达图表未初始化或已销毁");
        return;
      }

      const metrics = this.environmentMetrics;

      const option = {
        backgroundColor: "transparent",
        tooltip: {},
        radar: {
          indicator: [
            { name: "温度达标", max: 100 },
            { name: "湿度达标", max: 100 },
            { name: "CO₂达标", max: 100 },
            { name: "氨气达标", max: 100 },
          ],
          splitArea: {
            areaStyle: {
              color: ["rgba(64, 158, 255, 0.1)", "rgba(64, 158, 255, 0.2)"],
            },
          },
          axisLine: {
            lineStyle: {
              color: "rgba(255, 255, 255, 0.3)",
            },
          },
          splitLine: {
            lineStyle: {
              color: "rgba(255, 255, 255, 0.3)",
            },
          },
        },
        series: [
          {
            type: "radar",
            data: [
              {
                value: [
                  metrics.temperature &&
                  metrics.temperature.standardRate !== undefined
                    ? metrics.temperature.standardRate
                    : 0,
                  metrics.humidity &&
                  metrics.humidity.standardRate !== undefined
                    ? metrics.humidity.standardRate
                    : 0,
                  metrics.co2 && metrics.co2.standardRate !== undefined
                    ? metrics.co2.standardRate
                    : 0,
                  metrics.ammonia && metrics.ammonia.standardRate !== undefined
                    ? metrics.ammonia.standardRate
                    : 0,
                ],
                name: "达标率",
                areaStyle: {
                  color: "rgba(103, 194, 58, 0.3)",
                },
                lineStyle: {
                  color: "#67C23A",
                  width: 2,
                },
                itemStyle: {
                  color: "#67C23A",
                },
              },
            ],
          },
        ],
      };

      try {
        // 确保 series 配置完整
        if (
          !option.series ||
          !Array.isArray(option.series) ||
          option.series.length === 0
        ) {
          console.warn("雷达图 series 配置无效");
          return;
        }

        const firstSeries = option.series[0];
        if (
          !firstSeries ||
          firstSeries.type !== "radar" ||
          !firstSeries.data ||
          !Array.isArray(firstSeries.data)
        ) {
          console.warn("雷达图 series 数据格式不正确");
          return;
        }

        // 确保数据不为空
        if (firstSeries.data.length === 0) {
          console.warn("雷达图数据为空，使用默认值");
          firstSeries.data = [
            {
              value: [0, 0, 0, 0],
              name: "达标率",
            },
          ];
        }

        // 过滤掉无效的 series 项（防止 undefined），并确保 type 是有效的字符串
        option.series = (option.series || [])
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "radar", // 确保 type 总是存在
          }));
        if (option.series.length === 0) {
          console.warn("雷达图没有有效的 series");
          return;
        }

        this.radarChart.setOption(option, true);
      } catch (error) {
        console.error("渲染雷达图表失败:", error);
      }
    },

    /**
     * 渲染趋势图
     */
    renderTrendChart() {
      // 检查组件是否已销毁
      if (this.isDestroyed) {
        return;
      }

      // 检查图表实例和DOM节点
      if (
        !this.trendChart ||
        this.trendChart.isDisposed() ||
        !this.$refs.trendChart
      ) {
        console.warn("趋势图表未初始化或已销毁");
        return;
      }

      const data = this.trendData[this.trendType] || [];
      let xData = (data || []).map((item) => item?.time || "");
      let yData = (data || []).map((item) => item?.value ?? 0);

      // 如果没有数据，显示默认空数据
      if (!xData || xData.length === 0) {
        xData = ["暂无数据"];
        yData = [0];
      }

      // 确保数据数组不为空
      if (!yData || yData.length === 0) {
        yData = [0];
      }

      if (process.env.NODE_ENV === "development") {
        console.log("趋势图数据:", {
          type: this.trendType,
          count: xData.length,
        });
      }

      const option = {
        backgroundColor: "transparent",
        tooltip: {
          trigger: "axis",
        },
        grid: {
          left: "5%",
          right: "5%",
          bottom: "5%",
          top: "10%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          data: xData || [],
          axisLine: {
            lineStyle: { color: "rgba(255, 255, 255, 0.3)" },
          },
          axisLabel: {
            color: "#fff",
            rotate: 45,
          },
        },
        yAxis: {
          type: "value",
          name: this.trendType === "temperature" ? "温度(℃)" : "湿度(%)",
          nameTextStyle: { color: "#fff" },
          axisLine: {
            lineStyle: { color: "rgba(255, 255, 255, 0.3)" },
          },
          axisLabel: {
            color: "#fff",
          },
          splitLine: {
            lineStyle: {
              color: "rgba(255, 255, 255, 0.1)",
            },
          },
        },
        series: [
          {
            data: yData || [],
            type: "line",
            smooth: true,
            itemStyle: {
              color: this.trendType === "temperature" ? "#E6A23C" : "#409EFF",
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color:
                    this.trendType === "temperature"
                      ? "rgba(230, 162, 60, 0.5)"
                      : "rgba(64, 158, 255, 0.5)",
                },
                {
                  offset: 1,
                  color: "rgba(64, 158, 255, 0.05)",
                },
              ]),
            },
          },
        ],
      };

      try {
        // 确保 series 数组是有效的，过滤掉无效的项
        if (
          !option.series ||
          !Array.isArray(option.series) ||
          option.series.length === 0
        ) {
          console.warn("趋势图 series 配置无效");
          return;
        }

        // 过滤掉无效的 series 项（防止 undefined），并确保 type 是有效的字符串
        option.series = option.series
          .filter(
            (item) =>
              item &&
              item !== null &&
              item !== undefined &&
              item.type &&
              typeof item.type === "string",
          )
          .map((item) => ({
            ...item,
            type: item.type || "line", // 确保 type 总是存在
            data: item.data || [], // 确保 data 是数组
          }));

        if (option.series.length === 0) {
          console.warn("趋势图没有有效的 series");
          return;
        }

        // 使用 notMerge: false (merge 模式)，避免覆盖其他有效配置
        this.trendChart.setOption(option, false);
      } catch (error) {
        console.error("渲染趋势图表失败:", error);
      }
    },

    /**
     * 更新时间
     */
    updateTime() {
      // 如果组件已销毁，停止更新时间
      if (this.isDestroyed) {
        return;
      }

      const now = new Date();
      this.currentDate = now.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        weekday: "long",
      });
      this.currentTime = now.toLocaleTimeString("zh-CN", {
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false,
      });

      // 只在组件未销毁时继续更新
      if (!this.isDestroyed) {
        setTimeout(() => this.updateTime(), 1000);
      }
    },

    /**
     * 开始自动刷新
     */
    startAutoRefresh() {
      this.autoRefreshTimer = setInterval(() => {
        // 只在组件未销毁时刷新
        if (!this.isDestroyed) {
          this.loadData();
        }
      }, this.refreshInterval * 1000);
    },

    /**
     * 停止自动刷新
     */
    stopAutoRefresh() {
      if (this.autoRefreshTimer) {
        clearInterval(this.autoRefreshTimer);
        this.autoRefreshTimer = null;
      }
    },

    /**
     * 返回主页
     */
    goBack() {
      this.$router.push("/home");
    },

    /**
     * 生成粒子样式
     */
    getParticleStyle(index) {
      const size = Math.random() * 4 + 2;
      const x = Math.random() * 100;
      const y = Math.random() * 100;
      const duration = Math.random() * 20 + 10;
      const delay = Math.random() * 5;

      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${x}%`,
        top: `${y}%`,
        animationDuration: `${duration}s`,
        animationDelay: `${delay}s`,
      };
    },

    /**
     * 提交 Hadoop 任务（fire-and-forget，不阻塞界面）
     */
    submitHadoopJobFire() {
      try {
        // 不 await，fire-and-forget 模式
        submitHadoopJob()
          .then((res) => {
            if (process.env.NODE_ENV === "development") {
              console.log("✅ Hadoop 任务已提交:", res.data);
            }
          })
          .catch((err) => {
            // 忽略错误，但在控制台记录以便排查
            console.warn("⚠️  Hadoop 任务提交失败（已忽略）:", err.message || err);
          });
      } catch (err) {
        console.warn("⚠️  Hadoop 任务提交异常（已忽略）:", err);
      }
    },
  },
};
</script>

<style scoped>
.big-screen-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  font-family: "Microsoft YaHei", Arial, sans-serif;
  position: relative;
  background: linear-gradient(
    135deg,
    #0a0e27 0%,
    #0d1b2a 30%,
    #162338 60%,
    #1b2838 100%
  );
  background-attachment: fixed;
}

/* ========== 动态背景装饰 ========== */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

/* 网格背景 */
.bg-grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(64, 158, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(64, 158, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

/* 粒子效果 */
.bg-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  background: radial-gradient(
    circle,
    rgba(64, 158, 255, 0.9) 0%,
    rgba(52, 211, 153, 0.7) 30%,
    rgba(64, 158, 255, 0) 70%
  );
  border-radius: 50%;
  animation: float linear infinite;
  box-shadow:
    0 0 10px rgba(64, 158, 255, 0.6),
    0 0 20px rgba(64, 158, 255, 0.4);
}

@keyframes float {
  0% {
    transform: translate(0, 0) scale(0.5);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  50% {
    transform: translate(50px, -100px) scale(1.8);
    opacity: 1;
    box-shadow:
      0 0 20px rgba(64, 158, 255, 0.8),
      0 0 40px rgba(64, 158, 255, 0.6);
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translate(100px, -200px) scale(0.5);
    opacity: 0;
  }
}

/* 渐变光效1 */
.bg-gradient-1 {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 800px;
  height: 800px;
  background: radial-gradient(
    circle,
    rgba(102, 126, 234, 0.15),
    transparent 70%
  );
  animation: rotate 30s linear infinite;
}

/* 渐变光效2 */
.bg-gradient-2 {
  position: absolute;
  bottom: -50%;
  left: -20%;
  width: 800px;
  height: 800px;
  background: radial-gradient(
    circle,
    rgba(118, 75, 162, 0.15),
    transparent 70%
  );
  animation: rotate 25s linear infinite reverse;
}

/* 渐变光效3 */
.bg-gradient-3 {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 1000px;
  height: 1000px;
  transform: translate(-50%, -50%);
  background: radial-gradient(
    circle,
    rgba(52, 211, 153, 0.08),
    transparent 60%
  );
  animation: pulse-glow 8s ease-in-out infinite;
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes pulse-glow {
  0%,
  100% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

/* 光束效果 */
.light-beam {
  position: absolute;
  width: 2px;
  height: 100%;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    rgba(64, 158, 255, 0.6) 50%,
    transparent 100%
  );
  animation: beam-move 4s ease-in-out infinite;
  opacity: 0;
}

.light-beam-1 {
  left: 20%;
  animation-delay: 0s;
}

.light-beam-2 {
  left: 50%;
  animation-delay: 1.3s;
}

.light-beam-3 {
  left: 80%;
  animation-delay: 2.6s;
}

@keyframes beam-move {
  0%,
  100% {
    opacity: 0;
    transform: translateY(-100%);
  }
  10%,
  90% {
    opacity: 1;
  }
  50% {
    transform: translateY(0%);
  }
}

/* 扫描线效果 */
.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(
    to right,
    transparent 0%,
    rgba(52, 211, 153, 0.8) 50%,
    transparent 100%
  );
  animation: scan 6s linear infinite;
  box-shadow: 0 0 20px rgba(52, 211, 153, 0.8);
}

@keyframes scan {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(100vh);
  }
}

/* 顶部标题栏 */
.screen-header {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  background: linear-gradient(
    to right,
    rgba(11, 42, 110, 0.95),
    rgba(11, 42, 110, 0.85)
  );
  border-bottom: 2px solid rgba(64, 158, 255, 0.5);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left .logo {
  height: 50px;
  border-radius: 8px;
}

.back-button {
  background: rgba(64, 158, 255, 0.2) !important;
  border: 1px solid rgba(64, 158, 255, 0.5) !important;
  color: #fff !important;
  transition: all 0.3s;
}

.back-button:hover {
  background: rgba(64, 158, 255, 0.4) !important;
  border-color: #409eff !important;
  transform: translateX(-3px);
}

.refresh-button {
  background: rgba(103, 194, 58, 0.2) !important;
  border: 1px solid rgba(103, 194, 58, 0.5) !important;
  color: #fff !important;
  transition: all 0.3s;
}

.refresh-button:hover {
  background: rgba(103, 194, 58, 0.4) !important;
  border-color: #67c23a !important;
  transform: scale(1.05);
}

.header-center {
  text-align: center;
}

.main-title {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(
    135deg,
    #fff 0%,
    #409eff 25%,
    #52c41a 50%,
    #409eff 75%,
    #fff 100%
  );
  background-size: 300% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  text-shadow: 0 0 20px rgba(64, 158, 255, 0.5);
  animation: shimmer 4s linear infinite;
  letter-spacing: 3px;
  position: relative;
  filter: drop-shadow(0 0 30px rgba(64, 158, 255, 0.6))
    drop-shadow(0 0 60px rgba(64, 158, 255, 0.4));
}

.main-title::after {
  content: attr(data-text);
  position: absolute;
  left: 0;
  top: 0;
  z-index: -1;
  background: linear-gradient(135deg, #409eff, #52c41a, #409eff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: blur(20px);
  opacity: 0.6;
}

@keyframes shimmer {
  0% {
    background-position: 0% center;
  }
  100% {
    background-position: 300% center;
  }
}

.sub-title {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 5px 0 0;
  letter-spacing: 2px;
}

.time-display {
  text-align: right;
}

.time-display .date {
  font-size: 16px;
  color: #fff;
}

.time-display .time {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-top: 5px;
}

/* 主体内容区 */
.screen-body {
  flex: 1;
  display: flex;
  padding: 12px;
  gap: 12px;
  overflow: hidden;
  position: relative;
  z-index: 5;
  padding-bottom: 12px; /* 底部也保持一致的间距 */
}

.left-panel,
.right-panel {
  width: 28%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 面板样式 */
.panel-item {
  background: linear-gradient(
    135deg,
    rgba(11, 42, 110, 0.7) 0%,
    rgba(15, 52, 130, 0.6) 50%,
    rgba(11, 42, 110, 0.7) 100%
  );
  border: 1px solid rgba(64, 158, 255, 0.4);
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(15px);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    0 0 0 1px rgba(64, 158, 255, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  transform-style: preserve-3d;
  perspective: 1000px;
}

/* 面板边框发光效果 */
.panel-item::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12px;
  padding: 1px;
  background: linear-gradient(
    135deg,
    rgba(64, 158, 255, 0.6),
    rgba(52, 211, 153, 0.6),
    rgba(118, 75, 162, 0.6)
  );
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.4s ease;
}

/* 面板内部发光 */
.panel-item::after {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 80%;
  height: 80%;
  transform: translate(-50%, -50%);
  background: radial-gradient(
    circle,
    rgba(64, 158, 255, 0.1) 0%,
    transparent 70%
  );
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
  border-radius: 50%;
}

.panel-item:hover {
  transform: translateY(-4px) scale(1.01);
  border-color: rgba(64, 158, 255, 0.8);
  box-shadow:
    0 16px 48px rgba(64, 158, 255, 0.3),
    0 0 80px rgba(64, 158, 255, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.2),
    0 0 0 2px rgba(64, 158, 255, 0.2);
}

.panel-item:hover::before {
  opacity: 1;
  animation: border-flow 3s linear infinite;
}

.panel-item:hover::after {
  opacity: 1;
}

@keyframes border-flow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.large-panel {
  flex: 1;
}

.panel-title {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: linear-gradient(
    135deg,
    rgba(64, 158, 255, 0.2) 0%,
    rgba(118, 75, 162, 0.15) 50%,
    rgba(52, 211, 153, 0.15) 100%
  );
  border-bottom: 2px solid transparent;
  border-image: linear-gradient(
      to right,
      rgba(64, 158, 255, 0.5),
      rgba(52, 211, 153, 0.5),
      rgba(118, 75, 162, 0.5)
    )
    1;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.2);
}

/* 标题扫光效果 */
.panel-title::after {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.2) 50%,
    transparent 100%
  );
  animation: title-slide 4s ease-in-out infinite;
  transform: skewX(-20deg);
}

@keyframes title-slide {
  0%,
  100% {
    left: -100%;
  }
  50% {
    left: 150%;
  }
}

.title-text {
  font-size: 17px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
  text-shadow:
    0 0 10px rgba(64, 158, 255, 0.8),
    0 2px 15px rgba(64, 158, 255, 0.5),
    0 4px 20px rgba(64, 158, 255, 0.3);
  letter-spacing: 0.5px;
}

.title-text::before {
  content: "";
  width: 4px;
  height: 18px;
  background: linear-gradient(to bottom, #409eff 0%, #52c41a 50%, #667eea 100%);
  margin-right: 12px;
  border-radius: 2px;
  box-shadow:
    0 0 15px rgba(64, 158, 255, 0.8),
    0 0 30px rgba(64, 158, 255, 0.5);
  animation: bar-pulse 2s ease-in-out infinite;
}

@keyframes bar-pulse {
  0%,
  100% {
    transform: scaleY(1);
    box-shadow:
      0 0 15px rgba(64, 158, 255, 0.8),
      0 0 30px rgba(64, 158, 255, 0.5);
  }
  50% {
    transform: scaleY(1.2);
    box-shadow:
      0 0 20px rgba(64, 158, 255, 1),
      0 0 40px rgba(64, 158, 255, 0.8);
  }
}

.update-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.panel-content {
  flex: 1;
  padding: 10px;
  overflow: auto;
}

/* 概览统计卡片 */
.overview-panel {
  flex-shrink: 0;
  min-height: 200px;
}

.overview-panel .panel-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.stat-card {
  background: linear-gradient(
    135deg,
    rgba(64, 158, 255, 0.1) 0%,
    rgba(118, 75, 162, 0.08) 50%,
    rgba(52, 211, 153, 0.08) 100%
  );
  border: 1px solid rgba(64, 158, 255, 0.3);
  border-radius: 10px;
  padding: 14px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

/* 卡片渐变背景 */
.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(64, 158, 255, 0.15) 50%,
    transparent 100%
  );
  transition: left 0.6s ease;
}

/* 卡片边缘光晕 */
.stat-card::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 10px;
  box-shadow: inset 0 0 20px rgba(64, 158, 255, 0);
  transition: box-shadow 0.4s ease;
}

.stat-card:hover {
  transform: translateX(5px) scale(1.02);
  border-color: rgba(64, 158, 255, 0.6);
  box-shadow:
    0 8px 25px rgba(64, 158, 255, 0.3),
    0 0 30px rgba(64, 158, 255, 0.15);
}

.stat-card:hover::before {
  left: 100%;
}

.stat-card:hover::after {
  box-shadow: inset 0 0 30px rgba(64, 158, 255, 0.2);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  box-shadow:
    0 8px 20px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.3),
    0 0 20px rgba(64, 158, 255, 0.3);
  position: relative;
  z-index: 1;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 图标呼吸光晕 */
.stat-icon::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  transform: translate(-50%, -50%);
  border-radius: 12px;
  background: inherit;
  opacity: 0.5;
  filter: blur(10px);
  animation: icon-breathe 2s ease-in-out infinite;
}

@keyframes icon-breathe {
  0%,
  100% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0.3;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.6;
  }
}

.stat-card:hover .stat-icon {
  transform: scale(1.15) rotate(8deg);
  box-shadow:
    0 12px 30px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.4),
    0 0 40px rgba(64, 158, 255, 0.6);
}

.stat-info {
  flex: 1;
  position: relative;
  z-index: 1;
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 6px;
  font-weight: 500;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  text-shadow:
    0 0 10px rgba(64, 158, 255, 0.8),
    0 0 20px rgba(64, 158, 255, 0.6),
    0 2px 4px rgba(0, 0, 0, 0.5);
  font-family: "Arial", "Microsoft YaHei", sans-serif;
  background: linear-gradient(135deg, #fff 0%, #409eff 50%, #fff 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: value-shimmer 3s linear infinite;
  letter-spacing: 1px;
}

@keyframes value-shimmer {
  0% {
    background-position: 0% center;
  }
  100% {
    background-position: 200% center;
  }
}

.stat-unit {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-left: 5px;
  font-weight: 500;
}

/* 图表容器 */
.chart-container {
  width: 100%;
  height: 100%;
  min-height: 200px;
}

/* 包含饼图的面板固定高度 */
.left-panel > .panel-item:nth-child(2) {
  flex: 1;
  min-height: 280px;
}

/* 饼图容器特殊优化 */
.left-panel > .panel-item:nth-child(2) .panel-content {
  min-height: 240px;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 环境质量 */
/* 环境质量评价面板 */
.left-panel > .panel-item:nth-child(3) {
  flex: 1;
  min-height: 200px;
}

.env-quality {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quality-score {
  display: flex;
  align-items: center;
  gap: 15px;
}

.score-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 3px solid;
}

.score-circle.level-I {
  border-color: #67c23a;
}
.score-circle.level-II {
  border-color: #409eff;
}
.score-circle.level-III {
  border-color: #e6a23c;
}
.score-circle.level-IV {
  border-color: #f56c6c;
}

.score-number {
  font-size: 26px;
  font-weight: bold;
  color: #fff;
}

.score-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.quality-info {
  flex: 1;
}

.quality-level {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 4px;
}

.quality-grade {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.quality-suggestions {
  padding: 10px 12px;
  background: rgba(230, 162, 60, 0.1);
  border-left: 3px solid #e6a23c;
  border-radius: 4px;
  color: #fff;
  font-size: 12px;
}

/* 生长预测 */
/* 生长预测面板 */
.right-panel > .panel-item:nth-child(1) {
  flex-shrink: 0;
  min-height: 200px;
}

/* 环境指标雷达图面板 */
.right-panel > .panel-item:nth-child(2) {
  flex: 1;
  min-height: 240px;
}

/* 疾病风险预警面板（最后一个）*/
.right-panel > .panel-item:nth-child(3) {
  flex-shrink: 0;
  min-height: 260px;
}

/* 确保疾病风险面板内容区域有足够空间 */
.right-panel > .panel-item:nth-child(3) .panel-content {
  min-height: 200px;
}

.growth-prediction {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.prediction-item {
  text-align: center;
  padding: 10px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 6px;
}

.prediction-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 10px;
}

.prediction-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.prediction-value .unit {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-left: 5px;
}

.trend-indicator {
  grid-column: 1 / -1;
  padding: 10px;
  text-align: center;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
}

.trend-indicator.上升 {
  background: rgba(103, 194, 58, 0.2);
  color: #67c23a;
}

/* 监测点网格 */
.monitor-points-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.monitor-point-card {
  background: rgba(64, 158, 255, 0.05);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 6px;
  padding: 12px;
}

.monitor-point-card.abnormal {
  border-color: rgba(245, 108, 108, 0.5);
  background: rgba(245, 108, 108, 0.05);
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.point-name {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
}

.point-location {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 10px;
}

.point-metrics {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.metric-item {
  display: flex;
  flex-direction: column;
  font-size: 12px;
}

.metric-label {
  color: rgba(255, 255, 255, 0.6);
}

.metric-value {
  color: #409eff;
  font-weight: bold;
  margin-top: 2px;
}

/* 疾病风险 */
.risk-warning {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 5px;
}

.risk-level {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px 15px;
  border-radius: 8px;
}

.risk-level.risk-低 {
  background: rgba(103, 194, 58, 0.1);
}
.risk-level.risk-中 {
  background: rgba(230, 162, 60, 0.1);
}
.risk-level.risk-较高,
.risk-level.risk-高 {
  background: rgba(245, 108, 108, 0.1);
}

.risk-icon {
  font-size: 32px;
}

.risk-level.risk-低 .risk-icon {
  color: #67c23a;
}
.risk-level.risk-中 .risk-icon {
  color: #e6a23c;
}
.risk-level.risk-较高 .risk-icon,
.risk-level.risk-高 .risk-icon {
  color: #f56c6c;
}

.risk-info {
  flex: 1;
}

.risk-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 5px;
}

.risk-probability {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.risk-advice {
  padding: 15px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 6px;
}

.advice-title {
  font-size: 14px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.advice-content {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
}

/* 滚动条样式 */
.panel-content::-webkit-scrollbar {
  width: 6px;
}

.panel-content::-webkit-scrollbar-thumb {
  background: rgba(64, 158, 255, 0.3);
  border-radius: 3px;
}

.panel-content::-webkit-scrollbar-track {
  background: transparent;
}
</style>

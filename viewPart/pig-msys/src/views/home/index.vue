<template>
  <div>
    <h1 style="width: 100%; text-align: center">
      欢迎来到基于Hadoop的生猪智慧养殖系统
    </h1>

    <!-- 可视化大屏快捷入口 -->
    <div class="dashboard-entrance">
      <el-card
        class="big-screen-card"
        shadow="hover"
        @click.native="goToBigScreen"
      >
        <div class="card-content">
          <div class="icon-wrapper">
            <i class="el-icon-monitor"></i>
          </div>
          <div class="text-wrapper">
            <h2>🎯 智慧养猪可视化大屏</h2>
            <p>实时监控 · Hadoop大数据分析 · AI智能预测</p>
            <el-button type="primary" size="" icon="el-icon-full-screen">
              进入大屏模式
            </el-button>
          </div>
          <div class="features">
            <div class="feature-item">
              <i class="el-icon-data-analysis"></i>
              <span>实时数据</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-share"></i>
              <span>Hadoop分析</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-cpu"></i>
              <span>AI预测</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!--放置图表-->
    <div ref="chartDemodiv" style="width: 100%; height: 500px"></div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import { getTypeSum } from "../../api/piginfo";
export default {
  name: "index",
  data() {
    return {
      chartDemo: null,
      pigTypeData: {
        pigType: [],
        pigTypeSum: [],
      },
    };
  },
  computed: {
    options() {
      const option = {
        title: {
          text: "各类猪的量对比",
          left: "center",
          textStyle: {
            color: "#12acf3",
          },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          top: 30,
          data: [
            {
              name: "拥有数量",
              icon: "circle",
              textStyle: {
                color: "red",
                fontFamily: "微软雅黑",
                fontSize: 16,
              },
            },
          ],
        },
        xAxis: {
          type: "category",
          data: this.pigTypeData.pigType || [],
        },
        yAxis: {
          type: "value",
          min: 100,
          interval: 50,
        },
        series: [
          {
            name: "拥有数量",
            data: this.pigTypeData.pigTypeSum || [],
            type: "bar",
            color: "#07f6f6",
          },
        ]
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "bar", // 确保 type 总是存在
          })),
      };
      return option;
    },
  },
  watch: {
    options(newVal, oldVal) {
      if (newVal !== oldVal) {
        // 检查图表实例是否存在
        if (this.chartDemo && !this.chartDemo.isDisposed()) {
          try {
            // 确保 series 是有效的数组，过滤掉 undefined/null 项并确保 type 是字符串
            const safeOptions = {
              ...this.options,
              series: (this.options.series || [])
                .filter(
                  (item) => item && item.type && typeof item.type === "string",
                )
                .map((item) => ({
                  ...item,
                  type: item.type || "bar", // 确保 type 总是存在
                })),
            };
            // 如果 series 为空，使用默认配置
            if (!safeOptions.series || safeOptions.series.length === 0) {
              safeOptions.series = [
                {
                  name: "拥有数量",
                  data: [],
                  type: "bar",
                  color: "#07f6f6",
                },
              ];
            }
            // 使用 notMerge: false 来合并配置，避免完全替换导致的问题
            this.chartDemo.setOption(safeOptions, false);
          } catch (error) {
            console.error("更新图表配置失败:", error);
          }
        }
      }
    },
  },
  created() {
    getTypeSum()
      .then((response) => {
        const resp = response.data;
        if (resp.flag) {
          this.pigTypeData.pigType = resp.data.pigType || [];
          this.pigTypeData.pigTypeSum = resp.data.pigTypeSum || [];
        } else {
          this.$message({
            message: resp.message || "获取数据失败",
            type: "warning",
          });
        }
      })
      .catch((error) => {
        console.error("获取猪类型统计失败:", error);
        this.$message.error(
          "获取数据失败：" + (error.response?.data?.message || error.message),
        );
      });
  },
  mounted() {
    this.drawLine();
  },
  beforeUnmount() {
    // Vue 3 生命周期钩子，组件卸载前清理
    this.destroyChart();
  },
  methods: {
    drawLine() {
      try {
        // 检查 DOM 元素是否存在
        if (!this.$refs.chartDemodiv) {
          console.warn("图表容器DOM节点未就绪");
          this.$nextTick(() => {
            if (this.$refs.chartDemodiv) {
              this.drawLine();
            }
          });
          return;
        }

        this.chartDemo = echarts.init(this.$refs.chartDemodiv);
        // 确保 options.series 是有效的数组
        const safeOptions = {
          ...this.options,
          series: (this.options.series || [])
            .filter(
              (item) => item && item.type && typeof item.type === "string",
            )
            .map((item) => ({
              ...item,
              type: item.type || "bar", // 确保 type 总是存在
            })),
        };
        // 如果 series 为空，添加默认配置
        if (!safeOptions.series || safeOptions.series.length === 0) {
          safeOptions.series = [
            {
              name: "拥有数量",
              data: [],
              type: "bar",
              color: "#07f6f6",
            },
          ];
        }
        // 首次设置使用 notMerge: true，后续更新使用 false 来合并
        this.chartDemo.setOption(safeOptions, true);
        window.addEventListener("resize", this.handleResize);
      } catch (error) {
        console.error("初始化图表失败:", error);
      }
    },
    handleResize() {
      // 检查图表是否存在且未销毁
      if (this.chartDemo && !this.chartDemo.isDisposed()) {
        try {
          this.chartDemo.resize();
        } catch (error) {
          console.error("图表 resize 失败:", error);
          // 如果 resize 失败，尝试修复配置
          try {
            const currentOption = this.chartDemo.getOption();
            let allSeries = [];
            if (currentOption && currentOption.series) {
              if (Array.isArray(currentOption.series[0])) {
                allSeries = currentOption.series[0];
              } else {
                allSeries = currentOption.series;
              }
              const validSeries = allSeries.filter(
                (item) => item && item.type && typeof item.type === "string",
              );
              if (validSeries.length > 0) {
                this.chartDemo.setOption(
                  {
                    series: validSeries.map((item) => ({
                      ...item,
                      type: item.type || "bar",
                    })),
                  },
                  true,
                );
                this.chartDemo.resize();
              }
            }
          } catch (fixError) {
            console.error("修复图表配置失败:", fixError);
          }
        }
      }
    },
    destroyChart() {
      // 移除事件监听
      window.removeEventListener("resize", this.handleResize);
      // 销毁图表实例
      if (this.chartDemo && !this.chartDemo.isDisposed()) {
        this.chartDemo.dispose();
        this.chartDemo = null;
      }
    },
    goToBigScreen() {
      this.$router.push("/big-screen");
    },
  },
};
</script>

<style scoped>
.dashboard-entrance {
  margin: 30px auto;
  max-width: 1200px;
}

.big-screen-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.big-screen-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(102, 126, 234, 0.4);
}

.big-screen-card >>> .el-card__body {
  padding: 40px;
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 30px;
}

.icon-wrapper {
  font-size: 80px;
  color: rgba(255, 255, 255, 0.9);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.text-wrapper {
  flex: 1;
  text-align: left;
}

.text-wrapper h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: bold;
  color: white;
}

.text-wrapper p {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
}

.features {
  display: flex;
  gap: 20px;
  align-items: center;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  min-width: 100px;
}

.feature-item i {
  font-size: 30px;
  color: white;
}

.feature-item span {
  font-size: 14px;
  color: white;
}
</style>

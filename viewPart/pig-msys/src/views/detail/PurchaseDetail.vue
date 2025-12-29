<template>
  <div>
    <div
      ref="chartDemodiv"
      style="width: 100%; height: 500px; margin-top: 50px"
    ></div>
  </div>
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "PurchaseDetail",
  data() {
    return {
      chartDemo: null,
    };
  },
  mounted() {
    this.drawLine();
  },
  beforeUnmount() {
    // 清理事件监听器
    if (this.handleResize) {
      window.removeEventListener("resize", this.handleResize);
    }
    // 销毁图表实例
    if (this.chartDemo && !this.chartDemo.isDisposed()) {
      this.chartDemo.dispose();
      this.chartDemo = null;
    }
  },
  methods: {
    drawLine() {
      try {
        if (!this.$refs["chartDemodiv"]) {
          console.warn("图表容器不存在");
          return;
        }
        this.chartDemo = echarts.init(this.$refs["chartDemodiv"]);
      } catch (error) {
        console.error("初始化图表失败:", error);
        return;
      }

      const option = {
        title: {
          text: "各种猪新进入农场的统计",
          left: "center",
          textStyle: {
            color: "#409EFF",
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
              name: "入猪圈的数量",
              icon: "square",
              textStyle: {
                color: "#c513e5",
                fontFamily: "微软雅黑",
                fontSize: 18,
              },
            },
          ],
        },
        xAxis: {
          type: "category",
          data: ["白猪", "黑猪", "花猪"],
        },
        yAxis: {
          type: "value",
          min: 100,
          interval: 50,
        },
        series: [
          {
            name: "入猪圈的数量",
            data: [250, 200, 300],
            type: "line",
            smooth: true,
          },
        ].filter((item) => item && item.type),
      };
      try {
        // 确保 series 是有效的数组
        if (
          !option.series ||
          !Array.isArray(option.series) ||
          option.series.length === 0
        ) {
          console.warn("图表配置无效：series 为空");
          return;
        }
        // 过滤掉无效的 series 项，确保 type 是有效的字符串
        option.series = (option.series || [])
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "line", // 确保 type 总是存在
          }));
        if (option.series.length === 0) {
          console.warn("图表配置无效：没有有效的 series");
          return;
        }
        this.chartDemo.setOption(option, true);

        // 创建防抖的 resize 处理函数
        this.handleResize = () => {
          if (this.chartDemo && !this.chartDemo.isDisposed()) {
            try {
              // 直接调用 resize，ECharts 会自动处理
              this.chartDemo.resize();
            } catch (error) {
              // 如果 resize 失败，尝试重新设置配置
              console.error("图表 resize 失败，尝试修复:", error);
              try {
                const currentOption = this.chartDemo.getOption();
                // ECharts getOption() 可能返回嵌套数组，需要扁平化处理
                let allSeries = [];
                if (currentOption && currentOption.series) {
                  if (Array.isArray(currentOption.series[0])) {
                    // 嵌套数组格式
                    allSeries = currentOption.series[0];
                  } else {
                    allSeries = currentOption.series;
                  }
                  // 过滤并确保每个 series 都有有效的 type
                  const validSeries = allSeries.filter(
                    (item) =>
                      item && item.type && typeof item.type === "string",
                  );
                  if (
                    validSeries.length > 0 &&
                    validSeries.length !== allSeries.length
                  ) {
                    // 重新设置配置
                    this.chartDemo.setOption(
                      {
                        series: validSeries.map((item) => ({
                          ...item,
                          type: item.type || "line",
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
        };
        window.addEventListener("resize", this.handleResize);
      } catch (error) {
        console.error("设置图表配置失败:", error);
      }
    },
  },
};
</script>

<style scoped></style>

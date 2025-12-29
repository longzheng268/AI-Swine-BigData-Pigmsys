<template>
  <div class="visualization-container">
    <h2>数据可视化分析</h2>

    <!-- 时间选择器 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleDateChange"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadChartData">刷新数据</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图表展示区 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 温度趋势图 -->
      <el-col :span="12">
        <el-card>
          <template #header>温度变化趋势</template>
          <div ref="temperatureChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 湿度趋势图 -->
      <el-col :span="12">
        <el-card>
          <template #header>湿度变化趋势</template>
          <div ref="humidityChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- CO2浓度柱状图 -->
      <el-col :span="12">
        <el-card>
          <template #header>CO₂浓度对比</template>
          <div ref="co2Chart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 氨气浓度柱状图 -->
      <el-col :span="12">
        <el-card>
          <template #header>氨气浓度对比</template>
          <div ref="nh3Chart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 环境指标雷达图 -->
      <el-col :span="12">
        <el-card>
          <template #header>环境综合评价（雷达图）</template>
          <div ref="radarChart" style="height: 400px"></div>
        </el-card>
      </el-col>

      <!-- 异常数据饼图 -->
      <el-col :span="12">
        <el-card>
          <template #header>数据异常统计</template>
          <div ref="pieChart" style="height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 监测点分布地图（简化版） -->
    <el-card style="margin-top: 20px">
      <template #header>监测点分布</template>
      <el-table :data="monitorPoints" border>
        <el-table-column
          prop="monitorPoint"
          label="监测点"
          width="150"
        ></el-table-column>
        <el-table-column
          prop="monitorLocation"
          label="位置"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column label="温度" width="100">
          <template #default="scope">
            <span
              :style="{ color: getTemperatureColor(scope.row.temperature) }"
            >
              {{ scope.row.temperature }}℃
            </span>
          </template>
        </el-table-column>
        <el-table-column label="湿度" width="100">
          <template #default="scope">
            <span :style="{ color: getHumidityColor(scope.row.humidity) }">
              {{ scope.row.humidity }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column label="CO₂" width="100">
          <template #default="scope">
            <span :style="{ color: getCo2Color(scope.row.co2Concentration) }">
              {{ scope.row.co2Concentration }}ppm
            </span>
          </template>
        </el-table-column>
        <el-table-column label="氨气" width="100">
          <template #default="scope">
            <span :style="{ color: getNh3Color(scope.row.nh3Concentration) }">
              {{ scope.row.nh3Concentration }}ppm
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag
              :type="scope.row.isAbnormal === 1 ? 'danger' : 'success'"
              size="small"
            >
              {{ scope.row.isAbnormal === 1 ? "异常" : "正常" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="collectTime"
          label="采集时间"
          width="160"
        ></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import * as echarts from "echarts";
import { searchEnvironmentData, getLatestData } from "@/api/environment";

export default {
  name: "DataVisualization",
  data() {
    return {
      dateRange: null,
      chartData: [],
      monitorPoints: [],
      temperatureChart: null,
      humidityChart: null,
      co2Chart: null,
      nh3Chart: null,
      radarChart: null,
      pieChart: null,
    };
  },
  mounted() {
    this.initCharts();
    this.loadChartData();
  },
  beforeUnmount() {
    // Vue 3 生命周期钩子，组件卸载前清理
    this.destroyCharts();
  },
  methods: {
    initCharts() {
      try {
        // 检查 DOM 元素是否存在
        if (
          !this.$refs.temperatureChart ||
          !this.$refs.humidityChart ||
          !this.$refs.co2Chart ||
          !this.$refs.nh3Chart ||
          !this.$refs.radarChart ||
          !this.$refs.pieChart
        ) {
          console.warn("图表容器DOM节点未就绪，延迟初始化");
          this.$nextTick(() => {
            if (this.$refs.temperatureChart) {
              this.initCharts();
            }
          });
          return;
        }

        this.temperatureChart = echarts.init(this.$refs.temperatureChart);
        this.humidityChart = echarts.init(this.$refs.humidityChart);
        this.co2Chart = echarts.init(this.$refs.co2Chart);
        this.nh3Chart = echarts.init(this.$refs.nh3Chart);
        this.radarChart = echarts.init(this.$refs.radarChart);
        this.pieChart = echarts.init(this.$refs.pieChart);

        window.addEventListener("resize", this.handleResize);
      } catch (error) {
        console.error("初始化图表失败:", error);
      }
    },
    destroyCharts() {
      // 移除事件监听
      window.removeEventListener("resize", this.handleResize);
      // 安全销毁图表实例
      if (this.temperatureChart && !this.temperatureChart.isDisposed()) {
        this.temperatureChart.dispose();
      }
      if (this.humidityChart && !this.humidityChart.isDisposed()) {
        this.humidityChart.dispose();
      }
      if (this.co2Chart && !this.co2Chart.isDisposed()) {
        this.co2Chart.dispose();
      }
      if (this.nh3Chart && !this.nh3Chart.isDisposed()) {
        this.nh3Chart.dispose();
      }
      if (this.radarChart && !this.radarChart.isDisposed()) {
        this.radarChart.dispose();
      }
      if (this.pieChart && !this.pieChart.isDisposed()) {
        this.pieChart.dispose();
      }
      // 重置为 null
      this.temperatureChart = null;
      this.humidityChart = null;
      this.co2Chart = null;
      this.nh3Chart = null;
      this.radarChart = null;
      this.pieChart = null;
    },
    handleResize() {
      // 安全地调整图表大小，检查每个图表是否存在且未销毁
      try {
        if (this.temperatureChart && !this.temperatureChart.isDisposed()) {
          this.temperatureChart.resize();
        }
        if (this.humidityChart && !this.humidityChart.isDisposed()) {
          this.humidityChart.resize();
        }
        if (this.co2Chart && !this.co2Chart.isDisposed()) {
          this.co2Chart.resize();
        }
        if (this.nh3Chart && !this.nh3Chart.isDisposed()) {
          this.nh3Chart.resize();
        }
        if (this.radarChart && !this.radarChart.isDisposed()) {
          this.radarChart.resize();
        }
        if (this.pieChart && !this.pieChart.isDisposed()) {
          this.pieChart.resize();
        }
      } catch (error) {
        console.error("调整图表大小时出错:", error);
      }
    },
    handleDateChange() {
      this.loadChartData();
    },
    loadChartData() {
      const params = {};
      if (this.dateRange && this.dateRange.length === 2) {
        params.startTime = this.dateRange[0];
        params.endTime = this.dateRange[1];
      }

      searchEnvironmentData(1, 100, params).then((response) => {
        const resp = response.data;
        if (resp.flag && resp.data.rows) {
          this.chartData = resp.data.rows;
          this.renderCharts();
        }
      });

      // 加载最新监测点数据
      getLatestData(10).then((response) => {
        const resp = response.data;
        if (resp.flag) {
          this.monitorPoints = resp.data;
        }
      });
    },
    renderCharts() {
      // 确保图表已初始化
      if (
        !this.temperatureChart ||
        !this.humidityChart ||
        !this.co2Chart ||
        !this.nh3Chart ||
        !this.radarChart ||
        !this.pieChart
      ) {
        console.warn("图表未初始化，延迟渲染");
        // 延迟重新初始化并渲染
        this.$nextTick(() => {
          setTimeout(() => {
            // 重新尝试初始化
            this.initCharts();
            // 如果初始化成功，再次尝试渲染
            if (
              this.temperatureChart &&
              this.humidityChart &&
              this.co2Chart &&
              this.nh3Chart &&
              this.radarChart &&
              this.pieChart
            ) {
              this.renderTemperatureChart();
              this.renderHumidityChart();
              this.renderCo2Chart();
              this.renderNh3Chart();
              this.renderRadarChart();
              this.renderPieChart();
            }
          }, 100);
        });
        return;
      }

      this.renderTemperatureChart();
      this.renderHumidityChart();
      this.renderCo2Chart();
      this.renderNh3Chart();
      this.renderRadarChart();
      this.renderPieChart();
    },
    renderTemperatureChart() {
      // 检查图表实例是否存在
      if (!this.temperatureChart || this.temperatureChart.isDisposed()) {
        return;
      }

      const xData = this.chartData.map(
        (item) => item.collectTime?.substring(5, 16) || "",
      );
      const yData = this.chartData.map((item) => item.temperature || 0);

      const option = {
        tooltip: { trigger: "axis" },
        xAxis: {
          type: "category",
          data: xData,
          axisLabel: { rotate: 45 },
        },
        yAxis: { type: "value", name: "温度(℃)" },
        series: [
          {
            data: yData,
            type: "line",
            smooth: true,
            itemStyle: { color: "#409EFF" },
            areaStyle: { color: "rgba(64, 158, 255, 0.2)" },
            markLine: {
              data: [
                {
                  yAxis: 18,
                  label: { formatter: "最低标准 18℃" },
                  lineStyle: { color: "#E6A23C" },
                },
                {
                  yAxis: 28,
                  label: { formatter: "最高标准 28℃" },
                  lineStyle: { color: "#E6A23C" },
                },
              ],
            },
          },
        ],
      };

      try {
        // 确保 series 是有效的数组
        if (!option.series || !Array.isArray(option.series)) {
          console.warn("温度图表 series 配置无效");
          return;
        }
        option.series = option.series
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "line",
          }));
        if (option.series.length === 0) {
          console.warn("温度图表没有有效的 series");
          return;
        }
        this.temperatureChart.setOption(option);
      } catch (error) {
        console.error("渲染温度图表失败:", error);
      }
    },
    renderHumidityChart() {
      if (!this.humidityChart || this.humidityChart.isDisposed()) {
        return;
      }

      const xData = this.chartData.map(
        (item) => item.collectTime?.substring(5, 16) || "",
      );
      const yData = this.chartData.map((item) => item.humidity || 0);

      const option = {
        tooltip: { trigger: "axis" },
        xAxis: {
          type: "category",
          data: xData,
          axisLabel: { rotate: 45 },
        },
        yAxis: { type: "value", name: "湿度(%)" },
        series: [
          {
            data: yData,
            type: "line",
            smooth: true,
            itemStyle: { color: "#67C23A" },
            areaStyle: { color: "rgba(103, 194, 58, 0.2)" },
            markLine: {
              data: [
                { yAxis: 50, label: { formatter: "最低标准 50%" } },
                { yAxis: 70, label: { formatter: "最高标准 70%" } },
              ],
            },
          },
        ],
      };

      try {
        // 确保 series 是有效的数组
        if (!option.series || !Array.isArray(option.series)) {
          console.warn("湿度图表 series 配置无效");
          return;
        }
        option.series = option.series
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "line",
          }));
        if (option.series.length === 0) {
          console.warn("湿度图表没有有效的 series");
          return;
        }
        this.humidityChart.setOption(option);
      } catch (error) {
        console.error("渲染湿度图表失败:", error);
      }
    },
    renderCo2Chart() {
      if (!this.co2Chart || this.co2Chart.isDisposed()) {
        return;
      }

      const xData = this.chartData
        .slice(0, 10)
        .map((item) => item.monitorPoint || "");
      const yData = this.chartData
        .slice(0, 10)
        .map((item) => item.co2Concentration || 0);

      const option = {
        tooltip: { trigger: "axis" },
        xAxis: { type: "category", data: xData },
        yAxis: { type: "value", name: "CO₂浓度(ppm)" },
        series: [
          {
            data: yData,
            type: "bar",
            itemStyle: {
              color: (params) => (params.data > 600 ? "#F56C6C" : "#409EFF"),
            },
            markLine: {
              data: [{ yAxis: 600, label: { formatter: "标准上限 600ppm" } }],
            },
          },
        ],
      };

      try {
        // 确保 series 是有效的数组
        if (!option.series || !Array.isArray(option.series)) {
          console.warn("CO2图表 series 配置无效");
          return;
        }
        option.series = option.series
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "bar",
          }));
        if (option.series.length === 0) {
          console.warn("CO2图表没有有效的 series");
          return;
        }
        this.co2Chart.setOption(option);
      } catch (error) {
        console.error("渲染CO2图表失败:", error);
      }
    },
    renderNh3Chart() {
      if (!this.nh3Chart || this.nh3Chart.isDisposed()) {
        return;
      }

      const xData = this.chartData
        .slice(0, 10)
        .map((item) => item.monitorPoint || "");
      const yData = this.chartData
        .slice(0, 10)
        .map((item) => item.nh3Concentration || 0);

      const option = {
        tooltip: { trigger: "axis" },
        xAxis: { type: "category", data: xData },
        yAxis: { type: "value", name: "氨气浓度(ppm)" },
        series: [
          {
            data: yData,
            type: "bar",
            itemStyle: {
              color: (params) => (params.data > 25 ? "#F56C6C" : "#67C23A"),
            },
            markLine: {
              data: [{ yAxis: 25, label: { formatter: "标准上限 25ppm" } }],
            },
          },
        ],
      };

      try {
        // 确保 series 是有效的数组
        if (!option.series || !Array.isArray(option.series)) {
          console.warn("NH3图表 series 配置无效");
          return;
        }
        option.series = option.series
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "bar",
          }));
        if (option.series.length === 0) {
          console.warn("NH3图表没有有效的 series");
          return;
        }
        this.nh3Chart.setOption(option);
      } catch (error) {
        console.error("渲染NH3图表失败:", error);
      }
    },
    renderRadarChart() {
      if (!this.radarChart || this.radarChart.isDisposed()) {
        return;
      }

      if (this.chartData.length === 0) return;

      const avgData = this.calculateAverage();

      const option = {
        tooltip: {},
        radar: {
          indicator: [
            { name: "温度达标率", max: 100 },
            { name: "湿度达标率", max: 100 },
            { name: "CO₂达标率", max: 100 },
            { name: "氨气达标率", max: 100 },
            { name: "光照达标率", max: 100 },
          ],
        },
        series: [
          {
            type: "radar",
            data: [
              {
                value: [
                  avgData.temperatureRate || 0,
                  avgData.humidityRate || 0,
                  avgData.co2Rate || 0,
                  avgData.nh3Rate || 0,
                  avgData.lightRate || 0,
                ],
                name: "环境达标情况",
                areaStyle: { color: "rgba(64, 158, 255, 0.3)" },
              },
            ],
          },
        ],
      };

      try {
        // 确保 series 是有效的数组
        if (!option.series || !Array.isArray(option.series)) {
          console.warn("雷达图表 series 配置无效");
          return;
        }
        option.series = option.series
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "radar",
          }));
        if (option.series.length === 0) {
          console.warn("雷达图表没有有效的 series");
          return;
        }
        this.radarChart.setOption(option);
      } catch (error) {
        console.error("渲染雷达图表失败:", error);
      }
    },
    renderPieChart() {
      if (!this.pieChart || this.pieChart.isDisposed()) {
        return;
      }

      const normalCount = this.chartData.filter(
        (item) => item.isAbnormal === 0,
      ).length;
      const abnormalCount = this.chartData.filter(
        (item) => item.isAbnormal === 1,
      ).length;

      const option = {
        tooltip: { trigger: "item" },
        legend: { orient: "vertical", left: "left" },
        series: [
          {
            type: "pie",
            radius: "50%",
            data: [
              {
                value: normalCount,
                name: "正常数据",
                itemStyle: { color: "#67C23A" },
              },
              {
                value: abnormalCount,
                name: "异常数据",
                itemStyle: { color: "#F56C6C" },
              },
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
      };

      try {
        // 确保 series 是有效的数组
        if (!option.series || !Array.isArray(option.series)) {
          console.warn("饼图 series 配置无效");
          return;
        }
        option.series = option.series
          .filter((item) => item && item.type && typeof item.type === "string")
          .map((item) => ({
            ...item,
            type: item.type || "pie",
          }));
        if (option.series.length === 0) {
          console.warn("饼图没有有效的 series");
          return;
        }
        this.pieChart.setOption(option);
      } catch (error) {
        console.error("渲染饼图失败:", error);
      }
    },
    calculateAverage() {
      let tempOk = 0,
        humOk = 0,
        co2Ok = 0,
        nh3Ok = 0,
        lightOk = 0;

      this.chartData.forEach((item) => {
        if (item.temperature >= 18 && item.temperature <= 28) tempOk++;
        if (item.humidity >= 50 && item.humidity <= 70) humOk++;
        if (item.co2Concentration <= 600) co2Ok++;
        if (item.nh3Concentration <= 25) nh3Ok++;
        if (item.lightIntensity >= 200 && item.lightIntensity <= 500) lightOk++;
      });

      const total = this.chartData.length || 1;
      return {
        temperatureRate: ((tempOk / total) * 100).toFixed(1),
        humidityRate: ((humOk / total) * 100).toFixed(1),
        co2Rate: ((co2Ok / total) * 100).toFixed(1),
        nh3Rate: ((nh3Ok / total) * 100).toFixed(1),
        lightRate: ((lightOk / total) * 100).toFixed(1),
      };
    },
    getTemperatureColor(value) {
      if (value < 18 || value > 28) return "#F56C6C";
      return "#67C23A";
    },
    getHumidityColor(value) {
      if (value < 50 || value > 70) return "#F56C6C";
      return "#67C23A";
    },
    getCo2Color(value) {
      if (value > 600) return "#F56C6C";
      return "#67C23A";
    },
    getNh3Color(value) {
      if (value > 25) return "#F56C6C";
      return "#67C23A";
    },
  },
};
</script>

<style scoped>
.visualization-container {
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
</style>

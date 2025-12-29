<template>
  <div class="environment-container">
    <h2>环境监测数据管理</h2>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="监测点">
          <el-input
            v-model="searchForm.monitorPoint"
            placeholder="请输入监测点名称"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="date"
            placeholder="选择日期"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="date"
            placeholder="选择日期"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.isAbnormal"
            placeholder="请选择"
            clearable
          >
            <el-option label="正常" :value="0"></el-option>
            <el-option label="异常" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加数据</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i class="el-icon-data-line stat-icon"></i>
            <div>
              <p class="stat-value">{{ totalCount }}</p>
              <p class="stat-label">总数据量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i class="el-icon-warning stat-icon" style="color: #e6a23c"></i>
            <div>
              <p class="stat-value">{{ abnormalCount }}</p>
              <p class="stat-label">异常数据</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i class="el-icon-location stat-icon" style="color: #409eff"></i>
            <div>
              <p class="stat-value">{{ monitorPointCount }}</p>
              <p class="stat-label">监测点数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i
              class="el-icon-circle-check stat-icon"
              style="color: #67c23a"
            ></i>
            <div>
              <p class="stat-value">{{ normalRate }}%</p>
              <p class="stat-label">达标率</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card style="margin-top: 20px">
      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column
          prop="monitorPoint"
          label="监测点"
          width="120"
        ></el-table-column>
        <el-table-column
          prop="temperature"
          label="温度(℃)"
          width="100"
        ></el-table-column>
        <el-table-column
          prop="humidity"
          label="湿度(%)"
          width="100"
        ></el-table-column>
        <el-table-column
          prop="co2Concentration"
          label="CO₂(ppm)"
          width="100"
        ></el-table-column>
        <el-table-column
          prop="nh3Concentration"
          label="氨气(ppm)"
          width="100"
        ></el-table-column>
        <el-table-column
          prop="collectTime"
          label="采集时间"
          width="160"
        ></el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isAbnormal === 1 ? 'danger' : 'success'">
              {{ scope.row.isAbnormal === 1 ? "异常" : "正常" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="abnormalReason"
          label="异常原因"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)"
              >查看</el-button
            >
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount"
        style="margin-top: 20px; text-align: right"
      >
      </el-pagination>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form
        :model="formData"
        :rules="formRules"
        ref="dataForm"
        label-width="120px"
      >
        <el-form-item label="监测点" prop="monitorPoint">
          <el-input v-model="formData.monitorPoint"></el-input>
        </el-form-item>
        <el-form-item label="监测位置" prop="monitorLocation">
          <el-input v-model="formData.monitorLocation"></el-input>
        </el-form-item>
        <el-form-item label="温度(℃)" prop="temperature">
          <el-input-number
            v-model="formData.temperature"
            :precision="2"
            :min="0"
            :max="50"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="湿度(%)" prop="humidity">
          <el-input-number
            v-model="formData.humidity"
            :precision="2"
            :min="0"
            :max="100"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="CO₂浓度(ppm)" prop="co2Concentration">
          <el-input-number
            v-model="formData.co2Concentration"
            :precision="2"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="氨气浓度(ppm)" prop="nh3Concentration">
          <el-input-number
            v-model="formData.nh3Concentration"
            :precision="2"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="光照强度(lux)" prop="lightIntensity">
          <el-input-number
            v-model="formData.lightIntensity"
            :precision="2"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="采集时间" prop="collectTime">
          <el-date-picker
            v-model="formData.collectTime"
            type="datetime"
            placeholder="选择日期时间"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  searchEnvironmentData,
  addEnvironmentData,
  deleteEnvironmentData,
} from "@/api/environment";
import { ElMessage } from "element-plus";

export default {
  name: "EnvironmentData",
  data() {
    return {
      searchForm: {
        monitorPoint: "",
        startTime: null,
        endTime: null,
        isAbnormal: "", // 使用空字符串，配合 clearable 使用
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      abnormalCount: 0,
      monitorPointCount: 0,
      normalRate: 0,
      dialogVisible: false,
      dialogTitle: "添加数据",
      formData: {
        monitorPoint: "",
        monitorLocation: "",
        temperature: null,
        humidity: null,
        co2Concentration: null,
        nh3Concentration: null,
        lightIntensity: null,
        collectTime: new Date(),
      },
      formRules: {
        monitorPoint: [
          { required: true, message: "请输入监测点", trigger: "blur" },
        ],
        temperature: [
          { required: true, message: "请输入温度", trigger: "blur" },
        ],
        humidity: [{ required: true, message: "请输入湿度", trigger: "blur" }],
      },
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      const params = {
        monitorPoint: this.searchForm.monitorPoint || undefined,
        startTime: this.searchForm.startTime,
        endTime: this.searchForm.endTime,
        // 空字符串转换为 undefined，避免传空值
        isAbnormal:
          this.searchForm.isAbnormal === ""
            ? undefined
            : this.searchForm.isAbnormal,
      };

      searchEnvironmentData(this.currentPage, this.pageSize, params)
        .then((response) => {
          const resp = response.data;
          if (resp.flag) {
            this.tableData = resp.data.rows;
            this.totalCount = resp.data.total;
            this.calculateStats();
          }
        })
        .catch((error) => {
          ElMessage.error("数据加载失败");
        });
    },
    calculateStats() {
      this.abnormalCount = this.tableData.filter(
        (item) => item.isAbnormal === 1,
      ).length;
      const uniquePoints = new Set(
        this.tableData.map((item) => item.monitorPoint),
      );
      this.monitorPointCount = uniquePoints.size;
      this.normalRate =
        this.totalCount > 0
          ? (
              ((this.totalCount - this.abnormalCount) / this.totalCount) *
              100
            ).toFixed(1)
          : 0;
    },
    handleSearch() {
      this.currentPage = 1;
      this.loadData();
    },
    handleReset() {
      this.searchForm = {
        monitorPoint: "",
        startTime: null,
        endTime: null,
        isAbnormal: "", // 使用空字符串
      };
      this.handleSearch();
    },
    handleAdd() {
      this.dialogTitle = "添加数据";
      this.formData = {
        monitorPoint: "",
        monitorLocation: "",
        temperature: null,
        humidity: null,
        co2Concentration: null,
        nh3Concentration: null,
        lightIntensity: null,
        collectTime: new Date(),
      };
      this.dialogVisible = true;
    },
    handleView(row) {
      this.$alert(JSON.stringify(row, null, 2), "详细信息", {
        confirmButtonText: "确定",
      });
    },
    handleDelete(row) {
      this.$confirm("确定要删除这条数据吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteEnvironmentData(row.id).then((response) => {
          ElMessage.success("删除成功");
          this.loadData();
        });
      });
    },
    handleSubmit() {
      this.$refs.dataForm.validate((valid) => {
        if (!valid) {
          ElMessage.error("请填写所有必填项：监测点、温度、湿度");
          return false;
        }

        if (valid) {
          // 准备提交数据，确保数据类型正确
          const submitData = {
            monitorPoint: this.formData.monitorPoint,
            monitorLocation: this.formData.monitorLocation || null, // 空字符串改为 null
            latitude: this.formData.latitude
              ? Number(this.formData.latitude)
              : null, // 确保是数字
            longitude: this.formData.longitude
              ? Number(this.formData.longitude)
              : null,
            temperature: Number(this.formData.temperature), // 必填，确保是数字
            humidity: Number(this.formData.humidity), // 必填，确保是数字
            co2Concentration: this.formData.co2Concentration
              ? Number(this.formData.co2Concentration)
              : null,
            nh3Concentration: this.formData.nh3Concentration
              ? Number(this.formData.nh3Concentration)
              : null,
            lightIntensity: this.formData.lightIntensity
              ? Number(this.formData.lightIntensity)
              : null,
            isAbnormal: 0, // 默认正常
            abnormalReason: null,
          };

          // 格式化采集时间
          if (this.formData.collectTime) {
            const d = new Date(this.formData.collectTime);
            submitData.collectTime =
              d.getFullYear() +
              "-" +
              String(d.getMonth() + 1).padStart(2, "0") +
              "-" +
              String(d.getDate()).padStart(2, "0") +
              " " +
              String(d.getHours()).padStart(2, "0") +
              ":" +
              String(d.getMinutes()).padStart(2, "0") +
              ":" +
              String(d.getSeconds()).padStart(2, "0");
          } else {
            // 如果没有指定时间，使用当前时间
            const now = new Date();
            submitData.collectTime =
              now.getFullYear() +
              "-" +
              String(now.getMonth() + 1).padStart(2, "0") +
              "-" +
              String(now.getDate()).padStart(2, "0") +
              " " +
              String(now.getHours()).padStart(2, "0") +
              ":" +
              String(now.getMinutes()).padStart(2, "0") +
              ":" +
              String(now.getSeconds()).padStart(2, "0");
          }

          console.log("提交数据：", submitData); // 调试输出

          addEnvironmentData(submitData)
            .then((response) => {
              if (response.data.flag) {
                ElMessage.success("添加成功");
                this.dialogVisible = false;
                this.loadData();
              } else {
                ElMessage.error(response.data.message || "添加失败");
              }
            })
            .catch((error) => {
              console.error("添加失败：", error);
              console.error("错误详情：", error.response); // 调试输出

              // 显示详细的后端错误信息
              if (error.response) {
                const errorData = error.response.data;
                console.error("后端返回数据：", errorData); // 打印后端返回的完整数据

                if (error.response.status === 403) {
                  ElMessage.error("您没有权限执行此操作，请以管理员身份登录");
                } else if (error.response.status === 400) {
                  // 尝试获取后端的详细错误信息
                  let errorMsg = "数据格式错误";
                  if (errorData && typeof errorData === "object") {
                    errorMsg =
                      errorData.message ||
                      errorData.error ||
                      JSON.stringify(errorData);
                  } else if (typeof errorData === "string") {
                    errorMsg = errorData;
                  }
                  ElMessage.error("添加失败：" + errorMsg);
                } else {
                  ElMessage.error(
                    "添加失败：" +
                      (errorData?.message || error.message || "未知错误"),
                  );
                }
              } else {
                ElMessage.error("网络错误：" + error.message);
              }
            });
        }
      });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.loadData();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadData();
    },
  },
};
</script>

<style scoped>
.environment-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}
.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.stat-icon {
  font-size: 48px;
  color: #67c23a;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin: 0;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin: 5px 0 0 0;
}
</style>

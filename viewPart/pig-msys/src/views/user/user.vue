<template>
  <div>
    <el-form
      ref="searchForm"
      :inline="true"
      :model="searchWhere"
      class="demo-form-inline"
      style="margin-top: 20px"
    >
      <el-form-item label="姓名" size="small" prop="name">
        <el-input
          v-model="searchWhere.name"
          placeholder="姓名"
          style="width: 150px"
        ></el-input>
      </el-form-item>
      <el-form-item label="家庭地址" size="small" prop="address">
        <el-input
          v-model="searchWhere.address"
          placeholder="家庭地址"
          style="width: 150px"
        ></el-input>
      </el-form-item>
      <el-form-item label="角色" size="small" prop="userRole">
        <el-select
          v-model="searchWhere.userRole"
          placeholder="用户等级"
          style="width: 120px"
        >
          <el-option
            v-for="option in userRoleOptions"
            :label="option.name"
            :value="option.role"
            :key="option.role"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="性别" size="small" prop="sex">
        <el-select
          v-model="searchWhere.sex"
          placeholder="用户性别"
          style="width: 120px"
        >
          <el-option
            v-for="option in userSexOptions"
            :label="option.name"
            :value="option.sex"
            :key="option.sex"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item size="small">
        <el-button
          type="primary"
          plain
          @click="searchUserList"
          icon="el-icon-search"
          >查询</el-button
        >
        <el-button
          type="success"
          plain
          class="el-icon-refresh"
          @click="resetForm('searchForm')"
          >重置</el-button
        >
        <el-button
          type="warning"
          plain
          class="el-icon-plus"
          @click="HandlerAdd('searchData')"
          >新增</el-button
        >
      </el-form-item>
    </el-form>
    <el-table
      ref="searchData"
      :data="userInfoList"
      style="width: 100%"
      max-height="500"
    >
      <el-table-column fixed type="index" label="序号" width="60">
      </el-table-column>
      <el-table-column prop="userisbn" label="信息号" width="150">
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="100"> </el-table-column>
      <el-table-column prop="age" label="年龄" width="100"> </el-table-column>
      <el-table-column prop="sex" label="性别" width="100"> </el-table-column>
      <el-table-column prop="birthday" label="出生年月" width="150">
      </el-table-column>
      <el-table-column prop="address" label="家庭地址" width="250">
      </el-table-column>
      <el-table-column prop="phone" label="手机号码" width="150">
      </el-table-column>
      <el-table-column prop="userrole" label="角色" width="100">
        <template #default="scope">
          <span>{{ userRoleFilter(scope.row.userrole) }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button
            class="el-icon-edit"
            size="small"
            type="primary"
            title="编辑"
            @click="handleEdit(scope.row.id)"
          ></el-button>
          <el-button
            class="el-icon-delete"
            size="small"
            type="danger"
            title="删除"
            @click="handleDelete(scope.row.id)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[5, 10, 15, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    >
    </el-pagination>
    <el-dialog title="编辑用户信息" v-model="dialogFormVisible" width="500px">
      <el-form
        :model="form"
        :rules="rules"
        label-width="100px"
        label-position="right"
        ref="addForm"
        style="width: 400px; margin-top: -20px"
      >
        <el-form-item label="信息号" size="small" prop="userisbn">
          <el-input v-model="form.userisbn"></el-input>
        </el-form-item>
        <el-form-item label="姓名" size="small" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="年龄" size="small" prop="age">
          <el-input v-model="form.age"></el-input>
        </el-form-item>
        <el-form-item label="性别" size="small" prop="sex">
          <el-input v-model="form.sex"></el-input>
        </el-form-item>
        <el-form-item label="出生年月" size="small" prop="birthday">
          <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="选择日期"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="家庭地址" size="small" prop="address">
          <el-input v-model="form.address"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" size="small" prop="phone">
          <el-input v-model.number="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="角色" size="small" prop="userrole">
          <el-select v-model="form.userrole">
            <el-option
              :label="option.name"
              :value="option.role"
              v-for="option in userRoleOptions"
              :key="option.role"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false" size="small"
            >取 消</el-button
          >
          <el-button
            type="primary"
            @click="
              form.id == null ? addData('addForm') : updateData('addForm')
            "
            size="small"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import userinfoApi from "../../api/userinfo";
import { ElMessage, ElMessageBox } from "element-plus";
const userRoleOptions = [
  { role: 1, name: "一般用户" },
  { role: 2, name: "会员用户" },
];
const userSexOptions = [
  { sex: "男", name: "男" },
  { sex: "女", name: "女" },
];
export default {
  name: "user",
  data() {
    return {
      userInfoList: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchWhere: {
        name: "",
        address: "",
        userRole: "",
        sex: "",
      },
      pickerOptions: {
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      userRoleOptions,
      userSexOptions,
      form: { id: null },
      rules: {
        userISBN: [{ required: true, message: "请输入编号", trigger: "blur" }],
        name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
        birthday: [
          {
            required: true,
            message: "请输入出生日期",
            trigger: ["blur", "change"],
          },
        ],
        phone: [{ required: true, message: "请输入电话号码", trigger: "blur" }],
      },
      dialogFormVisible: false,
    };
  },
  created() {
    this.searchUserList();
  },
  methods: {
    searchUserList() {
      // userinfoApi.getUserList().then(response=>{
      //   const  resp = response.data;
      //   if (resp.flag){
      //     this.userInfoList = resp.data;
      //   }
      // })

      userinfoApi
        .search(this.currentPage, this.pageSize, this.searchWhere)
        .then((response) => {
          const resp = response.data;
          if (resp.flag) {
            this.userInfoList = resp.data.rows;
            this.total = resp.data.total;
          }
        });
    },
    HandlerAdd(tableName) {
      this.dialogFormVisible = true;
      this.$refs[tableName].resetFields();
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    handleSizeChange(size) {
      this.pageSize = size;
      console.log(`每页 ${size} 条`);
      this.searchUserList();
    },
    handleCurrentChange(currentPage) {
      console.log(`当前页: ${currentPage}`);
      this.currentPage = currentPage;
      this.searchUserList();
    },
    handleEdit(id) {
      userinfoApi.getUserInfoById(id).then((response) => {
        const resp = response.data;
        if (resp.flag) {
          this.form = resp.data;
          this.dialogFormVisible = true;
        }
      });
    },
    updateData(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          userinfoApi.updateUserInfo(this.form).then((response) => {
            const resp = response.data;
            if (resp.flag) {
              ElMessage({
                message: resp.message,
                type: "success",
              });
              this.dialogFormVisible = false;
              this.searchUserList();
            } else {
              ElMessage({
                message: resp.message,
                type: "error",
              });
            }
          });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
      this.resetForm(formName);
      this.form.id = null;
    },
    addData(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          userinfoApi.addUserInfo(this.form).then((response) => {
            const resp = response.data;
            if (resp.flag) {
              ElMessage({
                message: resp.message,
                type: "success",
              });
              this.dialogFormVisible = false;
              this.searchUserList();
            } else {
              ElMessage({
                message: resp.message,
                type: "error",
              });
            }
          });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
      this.resetForm(formName);
    },
    handleDelete(id) {
      ElMessageBox.confirm("确定要删除吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          userinfoApi.delUserInfo(id).then((response) => {
            const resp = response.data;
            if (resp.flag) {
              this.searchUserList();
              ElMessage({
                type: "success",
                message: "删除成功!",
              });
            } else {
              ElMessage({
                type: "error",
                message: "删除失败!",
              });
            }
          });
        })
        .catch(() => {});
    },
    // Vue 3 不再支持 filters，改为 methods
    userRoleFilter(role) {
      const roleObj = userRoleOptions.find((obj) => obj.role === role);
      return roleObj ? roleObj.name : null;
    },
    userSexFilter(sex) {
      const sexObj = userSexOptions.find((obj) => obj.sex === sex);
      return sexObj ? sexObj.name : null;
    },
  },
};
</script>

<style scoped></style>

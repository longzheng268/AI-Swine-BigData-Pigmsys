<template>
  <div class="header">
    <a href="/" style="padding-left: 30px">
      <img :src="logoImage" alt="" class="logo" />
      <span class="title">基于Hadoop的生猪智慧养殖系统</span>
    </a>

    <el-dropdown @command="handleCommand">
      <span class="el-dropdown-link">
        欢迎,{{ this.user.name }}
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="a">修改密码</el-dropdown-item>
          <el-dropdown-item command="b">退出系统</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <el-dialog title="修改密码" v-model="dialogFormVisible" width="400px">
      <el-form
        :model="ruleForm"
        status-icon
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        style="margin-top: -40px; width: 300px; margin-bottom: -20px"
        size="mini"
      >
        <el-form-item label="旧密码" prop="oldpass">
          <el-input
            type="password"
            v-model="ruleForm.oldpass"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newpass">
          <el-input
            type="password"
            v-model="ruleForm.newpass"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkpass">
          <el-input
            type="password"
            v-model="ruleForm.checkpass"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')"
            >提交</el-button
          >
          <el-button @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { logout } from "../../api/login";
import pwdApi from "../../api/password";
import { ElMessage } from "element-plus";
import { ArrowDown } from "@element-plus/icons-vue";
import logoImage from "../../assets/logo.jpg";

export default {
  components: {
    ArrowDown,
  },
  name: "index",
  data() {
    const validateOldPass = (rule, value, callback) => {
      pwdApi.checkPwd(this.user.id, value).then((response) => {
        const resp = response.data;
        if (resp.flag) {
          callback();
        } else {
          callback(new Error("旧密码输入错误,请重新输入"));
        }
      });
    };
    const validateCheckPass = (rule, value, callback) => {
      if (value !== this.ruleForm.newpass) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    // 安全地获取用户信息，避免解析错误
    const getUserInfo = () => {
      try {
        const userInfo = localStorage.getItem("my-login-user");
        if (userInfo) {
          return JSON.parse(userInfo);
        }
      } catch (e) {
        console.error("解析用户信息失败:", e);
      }
      // 返回默认用户对象，避免模板报错
      return { name: "用户", id: null };
    };

    return {
      logoImage,
      user: getUserInfo(),
      dialogFormVisible: false,
      ruleForm: {
        oldpass: "",
        newpass: "",
        checkpass: "",
      },
      rules: {
        oldpass: [
          { required: true, message: "旧密码不能为空", trigger: "blur" },
          { validator: validateOldPass, trigger: "blur" },
        ],
        newpass: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
          {
            min: 6,
            max: 32,
            message: "密码必须为长度在6到32个字符",
            trigger: ["blur", "change"],
          },
        ],
        checkpass: [
          { required: true, message: "确认密码不能为空", trigger: "blur" },
          { validator: validateCheckPass, trigger: ["blur", "change"] },
        ],
      },
    };
  },
  methods: {
    submitForm(formName) {
      const formRef = this.$refs[formName];
      if (!formRef) {
        console.error("表单引用不存在:", formName);
        return;
      }
      formRef.validate((valid) => {
        if (valid) {
          pwdApi
            .updatePwd(this.user.id, this.ruleForm.newpass)
            .then((response) => {
              const resp = response.data;
              ElMessage({
                message: resp.message,
                type: resp.flag ? "success" : "error",
              });
              if (resp.flag) {
                this.logoutSystem();
                this.dialogFormVisible = false;
              }
            });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    resetForm(formName) {
      const formRef = this.$refs[formName];
      if (formRef) {
        formRef.resetFields();
      }
    },
    updatePwd() {
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        if (this.$refs.ruleForm) {
          this.$refs.ruleForm.resetFields();
        }
      });
    },
    logoutSystem() {
      const token = localStorage.getItem("my-login-token");
      logout(token).then((resp) => {
        if (resp.data.flag) {
          ElMessage({
            message: resp.data.message,
            type: "success",
          });
          localStorage.removeItem("my-login-token");
          localStorage.removeItem("my-login-user");
          this.$router.push("/login");
        } else {
          ElMessage({
            message: "退出失败",
            type: "error",
          });
        }
      });
    },
    handleCommand(command) {
      switch (command) {
        case "a":
          this.updatePwd();
          break;
        case "b":
          this.logoutSystem();
          break;
      }
    },
  },
};
</script>

<style scoped>
.logo {
  width: 40px;
  height: 40px;
  vertical-align: middle;
  border-radius: 5px;
}

.title {
  position: absolute;
  color: white;
  padding-left: 5px;
  font-size: 20px;
}
.el-dropdown-link {
  cursor: pointer;
  color: white;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.el-dropdown {
  float: right;
  margin-right: 50px;
}
</style>

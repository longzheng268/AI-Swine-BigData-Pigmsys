<template>
  <div id="login-container" :style="loginContainerStyle">
    <el-form
      ref="form"
      :rules="rules"
      :model="form"
      label-width="60px"
      class="login-form"
    >
      <h2 class="login-title">基于Hadoop的生猪智慧养殖系统</h2>
      <el-form-item label="账号" prop="username">
        <el-input v-model="form.username" placeholder="请输入账号"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="userpassword">
        <el-input
          v-model="form.userpassword"
          placeholder="请输入密码"
          type="password"
        ></el-input>
      </el-form-item>
      <el-form-item label="角色" prop="roleId">
        <el-select
          v-model="form.roleId"
          placeholder="请选择登录角色"
          style="width: 100%"
        >
          <el-option label="管理员" :value="1"></el-option>
          <el-option label="普通用户" :value="2"></el-option>
          <el-option label="科研人员" :value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="验证码" prop="code" label-width="65px">
        <el-input
          v-model="form.code"
          placeholder="请输入验证码"
          clearable
          :style="{ width: '50%' }"
        >
        </el-input>
        <div class="verify">
          <img
            :src="imgUrl"
            alt="更换验证码"
            @click="getVerify(this)"
            width="100px"
          />
        </div>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          @click="submitForm('form')"
          style="width: 100px"
          >登录</el-button
        >
        <el-button
          type="primary"
          @click="resetForm('form')"
          style="width: 100px; margin-left: 35px"
          >重置</el-button
        >
      </el-form-item>
      <el-form-item class="register-button">
        <el-button
          link
          @click="showRegisterDialog"
          style="width: 100%; margin-top: 5px"
          >注册新账号</el-button
        >
      </el-form-item>
    </el-form>

    <!-- 注册对话框 -->
    <el-dialog title="用户注册" v-model="registerDialogVisible" width="500px">
      <el-form
        :model="registerForm"
        :rules="registerRules"
        ref="registerForm"
        label-width="100px"
      >
        <el-form-item label="账号" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入账号（4-20个字符）"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="userpassword">
          <el-input
            v-model="registerForm.userpassword"
            placeholder="请输入密码（6-18个字符）"
            type="password"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            placeholder="请再次输入密码"
            type="password"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="usermobile">
          <el-input
            v-model="registerForm.usermobile"
            placeholder="请输入手机号"
          ></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select
            v-model="registerForm.roleId"
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option label="普通用户" :value="2"></el-option>
            <el-option label="科研人员" :value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRegister">注册</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { login, getUserInfo, aVerify } from "../../api/login";
import myaxios from "../../utils/myaxios";
import qs from "qs";
import { ElMessage } from "element-plus";
import bgImage from "../../assets/bg.png";

export default {
  name: "index",
  data() {
    // 自定义验证器：确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.registerForm.userpassword) {
        callback(new Error("两次输入密码不一致"));
      } else {
        callback();
      }
    };

    return {
      bgImage,
      form: {
        username: "",
        userpassword: "",
        roleId: 2, // 默认选择普通用户
        code: "",
      },
      imgUrl: "/getVerify",
      rules: {
        username: [{ required: true, message: "请输入账号", trigger: "blur" }],
        userpassword: [
          { required: true, message: "请输入密码", trigger: "blur" },
          {
            min: 6,
            max: 18,
            message: "密码长度必须为6到18个字符",
            trigger: ["blur", "change"],
          },
        ],
        roleId: [
          { required: true, message: "请选择登录角色", trigger: "change" },
        ],
        code: [
          {
            required: true,
            message: "请输入验证码",
            trigger: "blur",
          },
        ],
      },
      // 注册相关
      registerDialogVisible: false,
      registerForm: {
        username: "",
        userpassword: "",
        confirmPassword: "",
        usermobile: "",
        roleId: 2,
      },
      registerRules: {
        username: [
          { required: true, message: "请输入账号", trigger: "blur" },
          { min: 4, max: 20, message: "账号长度为4-20个字符", trigger: "blur" },
        ],
        userpassword: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, max: 18, message: "密码长度为6-18个字符", trigger: "blur" },
        ],
        confirmPassword: [
          {
            required: true,
            validator: validateConfirmPassword,
            trigger: "blur",
          },
        ],
        usermobile: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号",
            trigger: "blur",
          },
        ],
        roleId: [{ required: true, message: "请选择角色", trigger: "change" }],
      },
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          //表单验证成功，开始向后端传送数据进行验证
          aVerify(this.form.code).then((resp) => {
            if (resp.data.flag) {
              login(this.form.username, this.form.userpassword).then((resp) => {
                ElMessage({
                  message: "登录成功",
                  type: "success",
                  showClose: true,
                  center: true,
                });
                const respLogin = resp.data;
                if (respLogin.flag) {
                  getUserInfo(respLogin.data.token).then((resp) => {
                    /*this.$message({
                          message: '获取用户信息成功',
                          type: 'success'
                        });*/
                    const respUserInfo = resp.data;
                    if (respUserInfo.flag) {
                      // 将选择的角色ID添加到用户信息中
                      const userInfo = respUserInfo.data;
                      userInfo.roleId = this.form.roleId;
                      localStorage.setItem(
                        "my-login-user",
                        JSON.stringify(userInfo),
                      );
                      localStorage.setItem(
                        "my-login-token",
                        respLogin.data.token,
                      );
                      this.$router.push("/home");
                    } else {
                      ElMessage.error(respUserInfo.message);
                    }
                  });
                } else {
                  ElMessage.error(respLogin.message);
                }
              });
            } else {
              return false;
            }
          });
        } else {
          ElMessage.error("校验失败,请输入正确的格式");
          return false;
        }
      });
    },
    getVerify(obj) {
      console.log(obj);
      // obj.src = "/api/Code/getVerify?" + Math.random();
      this.imgUrl = "/getVerify?" + Math.random();
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    // 显示注册对话框
    showRegisterDialog() {
      this.registerDialogVisible = true;
      this.$nextTick(() => {
        if (this.$refs.registerForm) {
          this.$refs.registerForm.resetFields();
        }
      });
    },
    // 处理注册
    handleRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          // 准备注册数据
          const registerData = {
            username: this.registerForm.username,
            userpassword: this.registerForm.userpassword,
            usermobile: this.registerForm.usermobile,
            state: "1", // 默认启用
            roleId: this.registerForm.roleId,
          };

          // 调用注册接口
          myaxios
            .post("/user/register", registerData)
            .then((response) => {
              const resp = response.data;
              if (resp.flag) {
                ElMessage({
                  message: "注册成功！请登录",
                  type: "success",
                  duration: 2000,
                });
                this.registerDialogVisible = false;
                // 自动填充登录信息
                this.form.username = this.registerForm.username;
                this.form.roleId = this.registerForm.roleId;
              } else {
                ElMessage.error(resp.message || "注册失败");
              }
            })
            .catch((error) => {
              console.error("注册失败：", error);
              ElMessage.error(
                "注册失败：" +
                  (error.response?.data?.message ||
                    error.message ||
                    "网络错误"),
              );
            });
        }
      });
    },
  },
  computed: {
    loginContainerStyle() {
      return {
        backgroundImage: `url(${this.bgImage})`
      };
    }
  },
  // mounted(){
  //   const obj={
  //     name:'小明',
  //     age:21,
  //     sex:'男'
  //   }
  //   testApi.test(obj).then(response=>{
  //
  //   })
  // }
};
</script>

<style scoped>
#login-container {
  position: absolute;
  width: 100%;
  height: 100%;
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  /* Background image is applied via computed style */
}
.login-form {
  width: 350px;
  height: 430px;
  background-color: #d9afd9;
  background-image: linear-gradient(0deg, #fff4ff 0%, #97d9e1 100%);

  /*margin: 160px auto;*/
  padding: 15px;
  /*border-radius: 20px;*/
  left: 50%;
  top: 50%;
  margin-left: -175px;
  margin-top: -265px;
  position: absolute;
}
.login-title {
  text-align: center;
  color: #606266;
}
.verify {
  float: right;
  margin-right: 20px;
  margin-top: 5px;
  width: 100px;
  height: 30px;
}
.register-button >>> .el-form-item__content {
  margin-left: 0 !important;
  text-align: center;
}
.register-button >>> .el-button--text {
  color: #409eff;
  font-size: 14px;
}
.register-button >>> .el-button--text:hover {
  color: #66b1ff;
}
</style>

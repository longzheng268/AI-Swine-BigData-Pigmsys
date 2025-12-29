<template>
<div>
  <h2>个人信息</h2>
  <hr>
  <el-descriptions class="margin-top" title="" :column="1" :size="size" border>

    <el-descriptions-item>
      <template #label>
        <i class="el-icon-user"></i>
        当前账号
      </template>
      {{ personalInfo.username }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <i class="el-icon-mobile-phone"></i>
        手机号
      </template>
      {{ personalInfo.usermobile }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <i class="el-icon-view"></i>
        账号状态
      </template>
      <el-switch
        v-model="value"
        active-color="#13ce66"
        inactive-color="#ff4949">
      </el-switch>
      <span v-if="value">{{ personalInfo.state }}</span>
      <span v-else>禁用</span>
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <i class="el-icon-tickets"></i>
        更多
      </template>
      <el-tag size="small">{{ personalInfo.more }}</el-tag>
    </el-descriptions-item>

  </el-descriptions>
</div>
</template>

<script>
import personal from "../../api/personal";
import { ElMessage } from 'element-plus'

export default {
  name: "InfoPerson",
  data(){
    // 安全地获取用户信息
    const getUserInfo = () => {
      try {
        const userInfo = localStorage.getItem('my-login-user')
        if (userInfo) {
          return JSON.parse(userInfo)
        }
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
      return { id: null }
    }
    
    return{
      personalInfo:{},
      personal: getUserInfo(),
      form:{},
      value: true,
      size: ''
    }
  },
  created() {
    this.searchPersonal()
  },
  methods:{
    searchPersonal(){
      if (!this.personal || !this.personal.id) {
        console.warn('用户ID不存在，无法获取个人信息')
        return
      }
      personal.getPersonalInfoById(this.personal.id).then(response=>{
        const resp=response.data
        if(resp.flag){
          this.form=resp.data
          this.personalInfo=resp.data
        } else {
          ElMessage.warning(resp.message || '获取个人信息失败')
        }
      }).catch(error => {
        console.error('获取个人信息失败:', error)
        ElMessage.error('获取个人信息失败：' + (error.response?.data?.message || error.message))
      })
    }

  }
};
</script>

<style scoped>

</style>

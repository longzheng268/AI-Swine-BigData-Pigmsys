import axios from "axios"
import {ElLoading, ElMessage} from "element-plus";

//验证码要用的
axios.defaults.withCredentials = true

//改进过后的axios  重新封装后的axios=>myaxios
const myaxios=axios.create({
    baseURL: process.env.VUE_APP_BASE_API || '/',  // 使用相对路径，会自动代理到后端
    timeout:30000  // 增加到30秒，数据聚合需要时间
})
//建立还没加载完数据时,给个正在玩命加载中的,这样可以提高用户的体验
const loading={
    loadingInStance:null,
    open:function () {
        //使用单例模式,不能让用一直发送ajax请求,一直创建loadingInStance实例,消耗系统性能,所以使用单例模式
        //而在关闭loadingInStance实例的时候,我们需要将loadingInStance实例变成空,这样点别的路由才会触发loadingInStance实例
        if(this.loadingInStance===null) {
            this.loadingInStance = ElLoading.service({
                target: '.main',
                text: 'loading...',
                background: 'rgba(0,0,0,0.5)'
            })
        }
    },
    close:function () {
        if(this.loadingInStance!==null){
            this.loadingInStance.close()
        }
        this.loadingInStance=null
    }
}
// 添加请求拦截器
myaxios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    loading.open()
    
    // 自动添加 JWT Token 到请求头
    const token = localStorage.getItem('my-login-token')
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
    }
    
    return config;
}, function (error) {
    loading.close()
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
myaxios.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    loading.close()
    const resp=response.data
    // 检查返回码（某些接口可能使用 flag 而不是 code）
    if(resp.code && resp.code != 200){
        ElMessage({
            message:resp.message || '系统异常',
            showClose:true,
            type:'warning',
            duration:5000
        })
    }
    return response;
}, function (error) {
    // 对响应错误做点什么
    loading.close()
    
    // 处理 401 未授权错误
    if (error.response && error.response.status === 401) {
        ElMessage({
            message: '登录已过期，请重新登录',
            showClose: true,
            type: 'error',
            duration: 3000
        })
        // 清除本地存储
        localStorage.removeItem('my-login-token')
        localStorage.removeItem('my-login-user')
        // 跳转到登录页
        setTimeout(() => {
            window.location.href = '/login'
        }, 1000)
    } else if (error.response && error.response.status === 403) {
        // 处理 403 禁止访问错误
        if (process.env.NODE_ENV === 'development') {
          console.error('🚨 403错误详情:', {
            url: error.config?.url,
            method: error.config?.method,
            headers: error.config?.headers,
            response: error.response?.data
          })
        }
        
        // 检查token（仅在开发环境）
        if (process.env.NODE_ENV === 'development') {
          const token = localStorage.getItem('my-login-token')
          console.log('当前token:', token ? '存在, 长度: ' + token.length : '不存在')
        }
        
        ElMessage({
            message: '访问被拒绝(403) - 请检查控制台查看详情',
            showClose: true,
            type: 'error',
            duration: 5000
        })
        
        // 暂时不自动跳转，方便调试
        // localStorage.removeItem('my-login-token')
        // localStorage.removeItem('my-login-user')
        // setTimeout(() => {
        //     window.location.href = '/login'
        // }, 1000)
    } else {
        ElMessage({
            message: error.message || '请求失败',
            showClose: true,
            type: 'error',
            duration: 5000
        })
    }
    return Promise.reject(error);
});
export default myaxios

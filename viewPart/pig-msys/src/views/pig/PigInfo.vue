<template>
<div>
  <el-form ref="searchForm" :inline="true" :model="searchWhere" class="demo-form-inline" style="margin-top: 20px">
    <el-form-item label="猪的编号" size="small" prop="pigISBN">
      <el-input v-model="searchWhere.pigISBN" placeholder="猪的编号" style="width: 150px"></el-input>
    </el-form-item>
    <el-form-item label="猪的代名" size="small" prop="pigName">
      <el-input  v-model="searchWhere.pigName" placeholder="猪的代名" style="width: 150px"></el-input>
    </el-form-item>
    <el-form-item label="猪的种类" size="small" prop="pigType">
      <el-select v-model="searchWhere.pigType" placeholder="猪的种类" style="width: 120px">
        <el-option v-for="option in pigTypeOptions" :label="option.name" :value="option.type" :key="option.type"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="雄|雌" size="small" prop="pinSex">
      <el-select v-model="searchWhere.pinSex" placeholder="猪的雄雌" style="width: 120px">
        <el-option v-for="option in pigSexOptions" :label="option.sex" :value="option.type" :key="option.type"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item size="small">
      <el-button type="primary" plain @click="searchData" icon="el-icon-search">查询</el-button>
      <el-button type="success" plain class="el-icon-refresh" @click="resetForm('searchForm')">重置</el-button>
      <el-button type="warning" plain class="el-icon-plus" @click="addHandler">新增</el-button>
    </el-form-item>
  </el-form>
  <el-table
      :data="pigInfoList"
      style="width: 100%"
      max-height="500">
    <el-table-column
        fixed
        type="index"
        label="序号"
        width="60">
    </el-table-column>
    <el-table-column
        prop="pigisbn"
        label="小猪编号"
        width="150">
    </el-table-column>
    <el-table-column
        prop="pigname"
        label="小猪名称"
        width="100">
    </el-table-column>
    <el-table-column
        prop="pigage"
        label="小猪年龄"
        width="100">
    </el-table-column>
    <el-table-column
        prop="pinsex"
        label="小猪性别"
        width="100">
      <template #default="scope">
        <span>{{pigSexFilter(scope.row.pinsex)}}</span>
      </template>
    </el-table-column>
    <el-table-column
        prop="manufacturedate"
        label="出生日期"
        width="200">
    </el-table-column>
    <el-table-column
        prop="pigaddress"
        label="生产地"
        width="200">
    </el-table-column>
    <el-table-column
        prop="pigimage"
        label="图片链接"
        width="100">
      <template #default="scope">
        <img :src="scope.row.pigimage" width="100px" height="100px"/>
      </template>
    </el-table-column>
    <el-table-column
        prop="pigtype"
        label="小猪种类"
        width="100">
      <template #default="scope">
        <span>{{pigTypeFilter(scope.row.pigtype)}}</span>
      </template>
    </el-table-column>
    <el-table-column
        fixed="right"
        label="操作"
        width="200">
      <template #default="scope">
        <el-button
          class="el-icon-edit"
            size="small"
            type="primary"
          title="编辑"
            @click="handleEdit(scope.row.id)"></el-button>
        <el-button
          class="el-icon-delete-solid"
            size="small"
            type="danger"
          title="删除"
            @click="handleDelete(scope.row.id)"></el-button>
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
      :total="total">
  </el-pagination>
  <el-dialog title="编辑猪的信息" v-model="dialogFormVisible" width="500px">
    <el-form :model="form" :rules="rules" label-width="100px" label-position="right" ref="addForm" style="width: 400px;margin-top: -20px">
      <el-form-item label="小猪编号" size="small" prop="pigisbn">
        <el-input v-model="form.pigisbn"></el-input>
      </el-form-item>
        <el-form-item label="小猪名称" size="small" prop="pigname">
          <el-input v-model="form.pigname"></el-input>
        </el-form-item>
        <el-form-item label="小猪年龄" size="small" prop="pigage">
          <el-input v-model.number="form.pigage"></el-input>
        </el-form-item>
        <el-form-item label="小猪性别" size="small" prop="pinsex">
          <el-select v-model="form.pinsex">
            <el-option :label="option.sex" :value="option.type" v-for="option in pigSexOptions" :key="option.type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期" size="small" prop="manufacturedate">
          <el-date-picker
              v-model="form.manufacturedate"
              type="date"
              placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="生产地" size="small" prop="pigaddress">
          <el-input v-model="form.pigaddress"></el-input>
        </el-form-item>
        <el-form-item label="图片链接" size="small" prop="pigimage">
          <el-input v-model="form.pigimage"></el-input>
        </el-form-item>
      <el-form-item label="小猪种类" size="small" prop="pigtype">
        <el-select v-model="form.pigtype">
          <el-option :label="option.name" :value="option.type" v-for="option in pigTypeOptions" :key="option.type"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="form.id==null?addData('addForm'):updateData('addForm')" size="small">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</div>
</template>

<script>
import {search, add, getPigById, updatePigInfo, deletePigById} from "../../api/piginfo";
import { ElMessage, ElMessageBox } from 'element-plus';
//这里为什么定义全局而不定义在data中  因为过滤器中不能用this的vue实例  不信可以用数据测试
const pigTypeOptions=[
  {type:1,name:'白猪'},
  {type:2,name:'黑猪'},
  {type:3,name:'花猪'}
]
const pigSexOptions=[
    {type:1,sex:'雄性'},
    {type:2,sex:'雌性'}
]
export default {
  name: "PigInfo",
  data(){
    return{
      dialogPressVisible:false,
      dialogFormVisible:false,
      form:{
        pigisbn: '',
        pigname:'',
        pigage:'',
        pinsex:'',
        manufacturedate:'',
        pigaddress: '',
        pigimage:'',
        pigtype: ''
      },
      pigInfoList:[],
      total:0,
      currentPage:1,
      pageSize:10,
      searchWhere:{
        pigName:'',
        pigISBN:'',
        pigType:'',
        pinSex: ''

      },
      pigTypeOptions,
      pigSexOptions,
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      rules:{
        pigISBN:[
          {required:true,message:'请输入猪的编号',trigger:'blur'}
        ],
        pigName:[
          {required:true,message:'请输入猪的名称',trigger:'blur'}
        ],
        manufactureDate:[
          {required:true,message:'请输入出生日期',trigger:['blur','change']}
        ]
      }
    }
  },
  created() {
    this.searchData()
  },
  methods: {
    getPress(press){
      this.searchWhere.press=press
      this.dialogPressVisible=false
    },
    updateData(formName){
      const formRef = this.$refs[formName]
      if (!formRef) {
        console.error('表单引用不存在:', formName)
        return
      }
      formRef.validate((valid) => {
        if (valid) {
          updatePigInfo(this.form).then(response=>{
            const resp=response.data
            if(resp.flag){
              this.searchData()
              this.dialogFormVisible=false
            }else {
              ElMessage({
                message:resp.message,
                type:'error'
              })
            }
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
      this.form.id=null
    },
    addHandler(){
      this.dialogFormVisible=true
      this.$nextTick(()=>{
        const addFormRef = this.$refs.addForm
        if (addFormRef) {
          addFormRef.resetFields()
        }
      })
    },
    addData(formName){
      const formRef = this.$refs[formName]
      if (!formRef) {
        console.error('表单引用不存在:', formName)
        return
      }
      formRef.validate((valid) => {
        if (valid) {
          add(this.form).then(response=>{
            const resp=response.data
            if(resp.flag){
              this.searchData()
              this.dialogFormVisible=false
            }else {
              ElMessage({
                message:resp.message,
                type:'error'
              })
            }
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    handleEdit(id) {
      // console.log('编辑'+id);
      this.addHandler()
      getPigById(id).then(response=>{
        const resp=response.data
        if(resp.flag){
          this.form=resp.data
        }else {
          ElMessage.error('根据id查询失败')
        }
      })
    },
    handleDelete(id) {
      ElMessageBox.confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletePigById(id).then(response=>{
          const resp=response.data
          if(resp.flag){
            this.searchData()
            ElMessage({
              type: 'success',
              message: '删除成功!'
            });
          }else {
            ElMessage({
              type: 'error',
              message: '删除失败!'
            });
          }
        })
      }).catch(() => {
      });
    },
    handleSizeChange(size) {
      this.pageSize=size
      if (process.env.NODE_ENV === 'development') {
        console.log(`每页 ${size} 条`);
      }
      this.searchData()
    },
    handleCurrentChange(currentPage) {
      if (process.env.NODE_ENV === 'development') {
        console.log(`当前页: ${currentPage}`);
      }
      this.currentPage=currentPage
      this.searchData()
    },
    searchData(){
      search(this.currentPage,this.pageSize,this.searchWhere).then(response=>{
        if (process.env.NODE_ENV === 'development') {
          console.log('查询结果:', response)
        }
        this.pigInfoList=response.data.data.rows
        this.total=response.data.data.total
      }).catch(error => {
        console.error('查询失败:', error)
        ElMessage.error('查询失败：' + (error.response?.data?.message || error.message))
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    // Vue 3 不再支持 filters，改为 methods
    pigTypeFilter(type){
      //通过find函数去数组对象中找到一个对象的type与传过来的参数type相同,返回这个对象
      const woolObj=pigTypeOptions.find(obj=>obj.type===type)
      return woolObj?woolObj.name:null
    },
    pigSexFilter(type){
      const pigObj = pigSexOptions.find(obj=>obj.type===type)
      return pigObj?pigObj.sex:null;
    }
  }
}
</script>

<style scoped>
.dialog-footer{
  margin-top: -40px;
}
</style>

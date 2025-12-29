import myaxios from "../utils/myaxios";
import qs from "qs";

export default {
    checkPwd(userId,oldPassword){
        return myaxios({
            url:'/user/pwd',
            method:'post',
            data:{
                userId,
                oldPassword
            },
            transformRequest: [
              data=>{
                return qs.stringify(data);
              }
            ]
        })
    },
    updatePwd(userId,newPassword){
        return myaxios({
            url:'/user/pwd',
            method:'put',
            data:{
                userId,
                newPassword
            },
            transformRequest: [
              data=>{
                return qs.stringify(data);
              }
            ]
        })
    }
}

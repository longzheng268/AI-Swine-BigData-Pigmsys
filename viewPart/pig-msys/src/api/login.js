//登录的ajax
import myaxios from "../utils/myaxios"
import qs from "qs"

export function login(username,userpassword) {
    return myaxios({
        url:'/user/login',
        method:'post',
        data:{
            username,
            userpassword
        },
        transformRequest: [
          data=>{
            return qs.stringify(data);
          }
        ]

    })
}

export function getUserInfo(token) {
    return myaxios({
        url:`/user/info/${token}`,
        method:'get',
        baseURL: 'http://localhost:8080'
    })
}

export function logout(token) {
    return myaxios({
        url:`/user/logout/${token}`,
        method:'get',
        baseURL: 'http://localhost:8080'
    })
}

export function aVerify(code){
    return myaxios({
        url: '/checkVerify',
        method: 'post',
        data: {code},
        transformRequest: [
            data=>{
                return qs.stringify(data);
            }
        ]
    })
}

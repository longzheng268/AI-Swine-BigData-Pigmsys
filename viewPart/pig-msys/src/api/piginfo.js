//图书信息列表的接口
import myaxios from "../utils/myaxios"

export function getPigList(){
    return myaxios({
        url:'/pigInfo/list',
        method:'get'
    })
}
export function search(page,size,searchWhere) {
    return myaxios({
        url:`/pigInfo/list/search/${page}/${size}`,
        method:'post',
        data:searchWhere
    })
}

export function add(pigForm) {
    return myaxios({
        url:'/addPig',
        method:'post',
        data:pigForm
    })
}

export function getPigById(id) {
    return myaxios({
        url:`/pigInfo/${id}`,
        method:'get'
    })
}

export function updatePigInfo(pigObj) {
    return myaxios({
        url:`/pigInfo/${pigObj.id}`,
        method:'put',
        data:pigObj
    })
}

export function deletePigById(id) {
    return myaxios({
        url:`pigInfo/${id}`,
        method:'delete'
    })
}

export function getTypeSum() {
    return myaxios({
        url:'/pig/getTypeSum',
        method:'get'
    })
}

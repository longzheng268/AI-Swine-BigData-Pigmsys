import myaxios from "../utils/myaxios";
import qs from "qs";

export default {
  getUserList() {
    return myaxios({
      url: "/userinfo/list",
      method: "get",
    });
  },
  search(page, size, searchWhere) {
    return myaxios({
      url: `/userinfo/list/search/${page}/${size}`,
      method: "post",
      data: searchWhere,
      // transformRequest: [
      //   data=>{
      //    return qs.stringify(data);
      //   }
      // ]
    });
  },
  getUserInfoById(id) {
    return myaxios({
      url: `/userinfo/${id}`,
      method: "get",
    });
  },
  updateUserInfo(formInfo) {
    return myaxios({
      url: `/userinfo/${formInfo.id}`,
      method: "put",
      data: formInfo,
    });
  },
  addUserInfo(formInfo) {
    return myaxios({
      url: "/addUser",
      method: "post",
      data: formInfo,
    });
  },
  delUserInfo(id) {
    return myaxios({
      url: `/userinfo/${id}`,
      method: "delete",
    });
  },
};

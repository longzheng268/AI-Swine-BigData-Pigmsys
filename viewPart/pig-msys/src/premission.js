import router from "./router";
import { getUserInfo } from "./api/login";

//路由的钩子函数  表示路由跳转前
router.beforeEach((to, from, next) => {
  //获取登录成功时,存储在本地的信息
  const token = localStorage.getItem("my-login-token");
  const userInfo = localStorage.getItem("my-login-user");
  if (!token) {
    //本地存储中没有令牌
    if (to.path !== "/login") {
      //没有令牌还去了除了登录路由外的路由,直接强制去登录路由
      next({ path: "/login" });
    } else {
      //没令牌去登录路由,直接去
      next();
    }
  } else {
    //有令牌
    if (to.path === "/login") {
      //有令牌去登录路由,直接去
      next();
    } else {
      //有令牌不去登录路由
      if (userInfo) {
        //本地存储有用户信息,直接去想去的路由
        next();
      } else {
        //本地存储没有用户信息
        getUserInfo(token) //根据令牌去获取用户信息
          .then((response) => {
            const respUser = response.data;
            if (respUser.flag) {
              //获取到了用户信息,存储在本地,并且去自己想去的路由
              localStorage.setItem(
                "my-login-user",
                JSON.stringify(respUser.data),
              );
              next();
            } else {
              //没获取到用户信息,直接去登录路由
              next({ path: "/login" });
            }
          })
          .catch(() => {
            next({ path: "/login" });
          });
      }
    }
  }
});

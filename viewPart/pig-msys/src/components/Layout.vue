<template>
<div>
  <Header></Header>
  <NavBar></NavBar>
  <Main></Main>
</div>
</template>

<script>
import Header from './AppHeader/index.vue'
import NavBar from './AppNavBar/index'
import Main from './AppMain'
export default {
  name: "Layout",
  components:{
    Header,
    NavBar,
    Main
  }
}
</script>

<style scoped>
.header{
  position: absolute;
  line-height: 50px;
  top: 0px;
  left: 0px;
  right: 0px;
  background-color: #00DBDE;
  background-image: linear-gradient(90deg, #00DBDE 0%, #FC00FF 100%)
}
.navbar{
  position: absolute;
  width: 200px;
  left: 0px;
  top: 50px;
  bottom: 0px;
  background-color: #0093E9;
  background-image: linear-gradient(160deg, #0093E9 0%, #80D0C7 100%);
  overflow-y:auto;
}
.main{
  position: absolute;
  background: white;
  left: 200px;
  top: 50px;
  right: 0px;
  bottom: 0px;
  padding: 5px;
  overflow-y:auto;
}
</style>

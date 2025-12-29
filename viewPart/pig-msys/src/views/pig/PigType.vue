<template>
  <div>
    <el-carousel :interval="3000" type="card" height="350px">
      <el-carousel-item v-for="item in images" :key="item">
        <img :src="item" width="100%" height="100%" />
      </el-carousel-item>
    </el-carousel>

    <el-divider><i class="el-icon-mobile-phone"></i></el-divider>
    <el-row>
      <el-col
        :span="8"
        v-for="(o, index) in imagesAbout"
        :key="o"
        :offset="index > 0 ? 0 : 0"
      >
        <el-card :body-style="{ padding: '0px' }">
          <img :src="o" class="image" />
          <div style="padding: 14px">
            <span>每日新图</span>
            <div class="bottom clearfix">
              <time class="time">{{ currentDate }}</time>
              <el-button link @click="open">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-divider content-position="left">猪的周期安排</el-divider>
    <el-tag type="success" size=""> 您可以自定义日期来查看猪的详情！ </el-tag>
    <el-calendar>
      <!-- Vue 3 语法 -->
      <template #dateCell="{ date, data }">
        <p :class="data.isSelected ? 'is-selected' : ''">
          {{ data.day.split("-").slice(1).join("-") }}
          {{ data.isSelected ? "✔️" : "" }}
        </p>
      </template>
    </el-calendar>
  </div>
</template>

<script>
export default {
  name: "PigType",
  data() {
    return {
      images: [
        "https://jmy-pic.baidu.com/0/pic/-1945732292_727420227_-1353636996.jpg",
        "https://jmy-pic.baidu.com/0/pic/-957508777_2051708623_735755312.jpg",
        "https://img0.baidu.com/it/u=2940276233,1881460354&fm=253&fmt=auto&app=138&f=JPEG?w=753&h=500",
        "https://t7.baidu.com/it/u=3766339589,272600460&fm=193&f=GIF",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpmscxz.com%2Fwp-content%2Fuploads%2F2020%2F07%2F21%2F21064303408.jpg&refer=http%3A%2F%2Fpmscxz.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655178084&t=47c2ee3d0fe347384208da126b87a0da",
      ],
      currentDate: new Date(),
      imagesAbout: [
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp3.itc.cn%2Fimages01%2F20200915%2F471c076f446c40b69cef28a418a5987c.jpeg&refer=http%3A%2F%2Fp3.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655193533&t=7cdff598e5cc0dae41e71f23430f0a52",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdpic.tiankong.com%2Fmj%2Fd8%2FQJ8750406571.jpg&refer=http%3A%2F%2Fdpic.tiankong.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655193591&t=c215b663529dba6412ca163008d56b15",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fcn.chinadaily.com.cn%2Fimg%2Fattachement%2Fjpg%2Fsite1%2F20170316%2Fd8cb8a14fcf91a347b9733.jpg&refer=http%3A%2F%2Fcn.chinadaily.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655193612&t=d2b030bd9e32f0fe7efb2bbb594d4518",
        "https://img1.baidu.com/it/u=2312288303,2027256709&fm=253&fmt=auto&app=138&f=JPEG?w=890&h=500",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fphoto.tuchong.com%2F32660%2Ff%2F1782207.jpg&refer=http%3A%2F%2Fphoto.tuchong.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655193674&t=aeb39638b306286f255bea329d50a0c1",
        "https://pic.rmb.bdstatic.com/3d29730c8668dafab77c042f0308cf28.jpeg",
      ],
    };
  },

  methods: {
    open() {
      let con =
        "MTC养猪全产业链信息化总方案，从饲料到种猪、扩繁、育肥、屠宰加工、物流等，规模化养猪各环节的信息化，可分为养殖现场管理，农场管理，公司管控。方案旨在帮助养猪企业搭建养殖一体化信息化平台，全面加强传统养殖信息化建设，提高企业养殖产业链管理水平！";

      this.$alert(con, "每日新图", {
        confirmButtonText: "确定",
        callback: (action) => {
          this.$message({
            type: "info",
            message: `action: ${action}`,
          });
        },
      });
    },
  },
};
</script>

<style>
.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.time {
  font-size: 13px;
  color: #999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
}

.image {
  width: 100%;
  height: 250px;
  display: block;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

.is-selected {
  color: #1989fa;
}
</style>

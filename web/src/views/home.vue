<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"

          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
          <router-link :to="'/'">
            <MailOutlined />
            <span>Welcome!</span>
          </router-link>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span><user-outlined />{{item.name}}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>

      </a-menu>
    </a-layout-sider>
<!--    <a-layout style="padding: 0 24px 24px">-->
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
<!--下面是ant design vue 裡面的list代碼 https://2x.antdv.com/components/list-cn-->
<!--        gutter 200px 間距 -->
          <a-list item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }"  :data-source="ebooks">

            <template #renderItem="{ item }">
              <a-list-item key="item.name">
                <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
                </template>

                <a-list-item-meta :description="item.description">
                  <template #title>
                    <a :href="item.href">{{ item.name }}</a>
                  </template>
                  <template #avatar><a-avatar :src="item.cover" /></template>
                </a-list-item-meta>

              </a-list-item>
            </template>
          </a-list>

<!--ant design view的list 模板代碼-->



      </a-layout-content>
<!--    </a-layout>-->
  </a-layout>
</template>



<script lang="ts">
import { defineComponent,onMounted,ref,reactive,toRef } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";

// ant design view的list 模板代碼
// const listData: any = [];
// for (let i = 0; i < 23; i++) {
//   listData.push({
//     href: 'https://www.antdv.com/',
//     title: `ant design vue part ${i}`,
//     avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
//     description:
//         'Ant Design, a design language for background applications, is refined by Ant UED Team.',
//     content:
//         'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
//   });
// }

export default defineComponent({
  name: 'Home',
  //包含vue2的 data(各種變量), mounted and methods
  setup(){
    console.log("setup");
    //ref響應式數據 更改js的值 會時時更新
    const ebooks = ref();
    //books自己取的 因為一定要放到一個屬性裡面
    // const ebooksR = reactive({books:[]});

    const level1 =  ref();
    let categorys: any;
    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数组：", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);
        } else {
          message.error(data.message);
        }
      });
    };

    const handleClick = () => {
      console.log("menu click")
    };



    //onMounted 頁面渲染完和組件都加載完後的生命週期函數, 可以避免頁面還沒載完 就去執行函數
    onMounted(()=>{
      handleQueryCategory();
      console.log("onMounted");
      axios.get("/ebook/list",{
        //首頁顯示全部的資料  因為電子書應該不會超過1000 所以設1000
        params:{
          page:1,
          size: 1000
        }
      })
          // .then(function (response) {   和下面寫法一樣
          .then((response) => {
            const data = response.data;
            ebooks.value = data.content.list;
            // ebooksR.books = data.content;

    });

    });
    return{
      //下面是ref的方法回傳
      ebooks,
      //reactive的方法回傳  最前面的ebooks是自己定義的變量 toRef是把所有屬性變成響應式變量   第一個參數是reactive的 const 變數, 第二個是裡面的屬性,
      // ebooksR:toRef(ebooksR,"books"),

      // 測試數據 ant design view的list 模板代碼
      // listData,

      pagination : {
        onChange: (page: number) => {
          console.log(page);
        },
        pageSize: 3,
      },
      actions:  [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ],
      handleClick,
      level1,
    }
  }

});
</script>
<!--scoped 表示只在當前頁面home.vue生效-->
<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>

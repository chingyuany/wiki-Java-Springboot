<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
<!--      openkeys 要展開的節點-->
      <a-menu
          mode="inline"
          :openKeys="openKeys"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
          <MailOutlined/>
          <span>Welcome!</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id" :disabled="true">
          <template v-slot:title>
            <span><user-outlined/>{{ item.name }}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined/>
            <span>{{ child.name }}</span>
          </a-menu-item>
        </a-sub-menu>

      </a-menu>
    </a-layout-sider>
    <!--    <a-layout style="padding: 0 24px 24px">-->
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <!--        顯示歡迎頁面 或下面的list-->
      <div class="welcome" v-show="isShowWelcome">
         <the-welcome></the-welcome>

      </div>
      <!--下面是ant design vue 裡面的list代碼 https://2x.antdv.com/components/list-cn-->
      <!--        gutter 200px 間距 -->
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }"
              :data-source="ebooks">

        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
               <span>
                <component v-bind:is="'FileOutlined'" style="margin-right: 8px"/>
                {{ item.docCount }}
              </span>
              <span>
                <component v-bind:is="'UserOutlined'" style="margin-right: 8px"/>
                {{ item.viewCount }}
              </span>
              <span>
                <component v-bind:is="'LikeOutlined'" style="margin-right: 8px"/>
                {{ item.voteCount }}
              </span>
            </template>

            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar>
                <a-avatar :src="item.cover"/>
              </template>
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
import {defineComponent, onMounted, ref, reactive, toRef} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import TheWelcome from '@/components/the-welcome.vue';
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
  components: {
    TheWelcome
  },
  //包含vue2的 data(各種變量), mounted and methods
  setup() {
    console.log("setup");
    //ref響應式數據 更改js的值 會時時更新
    const ebooks = ref();
    //books自己取的 因為一定要放到一個屬性裡面
    // const ebooksR = reactive({books:[]});
    const openKeys =  ref();
    const level1 = ref();
    let categorys: any;
    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("original data array：", categorys);
          // 加载完分类后，将侧边栏全部展开
          openKeys.value = [];
          for (let i = 0; i < categorys.length; i++) {
            openKeys.value.push(categorys[i].id)
          }
          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("tree data array：", level1.value);
        } else {
          message.error(data.message);
        }
      });
    };


    const isShowWelcome = ref(true);
    let categoryId2 = 0;
    const handleQueryEbook = () => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          categoryId2: categoryId2
        }
      }).then((response) => {
        const data = response.data;
        ebooks.value = data.content.list;
        // ebooks1.books = data.content;
      });
    };


    const handleClick = (value: any) => {
      // console.log("menu click", value)
      if (value.key === 'welcome') {
        isShowWelcome.value = true;
      } else {
        isShowWelcome.value = false;
        //只查詢二級分類 因為menu模板的onclick只有二級menu才有效
        categoryId2 = value.key
        handleQueryEbook();
      }
      //另外一種寫法
      // isShowWelcome.value = value.key === 'welcome';
    };


    //onMounted 頁面渲染完和組件都加載完後的生命週期函數, 可以避免頁面還沒載完 就去執行函數
    onMounted(() => {
      handleQueryCategory();
    });


    return {
      //下面是ref的方法回傳
      ebooks,
      //reactive的方法回傳  最前面的ebooks是自己定義的變量 toRef是把所有屬性變成響應式變量   第一個參數是reactive的 const 變數, 第二個是裡面的屬性,
      // ebooksR:toRef(ebooksR,"books"),

      // 測試數據 ant design view的list 模板代碼
      // listData,

      pagination: {
        onChange: (page: number) => {
          console.log(page);
        },
        pageSize: 3,
      },

      handleClick,
      level1,
      isShowWelcome,
      openKeys
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

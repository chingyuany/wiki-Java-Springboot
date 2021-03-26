<!--slots 自定義渲染, custom render 對值渲染, title 表頭渲染-->
<!--  看6-3  6-6介紹-->
<!--record 是Ebook class-->
<!--loading 是內建的 true的時候會顯示讀取的動畫-->
<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
<!--        inline 排在同一列-->
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="Name">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              Search
            </a-button>
          </a-form-item>
          <a-form-item>
          <a-button type="primary" @click="add()" >
            New
          </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
<!--        套用的樣式 customRender -->
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
<!--          text 和record是一樣的值 category 是自己取的名字-->
          <template v-slot:category="{ text, record }">
            <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
          </template>

<!--        record 是一列的數據-->
        <template v-slot:action="{ text, record }">
<!--          空格組件-->
          <a-space size="small">
            <router-link to ="/admin/doc">
            <a-button type="primary" >
              Document Mgmt
            </a-button>
            </router-link>
            <a-button type="primary" @click="edit(record)">
              Edit
            </a-button>
            <a-popconfirm
                title="Delete cannot recover. Are you sure delete this row?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleDelete(record.id)"

            >
              <a-button type="danger">
                Delete
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
   <a-modal
      title="Ebook Form"
        v-model:visible="modalVisible"
       :confirm-loading="modalLoading"
        @ok="handleModalOk"
      >
     <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
       <a-form-item label="Cover">
         <a-input v-model:value="ebook.cover" />
       </a-form-item>
       <a-form-item label="Name">
         <a-input v-model:value="ebook.name" />
       </a-form-item>
       <a-form-item label="Category">
         <a-cascader
             v-model:value="categoryIds"
             :field-names="{ label: 'name', value: 'id', children: 'children' }"
             :options="level1"
         />
       </a-form-item>
       <a-form-item label="Description">
         <a-input v-model:value="ebook.description" type="textarea" />
       </a-form-item>
     </a-form>



    </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminEbook',
  setup() {
    const param = ref();
    param.value = {};
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0
    });
    //loading 是內建的 true的時候會顯示讀取的動畫
    const loading = ref(false);

    const columns = [
      {
        title: 'Cover',
        dataIndex: 'cover',
        slots: { customRender: 'cover' }
      },
      {
        title: 'Name',
        dataIndex: 'name'
      },
      {
        title: 'Category',
        slots: { customRender: 'category' }
      },
      {
        title: 'Document Amount',
        dataIndex: 'docCount'
      },
      {
        title: 'Read',
        dataIndex: 'viewCount'
      },
      {
        title: 'Like',
        dataIndex: 'voteCount'
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      ebooks.value = [];
      //另一種get方式為拼接  "?page="+page+"&" 下面是第一種方法 直接寫params 固定寫法
      axios.get("/ebook/list", {
        params: {
          page: params.page,
          size: params.size,
          name: param.value.name
        }
      }).then((response) => {

        loading.value = false;
        //response 是後端傳回來的值
        const data = response.data;
        if (data.success){

          //響應變量用.value
          ebooks.value = data.content.list;

          // 重置分页按钮  pagination.value.current是內建的函數
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        }else{
          message.error(data.message);
        }

      });
    };

    /**
     * 有人點擊頁數按鈕 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };
    // -------- Edit 表单 ---------
    /**
     * 数组，[100, 101]对应：前端开发 / Vue
     */
    const categoryIds = ref();
    const ebook = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
      //post 不需要像get一樣寫param
      axios.post("/ebook/save", ebook.value
      ).then((response) => {
        modalLoading.value = false;

          //response 是後端傳回來的值
        const data = response.data;
        if (data.success){
          modalVisible.value = false;

          //reload form
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        }else{
          message.error(data.message);
        }
      });


    };

    /**
     * 编辑
     */
    const edit = (record:any) => {
      modalVisible.value = true;
      ebook.value = Tool.copy(record);
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
    };

    const add = () => {
      modalVisible.value = true;
      ebook.value = {}
    };
    //後端long 前端number
    const handleDelete = (id:number) =>{
      axios.delete("/ebook/delete/"+id
      ).then((response) => {
        //response 是後端傳回來的值, data = common response
        const data = response.data;
        if (data.success){
          //reload form
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        }
      });
    }
    const level1 =  ref();
    let categorys: any;
    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      loading.value = true;
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数组：", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);
          // 7-7 加载完分类后，再加载电子书，否则如果分类树加载很慢，则电子书渲染会报错
          // 因為handlequery 和handleQueryCategory 都是異步, handle query完 ebook有值後,html表格才能渲染 渲染時呼叫getCategoryName foreach報錯

          handleQuery({
            page: 1,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      })};
    const getCategoryName = (cid: number) => {
      // console.log(cid)
      let result = "";
      categorys.forEach((item: any) => {
        if (item.id === cid) {
          // return item.name; // 注意，这里直接return不起作用
          result = item.name;
        }
      });
      return result;
    };

    //初始化頁面的時候 也是需要先查詢一次 第一頁
    onMounted(() => {
      handleQueryCategory();

    });

    return {
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange,
      param,
      edit,
      add,
      handleDelete,

      getCategoryName,
      modalVisible,
      modalLoading,
      handleModalOk,
      ebook,
      handleQuery,
      categoryIds,
      level1,

    }
  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
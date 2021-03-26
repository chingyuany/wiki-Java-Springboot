<!--slots 自定義渲染, custom render 對值渲染, title 表頭渲染-->
<!--  看6-3  6-6介紹-->
<!--record 是Doc class-->
<!--loading 是內建的 true的時候會顯示讀取的動畫-->
<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
<!--        inline 排在同一列-->
        <a-form layout="inline" :model="param">
<!--          <a-form-item>-->
<!--            <a-input v-model:value="param.name" placeholder="Name">-->
<!--            </a-input>-->
<!--          </a-form-item>-->
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              Show All
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
          :data-source="level1"

          :loading="loading"
          :pagination="false"
      >
<!--        套用的樣式 customRender -->
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
<!--        record 是一列的數據-->
        <template v-slot:action="{ text, record }">
<!--          空格組件-->
          <a-space size="small">
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
      title="Doc Form"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="Name">
        <a-input v-model:value="doc.name" />
      </a-form-item>
      <a-form-item label="Parent Doc">
        <a-select
            v-model:value="doc.parent"
            ref="select"
        >
          <a-select-option :value="0">
            None
          </a-select-option>
<!--          循環1級分類 把name顯示   id放到 doc.parent   如果當前文本框分類的id 是目前選項的id 就變灰色不能選  因為選自己會把自己父分類改成自己本身ID-->
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">
<!--            在Html裡面要使用響應式變量就要用{{}}-->
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Sort">
        <a-input v-model:value="doc.sort" />
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
  name: 'AdminDoc',
  setup() {
    const param = ref();
    param.value = {};
    const docs = ref();

    //loading 是內建的 true的時候會顯示讀取的動畫
    const loading = ref(false);

    const columns = [
      {
        title: 'Name',
        dataIndex: 'name'
      },
      {
        title: 'Parent Doc',
        key: 'parent',
        dataIndex: 'parent'
      },
      {
        title: 'Sort',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];
    /**
     * 一级分类树，children属性就是二级分类
     * [{
     *   id: "",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */

    const level1 = ref(); // 一级分类树，children属性就是二级分类
    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get("/doc/all").then((response) => {

        loading.value = false;
        //response 是後端傳回來的值
        const data = response.data;
        if (data.success){

          //響應變量用.value
          docs.value = data.content;
          console.log("Original table：", docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("Tree data：", level1);


        }else{
          message.error(data.message);
        }

      });
    };



    // -------- Edit 表单 ---------
    const doc = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true
      //post 不需要像get一樣寫param
      axios.post("/doc/save", doc.value
      ).then((response) => {
        modalLoading.value = false;
          //response 是後端傳回來的值
        const data = response.data;
        if (data.success){
          modalVisible.value = false;

          //reload form
          handleQuery();
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
      doc.value = Tool.copy(record);
    };

    const add = () => {
      modalVisible.value = true;
      doc.value = {}
    };
    //後端long 前端number
    const handleDelete = (id:number) =>{
      axios.delete("/doc/delete/"+id
      ).then((response) => {
        //response 是後端傳回來的值, data = common response
        const data = response.data;
        if (data.success){
          //reload form
          handleQuery();
        }
      });
    }
    //初始化頁面的時候 也是需要先查詢一次 第一頁
    onMounted(() => {
      handleQuery();
    });

    return {
      // docs,
      level1,
      columns,
      loading,

      param,
      edit,
      add,
      handleDelete,

      modalVisible,
      modalLoading,
      handleModalOk,
      doc,
      handleQuery
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
<!--slots 自定義渲染, custom render 對值渲染, title 表頭渲染-->
<!--  看6-3  6-6介紹-->
<!--record 是Doc class-->
<!--loading 是內建的 true的時候會顯示讀取的動畫-->
<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
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
<!--          v-if 是說當讀到資料level1後 才去展開a-table(defaultExpandAllRows)-->
<!--         因為defaultExpandAllRows 是初始就執行 但是還沒讀到level1的資料-->
        <a-table
            v-if="level1.length > 0"
            :columns="columns"
            :row-key="record => record.id"
            :data-source="level1"

            :loading="loading"
            :pagination="false"
            size="small"
            :defaultExpandAllRows="true"
        >
          <!--        套用的樣式 customRender -->

          <template #name="{ text, record}">

            {{record.sort}}{{text}}
          </template>
          <!--        record 是一列的數據-->
          <template v-slot:action="{ text, record }">
            <!--          空格組件-->
            <a-space size="small">
              <a-button type="primary" @click="edit(record)" size ="small">
                Edit
              </a-button>
              <a-popconfirm
                  title="Delete cannot recover. Are you sure delete this row?"
                  ok-text="Yes"
                  cancel-text="No"
                  @confirm="handleDelete(record.id)"

              >
                <a-button type="danger" size ="small">
                  Delete
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </a-table>
        </a-col>
        <a-col :span="16">

            <p>
              <a-form layout="inline" :model="param">
                <a-form-item>
                  <a-button type="primary" @click="handleSave()">
                    Save
                  </a-button>
                </a-form-item>
              </a-form>
            </p>
            <a-form :model="doc" layout="vertical">

              <a-form-item>
                <a-input v-model:value="doc.name" placeholder="Name"/>
              </a-form-item>

            <a-form-item>
            <!--          : 後面可以放變量, 沒有冒號 後面就是字串, replace fields 是把原本a-tree-select裡面的欄位名稱換成我們自訂的-->
            <a-tree-select
                v-model:value="doc.parent"
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                :tree-data="treeSelectData"
                placeholder="Please select parent doc"
                tree-default-expand-all

                :replaceFields ="{title:'name',key:'id',value:'id'}"
            >

            </a-tree-select>

          </a-form-item>
<!--            <a-form-item label="Parent Doc">-->
<!--              <a-select-->
<!--                  v-model:value="doc.parent"-->
<!--                  ref="select"-->
<!--              >-->
<!--                <a-select-option :value="0">-->
<!--                  None-->
<!--                </a-select-option>-->
<!--                &lt;!&ndash;          循環1級分類 把name顯示   id放到 doc.parent   如果當前文本框分類的id 是目前選項的id 就變灰色不能選  因為選自己會把自己父分類改成自己本身ID&ndash;&gt;-->
<!--                <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">-->
<!--                  &lt;!&ndash;            在Html裡面要使用響應式變量就要用{{}}&ndash;&gt;-->
<!--                  {{c.name}}-->
<!--                </a-select-option>-->
<!--              </a-select>-->
<!--            </a-form-item>-->
            <a-form-item >
              <a-input v-model:value="doc.sort" placeholder="order"/>
            </a-form-item>

              <a-form-item>
                <a-button type="primary" @click="handlePreviewContent()">
                  <EyeOutlined /> Content Preview
                </a-button>
              </a-form-item>

            <a-form-item >
              <div id="content">

              </div>
            </a-form-item>
          </a-form>

        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>
<!--  <a-modal-->
<!--      title="Doc Form"-->
<!--      v-model:visible="modalVisible"-->
<!--      :confirm-loading="modalLoading"-->
<!--      @ok="handleModalOk"-->
<!--  >-->

<!--  </a-modal>-->
</template>

<script lang="ts">
import { defineComponent, onMounted, ref,createVNode } from 'vue';
import axios from 'axios';
import { message,Modal } from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
import E from 'wangeditor';
import i18next from "i18next";
export default defineComponent({
  name: 'AdminDoc',
  setup: function () {
    const route = useRoute()
    // console.log("路由：", route);
    // console.log("route.path：", route.path);
    // console.log("route.query：", route.query);
    //用另一種傳遞方式 可以用params取參數值  admin/doc/docid
    // console.log("route.param：", route.params);
    // console.log("route.fullPath：", route.fullPath);
    // console.log("route.name：", route.name);
    // console.log("route.meta：", route.meta);
    const param = ref();
    param.value = {};
    const docs = ref();

    //loading 是內建的 true的時候會顯示讀取的動畫
    const loading = ref(false);
    // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];
    const columns = [
      {
        title: 'Name',
        dataIndex: 'name',
        slots: { customRender: 'name' }
      },

      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
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
    //因為上面table有 v-if 不能null.length, 所以這邊初始空陣列
    level1.value = [];
    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get("/doc/all/"+ route.query.ebookId).then((response) => {

        loading.value = false;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        level1.value = [];
        //response 是後端傳回來的值
        const data = response.data;
        if (data.success) {

          //響應變量用.value
          docs.value = data.content;
          console.log("Original table：", docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("Tree data：", level1);
          // 父文档下拉框初始化，相当于点击新增
          treeSelectData.value = Tool.copy(level1.value) || [];
          // 为选择树添加一个"无"
          treeSelectData.value.unshift({id: 0, name: 'None'});

        } else {
          message.error(data.message);
        }

      });
    };



    // -------- Edit 表单 ---------
    // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量

    const doc = ref();
    //因為一進來頁面  就可以直接新增紀錄 要先給id
    doc.value = {ebookId: route.query.ebookId};
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const editor = new E('#content')
    //改成英文
    editor.config.lang = 'en'
    editor.i18next = i18next
    //富文本 優先層級原本是500 設成0 才不會擋住其他
    editor.config.zIndex = 0;
    const handleSave = () => {
      modalLoading.value = true
      //wangeditor 官方頁面的方法
      doc.value.content = editor.txt.html();
      //post 不需要像get一樣寫param
      axios.post("/doc/save", doc.value
      ).then((response) => {
        modalLoading.value = false;
        //response 是後端傳回來的值
        const data = response.data;
        if (data.success) {
           message.success("Save Successful");

          //reload form
          handleQuery();
        } else {
          message.error(data.message);
        }
      });


    };

    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          // console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };
    const deleteIds: Array<string> = [];
    const deleteNames: Array<string> = [];
    const getDeleteIds = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          deleteIds.push(node.id);
          deleteNames.push(node.name);
          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    };
    const handleQueryContent = () => {

      axios.get("/doc/find-content/"+doc.value.id).then((response) => {
        //response 是後端傳回來的值
        const data = response.data;
        if (data.success) {
          //富文本賦值
          editor.txt.html(data.content)
        } else {
          message.error(data.message);
        }

      });
    };
    /**
     * 编辑
     */
    const edit = (record: any) => {
      //清空富文本
      editor.txt.html("")
      modalVisible.value = true;
      doc.value = Tool.copy(record);
      handleQueryContent();
      // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value) ;
      setDisable(treeSelectData.value, record.id);

      // 为选择树添加一个"无"  unshift往數組前面添加
      treeSelectData.value.unshift({id: 0, name: 'None'});


    };

    const add = () => {
      //清空富文本
      editor.txt.html("")
      modalVisible.value = true;
      doc.value = {ebookId: route.query.ebookId}
      treeSelectData.value = Tool.copy(level1.value)|| [];
      // 为选择树添加一个"无"  unshift往數組前面添加
      treeSelectData.value.unshift({id: 0, name: 'None'});

    };
    //後端long 前端number
    const handleDelete = (id: number) => {
      // 清空数组，否则多次删除时，数组会一直增加
      deleteIds.length = 0;
      deleteNames.length = 0;
      getDeleteIds(level1.value,id);
      Modal.confirm({
        title: 'Important Reminder',
        icon: createVNode(ExclamationCircleOutlined),
        content: 'Deleting：【' + deleteNames.join("，") + "】cannot recover，confirm delete？",
        onOk() {
          // console.log(ids)
          axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
            const data = response.data; // data = commonResp
            if (data.success) {
              // 重新加载列表
              handleQuery();
            }
          });
        },
      });
    }

    // ----------------富文本预览--------------
    const drawerVisible = ref(false);
    const previewHtml = ref();
    const handlePreviewContent = () => {
      const html = editor.txt.html();
      previewHtml.value = html;
      drawerVisible.value = true;
    };
    const onDrawerClose = () => {
      drawerVisible.value = false;
    };

    onMounted(() => {
      handleQuery();
      editor.create();
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
      handleSave,
      doc,
      handleQuery,
      treeSelectData,
      handleQueryContent,

      drawerVisible,
      previewHtml,
      handlePreviewContent,
      onDrawerClose,
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
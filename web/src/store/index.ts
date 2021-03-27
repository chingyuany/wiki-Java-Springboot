import { createStore } from 'vuex'
// 雖然已經引用 js/session-storage.js 需要宣告才能用
declare let SessionStorage: any;
const USER = "USER";
//vuex 是全局式的響應變量
const store = createStore({
  state: {
    //會先去session裡面抓取上面定義的USER變量 如果抓不到 就會{}
    user:SessionStorage.get(USER) || {}
  },
  //變量操作 同步
  mutations: {
    //這裡的user是外面傳進來的 不是上面的  state已經有了 不需要再傳
    setUser(state,user){
      state.user = user;
      //把傳進來的user 放到 USER變量裡面
      //因為只放到state.user 刷新後就會不見 要放到sessionstorage 才會保留
      SessionStorage.set(USER,user);
    }
  },
  //變量操作 異步
  actions: {
  },
  modules: {
  }
})
//返回出去
export default store;
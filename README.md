# wiki
* This is a [Knowledge base](http://wiki.chingyuanyang.com) that includes E-books(wangEditor), categories, user management and statistics reports (Apache ECharts), the
interface adopts Restful API and Axios, the Ant Design of Vue interacts with back end via Nginx reverse proxy.  
* The tech stack is Java Spring Boot, Vue3, TypeScript, Redis, MySQL, RocketMQ, Nginx.  
* It utilized MyBatis generator for CRUD, custom SQL and collocates with PageHelper plugin to reduce SQL code and apply
Snowflake algorithm to generate unique IDs, employ Spring schedule in Cron expression to update tables and statistics report.  
* In addition, it apply RocketMQ to dispatch messages via WebSocket to all clients and use Spring AOP interceptors to intercept the
request and provide the request parameters, address logs for DevOps maintenance.The single sign-on adpot Vuex to store login user info.  

* The testing account ID is admin, password is admin1234.  

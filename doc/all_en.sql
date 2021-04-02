#电子书表

drop table if exists `ebook`;
create table `ebook`
(
    `id`           bigint not null comment 'id',
    `name`         varchar(50) comment 'name',
    `category1_id` bigint comment 'category1',
    `category2_id` bigint comment 'category2',
    `description`  varchar(200) comment 'Description',
    `cover`        varchar(200) comment 'Cover',
    `doc_count`    int    not null default 0 comment 'Doc count',
    `view_count`   int    not null default 0 comment 'Read count',
    `vote_count`   int    not null default 0 comment 'Like count',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='ebook';

-- insert into `ebook` (id, name, description)
-- values (1, 'Spring Boot 入门教程', '零基础入门 Java 开发，企业级应用开发最佳首选框架');
-- insert into `ebook` (id, name, description)
-- values (2, 'Vue 入门教程', '零基础入门 Vue 开发，企业级应用开发最佳首选框架');
-- insert into `ebook` (id, name, description)
-- values (3, 'Python 入门教程', '零基础入门 Python 开发，企业级应用开发最佳首选框架');
-- insert into `ebook` (id, name, description)
-- values (4, 'Mysql 入门教程', '零基础入门 Mysql 开发，企业级应用开发最佳首选框架');
-- insert into `ebook` (id, name, description)
-- values (5, 'Oracle 入门教程', '零基础入门 Oracle 开发，企业级应用开发最佳首选框架');

drop table if exists `test`;
create table `test`
(
    `id`       bigint not null comment 'id',
    `name`     varchar(50) comment 'name',
    `password` varchar(50) comment 'password',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='test';

insert into `test` (id, name, password)
values (1, 'test', 'password');

#分类
drop table if exists `category`;
create table `category`
(
    `id`     bigint      not null comment 'id',
    `parent` bigint      not null default 0 comment 'parent id',
    `name`   varchar(50) not null comment 'name',
    `sort`   int comment 'sort',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='category';
--
-- insert into `category` (id, parent, name, sort)
-- values (100, 000, '前端开发', 100);
-- insert into `category` (id, parent, name, sort)
-- values (101, 100, 'Vue', 101);
-- insert into `category` (id, parent, name, sort)
-- values (102, 100, 'HTML & CSS', 102);
-- insert into `category` (id, parent, name, sort)
-- values (200, 000, 'Java', 200);
-- insert into `category` (id, parent, name, sort)
-- values (201, 200, '基础应用', 201);
-- insert into `category` (id, parent, name, sort)
-- values (202, 200, '框架应用', 202);
-- insert into `category` (id, parent, name, sort)
-- values (300, 000, 'Python', 300);
-- insert into `category` (id, parent, name, sort)
-- values (301, 300, '基础应用', 301);
-- insert into `category` (id, parent, name, sort)
-- values (302, 300, '进阶方向应用', 302);
-- insert into `category` (id, parent, name, sort)
-- values (400, 000, '数据库', 400);
-- insert into `category` (id, parent, name, sort)
-- values (401, 400, 'MySQL', 401);
-- insert into `category` (id, parent, name, sort)
-- values (500, 000, '其它', 500);
-- insert into `category` (id, parent, name, sort)
-- values (501, 500, '服务器', 501);
-- insert into `category` (id, parent, name, sort)
-- values (502, 500, '开发工具', 502);
-- insert into `category` (id, parent, name, sort)
-- values (503, 500, '热门服务端语言', 503);

-- 文档表
drop table if exists `doc`;
create table `doc`
(
    `id`         bigint      not null comment 'id',
    `ebook_id`   bigint      not null default 0 comment 'Ebook id',
    `parent`     bigint      not null default 0 comment 'parent id',
    `name`       varchar(50) not null comment 'name',
    `sort`       int comment 'sort',
    `view_count` int                  default 0 comment 'read count',
    `vote_count` int                  default 0 comment 'like count',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='document';

-- insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
-- values (1, 1, 0, '文档1', 1, 0, 0);
-- insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
-- values (2, 1, 1, '文档1.1', 1, 0, 0);
-- insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
-- values (3, 1, 0, '文档2', 2, 0, 0);
-- insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
-- values (4, 1, 3, '文档2.1', 1, 0, 0);
-- insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
-- values (5, 1, 3, '文档2.2', 2, 0, 0);
-- insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
-- values (6, 1, 5, '文档2.2.1', 1, 0, 0);

-- 文档内容
drop table if exists `content`;
create table `content`
(
    `id`      bigint     not null comment 'doc id',
    `content` mediumtext not null comment 'content',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='doc content';

-- 用户表
drop table if exists `user`;
create table `user`
(
    `id`         bigint      not null comment 'ID',
    `login_name` varchar(50) not null comment 'login name',
    `name`       varchar(50) comment 'nick name',
    `password`   char(32)    not null comment 'password',
    primary key (`id`),
    unique key `login_name_unique` (`login_name`)
) engine=innodb default charset=utf8mb4 comment='user';

-- insert into `user` (id, `login_name`, `name`, `password`)
-- values (1, 'test', '测试', 'e70e2222a9d67c4f2eae107533359aa4');

-- 电子书快照表
drop table if exists `ebook_snapshot`;
create table `ebook_snapshot`
(
    `id`            bigint auto_increment not null comment 'id',
    `ebook_id`      bigint not null default 0 comment 'ebook id',
    `date`          date   not null comment 'snapshot date',
    `view_count`    int    not null default 0 comment 'read count',
    `vote_count`    int    not null default 0 comment 'like count',
    `view_increase` int    not null default 0 comment 'read increase',
    `vote_increase` int    not null default 0 comment 'like increase',
    primary key (`id`),
    unique key `ebook_id_date_unique` (`ebook_id`, `date`)
) engine=innodb default charset=utf8mb4 comment='ebook_snapshot';


drop table if exists `demo`;
create table `demo`
(
    `id`   bigint not null comment 'id',
    `name` varchar(50) comment 'name',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='demo';

-- insert into `demo` (id, name)
-- values (1, '测试');

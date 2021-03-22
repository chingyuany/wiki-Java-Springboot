drop table if exists `test`;
create table `test`(
    `id` bigint not null,
    `name` varchar(50),
    `password` varchar(50),
    primary key (`id`)
);
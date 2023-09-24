create table user
(
    id             bigint auto_increment comment 'id'
        primary key,
    username       varchar(256)                       null comment '用户昵称',
    userAccount    varchar(256)                       null comment '账号',
    avatarUrl      varchar(1024)                      null comment '用户头像',
    gender         tinyint                            null comment '性别',
    password       varchar(512)                       not null comment '密码',
    phone          varchar(128)                       null comment '电话',
    email          varchar(512)                       null comment '邮箱',
    userStatus     int      default 0                 not null comment '用户状态  0 - 正常',
    createTime     datetime default CURRENT_TIMESTAMP null comment '数据创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '数据更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    userRole       int      default 0                 not null comment '用户角色  0 - 普通用户  1 - 管理员',
    invitationCode varchar(512)                       null comment '邀请码'
);

create table invitationlib
(
    invitationCode varchar(512)  not null comment '邀请码'
        primary key,
    isUsed         int default 0 not null comment '是否已被使用  0 -未使用'
);


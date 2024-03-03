
-- 切换库
use ry-vue;

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 null comment '点赞数',
    favourNum  int      default 0                 null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 图表信息表
create table if not exists chart
(
    id          bigint auto_increment comment 'id' primary key,
    goal        text                               null comment '分析目标',
    chartName   varchar(256)                       null comment '图表名称',
    chartData   text                               null comment '图表数据',
    chartType   varchar(256)                       null comment '图表类型',
    genChart    text                               null comment '生成的图表信息',
    genResult   text                               null comment '生成的分析结论',
    -- 任务状态字段(排队中wait、执行中running、已完成succeed、失败failed)
    status      varchar(128)                       not null default 'wait' comment 'wait,running,succeed,failed',
    -- 任务执行信息字段
    execMessage text                               null comment '执行信息',
    userId      bigint                             null comment '创建图标用户id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment '图表信息表' collate = utf8mb4_unicode_ci;

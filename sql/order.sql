
-- 切换库
use ry-vue;

-- 为用户表增加‘通行货币’
ALTER TABLE sys_user ADD COLUMN currency INT DEFAULT 5 COMMENT '通行货币';
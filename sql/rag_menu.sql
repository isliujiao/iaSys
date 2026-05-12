-- ============================================
-- RAG 文档管理菜单 SQL 脚本
-- ============================================
-- 说明：执行此脚本添加文档管理菜单到系统
-- 菜单路径：系统管理 -> 文档管理
-- 权限：仅管理员可见
-- ============================================

-- 1. 查询系统管理菜单ID（假设 parent_id 为 100）
-- 请根据实际系统菜单ID调整
-- SELECT menu_id, menu_name FROM sys_menu WHERE menu_name = '系统管理';

-- 2. 插入 RAG 文档管理菜单
-- 注意：请根据实际的 '系统管理' 菜单ID调整 parent_id

-- 2.1 查询系统管理的 menu_id
-- (如果已知可跳过此查询)
-- SELECT menu_id FROM sys_menu WHERE perms = 'system' LIMIT 1;

-- 2.2 插入菜单记录（请将下面的 {parent_id} 替换为实际的父菜单ID）
-- 假设系统管理的 menu_id = 100

INSERT INTO sys_menu (
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    visible,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    '文档管理',
    100,  -- 请根据实际情况调整父菜单ID
    5,    -- 排序号
    'rag',
    'system/rag/index',
    'C',  -- C: 菜单，M: 按钮
    '0',  -- 0: 显示，1: 隐藏
    'rag:document:list',
    'documentation',
    'admin',
    NOW(),
    'admin',
    NOW(),
    'RAG文档管理菜单'
);

-- 3. 插入按钮权限（用于权限控制）
-- 3.1 新增按钮
INSERT INTO sys_menu (
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    visible,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    '新增',
    (SELECT m.menu_id FROM (SELECT menu_id FROM sys_menu WHERE perms = 'rag:document:list') m),
    1,
    '',
    NULL,
    'F',  -- F: 按钮
    '0',
    'rag:document:add',
    '#',
    'admin',
    NOW(),
    'admin',
    NOW(),
    ''
);

-- 3.2 修改按钮
INSERT INTO sys_menu (
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    visible,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    '修改',
    (SELECT m.menu_id FROM (SELECT menu_id FROM sys_menu WHERE perms = 'rag:document:list') m),
    2,
    '',
    NULL,
    'F',
    '0',
    'rag:document:edit',
    '#',
    'admin',
    NOW(),
    'admin',
    NOW(),
    ''
);

-- 3.3 删除按钮
INSERT INTO sys_menu (
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    visible,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    '删除',
    (SELECT m.menu_id FROM (SELECT menu_id FROM sys_menu WHERE perms = 'rag:document:list') m),
    3,
    '',
    NULL,
    'F',
    '0',
    'rag:document:remove',
    '#',
    'admin',
    NOW(),
    'admin',
    NOW(),
    ''
);

-- 4. 给角色分配权限（可选）
-- 4.1 给管理员角色分配文档管理权限
-- INSERT INTO sys_role_menu (role_id, menu_id)
-- SELECT 1, menu_id FROM sys_menu WHERE perms IN ('rag:document:list', 'rag:document:add', 'rag:document:edit', 'rag:document:remove');

-- ============================================
-- 执行后需要：
-- 1. 刷新浏览器或重新登录后台系统
-- 2. 菜单将出现在：系统管理 -> 文档管理
-- 3. 只有拥有管理员角色的用户可以看到
-- ============================================

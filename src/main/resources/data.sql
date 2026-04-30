-- ====================================
-- 修复中文乱码 - 清空并重新插入数据
-- ====================================
USE aitest;

-- 清空现有数据
TRUNCATE TABLE `loan_application`;
TRUNCATE TABLE `customer`;

-- 重新插入客户数据
INSERT INTO `customer` (`name`, `id_no`, `phone`, `email`) VALUES
('张三', '110101199001011234', '13800138001', 'zhangsan@example.com'),
('李四', '110101199002022345', '13800138002', 'lisi@example.com'),
('王五', '110101199003033456', '13800138003', 'wangwu@example.com');

-- 重新插入贷款申请数据
INSERT INTO `loan_application` (`application_no`, `customer_id`, `customer_name`, `customer_id_no`, `customer_phone`, `loan_amount`, `loan_term`, `loan_purpose`, `guarantee_type`, `status`, `current_stage`, `created_by`) VALUES
('A202604020001', 1, '张三', '110101199001011234', '13800138001', 50000.00, 12, '消费装修', 'CREDIT', 'INITIAL', 'INITIAL', 1),
('A202604020002', 2, '李四', '110101199002022345', '13800138002', 200000.00, 36, '企业经营', 'MORTGAGE', 'RISK', 'RISK', 1),
('A202604020003', 3, '王五', '110101199003033456', '13800138003', 80000.00, 24, '购车', 'GUARANTEE', 'APPROVED', 'APPROVED', 1);

-- 知识库文章测试数据
INSERT INTO `knowledge_article` (`category`, `title`, `content`, `tags`, `view_count`, `is_active`) VALUES
('BUSINESS_PROCESS', '贷款申请流程指南', '一、客户提交申请\n1. 填写贷款申请表\n2. 提交身份证明文件\n3. 提供收入证明材料\n\n二、初审阶段\n1. 材料完整性检查\n2. 客户资质初审\n3. 征信查询\n\n三、审批阶段\n1. 风险评估\n2. 额度核定\n3. 利率确定', '贷款,流程,申请', 156, 1),
('FAQ', '常见问题：如何查询贷款进度？', '您可以通过以下方式查询贷款进度：\n1. 登录网银查看申请状态\n2. 拨打客服热线 400-XXX-XXXX\n3. 前往就近网点咨询\n\n当前进度说明：\n- 初审：1-3个工作日\n- 审批：3-5个工作日\n- 放款：审批通过后2个工作日内', 'FAQ,进度查询,客服', 89, 1),
('PRODUCT_INFO', '个人消费贷款产品介绍', '产品特点：\n- 额度：1万-50万\n- 期限：6-36个月\n- 利率：年化4.35%起\n- 担保方式：信用/抵押/担保\n\n申请条件：\n1. 年龄22-55周岁\n2. 有稳定收入来源\n3. 信用记录良好\n4. 无重大不良记录', '消费贷,产品,利率', 234, 1),
('APPROVAL_STANDARD', '信用贷款审批标准', '审批要点：\n\n一、信用评分要求\n- 征信评分≥650分\n- 近24个月无逾期记录\n- 征信查询次数合理（近3个月≤6次）\n\n二、收入负债比\n- 月收入≥月供的2倍\n- 总负债率≤50%\n- 有稳定收入来源证明\n\n三、材料真实性\n- 收入证明需加盖单位公章\n- 银行流水需连续6个月以上\n- 身份证需在有效期内', '审批,信用,标准', 167, 1);

-- 客户提醒测试数据（user_id 对应客户经理，测试中使用用户 ID 1）
INSERT INTO `customer_reminder` (`user_id`, `customer_id`, `reminder_type`, `title`, `content`, `priority`, `status`, `due_date`) VALUES
(1, 1, 'APPLICATION_EXPIRE', '申请即将过期提醒', '客户张三的贷款申请（A202604020001）将于7天后过期，请及时处理。', 2, 'PENDING', DATE_ADD(NOW(), INTERVAL 7 DAY)),
(1, 2, 'MATERIAL_EXPIRE', '材料即将过期', '客户李四的收入证明将于30天后过期，请通知客户更新材料。', 1, 'PENDING', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(1, 3, 'APPROVAL_TIMEOUT', '审批超时提醒', '客户王五的贷款申请（A202604020003）审批已超过3个工作日，请尽快处理。', 3, 'PENDING', DATE_ADD(NOW(), INTERVAL 1 DAY)),
(1, 1, 'FOLLOWUP_REQUIRED', '客户回访提醒', '客户张三咨询贷款产品后24小时内未进行回访，请尽快联系客户。', 2, 'NOTIFIED', DATE_ADD(NOW(), INTERVAL 12 HOUR)),
(1, 2, 'LOAN_RENEW', '续贷提醒', '客户李四的贷款（合同号：JK202501010001）将于30天后到期，建议推荐续贷产品。', 2, 'PENDING', DATE_ADD(NOW(), INTERVAL 30 DAY));

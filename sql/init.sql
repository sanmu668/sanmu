create database ai_resume_system;

use ai_resume_system;

#用户表
create table users(
    id int auto_increment primary key ,    #  用户ID
    email varchar(255) unique not null ,   #  邮箱
    password_hash varchar(255) not null,   #  密码哈希
    role enum('user','admin') default 'user',   #  用户角色
    created_at timestamp default current_timestamp,   #  创建时间
    updater_at timestamp default current_timestamp on update current_timestamp,   #  更新时间
    last_login timestamp null   #   最后登录时间
);

ALTER TABLE users ADD COLUMN username VARCHAR(100) NOT NULL AFTER id;

ALTER TABLE users ADD COLUMN photo LONGBLOB AFTER password_hash;

ALTER TABLE users ADD COLUMN number CHAR(11) NOT NULL  AFTER photo;

SHOW COLUMNS FROM users;
ALTER TABLE users ADD COLUMN bio TEXT;

#简历表
create table resumes(
    id int auto_increment primary key ,  #   简历ID
    user_id int not null,       #   用户ID
    title varchar(255),         # 简历标题
    summary text,           #简历概要、自我介绍
    file_path varchar(255),     #  简历文件路径
    created_at timestamp default current_timestamp,     # 创建时间
    updated_at timestamp default current_timestamp on update current_timestamp,    # 更新时间
    foreign key (user_id) references users(id) on delete cascade
);

ALTER TABLE resumes
    ADD COLUMN file_name VARCHAR(255) NOT NULL,
    ADD COLUMN original_file_name VARCHAR(255) NOT NULL,
    ADD COLUMN file_size BIGINT NOT NULL;

ALTER TABLE resumes ADD COLUMN templateName VARCHAR(255) AFTER title; #模版名
#教育经历表
create table education_experience(
    id int primary key auto_increment,
    resume_id int not null ,           # 简历ID
    school_name varchar(50),            #  学校名称
    degree varchar(50),             #  学历（本科、硕士）
    major varchar(50),          #   专业
    start_data DATE,            #  入学时间
    end_data DATE,          #  毕业时间
    description text,    #描述/成绩/奖项等
    foreign key (resume_id) references resumes(id) on delete cascade
);

#工作经历表
create table  work_experiences(
    id int primary key auto_increment,   #  工作经历ID
    resume_id int not null,             #   简历ID
    company_name varchar(50),           #  公司名称
    start_data DATE,                #  起始时间
    end_data DATE,                  #  终止时间
    description text,               #  描述
    foreign key (resume_id) references resumes(id) on delete cascade
);

#技能表
create table skills(
    id int primary key auto_increment,   #  技能ID
    resume_id int not null ,            #  简历ID
    skill_name varchar(100),            #  技能名称
    foreign key (resume_id) references resumes(id) on delete cascade
);

#项目经历表
create table projects(
    id int primary key auto_increment,   #  项目ID
    resume_id int not null,         #  简历ID
    project_name varchar(100),      #  项目名称
    decription text,                #  项目描述
    foreign key (resume_id) references resumes(id) on delete cascade
);

#证书表
create table certificates(
    id int primary key auto_increment,    # 证书ID
    resume_id int not null,         # 简历ID
    name varchar(100),              #  证书名称
    foreign key (resume_id) references resumes(id) on delete cascade
);

drop table if exists education_experience;

#职位表
CREATE TABLE job_position (
                              id int PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
                              job_name VARCHAR(100) NOT NULL COMMENT '职位名称',
                              department VARCHAR(50) COMMENT '所属部门',
                              salary_range VARCHAR(50) COMMENT '薪资范围',
                              experience VARCHAR(30) COMMENT '经验要求',
                              education VARCHAR(30) COMMENT '学历要求',
                              job_type VARCHAR(20) DEFAULT '全职' COMMENT '工作性质(全职/兼职/实习)',
                              description TEXT COMMENT '职位描述',
                              requirements TEXT COMMENT '任职要求',
                              company VARCHAR(100) COMMENT '公司名称',
                              location VARCHAR(100) COMMENT '工作地点',
                              tags VARCHAR(255) COMMENT '职位标签',
                              status TINYINT DEFAULT 0 COMMENT '状态(0-草稿 1-已发布 2-已下线)',
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

-- 插入IT类职位
INSERT INTO job_position (job_name, department, salary_range, experience, education, job_type, description, requirements, company, location, tags, status)
VALUES
    ('Java开发工程师', '技术部', '15-25K', '3-5年', '本科', '全职',
     '负责公司核心业务系统的设计与开发工作，参与架构优化和技术攻关。',
     '1. 精通Java语言，熟悉Spring Boot、MyBatis等框架\n2. 熟悉MySQL数据库设计和优化\n3. 有分布式系统开发经验者优先',
     'XX科技有限公司', '北京海淀区', 'Java,后端开发,高薪', 1),

    ('前端开发工程师', '产品研发部', '12-20K', '1-3年', '本科', '全职',
     '负责公司产品的前端开发与优化，实现高性能的用户界面。',
     '1. 精通HTML/CSS/JavaScript\n2. 熟悉Vue/React框架\n3. 有移动端开发经验优先',
     'YY互联网公司', '上海浦东新区', '前端,Vue,React', 1),

    ('产品经理', '产品部', '18-30K', '5年以上', '本科', '全职',
     '负责公司核心产品的规划与设计，协调研发团队推进产品迭代。',
     '1. 5年以上互联网产品经验\n2. 出色的沟通和团队协作能力\n3. 有成功产品案例者优先',
     'ZZ网络科技', '深圳南山区', '产品经理,高薪,管理岗', 1);

-- 插入金融类职位
INSERT INTO job_position (job_name, department, salary_range, experience, education, job_type, description, requirements, company, location, tags, status)
VALUES
    ('财务分析师', '财务部', '20-35K', '3年以上', '硕士', '全职',
     '负责公司财务数据分析、预算编制和投资评估工作。',
     '1. 财务、会计或相关专业背景\n2. 精通Excel和财务分析工具\n3. CPA/CFA持证者优先',
     'AA投资集团', '北京朝阳区', '财务,金融,分析师', 1),

    ('风险管理专员', '风控部', '15-25K', '2-5年', '本科', '全职',
     '负责公司业务风险识别、评估和防控工作。',
     '1. 熟悉风险管理流程和方法\n2. 良好的数据分析能力\n3. 有金融机构风控经验优先',
     'BB银行', '上海黄浦区', '风控,金融,银行', 1);

-- 插入市场类职位
INSERT INTO job_position (job_name, department, salary_range, experience, education, job_type, description, requirements, company, location, tags, status)
VALUES
    ('市场推广经理', '市场部', '12-18K', '3年以上', '大专', '全职',
     '负责公司产品的市场推广和品牌建设工作。',
     '1. 3年以上市场推广经验\n2. 熟悉线上线下推广渠道\n3. 有成功推广案例者优先',
     'CC消费品公司', '广州天河区', '市场,推广,品牌', 1),

    ('新媒体运营', '品牌部', '8-15K', '1-3年', '本科', '全职',
     '负责公司新媒体平台的内容策划和运营工作。',
     '1. 熟悉主流社交媒体平台\n2. 优秀的文案写作能力\n3. 有爆款内容创作经验优先',
     'DD文化传媒', '杭州余杭区', '新媒体,运营,内容创作', 1);

-- 插入草稿状态的职位
INSERT INTO job_position (job_name, department, salary_range, experience, education, job_type, description, requirements, company, location, tags, status)
VALUES
    ('人力资源专员', '人力资源部', '10-15K', '1-3年', '本科', '全职',
     '负责公司招聘、员工关系等人力资源相关工作。',
     '1. 人力资源管理相关专业\n2. 良好的沟通协调能力\n3. 有招聘经验者优先',
     'EE集团公司', '成都高新区', 'HR,人力资源,招聘', 0);
drop table if exists job_position;

#匹配记录表
create table job_matches(
    id int auto_increment primary key ,    # 匹配记录ID
    user_id int not null ,  #  用户ID
    resume_id int not null ,     # 简历ID
    job_id int not null ,      #  职位ID
    match_score decimal(5,2) not null ,    # 匹配得分
    matched_at timestamp default current_timestamp,    # 匹配时间
    foreign key (user_id) references users(id) on delete cascade ,
    foreign key (resume_id) references resumes(id) on delete cascade ,
    foreign key (job_id) references job_position(id) on delete cascade
);

drop table if exists job_matches;

#消息通知表
create table notefications(
    id int auto_increment primary key ,     #  消息ID
    user_id int not null ,              #  用户ID
    message text not null ,         #  消息内容
    type enum('system','nanlysis') not null ,   #  消息类型
    status enum('unread','read') default 'unread',   #  消息状态
    created_at timestamp default current_timestamp,    #  消息创建时间
    foreign key (user_id) references users(id) on delete cascade
);

-- AI对话记录表
CREATE TABLE `chat_message` (
                           `id` BIGINT AUTO_INCREMENT PRIMARY KEY,    # 对话记录ID
                           `user_id` INT NOT NULL,   # 用户ID
                           `session_id` VARCHAR(255) NOT NULL,  # 会话ID
                           `question` TEXT NOT NULL,
                           `answer` TEXT NOT NULL,
                           `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#缓存表
create table user_cache(
    id int auto_increment primary key ,   #  缓存ID
    user_id int not null ,     #  用户ID
    cached_data json not null ,   #  缓存数据
    cache_expiry timestamp not null ,    #  缓存过期时间
    foreign key (user_id) references users(id) on delete cascade
);


#删除旧的外键约束
# alter table chat_message drop foreign key chat_message_ibfk_1;

#添加新外键约束
alter table chat_message add constraint fk_chat_message_user foreign key (user_id) references users(id) on delete cascade ;




INSERT INTO resumes (id, user_id, title, summary, file_path) VALUES
                                                                 (1, 1, '张三的Java开发工程师简历', '3年Java开发经验，熟悉Spring框架和微服务架构', '/resumes/zhangsan_java.pdf'),
                                                                 (2, 1, '张三的全栈开发简历', '前端React+后端Spring的全栈开发经验', '/resumes/zhangsan_fullstack.pdf'),
                                                                 (3, 2, '李四的数据分析师简历', '精通Python数据分析与机器学习', '/resumes/lisi_data.pdf'),
                                                                 (4, 3, '王五的产品经理简历', '5年互联网产品管理经验', '/resumes/wangwu_pm.pdf');


INSERT INTO education_experience (resume_id, school_name, degree, major, start_data, end_data, description) VALUES
                                                                                                                (1, '清华大学', '本科', '计算机科学与技术', '2015-09-01', '2019-06-30', 'GPA 3.8/4.0，获得校级优秀毕业生称号'),
                                                                                                                (1, '北京大学', '硕士', '软件工程', '2019-09-01', '2022-06-30', '研究方向：分布式系统'),
                                                                                                                (2, '清华大学', '本科', '计算机科学与技术', '2015-09-01', '2019-06-30', '辅修经济学'),
                                                                                                                (3, '上海交通大学', '硕士', '应用统计学', '2018-09-01', '2021-06-30', '发表2篇SCI论文'),
                                                                                                                (4, '复旦大学', '本科', '工商管理', '2014-09-01', '2018-06-30', '学生会副主席');

INSERT INTO skills (resume_id, skill_name) VALUES
                                               (1, 'Java'),
                                               (1, 'Spring Boot'),
                                               (1, 'MySQL'),
                                               (1, 'Redis'),
                                               (2, 'JavaScript'),
                                               (2, 'React'),
                                               (2, 'Node.js'),
                                               (2, 'Java'),
                                               (3, 'Python'),
                                               (3, 'Pandas'),
                                               (3, 'TensorFlow'),
                                               (3, 'SQL'),
                                               (4, '产品规划'),
                                               (4, 'Axure'),
                                               (4, '用户研究'),
                                               (4, '数据分析');


INSERT INTO projects (resume_id, project_name, decription) VALUES
                                                               (1, '电商平台订单系统', '设计并开发了高并发的订单处理系统，日处理订单量超过100万'),
                                                               (1, '分布式缓存优化', '通过Redis集群优化系统性能，将响应时间降低40%'),
                                                               (2, '企业内部管理系统', '使用React+Spring Boot开发的全栈项目，提高了部门协作效率'),
                                                               (3, '用户流失预测模型', '开发了基于机器学习的用户流失预测模型，准确率达到85%'),
                                                               (4, '智能客服产品', '从0到1规划并上线了AI智能客服产品，用户满意度提升30%');

INSERT INTO certificates (resume_id, name) VALUES
                                               (1, 'Oracle Certified Professional: Java SE 11 Developer'),
                                               (1, 'AWS Certified Developer - Associate'),
                                               (2, '全栈开发工程师认证'),
                                               (3, '数据分析师高级认证'),
                                               (4, 'PMP项目管理专业人士资格认证');

#申请记录表
create table applications_record(
    id int primary key auto_increment comment '申请记录ID',
    resume_id int not null comment '简历ID',
    job_id int not null comment '职位ID',
    user_id int not null comment '用户ID',
    status enum('待处理', '面试中','已录用', '已拒绝') default '待处理' comment '申请状态',
    submitted_time DATETIME not null default current_timestamp comment '投递时间',
    updated_time DATETIME not null default current_timestamp on update current_timestamp comment '更新时间',
    note text comment '备注',
    foreign key (resume_id) references resumes(id) on delete cascade ,
    foreign key (job_id) references job_position(id) on delete cascade ,
    foreign key (user_id) references users(id) on delete cascade ,

    index `idx_user_id` (user_id),
    index `idx_job_id` (job_id),
    index  `idx_status` (status)
)engine = InnoDB default charset = utf8mb4 comment = '简历投递记录表';

DESCRIBE users;
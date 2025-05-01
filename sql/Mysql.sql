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

#简历表
create table resumes(
    id int auto_increment primary key ,
    user_id int not null,
    content json not null,
    score decimal(5,2) default 0,
    analysis_result text,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp,
    foreign key (user_id) references users(id) on delete cascade
);

#职位表
create table jobs(
    id int auto_increment primary key ,   # 职位ID
    title varchar(255) not null ,     # 职位标题
    description text not null,      # 职位描述
    requirements text,      # 职位要求
    tags json,    # 职位标签
    created_at timestamp default current_timestamp,   # 创建时间
    updated_at timestamp default current_timestamp on update current_timestamp   #  更新时间
);

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
    foreign key (job_id) references jobs(id) on delete cascade
);

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

#修改user_id字段类型为BIGINT
alter table chat_message modify column  user_id int not null ;

#删除旧的外键约束
# alter table chat_message drop foreign key chat_message_ibfk_1;

#添加新外键约束
alter table chat_message add constraint fk_chat_message_user foreign key (user_id) references users(id) on delete cascade ;

ALTER TABLE users ADD COLUMN username VARCHAR(100) NOT NULL AFTER id;

ALTER TABLE users ADD COLUMN photo LONGBLOB AFTER password_hash;

ALTER TABLE users ADD COLUMN number CHAR(11) NOT NULL  AFTER photo;
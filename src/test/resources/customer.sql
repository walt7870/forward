drop database if EXISTS ntech_db;
create database ntech_db default character set utf8 collate utf8_general_ci;
use ntech_db;

drop table if exists customer;
create table customer
(
  name varchar(32) not null,
  password varchar(64) not null,
  contype varchar(5),
  active int DEFAULT 0,
  email varchar(64),
  token varchar(64),
  regtime datetime,
  face_number int DEFAULT 0,
  primary key(name)
)engine=innodb default charset=utf8 auto_increment=1;

drop table if exists set_meal;
create table set_meal
(
  id int auto_increment,
  user_name varchar(32) not null,
  contype varchar(5),
  begin_time datetime,
  end_time datetime,
  total_times int,
  left_times int,
  enable int,
  primary key(id)
)engine=innodb default charset=utf8 auto_increment=1;

drop table if exists log;
create table log
(
  id int AUTO_INCREMENT PRIMARY KEY ,
  user_name varchar(32) not null,
  content varchar(32) not null,
  result int,
  time DATETIME
)engine=innodb default charset=utf8;


drop table if exists library;
create table library
(
  user_name varchar(32) not null,
  library_name varchar(32) not null,
  primary key(user_name,library_name)
)engine=innodb default charset=utf8;

insert into customer values('admin','d033e22ae348aeb5660fc2140aec35850c4da997','',1,'admin@admin.com','','2017-08-23 15:58:07',0);

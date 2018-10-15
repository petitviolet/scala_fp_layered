CREATE TABLE `user` (
  id char(36) not null,
  name varchar(100) not null,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp,
  primary key(id)
)

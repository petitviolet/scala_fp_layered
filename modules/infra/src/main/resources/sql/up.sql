CREATE TABLE `sample`.`user` (
  id char(36) not null,
  email varchar(255) not null,
  name varchar(100) not null,
  status char(16) not null,
  visibility char(16) not null,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp,
  primary key(id)
)

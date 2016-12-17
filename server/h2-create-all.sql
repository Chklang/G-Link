create table t_configuration (
  key                           varchar(32) not null,
  value                         varchar(2048),
  constraint pk_t_configuration primary key (key)
);

create table t_link (
  idlink                        integer auto_increment not null,
  command                       varchar(2048),
  name                          varchar(256),
  description                   varchar(4096),
  icon                          varchar(2048),
  parameters                    varchar(2048),
  constraint uq_t_link_name unique (name),
  constraint pk_t_link primary key (idlink)
);


create table api_keys
(
    id         bigint auto_increment primary key,
    name       varchar(100)                         not null,
    api_key    varchar(255)                         not null,
    active     tinyint(1) default 1                 null,
    created_at timestamp  default CURRENT_TIMESTAMP null
);

create table catalogos
(
    ID          int auto_increment primary key,
    DESCRIPTION varchar(200) not null,
    TYPE       varchar(50)  not null,
    ID_ROOT    int          null
);

create table estudiantes
(
    ID             int auto_increment primary key,
    NOMBRE         varchar(100)                        not null,
    EMAIL          varchar(100)                        null,
    EDAD           int                                 not null,
    FECHA_REGISTRO timestamp default CURRENT_TIMESTAMP not null,
    SKILLS         varchar(200)                        not null,
    IMAGEN_URL     varchar(1000)                       null,
    IMAGEN         longblob                            null,
    TURNO          int                                 null,
    INGENIERIA     int                                 null,
    TSU            int                                 null
);

create table person
(
    id          bigint auto_increment primary key,
    first_name  varchar(100) not null,
    last_name   varchar(100) not null,
    middle_name varchar(100) null,
    type        varchar(50)  null,
    enrollment  varchar(50)  null,
    degree      varchar(100) null,
    shift       varchar(50)  null,
    entry_time  time    not null,
    departure_time time not null
);



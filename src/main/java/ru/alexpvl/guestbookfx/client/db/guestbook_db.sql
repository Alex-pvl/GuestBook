create table users(
    id serial primary key,
    firstname varchar(20),
    lastname varchar(20),
    login varchar(20),
    email varchar(40),
    password varchar(20),
    role int
);

create table messages(
    id serial primary key,
    email varchar(40),
    message text,
    date timestamp(0) default (now())
);
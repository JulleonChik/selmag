drop table if exists catalogue.t_product;
create schema if not exists catalogue;

create table catalogue.t_product
(
    id            serial primary key,
    c_title       varchar(100) not null check(length(trim(c_title)) >= 3),
    c_description varchar(1000)
);


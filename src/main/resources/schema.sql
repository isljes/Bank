CREATE TABLE IF NOT EXISTS users (
    id bigserial primary key ,
    email varchar(60) unique not null ,
    password varchar(300) not null ,
    status varchar(30) not null default 'ACTIVE',
    role varchar(45) not null default 'UNCONFIRMED_USER',
    confirmation_code varchar(200)
);

CREATE TABLE IF NOT EXISTS cards(
    id bigserial primary key ,
    card_number varchar(16) not null unique ,
    date date not null ,
    cvv varchar(60) not null ,
    status varchar(30) not null default 'NOT_ACTIVE',
    payment_system varchar(20) not null ,
    card_type varchar(20) not null ,
    balance bigint ,
    user_id bigint references users(id) not null
);

CREATE TABLE IF NOT EXISTS personal_details(
    id bigserial primary key ,
    name varchar(80),
    surname varchar(80),
    birth_date date,
    address varchar(150),
    phone_number varchar(20),
    user_id bigint references users(id) not null
);
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
    status varchar(30) not null,
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

CREATE TABLE IF NOT EXISTS transaction_history(
    card_id bigint references cards(id) not null,
    amount bigint not null ,
    datetime timestamp not null,
    primary key(card_id,datetime)

);

INSERT INTO users(email,password,role)
VALUES ('admin@gmail.com',
        '$2a$12$j0b9JIh/hRvupv/0ruukU.2p1vg.HSqBpiBQELGvfJmXxQU/Q6Xb6',
        'ADMIN'),

       ('user@gmail.com',
        '$2a$12$tfLBK/jfZES5XAX3rar5ZOF6r1poAajLnDoriv099/EQ22abQoGwS',
        'USER');

INSERT INTO personal_details(user_id) VALUES (1),(2);

INSERT INTO cards(card_number,date,cvv,user_id,status,payment_system,card_type,balance)
VALUES ('0000001000000000',DATE'2024-01-18','000',1,'BANNED','MIR','DEBIT',0),
       ('2823901000000018',DATE'2030-01-01','000',2,'UNDER_CONSIDERATION','MIR','DEBIT',1000),
       ('4823901000000022',DATE'2030-01-01','000',2,'UNDER_CONSIDERATION','VISA','DEBIT',0);

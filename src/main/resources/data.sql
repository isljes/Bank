INSERT INTO users(email,password,role)
VALUES ('kenik000000@gmail.com',
        '$2a$12$qZBoy.k1VRk1P2S0OcakJ.gXy7xCzwAQ5hmDvj/4GCXX.VyvlM4xy',
        'ADMIN'),

        ('user@gmail.com',
        '$2a$12$tfLBK/jfZES5XAX3rar5ZOF6r1poAajLnDoriv099/EQ22abQoGwS',
        'USER');


INSERT INTO cards(card_number,date,cvv,user_id,payment_system,card_type,balance)
values ('0000001000000000','2024-01-18','000','1','MIR','DEBIT',0);


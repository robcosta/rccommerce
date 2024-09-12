INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_SELLER');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');

INSERT INTO tb_auth (auth) VALUES ('ALL');
INSERT INTO tb_auth (auth) VALUES ('CREATE');
INSERT INTO tb_auth (auth) VALUES ('READER');
INSERT INTO tb_auth (auth) VALUES ('UPDATE');
INSERT INTO tb_auth (auth) VALUES ('DELETE');
INSERT INTO tb_auth (auth) VALUES ('NONE');

INSERT INTO tb_user (name, email, password) VALUES ('Administrador', 'admin@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2');
INSERT INTO tb_user (name, email, password) VALUES ('Bob Green', 'bob@gmail.com', '$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze');
INSERT INTO tb_user (name, email, password) VALUES ('Alex Blue', 'alex@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');

INSERT INTO tb_user_role(user_id, role_id) VALUES(1,1)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,2)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,3)
INSERT INTO tb_user_role(user_id, role_id) VALUES(3,3)

INSERT INTO tb_user_auth(user_id, auth_id) VALUES(1,1)
INSERT INTO tb_user_auth(user_id, auth_id) VALUES(2,2)
INSERT INTO tb_user_auth(user_id, auth_id) VALUES(2,3)
INSERT INTO tb_user_auth(user_id, auth_id) VALUES(2,4)
INSERT INTO tb_user_auth(user_id, auth_id) VALUES(2,5)
INSERT INTO tb_user_auth(user_id, auth_id) VALUES(3,2)

INSERT INTO tb_operator(id, commission) VALUES (1, 2.0);
INSERT INTO tb_operator(id, commission) VALUES (2, 1.0);
INSERT INTO tb_operator(id, commission) VALUES (3, 0.0);

INSERT INTO tb_user (name, email, password) VALUES ('Venda ao Consumidor', 'venda@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2');
INSERT INTO tb_user (name, email, password) VALUES ('Peter Yellow', 'peter@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');

INSERT INTO tb_user_role(user_id, role_id) VALUES(4,4)
INSERT INTO tb_user_role(user_id, role_id) VALUES(5,4)

INSERT INTO tb_user_auth(user_id, auth_id) VALUES(4,6)
INSERT INTO tb_user_auth(user_id, auth_id) VALUES(5,6)

INSERT INTO tb_client(id, cpf) VALUES (4, '73995808042');
INSERT INTO tb_client(id, cpf) VALUES (5, '46311990083');

INSERT INTO tb_suplier(name, cnpj) VALUES ('Diversos','00000000000000'); 
INSERT INTO tb_suplier(name, cnpj) VALUES ('Lojas FOO','28104874000108'); 
INSERT INTO tb_suplier(name, cnpj) VALUES ('Inova Tech','40543322000100'); 
INSERT INTO tb_suplier(name, cnpj) VALUES ('Soluções Nexus','36687922000166'); 
INSERT INTO tb_suplier(name, cnpj) VALUES ('Nova Onda','49557765000116'); 
INSERT INTO tb_suplier(name, cnpj) VALUES ('PixelPower','85994937000182'); 

INSERT INTO tb_category(name) VALUES ('LIVROS');
INSERT INTO tb_category(name) VALUES ('ELETRÔNICOS');
INSERT INTO tb_category(name) VALUES ('COMPUTADORES');

INSERT INTO tb_stock (user_id, qtt_stock, moment, qtt_moved,  moviment) VALUES (1, 28.0, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2.0, 3);
INSERT INTO tb_stock (user_id, qtt_stock, moment, qtt_moved,  moviment) VALUES (1, 10.0, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2.0, 3);
INSERT INTO tb_stock (user_id, qtt_stock, moment, qtt_moved,  moviment) VALUES (1,7.0, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2.0, 3);
INSERT INTO tb_stock (user_id, qtt_stock, moment, qtt_moved,  moviment) VALUES (1, 25.0, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2.0, 5);


INSERT INTO tb_product (name, price, unit, description, img_url, stock_id, reference, suplier_id) VALUES ('The Lord of the Rings', 90.5,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg',1,'0000000000017',1);
INSERT INTO tb_product (name, price, unit, description, img_url, stock_id, reference, suplier_id) VALUES ('Smart TV', 2190.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg',2,'0000000000024',3);
INSERT INTO tb_product (name, price, unit, description, img_url, stock_id, reference, suplier_id) VALUES ('Macbook Pro', 1250.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg',4,'0000000000031',5);


INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-25T13:00:00Z', 1, 4, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-29T15:50:00Z', 3, 5, 3);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-08-03T14:20:00Z', 0, 4, 3);



INSERT INTO tb_payment (order_id, moment, payment_type) VALUES (1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', 1);
INSERT INTO tb_payment (order_id, moment, payment_type) VALUES (2, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2);



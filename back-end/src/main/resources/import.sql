INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_SELLER');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');

INSERT INTO tb_permission (authority) VALUES ('PERMISSION_ALL');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_CREATE');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_READER');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_UPDATE');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_DELETE');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_NONE');

INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Administrador', 'Administrador', 'admin@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2');
INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Bob Green', 'Bob Green', 'bob@gmail.com', '$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze');
INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Alex Blue', 'Alex Blue', 'alex@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');

INSERT INTO tb_user_role(user_id, role_id) VALUES(1,1)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,2)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,3)
INSERT INTO tb_user_role(user_id, role_id) VALUES(3,3)

INSERT INTO tb_user_permission(user_id, permission_id) VALUES(1,1)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,2)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,3)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,4)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,5)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(3,3)

INSERT INTO tb_operator(id, commission) VALUES (1, 2.0);
INSERT INTO tb_operator(id, commission) VALUES (2, 1.0);
INSERT INTO tb_operator(id, commission) VALUES (3, 0.0);

INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Venda ao Consumidor', 'Venda ao Consumidor', 'venda@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2');
INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Maria Yellow', 'Maria Yellow', 'maria@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');
INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('John Black', 'John Black', 'john@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');

INSERT INTO tb_user_role(user_id, role_id) VALUES(4,4)
INSERT INTO tb_user_role(user_id, role_id) VALUES(5,4)
INSERT INTO tb_user_role(user_id, role_id) VALUES(6,4)

INSERT INTO tb_user_permission(user_id, permission_id) VALUES(4,6)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(5,6)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(6,6)

INSERT INTO tb_client(id, cpf) VALUES (4, '73995808042');
INSERT INTO tb_client(id, cpf) VALUES (5, '46311990083');
INSERT INTO tb_client(id, cpf) VALUES (6, '83563189048');

INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Diversos','Diversos','00000000000000'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Lojas FOO','Lojas FOO','28104874000108'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Inova Tech','Inova Tech','40543322000100'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Soluções Nexus','Solucoes Nexus','36687922000166'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Nova Onda','Nova Onda','49557765000116'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('PixelPower','PixelPower','85994937000182'); 

INSERT INTO tb_category(name, name_unaccented) VALUES ('LIVROS', 'LIVROS');
INSERT INTO tb_category(name, name_unaccented) VALUES ('ELETRÔNICOS', 'ELETRONICOS');
INSERT INTO tb_category(name, name_unaccented) VALUES ('COMPUTADORES', 'COMPUTADORES');

INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('The Lord of the Rings', 'The Lord of the Rings', 90.5,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg',28,'0000000000017',1);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('Smart TV','Smart TV', 2190.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg',16,'0000000000024',3);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('Macbook Pro','Macbook Pro', 1250.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg',12,'0000000000031',5);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer','PC Gamer', 1200.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/4-big.jpg',6,'0000000000048',4);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('Rails for Dummies','Rails for Dummies', 100.99,'UN','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/5-big.jpg',13,'0000000000055',2);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Ex','PC Gamer Ex', 1350.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/6-big.jpg',6,'0000000000062',5);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer X','PC Gamer X', 1350.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/7-big.jpg',2,'0000000000079',1);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Alfa','PC Gamer Alfa', 1850.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/8-big.jpg',35,'0000000000086',4);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Tera','PC Gamer Tera', 1950.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/9-big.jpg',16,'0000000000093',2);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Y','PC Gamer Y', 1700.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/10-big.jpg',1,'0000000000109',1);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Nitro','PC Gamer Nitro', 1450.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/11-big.jpg',4,'0000000000116',3);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Card','PC Gamer Card', 1850.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/12-big.jpg',9,'0000000000123',2);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Plus','PC Gamer Plus', 1350.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/13-big.jpg',12,'0000000000130',4);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Hera','PC Gamer Hera', 2250.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/14-big.jpg',13,'0000000000147',5);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Weed','PC Gamer Weed', 2200.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/15-big.jpg',5,'0000000000154',4);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Max','PC Gamer Max', 2340.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/16-big.jpg',4,'0000000000161',2);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Turbo','PC Gamer Turbo', 1280.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/17-big.jpg',22,'0000000000178',5);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Hot','PC Gamer Hot', 1450.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/18-big.jpg',15,'0000000000185',3);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Ez','PC Gamer Ez', 1750.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/19-big.jpg',3,'0000000000192',4);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Tr', 'PC Gamer Tr', 1650.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/20-big.jpg',9,'0000000000208',2);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Tx', 'PC Gamer Tx', 1680.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/21-big.jpg',14,'0000000000215',1);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Er', 'PC Gamer Er', 1850.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/22-big.jpg',10,'0000000000222',3);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Min', 'PC Gamer Min', 2250.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/23-big.jpg',9,'0000000000239',5);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Boo','PC Gamer Boo', 2350.0,'UN', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/24-big.jpg',7,'0000000000246',4);
INSERT INTO tb_product (name, name_unaccented, price, unit, description, img_url, quantity, reference, suplier_id) VALUES ('PC Gamer Foo','PC Gamer Foo', 4170.0, 'UN','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/25-big.jpg',21,'0000000000253',2);
 
INSERT INTO tb_product_category (product_id, category_id) VALUES (1, 1);
INSERT INTO tb_product_category (product_id, category_id) VALUES (2, 2);
INSERT INTO tb_product_category (product_id, category_id) VALUES (2, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (3, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (4, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (5, 1);
INSERT INTO tb_product_category (product_id, category_id) VALUES (6, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (7, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (8, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (9, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (10, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (11, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (12, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (13, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (14, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (15, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (16, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (17, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (18, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (19, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (20, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (21, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (22, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (23, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (24, 3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (25, 3);

INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-25T13:00:00Z', 1, 4, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-29T15:50:00Z', 3, 5, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-08-03T14:20:00Z', 0, 4, 1);

INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (1, 1, 2.0, 90.5);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (1, 3, 1.0, 1250.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (2, 3, 1.0, 1250.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (3, 1, 1.0, 90.5);

INSERT INTO tb_payment (order_id, moment, payment_type) VALUES (1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', 1);
INSERT INTO tb_payment (order_id, moment, payment_type) VALUES (2, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2);

INSERT INTO tb_stock (user_id, product_id, quantity, moment, qtt_Moved, moviment) VALUES (1, 3, 12, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_stock (user_id, product_id, quantity, moment, qtt_Moved, moviment) VALUES (1, 3, 10, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_stock (user_id, product_id, quantity, moment, qtt_Moved, moviment) VALUES (1, 4, 12, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_stock (user_id, product_id, quantity, moment, qtt_Moved, moviment) VALUES (1, 3, 7, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 3, 1);
INSERT INTO tb_stock (user_id, product_id, quantity, moment, qtt_Moved, moviment) VALUES (1, 3, 6, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 1, 1);
INSERT INTO tb_stock (user_id, product_id, quantity, moment, qtt_Moved, moviment) VALUES (1, 4, 10, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_stock (user_id, product_id, quantity, moment, qtt_Moved, moviment) VALUES (1, 3, 2, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 4, 1);


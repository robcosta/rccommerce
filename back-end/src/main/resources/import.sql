INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_SELLER');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_CASH');

INSERT INTO tb_permission (authority) VALUES ('PERMISSION_ALL');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_NONE');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_CREATE');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_READER');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_UPDATE');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_DELETE');
INSERT INTO tb_permission (authority) VALUES ('PERMISSION_CASH');

INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Administrador', 'Administrador', 'admin@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2');
INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Bob Green', 'Bob Green', 'bob@gmail.com', '$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze');
INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Alex Blue', 'Alex Blue', 'alex@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');

INSERT INTO tb_user_role(user_id, role_id) VALUES(1,1)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,2)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,3)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,5)
INSERT INTO tb_user_role(user_id, role_id) VALUES(3,3)


INSERT INTO tb_user_permission(user_id, permission_id) VALUES(1,1)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,2)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,3)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,4)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,5)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(2,7)
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

INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Ney Green', 'Ney Green', 'ney@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');
INSERT INTO tb_operator(id, commission) VALUES (7, 1.0);
INSERT INTO tb_user_role(user_id, role_id) VALUES(7,2)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(7,6)

INSERT INTO tb_user (name, name_unaccented, email, password) VALUES ('Pedro Red', 'Pedro Red', 'pedro@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');
INSERT INTO tb_client(id, cpf) VALUES (8, '14496637022');
INSERT INTO tb_user_role(user_id, role_id) VALUES(8,4)
INSERT INTO tb_user_permission(user_id, permission_id) VALUES(8,2)


INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Diversos','Diversos','00000000000000'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Lojas FOO','Lojas FOO','28104874000108'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Inova Tech','Inova Tech','40543322000100'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Soluções Nexus','Solucoes Nexus','36687922000166'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('Nova Onda','Nova Onda','49557765000116'); 
INSERT INTO tb_suplier(name,name_unaccented,cnpj) VALUES ('PixelPower','PixelPower','85994937000182'); 

INSERT INTO tb_product_category(name, name_unaccented) VALUES ('LIVROS', 'LIVROS');
INSERT INTO tb_product_category(name, name_unaccented) VALUES ('ELETRÔNICOS', 'ELETRONICOS');
INSERT INTO tb_product_category(name, name_unaccented) VALUES ('COMPUTADORES', 'COMPUTADORES');

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
 
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (1, 1);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (2, 2);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (2, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (3, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (4, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (5, 1);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (6, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (7, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (8, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (9, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (10, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (11, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (12, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (13, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (14, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (15, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (16, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (17, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (18, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (19, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (20, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (21, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (22, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (23, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (24, 3);
INSERT INTO tb_productid_categoryid (product_id, category_id) VALUES (25, 3);

INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-01T10:00:00Z', 'WAITING_PAYMENT', 4, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-02T11:00:00Z', 'PAID', 5, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-03T12:00:00Z', 'SHIPPED', 6, 3);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-04T13:00:00Z', 'DELIVERED', 4, 1);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-05T14:00:00Z', 'CANCELED', 5, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-06T15:00:00Z', 'WAITING_PAYMENT', 6, 3);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-07T16:00:00Z', 'PAID', 4, 1);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-08T17:00:00Z', 'SHIPPED', 5, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-09T18:00:00Z', 'DELIVERED', 6, 3);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-10T19:00:00Z', 'CANCELED', 4, 1);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-11T20:00:00Z', 'WAITING_PAYMENT', 5, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-12T21:00:00Z', 'PAID', 6, 3);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-13T22:00:00Z', 'SHIPPED', 4, 1);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-14T23:00:00Z', 'DELIVERED', 5, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-15T09:00:00Z', 'CANCELED', 6, 3);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-16T08:00:00Z', 'WAITING_PAYMENT', 4, 1);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-17T07:00:00Z', 'PAID', 5, 2);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-18T06:00:00Z', 'SHIPPED', 6, 3);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-19T05:00:00Z', 'DELIVERED', 4, 1);
INSERT INTO tb_order (moment, status, client_id, user_id) VALUES (TIMESTAMP WITH TIME ZONE '2023-01-20T04:00:00Z', 'CANCELED', 5, 2);

INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (1, 1, 2.0, 90.5);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (1, 3, 1.0, 1250.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (2, 3, 1.0, 1250.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (3, 1, 1.0, 90.5);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (4, 2, 1.0, 2190.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (5, 4, 1.0, 1200.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (6, 5, 1.0, 100.99);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (7, 6, 1.0, 1350.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (8, 7, 1.0, 1350.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (9, 8, 1.0, 1850.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (10, 9, 1.0, 1950.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (11, 10, 1.0, 1700.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (12, 11, 1.0, 1450.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (13, 12, 1.0, 1850.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (14, 13, 1.0, 2250.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (15, 14, 1.0, 2200.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (16, 15, 1.0, 2340.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (17, 16, 1.0, 1280.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (18, 17, 1.0, 1450.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (19, 18, 1.0, 1750.0);
INSERT INTO tb_order_item (order_id, product_id, quantity, price) VALUES (20, 19, 1.0, 1650.0);

INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (1, 3, 12, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (1, 3, 10, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (1, 4, 12, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (1, 3, 7, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 3, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (1, 3, 6, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 1, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (1, 4, 10, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 2, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (1, 3, 2, TIMESTAMP WITH TIME ZONE '2022-07-30T11:00:00Z', 4, 1);

-- Insert CashRegister
INSERT INTO tb_cash_register (id, balance, operator_id, open_time, close_time) VALUES (1, 1631.50, 2, TIMESTAMP WITH TIME ZONE '2025-01-23T13:01:02Z', TIMESTAMP WITH TIME ZONE '2025-01-23T13:04:00Z');
INSERT INTO tb_cash_register (id, balance, operator_id, open_time, close_time) VALUES (2, 1951.49, 2, TIMESTAMP WITH TIME ZONE '2025-01-23T13:04:10Z', TIMESTAMP WITH TIME ZONE '2025-01-23T13:11:25Z');
INSERT INTO tb_cash_register (id, balance, operator_id, open_time, close_time) VALUES (3, 2740.50, 2, TIMESTAMP WITH TIME ZONE '2025-01-23T13:11:35Z', NULL);

-- -- Insert CashMovement for CashRegister 1
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (1, 1, 'OPENING_BALANCE', 'Abertura de caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:01:02Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (2, 1, 'WITHDRAWAL', 'Retirada de Caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:01:54Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (3, 1, 'SALE', 'Venda - Pedido: 1 - Pagamento: 1', TIMESTAMP WITH TIME ZONE '2025-01-23T13:03:39Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (4, 1, 'CLOSING_BALANCE', 'Fechamento de caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:04:00Z');

-- -- Insert MovementDetail for CashMovement 1
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (1, 1, 'MONEY', 400.50);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (2, 2, 'MONEY', -150.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (3, 3, 'MONEY', 31.0);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (4, 3, 'PIX', 1400.0);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (5, 4, 'MONEY', -150.00);

-- -- Insert CashMovement for CashRegister 2
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (5, 2, 'OPENING_BALANCE', 'Abertura de caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:04:10Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (6, 2, 'REINFORCEMENT', 'Reforço de Caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:04:30Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (7, 2, 'SALE', 'Venda - Pedido: 6 - Pagamento: 2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:07:00Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (8, 2, 'SALE', 'Venda - Pedido: 11 - Pagamento: 3', TIMESTAMP WITH TIME ZONE '2025-01-23T13:08:47Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (9, 2, 'WITHDRAWAL', 'Retirada de Caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:11:12Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (10, 2, 'CLOSING_BALANCE', 'Fechamento de caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:11:25Z');

-- -- Insert MovementDetail for CashMovement 2
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (6, 5, 'MONEY', 400.50);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (7, 6, 'MONEY', 50.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (8, 7, 'DEBIT_CARD', 50.99);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (9, 7, 'CREDIT_CARD', 50.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (10, 8, 'MONEY', 200.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (11, 8, 'PIX', 300.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (12, 8, 'DEBIT_CARD', 200.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (13, 8, 'CREDIT_CARD', 1000.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (14, 9, 'MONEY', -150.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (15, 10, 'MONEY', -150.00);

-- -- Insert CashMovement for CashRegister 3
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (11, 3, 'OPENING_BALANCE', 'Abertura de caixa - Usuário:2', TIMESTAMP WITH TIME ZONE '2025-01-23T13:11:35Z');
INSERT INTO tb_cash_movement (id, cash_register_id, cash_movement_type, description, timestamp) VALUES (12, 3, 'SALE', 'Venda - Pedido: 16 - Pagamento: 4', TIMESTAMP WITH TIME ZONE '2025-01-23T13:13:10Z');

-- -- Insert MovementDetail for CashMovement 3
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (16, 11, 'MONEY', 400.50);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (17, 12, 'MONEY', 210.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (18, 12, 'PIX', 130.00);
INSERT INTO tb_movement_detail (id, cash_movement_id, movement_type, amount) VALUES (19, 12, 'CREDIT_CARD', 2000.00);

-- -- Insert Payment
INSERT INTO tb_Payment (cash_register_id, order_id, moment) VALUES (1, 1, TIMESTAMP WITH TIME ZONE '2025-01-23T11:05:31Z');
INSERT INTO tb_Payment (cash_register_id, order_id, moment) VALUES (2, 6, TIMESTAMP WITH TIME ZONE '2025-01-23T11:52:33Z');
INSERT INTO tb_Payment (cash_register_id, order_id, moment) VALUES (2, 11,TIMESTAMP WITH TIME ZONE '2025-01-23T11:52:42Z');
INSERT INTO tb_Payment (cash_register_id, order_id, moment) VALUES (3, 16,TIMESTAMP WITH TIME ZONE '2025-01-23T11:54:14Z');

-- -- Update tb_movement_detail
UPDATE tb_movement_detail SET payment_id = 1 WHERE id = 1;
UPDATE tb_movement_detail SET payment_id = 1 WHERE id = 4;
UPDATE tb_movement_detail SET payment_id = 2 WHERE id = 8;
UPDATE tb_movement_detail SET payment_id = 2 WHERE id = 9;
UPDATE tb_movement_detail SET payment_id = 3 WHERE id = 10;
UPDATE tb_movement_detail SET payment_id = 3 WHERE id = 11;
UPDATE tb_movement_detail SET payment_id = 3 WHERE id = 12;
UPDATE tb_movement_detail SET payment_id = 3 WHERE id = 13;
UPDATE tb_movement_detail SET payment_id = 4 WHERE id = 17;
UPDATE tb_movement_detail SET payment_id = 4 WHERE id = 18;
UPDATE tb_movement_detail SET payment_id = 4 WHERE id = 19;

-- -- Insert tb_Product_stock
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (2, 1, 26, TIMESTAMP WITH TIME ZONE '2025-01-23T11:05:31Z', 2, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (2, 3, 11, TIMESTAMP WITH TIME ZONE '2025-01-23T11:05:31Z', 1, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (2, 5, 12, TIMESTAMP WITH TIME ZONE '2025-01-23T11:52:33Z', 1, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (2, 10, 0, TIMESTAMP WITH TIME ZONE '2025-01-23T11:52:42Z', 1, 1);
INSERT INTO tb_Product_stock (user_id, product_id, quantity, moment, qtt_Moved, movement) VALUES (2, 15, 4, TIMESTAMP WITH TIME ZONE '2025-01-23T11:54:14Z', 1, 1);

-- -- Update quantity tb_product
UPDATE tb_product SET quantity = 26 WHERE id = 1;
UPDATE tb_product SET quantity = 11 WHERE id = 3;
UPDATE tb_product SET quantity = 12 WHERE id = 5;
UPDATE tb_product SET quantity = 0 WHERE id = 10;
UPDATE tb_product SET quantity = 4 WHERE id = 15;



INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_SELLER');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');

INSERT INTO tb_user (name, email, password,auths) VALUES ('Administrador', 'admin@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2',ARRAY[0]);
INSERT INTO tb_user (name, email, password, auths) VALUES ('Bob Green', 'bob@gmail.com', '$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze',ARRAY[1, 2, 3, 4]);
INSERT INTO tb_user (name, email, password, auths) VALUES ('Alex Blue', 'alex@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6',ARRAY[2]);

INSERT INTO tb_user_role(user_id, role_id) VALUES(1,1)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,2)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,3)
INSERT INTO tb_user_role(user_id, role_id) VALUES(3,3)
INSERT INTO tb_user_role(user_id, role_id) VALUES(3,4)

INSERT INTO tb_operator(id, commission) VALUES (1, 2.0);
INSERT INTO tb_operator(id, commission) VALUES (2, 1.0);
INSERT INTO tb_operator(id, commission) VALUES (3, 0.0);

INSERT INTO tb_user (name, email, password, auths) VALUES ('John Red', 'john@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2', ARRAY[99]);
INSERT INTO tb_user (name, email, password, auths) VALUES ('Peter Yellow', 'peter@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6', ARRAY[99]);

INSERT INTO tb_user_role(user_id, role_id) VALUES(4,4)
INSERT INTO tb_user_role(user_id, role_id) VALUES(5,4)

INSERT INTO tb_client(id, cpf) VALUES (4, '73995808042');
INSERT INTO tb_client(id, cpf) VALUES (5, '46311990083');


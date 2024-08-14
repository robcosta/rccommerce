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

INSERT INTO tb_user (name, email, password) VALUES ('John Red', 'john@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2');
INSERT INTO tb_user (name, email, password) VALUES ('Peter Yellow', 'peter@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');

INSERT INTO tb_user_role(user_id, role_id) VALUES(4,4)
INSERT INTO tb_user_role(user_id, role_id) VALUES(5,4)

INSERT INTO tb_user_auth(user_id, auth_id) VALUES(4,6)
INSERT INTO tb_user_auth(user_id, auth_id) VALUES(5,6)

INSERT INTO tb_client(id, cpf) VALUES (4, '73995808042');
INSERT INTO tb_client(id, cpf) VALUES (5, '46311990083');


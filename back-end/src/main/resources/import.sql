INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_SELLER');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');

INSERT INTO tb_user (name, email, password) VALUES ('Maria Brown', 'maria@gmail.com', '$2a$10$efAKHcdzfnBwg5yCvuiOMeiu8pB6TuvNayPjVAZpopc1Ijx95Wyu2');
INSERT INTO tb_user (name, email, password) VALUES ('Bob Green', 'bob@gmail.com', '$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze');
INSERT INTO tb_user (name, email, password) VALUES ('Alex Blue', 'alex@gmail.com','$2a$10$bPi3ofxG3lv/.M4WnzHnR.qj7S2c7sKVRINI7fteO0GYz0e/9YrZ6');

INSERT INTO tb_user_role(user_id, role_id) VALUES(1,1)
INSERT INTO tb_user_role(user_id, role_id) VALUES(1,2)
INSERT INTO tb_user_role(user_id, role_id) VALUES(1,3)
INSERT INTO tb_user_role(user_id, role_id) VALUES(2,2)
INSERT INTO tb_user_role(user_id, role_id) VALUES(3,3)

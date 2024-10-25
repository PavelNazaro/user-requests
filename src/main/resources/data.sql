INSERT INTO users (id, name, password) VALUES
(1, 'Admin', '$2a$12$c./ds5lBsraznVePA0amXunmtJJbv3UsBZnaWR0LhED4QzsTcxf4W'),
(2, 'Operator 1', '$2a$12$4neSVpQCTxmjGh.8gmfmIuN8s1V2bG.a32wYDljLbNfG6yVgglGdK'),
(3, 'Operator 2', '$2a$12$4neSVpQCTxmjGh.8gmfmIuN8s1V2bG.a32wYDljLbNfG6yVgglGdK'),
(4, 'User 1', '$2a$12$SnlYpBwzSlAu0r.x7ofhte8131NwHApqRRMGHr40flW2gHIaU2gly'),
(5, 'User 2', '$2a$12$SnlYpBwzSlAu0r.x7ofhte8131NwHApqRRMGHr40flW2gHIaU2gly'),
(6, 'User 3', '$2a$12$SnlYpBwzSlAu0r.x7ofhte8131NwHApqRRMGHr40flW2gHIaU2gly'),
(7, 'User 4', '$2a$12$SnlYpBwzSlAu0r.x7ofhte8131NwHApqRRMGHr40flW2gHIaU2gly'),
(8, 'Admin and Operator', '$2a$12$c./ds5lBsraznVePA0amXunmtJJbv3UsBZnaWR0LhED4QzsTcxf4W'),
(9, 'Admin and Operator and User', '$2a$12$c./ds5lBsraznVePA0amXunmtJJbv3UsBZnaWR0LhED4QzsTcxf4W');

INSERT INTO user_roles (user_id, roles) VALUES
(1, 'ADMIN'),
(2, 'OPERATOR'),
(3, 'OPERATOR'),
(4, 'USER'),
(5, 'USER'),
(6, 'USER'),
(7, 'USER'),
(8, 'ADMIN'),
(8, 'OPERATOR'),
(9, 'ADMIN'),
(9, 'OPERATOR'),
(9, 'USER');

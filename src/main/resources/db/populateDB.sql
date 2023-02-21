DELETE
FROM user_role;
DELETE
FROM meals;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (datetime, description, calories, user_id)
VALUES ('2023-02-19T10:00', 'Breakfast', 600, 100000),
       ('2023-02-19T13:00', 'Lunch', 300, 100000),
       ('2023-02-19T19:00', 'Supper', 600, 100000),
       ('2023-02-20T00:00', 'Cheatmeal', 800, 100000),
       ('2023-02-20T10:00', 'Breakfast', 600, 100000),
       ('2023-02-20T14:00', 'Lunch', 500, 100000),
       ('2023-02-20T20:00', 'supper', 500, 100000),
       ('2023-02-19T10:00', 'Breakfast', 600, 100001),
       ('2023-02-19T13:00', 'Lunch', 300, 100001),
       ('2023-02-19T19:00', 'Supper', 600, 100001);
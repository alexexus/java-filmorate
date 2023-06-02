# Filmorate

### Описание
Сервис, который работает с фильмами и оценками пользователей, а также позволяет посмотреть топ фильмов по оценкам. Пользователи могут ставить лайки фильмам, писать рецензии, добавлять друг друга в друзья.

### Стек технологий
Java, Spring, Maven, H2, Lombok

### Инструкция по развёртыванию
Запустить файл FilmorateApplication.

Отправлять запросы по URL - http://localhost:8080/

### База данных для проекта filmorate.

![База данных](https://imgur.com/BnBpDHq.png)

### Примеры SQL запросов:

1) Получить всех пользователей: SELECT * FROM users;
2) Получить пользователя с ID = 1: SELECT * FROM users WHERE user_id = 1;
3) Получить ID друзей пользователя с ID = 1: SELECT f.friend_id FROM users
   AS u JOIN friends AS f ON u.user_id = f.user_id WHERE u.user_id = 1;
4) Получить фильмы с рейтингом больше 3: SELECT f.film_id FROM films AS f
   JOIN likes AS l ON f.film_id = l.film_id WHERE COUNT(l.user_id) > 3;
5) Получить названия жанров в фильме с ID = 1: SELECT g.genre_name FROM films
   AS f JOIN film_genre AS fg ON f.film_id = fg.film_id JOIN genre AS g ON
   fg.genre_id = g.genre_id WHERE f.film_id = 1;

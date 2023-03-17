CREATE TABLE IF NOT EXISTS films (
                         film_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                         name text,
                         description text,
                         release_date date,
                         duration int,
                         rating_MPA_id int
);

CREATE TABLE IF NOT EXISTS likes (
                         film_id int,
                         user_id int,
                         PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS genre (
                         genre_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                         genre_name text
);

CREATE TABLE IF NOT EXISTS film_genre (
                              genre_id int,
                              film_id int,
                              PRIMARY KEY (genre_id, film_id)
);

CREATE TABLE IF NOT EXISTS rating_mpa (
                              rating_mpa_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                              rating_mpa_name text
);

CREATE TABLE IF NOT EXISTS users (
                         user_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                         email text,
                         login text,
                         name text,
                         birthday date
);

CREATE TABLE IF NOT EXISTS friends (
                           user_id int,
                           friend_id int,
                           PRIMARY KEY (user_id, friend_id)
);

ALTER TABLE film_genre ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE film_genre ADD FOREIGN KEY (genre_id) REFERENCES genre (genre_id);

ALTER TABLE likes ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE likes ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE friends ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE friends ADD FOREIGN KEY (friend_id) REFERENCES users (user_id);

ALTER TABLE films ADD FOREIGN KEY (rating_mpa_id) REFERENCES rating_MPA (rating_mpa_id);
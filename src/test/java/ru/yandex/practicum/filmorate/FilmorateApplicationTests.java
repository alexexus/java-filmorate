package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

    private final UserDaoImpl userStorage;
    private final FilmDaoImpl filmDaoImpl;
    private final GenreDaoImpl genreDaoImpl;
    private final MpaDaoImpl mpaDaoImpl;

    @Test
    @Order(1)
    public void testGetUserById() {
        userStorage.addUser(User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1998, 12, 11))
                .build());
        assertThat(userStorage.getUserById(1)).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Order(2)
    public void testDeleteUserById() {
        userStorage.deleteUser(1);
        assertThat(userStorage.getAllUsers().isEmpty()).isTrue();
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        userStorage.addUser(User.builder()
                .email("email2")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1998, 12, 12))
                .build());
        userStorage.updateUser(User.builder()
                .id(2)
                .email("updateEmail2")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1998, 12, 12))
                .build());
        assertThat(userStorage.getUserById(2)).hasFieldOrPropertyWithValue("email", "updateEmail2");
    }

    @Test
    @Order(4)
    public void testGetAllUsers() {
        userStorage.addUser(User.builder()
                .email("email3")
                .login("login3")
                .name("name3")
                .birthday(LocalDate.of(1998, 12, 13))
                .build());
        assertThat(userStorage.getAllUsers().get(1)).hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    @Order(5)
    public void testAddFriend() {
        userStorage.addFriend(2, 3);
        assertThat(userStorage.getAllFriends(2).get(0)).hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    @Order(6)
    public void testDeleteFriend() {
        userStorage.deleteFriend(2, 3);
        assertThat(userStorage.getAllFriends(2).isEmpty()).isTrue();
    }

    @Test
    @Order(7)
    public void testGetAllFriends() {
        userStorage.addFriend(2, 3);
        assertThat(userStorage.getAllFriends(2).get(0)).hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    @Order(8)
    public void testGetCommonFriends() {
        userStorage.addUser(User.builder()
                .email("email4")
                .login("login4")
                .name("name4")
                .birthday(LocalDate.of(1998, 12, 14))
                .build());
        userStorage.addFriend(2, 4);
        userStorage.addFriend(3, 4);
        assertThat(userStorage.getCommonFriends(2, 3).get(0)).hasFieldOrPropertyWithValue("id", 4L);
    }

    @Test
    @Order(9)
    public void testUserNotExists() {
        assertThat(userStorage.userExists(999)).isFalse();
    }

    @Test
    @Order(10)
    public void testAddFilm() {
        filmDaoImpl.addFilm(Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 12, 11))
                .duration(111)
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(List.of(Genre.builder().id(1).name("Комедия").build()))
                .build());
        assertThat(filmDaoImpl.getFilmById(1)).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Order(11)
    public void testDeleteFilm() {
        filmDaoImpl.deleteFilm(1);
        assertThat(filmDaoImpl.getAllFilms().isEmpty()).isTrue();
    }

    @Test
    @Order(12)
    public void testUpdateFilm() {
        filmDaoImpl.addFilm(Film.builder()
                .id(2)
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(112)
                .mpa(Mpa.builder().id(2).name("PG").build())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build());
        filmDaoImpl.updateFilm(Film.builder()
                .id(2)
                .name("updateName2")
                .description("description2")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(112)
                .mpa(Mpa.builder().id(2).name("PG").build())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build());
        assertThat(filmDaoImpl.getFilmById(2)).hasFieldOrPropertyWithValue("name", "updateName2");
    }

    @Test
    @Order(13)
    public void testGetAllFilms() {
        assertThat(filmDaoImpl.getAllFilms().size() == 1).isTrue();
    }

    @Test
    @Order(14)
    public void testGetFilmById() {
        assertThat(filmDaoImpl.getFilmById(2)).hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    @Order(15)
    public void testAddLike() {
        filmDaoImpl.addFilm(Film.builder()
                .id(3)
                .name("name3")
                .description("description3")
                .releaseDate(LocalDate.of(2000, 12, 13))
                .duration(113)
                .mpa(Mpa.builder().id(3).name("PG-13").build())
                .genres(List.of(Genre.builder().id(3).name("Мультфильм").build()))
                .build());
        filmDaoImpl.addLike(2, 3);
        assertThat(filmDaoImpl.getPopularFilms(10).get(0)).hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    @Order(16)
    public void testDeleteLike() {
        filmDaoImpl.deleteLike(2, 3);
        assertThat(filmDaoImpl.getPopularFilms(10).size() == 2).isTrue();
    }

    @Test
    @Order(17)
    public void testGetPopularFilms() {
        filmDaoImpl.addLike(2, 3);
        assertThat(filmDaoImpl.getPopularFilms(10).get(0)).hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    @Order(18)
    public void testFilmNotExists() {
        assertThat(filmDaoImpl.filmExists(999)).isFalse();
    }

    @Test
    @Order(19)
    public void testGetGenreById() {
        assertThat(genreDaoImpl.getGenreById(1)).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @Order(20)
    public void testGetAllGenres() {
        assertThat(genreDaoImpl.getAllGenres().get(5)).hasFieldOrPropertyWithValue("name", "Боевик");
    }

    @Test
    @Order(21)
    public void testGetFilmGenres() {
        assertThat(genreDaoImpl.getFilmGenres(2).get(0)).hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    @Order(22)
    public void testGenreNotExists() {
        assertThat(genreDaoImpl.genreNotExists(999)).isTrue();
    }

    @Test
    @Order(23)
    public void testGetMpaById() {
        assertThat(mpaDaoImpl.getMpaById(1)).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @Order(24)
    public void testGetAllMpa() {
        assertThat(mpaDaoImpl.getAllMpa().get(4)).hasFieldOrPropertyWithValue("name", "NC-17");
    }

    @Test
    @Order(25)
    public void testMpaNotExists() {
        assertThat(mpaDaoImpl.mpaNotExists(999)).isTrue();
    }
}

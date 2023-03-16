package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        addGenre(film.getId(), film.getGenres());
        return getFilmById(film.getId());
    }

    @Override
    public void deleteFilm(long id) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE FILMS SET " +
                "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_MPA_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        addGenre(film.getId(), film.getGenres());
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATING_MPA_ID, " +
                "RM.RATING_MPA_NAME FROM FILMS F LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATING_MPA_ID, " +
                "RM.RATING_MPA_NAME FROM FILMS F LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                "WHERE F.FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public void addLike(long id, long userId) {
        String sqlQuery = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATING_MPA_ID, " +
                "RM.RATING_MPA_NAME, COUNT(L.FILM_ID) FROM FILMS F LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID GROUP BY F.FILM_ID ORDER BY COUNT(L.FILM_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public boolean filmNotExists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result != 1;
    }

    private void addGenre(long filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, filmId);
        } else {
            String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
            jdbcTemplate.update(sql, filmId);
            long count = 0;
            for (Genre genre : genres) {
                if (count < genre.getId()) {
                    getFilmById(filmId).getGenres().add(genre);
                    String sqlQuery = "INSERT INTO FILM_GENRE (GENRE_ID, FILM_ID) VALUES (?, ?)";
                    jdbcTemplate.update(sqlQuery, genre.getId(), filmId);
                    count = genre.getId();
                }
            }
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(LocalDate.parse(rs.getString("RELEASE_DATE")))
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getLong("RATING_MPA_ID"), rs.getString("RATING_MPA_NAME")))
                .genres(genreDbStorage.getFilmGenres(rs.getLong("FILM_ID")))
                .build();
    }
}

package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, generatedKeyHolder);
        Long filmId = generatedKeyHolder.getKey().longValue();
        film.setId(filmId);
        addGenres(filmId, film.getGenres());
        checkGenres(film);
        addDirectors(filmId, film.getDirectors());
        return film;
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
        addGenres(film.getId(), film.getGenres());
        checkGenres(film);
        addDirectors(film.getId(), film.getDirectors());
        return film;
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
        String sqlQuery = "SELECT F.*, RM.RATING_MPA_NAME, COUNT(L.FILM_ID) FROM FILMS F " +
                "LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                "GROUP BY F.FILM_ID ORDER BY COUNT(L.FILM_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getSortedFilmsByDirId(long directorId, String sort) {
        String sqlQuery;
        if (sort.equals("likes")) {
            sqlQuery = "SELECT F.*, RM.RATING_MPA_NAME, COUNT(L.FILM_ID) FROM FILMS F " +
                    "LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                    "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID WHERE FD.DIRECTOR_ID = ? " +
                    "GROUP BY F.FILM_ID ORDER BY COUNT(L.FILM_ID) DESC";
        } else {
            sqlQuery = "SELECT F.*, RM.RATING_MPA_NAME FROM FILMS F " +
                    "LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID WHERE FD.DIRECTOR_ID = ? " +
                    "GROUP BY F.RELEASE_DATE ORDER BY F.RELEASE_DATE";
        }
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getSortedFilmByQuery(String query, String by) {
        String sqlQuery = "";
        query = "%" + query + "%";
        if (by.equals("director")) {
            sqlQuery = "SELECT F.*, RM.RATING_MPA_NAME FROM FILMS F " +
                    "LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID " +
                    "LEFT JOIN DIRECTOR D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "WHERE LOWER(D.DIRECTOR_NAME) LIKE LOWER(?) " +
                    "GROUP BY D.DIRECTOR_NAME ORDER BY D.DIRECTOR_NAME";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
        } else if (by.equals("title")) {
            sqlQuery = "SELECT F.*, RM.RATING_MPA_NAME FROM FILMS F " +
                    "LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                    "WHERE LOWER(F.NAME) LIKE LOWER(?) " +
                    "GROUP BY F.NAME ORDER BY F.NAME";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
        }
        if (by.equals("director,title")) {
            sqlQuery = "SELECT F.*, RM.RATING_MPA_NAME FROM FILMS F " +
                    "LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID " +
                    "LEFT JOIN DIRECTOR D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "WHERE LOWER(D.DIRECTOR_NAME) LIKE LOWER(?) OR LOWER(F.NAME) LIKE LOWER(?) " +
                    "GROUP BY D.DIRECTOR_NAME, F.NAME ORDER BY D.DIRECTOR_NAME, F.NAME";
        } else if (by.equals("title,director")) {
            sqlQuery = "SELECT F.*, RM.RATING_MPA_NAME FROM FILMS F " +
                    "LEFT JOIN RATING_MPA RM ON F.RATING_MPA_ID = RM.RATING_MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID " +
                    "LEFT JOIN DIRECTOR D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "WHERE LOWER(F.NAME) LIKE LOWER(?) OR LOWER(D.DIRECTOR_NAME) LIKE LOWER(?) " +
                    "GROUP BY F.NAME, D.DIRECTOR_NAME ORDER BY F.NAME DESC, D.DIRECTOR_NAME";
        }
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query, query);
    }

    @Override
    public boolean filmExists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private void addGenres(long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, filmId);
            return;
        }
        List<Genre> genreListWithoutDuplicate = new ArrayList<>(genres);
        genreListWithoutDuplicate.sort((g1, g2) -> (int) (g1.getId() - g2.getId()));
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        jdbcTemplate.batchUpdate("INSERT INTO FILM_GENRE (GENRE_ID, FILM_ID) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, genreListWithoutDuplicate.get(i).getId());
                        ps.setLong(2, filmId);
                    }

                    @Override
                    public int getBatchSize() {
                        return genreListWithoutDuplicate.size();
                    }
                });
    }

    private void addDirectors(long filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, filmId);
            return;
        }
        List<Director> directorListWithoutDuplicate = new ArrayList<>(directors);
        directorListWithoutDuplicate.sort((g1, g2) -> (int) (g1.getId() - g2.getId()));
        String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        jdbcTemplate.batchUpdate("INSERT INTO FILM_DIRECTOR (DIRECTOR_ID, FILM_ID) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, directorListWithoutDuplicate.get(i).getId());
                        ps.setLong(2, filmId);
                    }

                    @Override
                    public int getBatchSize() {
                        return directorListWithoutDuplicate.size();
                    }
                });
    }

    private Set<Genre> getGenresByFilmId(long filmId) {
        String sqlQuery = "SELECT * FROM FILM_GENRE FG " +
                "LEFT JOIN GENRE G on FG.GENRE_ID = G.GENRE_ID WHERE FG.FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId));
    }

    private Set<Director> getDirectorsByFilmId(long filmId) {
        String sqlQuery = "SELECT * FROM FILM_DIRECTOR FD " +
                "LEFT JOIN DIRECTOR D on FD.DIRECTOR_ID = D.DIRECTOR_ID WHERE FD.FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId));
    }

    private void checkGenres(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        film.setGenres(film.getGenres().stream()
                .sorted((g1, g2) -> (int) (g1.getId() - g2.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(LocalDate.parse(rs.getString("RELEASE_DATE")))
                .duration(rs.getInt("DURATION"))
                .mpa(Mpa.builder()
                        .id(rs.getLong("RATING_MPA_ID"))
                        .name(rs.getString("RATING_MPA_NAME"))
                        .build())
                .genres(getGenresByFilmId(rs.getLong("FILM_ID")))
                .directors(getDirectorsByFilmId(rs.getLong("FILM_ID")))
                .build();
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }
}

package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Repository
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(long id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    public List<Genre> getAllGenre() {
        String sqlQuery = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public List<Genre> getFilmGenres(long filmId) {
        String sqlQuery = "SELECT * FROM FILM_GENRE FG LEFT JOIN GENRE G on FG.GENRE_ID = G.GENRE_ID WHERE FG.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
    }

    public boolean genreNotExists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM GENRE WHERE GENRE_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result != 1;
    }

}

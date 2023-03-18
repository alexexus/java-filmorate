package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(long id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public List<Genre> getFilmGenres(long filmId) {
        String sqlQuery = "SELECT * FROM FILM_GENRE FG LEFT JOIN GENRE G on FG.GENRE_ID = G.GENRE_ID WHERE FG.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    @Override
    public boolean genreExists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM GENRE WHERE GENRE_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
    }
}

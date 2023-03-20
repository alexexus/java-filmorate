package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(long id) {
        String sqlQuery = "SELECT * FROM RATING_MPA WHERE RATING_MPA_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM RATING_MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public boolean mpaExists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM RATING_MPA WHERE RATING_MPA_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("RATING_MPA_ID"))
                .name(rs.getString("RATING_MPA_NAME"))
                .build();
    }
}

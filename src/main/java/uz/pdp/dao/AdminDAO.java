package uz.pdp.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AdminDAO(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean activate(Long id) {
        var sql = "update authuser set active = true where id = :u_id";
        var paramSource = new MapSqlParameterSource()
                .addValue("u_id", id);
        namedParameterJdbcTemplate.update(sql, paramSource);
        return true;
    }

    public boolean deactivate(Long id) {
        var sql = "update authuser set active = false where id = :u_id";
        var paramSource = new MapSqlParameterSource()
                .addValue("u_id", id);
        namedParameterJdbcTemplate.update(sql, paramSource);
        return true;
    }
}
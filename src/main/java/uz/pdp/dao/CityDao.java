package uz.pdp.dao;

import lombok.NonNull;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import uz.pdp.domain.City;
import uz.pdp.domain.SubscribedCity;
import uz.pdp.domain.Weather;
import uz.pdp.dto.CityDTO;
import uz.pdp.dto.WeatherDTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CityDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CityDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Integer save(@NonNull City city, Authentication authentication) {
        var sql = "insert into city(name, country, created_by) values(:name, :country, :created_by)";

        var paramSource = new MapSqlParameterSource()
                .addValue("name", city.getName())
                .addValue("country", city.getCountry())
                .addValue("created_by", authentication.getName());

        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("id");
    }

    public List<City> getAllCity() {
        var sql = "select * from city order by id";
        var mapper = BeanPropertyRowMapper.newInstance(City.class);
        return namedParameterJdbcTemplate.query(sql, mapper);
    }

    public boolean deleteCity(Integer id) {
        var sql = "delete from city c using subscribedcities s where c.id = :id and c.id = s.city_id and c.name = s.city_name";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource().addValue("id", id));
        var paramSource = new MapSqlParameterSource()
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, paramSource);
        System.out.println("city id: " + id);
        return true;
    }

    public Optional<City> findById(int id) {
        var sql = "select * from city where id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", id);
        var rowMapper = BeanPropertyRowMapper.newInstance(City.class);
        City city = namedParameterJdbcTemplate.queryForObject(sql, paramSource, rowMapper);
        return Optional.ofNullable(city);
    }

    public boolean update(Integer id, CityDTO cityDTO) {
        var sql = "update city set name = :name, country = :country where id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("name", cityDTO.name())
                .addValue("country", cityDTO.country())
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, paramSource);
        return true;
    }

    public boolean updateWeather(Integer id, WeatherDTO weatherDTO) {
        var sql = "update weather set celsius = :celsius, fahrenheit = :fahrenheit where city_id = :id";
        var mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("celsius", weatherDTO.celsius())
                .addValue("fahrenheit", weatherDTO.fahrenheit())
                .addValue("city_id", id);
        namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
        return true;
    }

    public List<Weather> getWeather(Integer id) {
        var sql = "select * from weather where id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", id);
        var mapper = BeanPropertyRowMapper.newInstance(Weather.class);
        return namedParameterJdbcTemplate.query(sql, paramSource, mapper);
    }

    public Weather getWeatherById(Integer id) {
        var sql = "select * from weather where id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", id);
        var mapper = BeanPropertyRowMapper.newInstance(Weather.class);
        return namedParameterJdbcTemplate.queryForObject(sql, paramSource, mapper);
    }

    public Integer deleteWeather(int id) {
        var sql = "delete from weather where id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", id);
        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("id");
    }

    public Integer addWeather(int city_id, WeatherDTO weatherDTO) {
        var sql = "insert into weather (city_id, celsius, fahrenheit, dates) values (:city_id, :celsius, :fahrenheit, :dates);";
        var paramSource = new MapSqlParameterSource()
                .addValue("city_id", city_id)
                .addValue("celsius", weatherDTO.celsius())
                .addValue("fahrenheit", weatherDTO.fahrenheit())
                .addValue("dates", weatherDTO.date());

        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("id");
    }

    public Integer subscribeCity(Long id, String cityName, Integer cityId) {
        var checkSql = "select * from subscribedcities where user_id = :id and city_name = :city_name";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id).addValue("city_name", cityName);
        List<SubscribedCity> query = namedParameterJdbcTemplate.query(checkSql, parameterSource, BeanPropertyRowMapper.newInstance(SubscribedCity.class));
        System.out.println(query);
        if (query.isEmpty()) {
            var sql = "insert into subscribedcities(user_id, city_name, city_id) values(:user_id, :city_name, :city_id)";
            var paramSource = new MapSqlParameterSource()
                    .addValue("user_id", id)
                    .addValue("city_name", cityName)
                    .addValue("city_id", cityId);
            var keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
            return (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("id");
        } else return null;
    }

    public boolean checkSubscribedCity(int user_id, String cityName) {
        var sql = "select * from subscribedcities where user_id = :user_id and city_name = :city_name";
        var paramSource = new MapSqlParameterSource()
                .addValue("user_id", user_id)
                .addValue("city_name", cityName);
        var mapper = new BeanPropertyRowMapper<SubscribedCity>();
        SubscribedCity subscribedCity = namedParameterJdbcTemplate.queryForObject(sql, paramSource, mapper);
        return subscribedCity != null;
    }

    public List<SubscribedCity> getSubscribedCities(Long id) {
        var sql = "select * from subscribedcities where user_id = :id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", id);
        var mapper = BeanPropertyRowMapper.newInstance(SubscribedCity.class);
        return namedParameterJdbcTemplate.query(sql, paramSource, mapper);
    }

    public void unsubscribeCity(Long id, Integer cityId) {
        var sql = "delete from subscribedcities where user_id = :id and city_id = :city_id";
        var paramSource = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("city_id", cityId);
        namedParameterJdbcTemplate.update(sql, paramSource);
    }


//    public Optional<AuthUser> findByUsername(@NonNull String username) {
//        var sql = "select * from authuser t where t.username = :username and active = true";
//        var paramSource = new MapSqlParameterSource().addValue("username", username);
//        var rowMapper = BeanPropertyRowMapper.newInstance(AuthUser.class);
//        try {
//            var authUser = namedParameterJdbcTemplate.queryForObject(sql, paramSource, rowMapper);
//            return Optional.of(authUser);
//        } catch (Exception e) {
//            return Optional.empty();
//        }
//    }
}

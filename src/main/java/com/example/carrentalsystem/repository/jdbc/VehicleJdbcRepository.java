package com.example.carrentalsystem.repository.jdbc;

import com.example.carrentalsystem.entity.Vehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public VehicleJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Vehicle> vehicleRowMapper = new RowMapper<Vehicle>() {
        @Override
        public Vehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(rs.getLong("id"));
            vehicle.setBrand(rs.getString("brand"));
            vehicle.setModel(rs.getString("model"));
            vehicle.setYear(rs.getInt("year"));
            vehicle.setColor(rs.getString("color"));
            vehicle.setLicensePlate(rs.getString("license_plate"));
            vehicle.setVehicleType(Vehicle.VehicleType.valueOf(rs.getString("vehicle_type")));
            vehicle.setDailyRate(rs.getBigDecimal("daily_rate"));
            vehicle.setAvailable(rs.getBoolean("available"));
            vehicle.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            vehicle.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return vehicle;
        }
    };

    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            return insert(vehicle);
        } else {
            return update(vehicle);
        }
    }

    private Vehicle insert(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (brand, model, year, color, license_plate, vehicle_type, daily_rate, available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, vehicle.getBrand());
            ps.setString(2, vehicle.getModel());
            ps.setInt(3, vehicle.getYear());
            ps.setString(4, vehicle.getColor());
            ps.setString(5, vehicle.getLicensePlate());
            ps.setString(6, vehicle.getVehicleType().name());
            ps.setBigDecimal(7, vehicle.getDailyRate());
            ps.setBoolean(8, vehicle.getAvailable());
            return ps;
        }, keyHolder);

        vehicle.setId(keyHolder.getKey().longValue());
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());
        return vehicle;
    }

    private Vehicle update(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET brand = ?, model = ?, year = ?, color = ?, " +
                "license_plate = ?, vehicle_type = ?, daily_rate = ?, available = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        jdbcTemplate.update(sql, vehicle.getBrand(), vehicle.getModel(), vehicle.getYear(),
                vehicle.getColor(), vehicle.getLicensePlate(), vehicle.getVehicleType().name(),
                vehicle.getDailyRate(), vehicle.getAvailable(), vehicle.getId());

        vehicle.setUpdatedAt(LocalDateTime.now());
        return vehicle;
    }

    public Optional<Vehicle> findById(Long id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        List<Vehicle> vehicles = jdbcTemplate.query(sql, vehicleRowMapper, id);
        return vehicles.isEmpty() ? Optional.empty() : Optional.of(vehicles.get(0));
    }

    public List<Vehicle> findAll() {
        String sql = "SELECT * FROM vehicles ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, vehicleRowMapper);
    }

    public List<Vehicle> findAvailable() {
        String sql = "SELECT * FROM vehicles WHERE available = true ORDER BY brand, model";
        return jdbcTemplate.query(sql, vehicleRowMapper);
    }

    public List<Vehicle> findByBrand(String brand) {
        String sql = "SELECT * FROM vehicles WHERE LOWER(brand) = LOWER(?) ORDER BY model";
        return jdbcTemplate.query(sql, vehicleRowMapper, brand);
    }

    public List<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_type = ? ORDER BY brand, model";
        return jdbcTemplate.query(sql, vehicleRowMapper, vehicleType.name());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM vehicles WHERE license_plate = ?";
        List<Vehicle> vehicles = jdbcTemplate.query(sql, vehicleRowMapper, licensePlate);
        return vehicles.isEmpty() ? Optional.empty() : Optional.of(vehicles.get(0));
    }
}

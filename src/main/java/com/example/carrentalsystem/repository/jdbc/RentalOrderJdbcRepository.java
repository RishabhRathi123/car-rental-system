package com.example.carrentalsystem.repository.jdbc;

import com.example.carrentalsystem.entity.RentalOrder;
import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.entity.Vehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class RentalOrderJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public RentalOrderJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<RentalOrder> rentalOrderRowMapper = new RowMapper<RentalOrder>() {
        @Override
        public RentalOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
            RentalOrder order = new RentalOrder();
            order.setId(rs.getLong("id"));

            // Set user (basic info)
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            order.setUser(user);

            // Set vehicle (basic info)
            Vehicle vehicle = new Vehicle();
            vehicle.setId(rs.getLong("vehicle_id"));
            vehicle.setBrand(rs.getString("brand"));
            vehicle.setModel(rs.getString("model"));
            vehicle.setYear(rs.getInt("year"));
            vehicle.setLicensePlate(rs.getString("license_plate"));
            order.setVehicle(vehicle);

            order.setStartDate(rs.getDate("start_date").toLocalDate());
            order.setEndDate(rs.getDate("end_date").toLocalDate());
            order.setTotalDays(rs.getInt("total_days"));
            order.setDailyRate(rs.getBigDecimal("daily_rate"));
            order.setTotalAmount(rs.getBigDecimal("total_amount"));
            order.setStatus(RentalOrder.OrderStatus.valueOf(rs.getString("status")));
            order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

            return order;
        }
    };

    public RentalOrder save(RentalOrder order) {
        if (order.getId() == null) {
            return insert(order);
        } else {
            return update(order);
        }
    }

    private RentalOrder insert(RentalOrder order) {
        String sql = "INSERT INTO rental_orders (user_id, vehicle_id, start_date, end_date, " +
                "total_days, daily_rate, total_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getUser().getId());
            ps.setLong(2, order.getVehicle().getId());
            ps.setDate(3, Date.valueOf(order.getStartDate()));
            ps.setDate(4, Date.valueOf(order.getEndDate()));
            ps.setInt(5, order.getTotalDays());
            ps.setBigDecimal(6, order.getDailyRate());
            ps.setBigDecimal(7, order.getTotalAmount());
            ps.setString(8, order.getStatus().name());
            return ps;
        }, keyHolder);

        order.setId(keyHolder.getKey().longValue());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    private RentalOrder update(RentalOrder order) {
        String sql = "UPDATE rental_orders SET user_id = ?, vehicle_id = ?, start_date = ?, " +
                "end_date = ?, total_days = ?, daily_rate = ?, total_amount = ?, status = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        jdbcTemplate.update(sql, order.getUser().getId(), order.getVehicle().getId(),
                Date.valueOf(order.getStartDate()), Date.valueOf(order.getEndDate()),
                order.getTotalDays(), order.getDailyRate(), order.getTotalAmount(),
                order.getStatus().name(), order.getId());

        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public Optional<RentalOrder> findById(Long id) {
        String sql = "SELECT ro.*, u.username, u.email, u.first_name, u.last_name, " +
                "v.brand, v.model, v.year, v.license_plate " +
                "FROM rental_orders ro " +
                "JOIN users u ON ro.user_id = u.id " +
                "JOIN vehicles v ON ro.vehicle_id = v.id " +
                "WHERE ro.id = ?";

        List<RentalOrder> orders = jdbcTemplate.query(sql, rentalOrderRowMapper, id);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    public List<RentalOrder> findAll() {
        String sql = "SELECT ro.*, u.username, u.email, u.first_name, u.last_name, " +
                "v.brand, v.model, v.year, v.license_plate " +
                "FROM rental_orders ro " +
                "JOIN users u ON ro.user_id = u.id " +
                "JOIN vehicles v ON ro.vehicle_id = v.id " +
                "ORDER BY ro.created_at DESC";

        return jdbcTemplate.query(sql, rentalOrderRowMapper);
    }

    public List<RentalOrder> findByUserId(Long userId) {
        String sql = "SELECT ro.*, u.username, u.email, u.first_name, u.last_name, " +
                "v.brand, v.model, v.year, v.license_plate " +
                "FROM rental_orders ro " +
                "JOIN users u ON ro.user_id = u.id " +
                "JOIN vehicles v ON ro.vehicle_id = v.id " +
                "WHERE ro.user_id = ? ORDER BY ro.created_at DESC";

        return jdbcTemplate.query(sql, rentalOrderRowMapper, userId);
    }

    public List<RentalOrder> findByStatus(RentalOrder.OrderStatus status) {
        String sql = "SELECT ro.*, u.username, u.email, u.first_name, u.last_name, " +
                "v.brand, v.model, v.year, v.license_plate " +
                "FROM rental_orders ro " +
                "JOIN users u ON ro.user_id = u.id " +
                "JOIN vehicles v ON ro.vehicle_id = v.id " +
                "WHERE ro.status = ? ORDER BY ro.created_at DESC";

        return jdbcTemplate.query(sql, rentalOrderRowMapper, status.name());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM rental_orders WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

CREATE DATABASE IF NOT EXISTS car_rental_db;
USE car_rental_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(15),
    driver_license VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(30) NOT NULL,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type ENUM('SEDAN', 'SUV', 'HATCHBACK', 'CONVERTIBLE') NOT NULL,
    daily_rate DECIMAL(10, 2) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Rental Orders table
CREATE TABLE IF NOT EXISTS rental_orders (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             user_id BIGINT NOT NULL,
                                             vehicle_id BIGINT NOT NULL,
                                             start_date DATE NOT NULL,
                                             end_date DATE NOT NULL,
                                             total_days INT NOT NULL,
                                             daily_rate DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
    );

-- Insert sample data
INSERT INTO users (username, email, password, first_name, last_name, phone_number, driver_license) VALUES
                                                                                                       ('john_doe', 'john@example.com', 'password123', 'John', 'Doe', '1234567890', 'DL12345'),
                                                                                                       ('jane_smith', 'jane@example.com', 'password123', 'Jane', 'Smith', '0987654321', 'DL67890');

INSERT INTO vehicles (brand, model, year, color, license_plate, vehicle_type, daily_rate) VALUES
                                                                                              ('Toyota', 'Camry', 2022, 'White', 'ABC123', 'SEDAN', 50.00),
                                                                                              ('Honda', 'CR-V', 2023, 'Black', 'XYZ789', 'SUV', 75.00),
                                                                                              ('BMW', 'X5', 2022, 'Blue', 'BMW001', 'SUV', 120.00);

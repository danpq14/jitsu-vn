-- Jitsu Database Schema Initialization Script (NO FOREIGN KEYS)
-- This script runs automatically when PostgreSQL container starts
-- Database and user are already created by Docker environment variables

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'DRIVER'))
);

-- Booking sessions table
CREATE TABLE IF NOT EXISTS booking_sessions (
    id BIGSERIAL PRIMARY KEY,
    region_code VARCHAR(10) NOT NULL,
    name VARCHAR(255) NOT NULL,
    target_date DATE NOT NULL,
    start_booking_time TIMESTAMP NOT NULL,
    end_booking_time TIMESTAMP NOT NULL,
    latest_cancellation_time TIMESTAMP NOT NULL,
    max_tickets_per_driver INTEGER NOT NULL
);

-- Junction table for booking session target drivers (NO FK)
CREATE TABLE IF NOT EXISTS booking_session_drivers (
    booking_session_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    PRIMARY KEY (booking_session_id, driver_id)
);

-- Tickets table (NO FK)
CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    zone VARCHAR(10) NOT NULL,
    target_date DATE NOT NULL,
    booking_session_id BIGINT NOT NULL,
    booked_by_driver_id BIGINT,
    booked_at TIMESTAMP,
    end_booking_time TIMESTAMP NOT NULL
);

-- Assignments table (NO FK)
CREATE TABLE IF NOT EXISTS assignments (
    id BIGSERIAL PRIMARY KEY,
    zone VARCHAR(10) NOT NULL,
    target_date DATE NOT NULL,
    description TEXT NOT NULL,
    claimed_by_driver_id BIGINT,
    claimed_ticket_id BIGINT,
    claimed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'CLAIMED', 'COMPLETED'))
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_booking_sessions_region_date ON booking_sessions(region_code, target_date);
CREATE INDEX IF NOT EXISTS idx_tickets_session_zone ON tickets(booking_session_id, zone);
CREATE INDEX IF NOT EXISTS idx_tickets_driver ON tickets(booked_by_driver_id);
CREATE INDEX IF NOT EXISTS idx_tickets_cleanup ON tickets(end_booking_time, booked_by_driver_id);
CREATE INDEX IF NOT EXISTS idx_assignments_zone_date ON assignments(zone, target_date);
CREATE INDEX IF NOT EXISTS idx_assignments_driver ON assignments(claimed_by_driver_id);
CREATE INDEX IF NOT EXISTS idx_assignments_status ON assignments(status);
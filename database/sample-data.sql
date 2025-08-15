-- Sample Data for Jitsu Logistics System
-- This script runs automatically after schema initialization

-- Insert users (passwords are BCrypt hashed for admin123/driver123)
-- Note: Using proper BCrypt hashes generated with cost 10
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$12$HM/yvSqEuwOjbeGYYTgjX.8p7l3SLVlCauhpMig/xzwpliCDi3qum', 'ADMIN'),
('driver1', '$2a$12$V7cGbquYKUVr12kXpq3xjOrB93o/72w0ckzziyvDTMQgSn7ZAstPW', 'DRIVER'),
('driver2', '$2a$12$V7cGbquYKUVr12kXpq3xjOrB93o/72w0ckzziyvDTMQgSn7ZAstPW', 'DRIVER'),
('driver3', '$2a$12$V7cGbquYKUVr12kXpq3xjOrB93o/72w0ckzziyvDTMQgSn7ZAstPW', 'DRIVER'),
('driver4', '$2a$12$V7cGbquYKUVr12kXpq3xjOrB93o/72w0ckzziyvDTMQgSn7ZAstPW', 'DRIVER')
ON CONFLICT (username) DO NOTHING;

-- Insert sample booking session
INSERT INTO booking_sessions (region_code, name, target_date, start_booking_time, end_booking_time, latest_cancellation_time, max_tickets_per_driver) 
VALUES (
    'SFO', 
    'San Francisco Morning Routes', 
    CURRENT_DATE + INTERVAL '1 day',
    CURRENT_TIMESTAMP + INTERVAL '1 hour',
    CURRENT_TIMESTAMP + INTERVAL '8 hours', 
    CURRENT_TIMESTAMP + INTERVAL '6 hours',
    3
);

-- Insert target drivers for the booking session (assuming session id = 1)
INSERT INTO booking_session_drivers (booking_session_id, driver_id) VALUES 
(1, 2), -- driver1
(1, 3), -- driver2
(1, 4), -- driver3
(1, 5); -- driver4

-- Insert sample tickets for different zones
INSERT INTO tickets (zone, target_date, booking_session_id, end_booking_time) VALUES 
('Z1', CURRENT_DATE + INTERVAL '1 day', 1, CURRENT_TIMESTAMP + INTERVAL '8 hours'),
('Z1', CURRENT_DATE + INTERVAL '1 day', 1, CURRENT_TIMESTAMP + INTERVAL '8 hours'),
('Z2', CURRENT_DATE + INTERVAL '1 day', 1, CURRENT_TIMESTAMP + INTERVAL '8 hours'),
('Z2', CURRENT_DATE + INTERVAL '1 day', 1, CURRENT_TIMESTAMP + INTERVAL '8 hours'),
('Z3', CURRENT_DATE + INTERVAL '1 day', 1, CURRENT_TIMESTAMP + INTERVAL '8 hours'),
('Z3', CURRENT_DATE + INTERVAL '1 day', 1, CURRENT_TIMESTAMP + INTERVAL '8 hours');

-- Insert sample assignments
INSERT INTO assignments (zone, target_date, description) VALUES 
('Z1', CURRENT_DATE + INTERVAL '1 day', 'Deliver packages to Mission District area'),
('Z1', CURRENT_DATE + INTERVAL '1 day', 'Pick up returns from Castro Street shops'),
('Z2', CURRENT_DATE + INTERVAL '1 day', 'Express delivery to Financial District'),
('Z2', CURRENT_DATE + INTERVAL '1 day', 'Bulk delivery to SOMA warehouses'),
('Z3', CURRENT_DATE + INTERVAL '1 day', 'Residential deliveries in Richmond'),
('Z3', CURRENT_DATE + INTERVAL '1 day', 'Commercial pickup route on Geary Blvd');

-- Insert another booking session for LAX
INSERT INTO booking_sessions (region_code, name, target_date, start_booking_time, end_booking_time, latest_cancellation_time, max_tickets_per_driver) 
VALUES (
    'LAX', 
    'Los Angeles Evening Routes', 
    CURRENT_DATE + INTERVAL '2 days',
    CURRENT_TIMESTAMP + INTERVAL '24 hours',
    CURRENT_TIMESTAMP + INTERVAL '32 hours', 
    CURRENT_TIMESTAMP + INTERVAL '30 hours',
    2
);

-- Insert target drivers for LAX session (assuming session id = 2)
INSERT INTO booking_session_drivers (booking_session_id, driver_id) VALUES 
(2, 2), -- driver1
(2, 3); -- driver2
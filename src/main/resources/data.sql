-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS idp_dental;

-- Insert basic roles (Hibernate will create tables)
INSERT INTO idp_dental.roles (nombre) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO idp_dental.roles (nombre) VALUES ('DOCTOR') ON CONFLICT DO NOTHING;
INSERT INTO idp_dental.roles (nombre) VALUES ('RECEPTIONIST') ON CONFLICT DO NOTHING;
INSERT INTO idp_dental.roles (nombre) VALUES ('PATIENT') ON CONFLICT DO NOTHING;

-- CampusNova reference schema (JPA creates equivalent tables at startup)
CREATE TABLE category (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, icon VARCHAR(255));
CREATE TABLE faq (id BIGINT AUTO_INCREMENT PRIMARY KEY, category_id BIGINT NOT NULL, question VARCHAR(500) NOT NULL, answer VARCHAR(5000) NOT NULL, keywords VARCHAR(1000), active BOOLEAN NOT NULL, created_at TIMESTAMP, FOREIGN KEY (category_id) REFERENCES category(id));
CREATE INDEX idx_faq_category ON faq(category_id);
CREATE TABLE chat_history (id BIGINT AUTO_INCREMENT PRIMARY KEY, session_id VARCHAR(255), question VARCHAR(1000), response VARCHAR(5000), category VARCHAR(255), answered BOOLEAN, response_type VARCHAR(40), out_of_domain BOOLEAN, response_time_ms BIGINT, created_at TIMESTAMP);
CREATE INDEX idx_history_session ON chat_history(session_id);
CREATE TABLE announcement (id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), body VARCHAR(3000), created_at TIMESTAMP);
CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, email VARCHAR(255) UNIQUE, name VARCHAR(255), role VARCHAR(40), created_at TIMESTAMP);
CREATE TABLE admins (id BIGINT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL, role VARCHAR(40));
CREATE TABLE college_information (id BIGINT AUTO_INCREMENT PRIMARY KEY, info_key VARCHAR(255) UNIQUE, info_value VARCHAR(5000), verified BOOLEAN, updated_at TIMESTAMP);

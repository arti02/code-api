--liquibase formatted sql

--changeset school:1
CREATE TABLE IF NOT EXISTS student (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    language VARCHAR(100) NOT NULL,
    teacher_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER,
    PRIMARY KEY (id)
    );

--changeset school:2
CREATE TABLE IF NOT EXISTS teacher (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER,
    PRIMARY KEY (id)
    );

--changeset school:3
CREATE TABLE IF NOT EXISTS teacher_languages (
    teacher_id BIGINT NOT NULL,
    language VARCHAR(100) NOT NULL,
    PRIMARY KEY (teacher_id, language),
    CONSTRAINT fk_teacher_languages_teacher
    FOREIGN KEY (teacher_id) REFERENCES teacher(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
    );

--changeset school:4
CREATE TABLE IF NOT EXISTS lesson (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    date DATETIME(6) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER,
    PRIMARY KEY (id),
    INDEX idx_lesson_student (student_id),
    INDEX idx_lesson_teacher (teacher_id),
    INDEX idx_teacher_term (teacher_id, date),
    CONSTRAINT fk_lesson_student
    FOREIGN KEY (student_id) REFERENCES student(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
    CONSTRAINT fk_lesson_teacher
    FOREIGN KEY (teacher_id) REFERENCES teacher(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
    );
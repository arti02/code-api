package com.codingapi.dto;

import java.time.LocalDateTime;

public record LessonDTO(Long id, StudentDTO student, TeacherDTO teacher, LocalDateTime date) {
}

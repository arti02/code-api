package com.codingapi.dto;

import java.time.LocalDateTime;

public record LessonDTO(StudentDTO student, TeacherDTO teacher, LocalDateTime lessonTime) {
}

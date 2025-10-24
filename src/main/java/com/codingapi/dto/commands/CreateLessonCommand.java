package com.codingapi.dto.commands;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateLessonCommand(
		@NotNull(message = "Student ID must be provided") Long studentId,
		@NotNull(message = "Teacher ID must be provided") Long teacherId,
		@NotNull(message = "Lesson term must be provided")
		@Future(message = "Lesson term must be in the future") LocalDateTime lessonDate) {
}
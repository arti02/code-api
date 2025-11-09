package com.codingapi.dto.commands;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ChangeLessonDateCommand(
		@NotNull(message = "New date of lesson have to be provided") LocalDateTime date) {

}

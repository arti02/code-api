package com.codingapi.dto.commands;

import jakarta.validation.constraints.NotNull;

public record ChangeTeacherCommand(
		@NotNull(message = "New teacher ID must be provided") Long newTeacherId) {
}

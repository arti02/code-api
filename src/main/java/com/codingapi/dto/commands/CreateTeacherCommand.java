package com.codingapi.dto.commands;

import com.codingapi.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CreateTeacherCommand(
		@NotBlank(message = "First name cannot be blank") String firstName,
		@NotBlank(message = "Last name cannot be blank") String lastName,
		@NotEmpty(message = "At least one language must be selected") Set<Language> languages) {
}

package com.codingapi.dto.commands;

import com.codingapi.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStudentCommand(
		@NotBlank(message = "First name cannot be blank") String firstName,
		@NotBlank(message = "Last name cannot be blank") String lastName,
		@NotNull(message = "Language must be selected") Language language) {
}
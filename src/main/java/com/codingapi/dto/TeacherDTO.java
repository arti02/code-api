package com.codingapi.dto;

import com.codingapi.enums.Language;
import java.util.Set;

public record TeacherDTO(Long id, String firstName, String lastName, Set<Language> languages) {
}

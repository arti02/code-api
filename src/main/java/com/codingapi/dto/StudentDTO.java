package com.codingapi.dto;

import com.codingapi.enums.Language;

public record StudentDTO(Long id, String firstName, String lastName, Language language, Long teacherId) {
}

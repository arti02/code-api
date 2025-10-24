package com.codingapi.dto;

import com.codingapi.enums.Language;

public record StudentDTO(Long id, String firstName, String lastname, Language language) {
}

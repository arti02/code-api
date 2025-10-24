package com.codingapi.dto.commands;

public record ChangeTeacherCommand(Long lessonId, Long newTeacherId) {
}

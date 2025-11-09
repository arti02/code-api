package com.codingapi.mappers;

import com.codingapi.dto.TeacherDTO;
import com.codingapi.model.Teacher;

public final class TeacherMapper {
	private TeacherMapper() {}

	public static TeacherDTO toDto(Teacher teacher) {
		return new TeacherDTO(teacher.getId(), teacher.getFirstName(), teacher.getLastName(), teacher.getLanguages());
	}
}
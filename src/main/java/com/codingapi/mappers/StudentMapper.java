package com.codingapi.mappers;

import com.codingapi.dto.StudentDTO;
import com.codingapi.model.Student;

public final class StudentMapper {

	private StudentMapper() {}

	public static StudentDTO toDto(Student student) {
		return new StudentDTO(student.getId(), student.getFirstName(), student.getLastName(), student.getLanguage(), student.getTeacher().getId());
	}
}
package com.codingapi.mappers;

import com.codingapi.dto.StudentDTO;
import com.codingapi.model.Student;
import org.springframework.stereotype.Service;

@Service
public record StudentMapper() {

	public StudentDTO getDTO(Student student) {
		return new StudentDTO(student.getId(), student.getFirstName(), student.getLastName(), student.getLanguage());
	}
}

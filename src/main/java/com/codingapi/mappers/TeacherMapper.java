package com.codingapi.mappers;

import com.codingapi.dto.TeacherDTO;
import com.codingapi.model.Teacher;
import org.springframework.stereotype.Service;

@Service
public record TeacherMapper() {

	public TeacherDTO getDTO(Teacher teacher) {
		return new TeacherDTO(teacher.getId(), teacher.getFirstName(), teacher.getLastName(), teacher.getLanguages());
	}
}

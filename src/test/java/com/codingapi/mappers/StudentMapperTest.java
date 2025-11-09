package com.codingapi.mappers;

import com.codingapi.dto.StudentDTO;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import org.junit.jupiter.api.Test;

import static com.codingapi.enums.Language.PL;
import static org.assertj.core.api.Assertions.assertThat;

class StudentMapperTest {

	@Test
	void toDto_shouldMapAllFieldsCorrectly() {
		// given
		Teacher teacher = new Teacher();
		teacher.setId(100L);

		Student student = new Student();
		student.setId(1L);
		student.setFirstName("Jan");
		student.setLastName("Kowalski");
		student.setLanguage(PL);
		student.setTeacher(teacher);

		// when
		StudentDTO dto = StudentMapper.toDto(student);

		// then
		assertThat(dto)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("firstName", "Jan")
				.hasFieldOrPropertyWithValue("lastName", "Kowalski")
				.hasFieldOrPropertyWithValue("language", PL)
				.hasFieldOrPropertyWithValue("teacherId", 100L);
	}

}
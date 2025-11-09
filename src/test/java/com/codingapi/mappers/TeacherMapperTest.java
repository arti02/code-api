package com.codingapi.mappers;

import com.codingapi.dto.TeacherDTO;
import com.codingapi.model.Teacher;
import org.junit.jupiter.api.Test;

import static com.codingapi.enums.Language.EN;
import static com.codingapi.enums.Language.PL;
import static org.assertj.core.api.Assertions.*;

import java.util.Set;

class TeacherMapperTest {

	@Test
	void toDto_shouldMapAllFieldsCorrectly() {
		Teacher teacher = new Teacher();
		teacher.setId(1L);
		teacher.setFirstName("Adam");
		teacher.setLastName("Nowak");
		teacher.setLanguages(Set.of(PL, EN));

		TeacherDTO dto = TeacherMapper.toDto(teacher);

		assertThat(dto).isNotNull()
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("firstName", "Adam")
				.hasFieldOrPropertyWithValue("lastName", "Nowak")
				.hasFieldOrPropertyWithValue("languages", Set.of(PL, EN));
	}
}
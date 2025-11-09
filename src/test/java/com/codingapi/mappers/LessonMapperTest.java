package com.codingapi.mappers;

import com.codingapi.dto.LessonDTO;
import com.codingapi.enums.Language;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static com.codingapi.enums.Language.EN;
import static com.codingapi.enums.Language.PL;
import static org.assertj.core.api.Assertions.assertThat;

class LessonMapperTest {

	private static final LocalDateTime NOW = LocalDateTime.of(2025, 6, 1, 10, 0);

	@Test
	void toDto_shouldMapAllFieldsCorrectly() {

		Teacher teacher = createTeacher(Set.of(PL, EN));
		Student student = createStudent(teacher);

		Lesson lesson = new Lesson();
		lesson.setId(100L);
		lesson.setTeacher(teacher);
		lesson.setStudent(student);
		lesson.setDate(NOW);

		LessonDTO dto = LessonMapper.toDto(lesson);

		assertThat(dto).isNotNull().hasFieldOrPropertyWithValue("id", 100L).hasFieldOrPropertyWithValue("date", NOW);

		assertThat(dto.student()).hasFieldOrPropertyWithValue("id", 10L)
				.hasFieldOrPropertyWithValue("firstName", "Jan")
				.hasFieldOrPropertyWithValue("lastName", "Nowak")
				.hasFieldOrPropertyWithValue("language", PL)
				.hasFieldOrPropertyWithValue("teacherId", 1L);

		assertThat(dto.teacher()).hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("firstName", "Anna")
				.hasFieldOrPropertyWithValue("lastName", "Kowalska")
				.hasFieldOrPropertyWithValue("languages", Set.of(PL, EN));
	}

	private Teacher createTeacher(Set<Language> languages) {
		Teacher t = new Teacher();
		t.setId(1L);
		t.setFirstName("Anna");
		t.setLastName("Kowalska");
		t.setLanguages(languages);
		return t;
	}

	private Student createStudent(Teacher teacher) {
		Student s = new Student();
		s.setId(10L);
		s.setFirstName("Jan");
		s.setLastName("Nowak");
		s.setLanguage(Language.PL);
		s.setTeacher(teacher);
		return s;
	}
}

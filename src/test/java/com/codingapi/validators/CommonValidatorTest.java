package com.codingapi.validators;

import com.codingapi.common.CodingApiTestHelper;
import com.codingapi.exceptions.CodingApiException;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.LessonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static com.codingapi.enums.Language.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonValidatorTest extends CodingApiTestHelper {

	private static final LocalDateTime FUTURE_DATE = LocalDateTime.now().plusDays(3);
	private static final LocalDateTime PAST_DATE = LocalDateTime.now().minusDays(3);
	private static final LocalDateTime CONFLICT_DATE = LocalDateTime.now().plusDays(2);

	@Mock
	private LessonRepository lessonRepository;

	@InjectMocks
	private CommonValidator commonValidator;

	@Test
	void validate_shouldThrowIfLessonDateInPast() {
		//given
		Teacher teacher = createTeacherWithId(1L);
		Student student = createStudentWithId(1L, 1L);
		//then
		assertThatThrownBy(() -> commonValidator.validate(teacher, student, PAST_DATE))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Lesson should be scheduled for a future date, minimum one hour ahead");
	}

	@Test
	void validate_shouldThrowIfTeacherDoesNotSpeakStudentLanguage() {
		//given
		Teacher teacher = createTeacherWithId(1L);
		teacher.setLanguages(Set.of(PL));

		Student student = createStudentWithId(2L, 2L);
		student.setLanguage(ESP);

		//then
		assertThatThrownBy(() -> commonValidator.validate(
				teacher,
				student,
				FUTURE_DATE)).isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Teacher does not speak the student's language");
	}

	@Test
	void validate_shouldThrowIfTeacherHasConflict() {
		//given
		Teacher teacher = createTeacherWithId(1L);
		Student student = createStudentWithId(2L, 2L);
		LocalDateTime from = CONFLICT_DATE.minusHours(1);
		LocalDateTime to = CONFLICT_DATE.plusHours(1);

		when(lessonRepository.existsConflictLessonForTeacher(1L, from, to)).thenReturn(true);

		//then
		assertThatThrownBy(() -> commonValidator.validate(teacher, student, CONFLICT_DATE))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Lesson already exists for this teacher");

		verify(lessonRepository).existsConflictLessonForTeacher(1L, from, to);
	}

	@Test
	void validate_shouldThrowIfStudentHasConflict() {
		//given
		Teacher teacher = createTeacherWithId(1L);
		Student student = createStudentWithId(10L, 10L);

		LocalDateTime from = CONFLICT_DATE.minusHours(1);
		LocalDateTime to = CONFLICT_DATE.plusHours(1);

		when(lessonRepository.existsConflictLessonForTeacher(1L, from, to)).thenReturn(false);
		when(lessonRepository.existsConflictLessonForStudent(10L, from, to)).thenReturn(true);

		//then
		assertThatThrownBy(() -> commonValidator.validate(teacher, student, CONFLICT_DATE))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Lesson already exists for this student");

		verify(lessonRepository).existsConflictLessonForTeacher(1L, from, to);
		verify(lessonRepository).existsConflictLessonForStudent(10L, from, to);
	}

	@Test
	void validate_shouldPassForValidData() {
		//given
		Teacher teacher = createTeacherWithId(1L);
		Student student = createStudentWithId(10L, 10L);

		LocalDateTime from = FUTURE_DATE.minusHours(1);
		LocalDateTime to = FUTURE_DATE.plusHours(1);

		when(lessonRepository.existsConflictLessonForTeacher(1L, from, to)).thenReturn(false);
		when(lessonRepository.existsConflictLessonForStudent(10L, from, to)).thenReturn(false);

		//then
		assertThatCode(() -> commonValidator.validate(teacher, student, FUTURE_DATE))
				.doesNotThrowAnyException();

		verify(lessonRepository).existsConflictLessonForTeacher(1L, from, to);
		verify(lessonRepository).existsConflictLessonForStudent(10L, from, to);
	}
}
package com.codingapi.services;

import com.codingapi.common.CodingApiTestHelper;
import com.codingapi.dto.LessonDTO;
import com.codingapi.dto.commands.ChangeLessonDateCommand;
import com.codingapi.dto.commands.CreateLessonCommand;
import com.codingapi.exceptions.CodingApiException;
import com.codingapi.mappers.LessonMapper;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.LessonRepository;
import com.codingapi.repositories.StudentRepository;
import com.codingapi.repositories.TeacherRepository;
import com.codingapi.validators.CommonValidator;
import org.hibernate.exception.LockTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.PessimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest extends CodingApiTestHelper {

	@InjectMocks
	private LessonService classUnderTest;

	@Mock
	private LessonRepository lessonRepository;

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private TeacherRepository teacherRepository;

	@Mock
	private CommonValidator commonValidator;

	@Captor
	ArgumentCaptor<Lesson> lessonCaptor;

	@Test
	void getAll_shouldReturnMappedDTOs() {
		//given
		Lesson lesson1 = createLessonWithId(1L, 1L, 1L);
		Lesson lesson2 = createLessonWithId(2L, 2L, 2L);
		List<LessonDTO> expected = List.of(LessonMapper.toDto(lesson1), LessonMapper.toDto(lesson2));

		when(lessonRepository.findLessonsWithActiveParticipants()).thenReturn(List.of(lesson1, lesson2));

		//when
		List<LessonDTO> result = classUnderTest.getAll();

		//then
		assertThat(result).hasSize(2);
		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);

		//bean interaction
		verify(lessonRepository).findLessonsWithActiveParticipants();
	}

	@Test
	void addLesson_shouldSaveAndReturnDto() {
		//given
		long teacherId = 2L;
		long studentId = 1L;
		long expectedId = 10L;
		CreateLessonCommand cmd = new CreateLessonCommand(studentId, teacherId, CURRENT_DATE.plusDays(1));
		Student student = createStudentWithId(studentId, studentId);
		Teacher teacher = createTeacherWithId(teacherId);

		when(studentRepository.findByIdWithLock(studentId)).thenReturn(Optional.of(student));
		when(teacherRepository.findByIdWithLock(teacherId)).thenReturn(Optional.of(teacher));
		doNothing().when(commonValidator).validate(teacher, student, cmd.date());
		when(lessonRepository.save(any(Lesson.class))).thenAnswer(args -> {
			Lesson lesson = args.getArgument(0);
			lesson.setId(expectedId);
			return lesson;
		});

		//when
		LessonDTO result = classUnderTest.addLesson(cmd);

		//then
		verify(studentRepository).findByIdWithLock(studentId);
		verify(teacherRepository).findByIdWithLock(teacherId);
		verify(commonValidator).validate(teacher, student, cmd.date());

		//verification data before execute
		verify(lessonRepository).save(lessonCaptor.capture());
		Lesson saved = lessonCaptor.getValue();
		assertThat(saved.getStudent()).isEqualTo(student);
		assertThat(saved.getTeacher()).isEqualTo(teacher);
		assertThat(saved.getDate()).isEqualTo(cmd.date());

		//result
		assertThat(result.id()).isEqualTo(expectedId);
		assertThat(result.student().id()).isEqualTo(cmd.studentId());
		assertThat(result.teacher().id()).isEqualTo(cmd.teacherId());
		assertThat(result.date()).isEqualTo(cmd.date());
	}

	@Test
	void addLesson_shouldThrowWhenLockFails() {
		//given
		CreateLessonCommand cmd = new CreateLessonCommand(1L, 2L, CURRENT_DATE);
		Student student = createStudentWithId(1L, 1L);
		Teacher teacher = createTeacherWithId(2L);

		when(studentRepository.findByIdWithLock(1L)).thenReturn(Optional.of(student));
		when(teacherRepository.findByIdWithLock(2L)).thenReturn(Optional.of(teacher));
		when(lessonRepository.save(any(Lesson.class)))
				.thenThrow(new PessimisticLockingFailureException("Lock failed"));

		//then
		assertThatThrownBy(() -> classUnderTest.addLesson(cmd))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Could not acquire lock");

		verify(commonValidator).validate(teacher, student, cmd.date());
		verify(studentRepository).findByIdWithLock(1L);
		verify(teacherRepository).findByIdWithLock(2L);

		verify(lessonRepository).save(lessonCaptor.capture());
		Lesson saved = lessonCaptor.getValue();
		assertThat(saved).isNotNull();
		assertThat(saved.getStudent().getId()).isEqualTo(cmd.studentId());
		assertThat(saved.getTeacher().getId()).isEqualTo(cmd.teacherId());
		assertThat(saved.getDate()).isEqualTo(cmd.date());
	}

	@Test
	void deleteLesson_shouldDeleteWhenValid() {
		//given
		Lesson lesson = createLessonWithId(1L, 1L, 1L);
		when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

		//when
		classUnderTest.deleteLesson(1L);

		//then
		verify(commonValidator).validateLessonDateNotInThePast(lesson.getDate());
		verify(lessonRepository).delete(lesson);

		verify(lessonRepository).delete(lessonCaptor.capture());
		Lesson deleted = lessonCaptor.getValue();
		assertThat(deleted).isNotNull();
		assertThat(deleted.getId()).isEqualTo(1L);
	}

	@Test
	void deleteLesson_shouldThrowWhenNotFound() {
		//given
		when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> classUnderTest.deleteLesson(1L))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Lesson not found");

		verify(lessonRepository).findById(1L);
		verify(commonValidator, never()).validateLessonDateNotInThePast(any(LocalDateTime.class));
		verify(lessonRepository, never()).delete(any(Lesson.class));
	}

	@Test
	void changeDate_shouldChangeAndValidateDateSuccessfully() {
		//given
		Long lessonId = 1L;
		LocalDateTime newDate = CURRENT_DATE.plusDays(5);
		ChangeLessonDateCommand cmd = new ChangeLessonDateCommand(newDate);

		Lesson lesson = createLessonWithId(lessonId, 1L, 1L);

		when(lessonRepository.findByIdWithLock(lessonId)).thenReturn(Optional.of(lesson));

		when(studentRepository.findByIdWithLock(any(Long.class))).thenReturn(Optional.of(lesson.getStudent()));
		when(teacherRepository.findByIdWithLock(any(Long.class))).thenReturn(Optional.of(lesson.getTeacher()));
		when(lessonRepository.save(any(Lesson.class))).thenAnswer(args -> args.getArgument(0));

		//when
		LessonDTO result = classUnderTest.changeAndValidateDate(lessonId, cmd);

		//then
		assertThat(result.date()).isEqualTo(newDate);
		assertThat(result.id()).isEqualTo(lessonId);

		verify(commonValidator).validate(lesson.getTeacher(), lesson.getStudent(), newDate);
		verify(studentRepository).findByIdWithLock(lessonId);
		verify(teacherRepository).findByIdWithLock(lessonId);

		verify(lessonRepository).save(lessonCaptor.capture());
		Lesson saved = lessonCaptor.getValue();
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isEqualTo(lessonId);
		assertThat(saved.getDate()).isEqualTo(newDate);
	}

	@Test
	void changeAndValidateDate_shouldThrowWhenLessonNotFound() {
		//given
		when(lessonRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> classUnderTest.changeAndValidateDate(1L, new ChangeLessonDateCommand(CURRENT_DATE.plusDays(2))))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Lesson not found");
		verify(lessonRepository).findByIdWithLock(1L);
	}

	@Test
	void changeAndValidateDate_shouldThrowOnLockTimeout() {
		//given
		Long lessonId = 1L;
		LocalDateTime newDate = CURRENT_DATE.plusDays(2);
		Lesson lesson = createLessonWithId(1L, 1L , 1L);
		when(lessonRepository.findByIdWithLock(lessonId)).thenReturn(Optional.of(lesson));
		when(studentRepository.findByIdWithLock(any(Long.class))).thenReturn(Optional.of(lesson.getStudent()));
		when(teacherRepository.findByIdWithLock(any(Long.class))).thenReturn(Optional.of(lesson.getTeacher()));
		when(lessonRepository.save(lesson)).thenThrow(new LockTimeoutException("Timeout", null));

		//then
		assertThatThrownBy(() -> classUnderTest.changeAndValidateDate(lessonId, new ChangeLessonDateCommand(newDate)))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("please retry");

		verify(studentRepository).findByIdWithLock(1L);
		verify(teacherRepository).findByIdWithLock(1L);

		verify(lessonRepository).save(lessonCaptor.capture());
		Lesson saved = lessonCaptor.getValue();
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isEqualTo(lessonId);
		assertThat(saved.getDate()).isEqualTo(newDate);
	}
}
package com.codingapi.services;

import com.codingapi.common.CodingApiTestHelper;
import com.codingapi.dto.StudentDTO;
import com.codingapi.dto.commands.ChangeTeacherCommand;
import com.codingapi.dto.commands.CreateStudentCommand;
import com.codingapi.exceptions.CodingApiException;
import com.codingapi.mappers.StudentMapper;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.StudentRepository;
import com.codingapi.repositories.TeacherRepository;
import com.codingapi.validators.CommonValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest extends CodingApiTestHelper {

	@Mock
	private StudentRepository studentRepository;

	@InjectMocks
	private StudentService classUnderTest;

	@Mock
	private TeacherRepository teacherRepository;

	@Mock
	private CommonValidator commonValidator;

	@Captor
	private ArgumentCaptor<Student> studentCaptor;

	@Test
	void getAll_shouldReturnMappedDTOs() {
		//given
		Student s1 = createStudentWithId(1L, 1L);
		Student s2 = createStudentWithId(2L, 2L);

		List<StudentDTO> expected = List.of(StudentMapper.toDto(s1), StudentMapper.toDto(s2));
		when(studentRepository.findStudents()).thenReturn(List.of(s1, s2));

		//when
		List<StudentDTO> result = classUnderTest.getAll();

		//then
		assertThat(result).hasSize(2);
		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);

		verify(studentRepository).findStudents();
	}

	@Test
	void addStudent_shouldSaveAndReturnDto() {
		//given
		Student student = createStudentWithId(1L, 1L);
		CreateStudentCommand cmd = new CreateStudentCommand(
				student.getFirstName(),
				student.getLastName(),
				student.getLanguage(),
				student.getId());

		when(teacherRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(student.getTeacher()));
		when(studentRepository.save(any(Student.class))).thenAnswer(args -> {
			Student st = args.getArgument(0);
			st.setId(1L);
			return st;
		});

		//when
		StudentDTO result = classUnderTest.addStudent(cmd);

		//then
		assertThat(result)
				.isNotNull()
				.satisfies(dto -> {
					assertThat(dto.firstName()).isEqualTo(cmd.firstName());
					assertThat(dto.lastName()).isEqualTo(cmd.lastName());
					assertThat(dto.language()).isEqualTo(cmd.language());
					assertThat(dto.teacherId()).isEqualTo(1L);
				});

		verify(teacherRepository).findByIdAndIsActiveTrue(1L);
		verify(commonValidator).validateLanguageCompatibility(any(), any());

		verify(studentRepository).save(studentCaptor.capture());
		Student captured = studentCaptor.getValue();
		assertThat(captured.getFirstName()).isEqualTo(cmd.firstName());
		assertThat(captured.getLastName()).isEqualTo(cmd.lastName());
		assertThat(captured.getLanguage()).isEqualTo(cmd.language());
		assertThat(captured.getTeacher().getId()).isEqualTo(cmd.teacherId());
	}

	@Test
	void deleteStudent_whenExists_shouldSetActiveFalseAndSave() {
		//when
		classUnderTest.deleteStudent(1L);

		//then
		verify(studentRepository).deleteById(1L);
	}

	@Test
	void changeTeacher_shouldChangeTeacherSuccessfully() {
		// given
		Long studentId = 5L;
		Long newTeacherId = 6L;
		Student student = createStudentWithId(studentId, 1L);
		Teacher newTeacher = createTeacherWithId(newTeacherId);
		ChangeTeacherCommand cmd = new ChangeTeacherCommand(newTeacherId);

		when(teacherRepository.findByIdAndIsActiveTrue(newTeacherId)).thenReturn(Optional.of(newTeacher));
		when(studentRepository.findByIdWithLock(studentId)).thenReturn(Optional.of(student));
		when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

		//when
		StudentDTO result = classUnderTest.changeTeacher(studentId, cmd);

		// then
		assertThat(result).isNotNull();
		assertThat(student.getTeacher().getId()).isEqualTo(newTeacherId);

		verify(teacherRepository).findByIdAndIsActiveTrue(newTeacherId);
		verify(studentRepository).findByIdWithLock(studentId);
		verify(commonValidator).validateLanguageCompatibility(newTeacher, student);

		verify(studentRepository).save(studentCaptor.capture());
		Student savedStudent = studentCaptor.getValue();
		assertThat(savedStudent.getTeacher().getId()).isEqualTo(newTeacherId);
	}

	@Test
	void changeTeacher_shouldThrowWhenStudentIsInactive() {
		//given
		Long studentId = 1L;
		Long newTeacherId = 20L;
		Student student = createStudentWithId(studentId, 1L);
		student.setActive(false);
		Teacher newTeacher = createTeacherWithId(newTeacherId);
		ChangeTeacherCommand cmd = new ChangeTeacherCommand(newTeacherId);

		when(teacherRepository.findByIdAndIsActiveTrue(newTeacherId)).thenReturn(Optional.of(newTeacher));
		doThrow(new CodingApiException("Student not exist with id: " + studentId))
				.when(studentRepository).findByIdWithLock(studentId);

		//then
		assertThatThrownBy(() -> classUnderTest.changeTeacher(studentId, cmd))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Student not exist");

		verify(teacherRepository).findByIdAndIsActiveTrue(newTeacherId);
		verify(studentRepository).findByIdWithLock(studentId);
		verify(studentRepository, never()).save(any());
	}

	@Test
	void changeTeacher_shouldThrowWhenTeacherNotFound() {
		//given
		Long studentId = 1L;
		Long newTeacherId = 20L;
		ChangeTeacherCommand cmd = new ChangeTeacherCommand(newTeacherId);

		//then
		assertThatThrownBy(() -> classUnderTest.changeTeacher(studentId, cmd))
				.isInstanceOf(CodingApiException.class)
				.hasMessageContaining("Teacher not found with id: 20");

		verify(studentRepository, never()).save(any());}

	@Test
	void changeTeacher_shouldPreventRaceCondition_withPessimisticLock() throws InterruptedException {
		//given
		Long studentId = 1L;
		Long teacherId1 = 10L;
		Long teacherId2 = 20L;

		Teacher teacher1 = createTeacherWithId(teacherId1);
		Teacher teacher2 = createTeacherWithId(teacherId2);

		Student student = createStudentWithId(studentId, 1L);

		when(teacherRepository.findByIdAndIsActiveTrue(teacherId1)).thenReturn(Optional.of(teacher1));
		when(teacherRepository.findByIdAndIsActiveTrue(teacherId2)).thenReturn(Optional.of(teacher2));
		when(studentRepository.findByIdWithLock(studentId)).thenReturn(Optional.of(student));
		when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

		//when
		Thread thread1 = new Thread(() -> {
			ChangeTeacherCommand cmd = new ChangeTeacherCommand(teacherId1);
			classUnderTest.changeTeacher(studentId, cmd);
		});

		Thread thread2 = new Thread(() -> {
			ChangeTeacherCommand cmd = new ChangeTeacherCommand(teacherId2);
			classUnderTest.changeTeacher(studentId, cmd);
		});

		thread1.start();
		thread2.start();

		thread1.join();
		thread2.join();

		//then
		verify(studentRepository, times(2)).save(student);
	}
}
package com.codingapi.services;

import com.codingapi.common.CodingApiTestHelper;
import com.codingapi.dto.TeacherDTO;
import com.codingapi.dto.commands.CreateTeacherCommand;
import com.codingapi.mappers.TeacherMapper;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest extends CodingApiTestHelper {

	@Mock
	private TeacherRepository teacherRepository;

	@InjectMocks
	private TeacherService classUnderTest;

	@Captor
	private ArgumentCaptor<Teacher> teacherCaptor;

	@Test
	void getAll_shouldReturnMappedDTOs() {
		//given
		Teacher t1 = createTeacherWithId(1L);
		Teacher t2 = createTeacherWithId(2L);

		List<TeacherDTO> expected = List.of(TeacherMapper.toDto(t1), TeacherMapper.toDto(t2));
		when(teacherRepository.findTeachersWithLanguages()).thenReturn(List.of(t1, t2));

		//when
		List<TeacherDTO> result = classUnderTest.getAll();

		//then
		assertThat(result).hasSize(2);
		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);

		verify(teacherRepository).findTeachersWithLanguages();
	}

	@Test
	void addTeacher_shouldSaveAndReturnDto() {
		//given
		Teacher teacher = createTeacherWithId(1L);
		CreateTeacherCommand cmd = new CreateTeacherCommand(teacher.getFirstName(), teacher.getLastName(), teacher.getLanguages());

		when(teacherRepository.save(any(Teacher.class))).thenAnswer( args -> {
			Teacher t = args.getArgument(0);
			t.setId(1L);
			return t;
		});
		//when
		TeacherDTO result = classUnderTest.addTeacher(cmd);

		//then
		assertThat(result)
				.isNotNull()
				.satisfies(dto -> {
					assertThat(dto.id()).isEqualTo(1L);
					assertThat(dto.firstName()).isEqualTo(cmd.firstName());
					assertThat(dto.lastName()).isEqualTo(cmd.lastName());
					assertThat(dto.languages()).isEqualTo(cmd.languages());
				});

		verify(teacherRepository).save(teacherCaptor.capture());
		Teacher savedTeacher = teacherCaptor.getValue();
		assertThat(savedTeacher.getFirstName()).isEqualTo(cmd.firstName());
		assertThat(savedTeacher.getLastName()).isEqualTo(cmd.lastName());
		assertThat(savedTeacher.getLanguages()).isEqualTo(cmd.languages());

	}

	@Test
	void deleteTeacher_shouldSetActiveFalse_whenTeacherExists() {
		//given
		classUnderTest.deleteTeacher(1L);

		verify(teacherRepository).deleteById(1L);
	}

}
package com.codingapi.controllers;

import static com.codingapi.enums.Language.EN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.codingapi.common.BaseIntegrationTest;
import com.codingapi.dto.commands.ChangeTeacherCommand;
import com.codingapi.dto.commands.CreateStudentCommand;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.StudentRepository;
import com.codingapi.repositories.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.extension.ExtendWith;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class StudentControllerIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@AfterEach
	void cleanup() {
		studentRepository.deleteAll();
		teacherRepository.deleteAll();
	}

	@Test
	void getAll_returnsStudents() throws Exception {
		// given
		Teacher teacher = createTeacherWithId(null);
		Teacher savedTeacher = teacherRepository.save(teacher);

		Student s1 = createStudentWithId(null, null);
		s1.setTeacher(savedTeacher);
		Student s2 = createStudentWithId(null, null);
		s2.setTeacher(savedTeacher);
		studentRepository.save(s1);
		studentRepository.save(s2);

		String expectedJson = readJson("src/test/resources/students.json");

		// when / then
		mockMvc.perform(get("/api/v1/students"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void addStudent_createsStudentAndReturnsCreated() throws Exception {
		Teacher teacher = createTeacherWithId(null);
		Teacher savedTeacher = teacherRepository.save(teacher);

		CreateStudentCommand cmd = new CreateStudentCommand("Jane", "Smith", EN, savedTeacher.getId());
		String body = toJson(cmd);

		String expectedJson = readJson("src/test/resources/single_student.json");

		// when
		mockMvc.perform(post("/api/v1/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(expectedJson));

		// then
		List<Student> all = studentRepository.findStudents();
		assertThat(all).hasSize(1);
		Student persisted = all.get(0);
		assertThat(persisted.getId()).isNotNull();
		assertThat(persisted.getFirstName()).isEqualTo(cmd.firstName());
		assertThat(persisted.getLastName()).isEqualTo(cmd.lastName());
		assertThat(persisted.getLanguage()).isEqualTo(cmd.language());
		assertThat(persisted.getTeacher()).isNotNull();
		assertThat(persisted.getTeacher().getId()).isEqualTo(savedTeacher.getId());
	}

	@Test
	void deleteStudent_setActiveFalse() throws Exception {
		// given
		Teacher teacher = createTeacherWithId(null);
		Teacher savedTeacher = teacherRepository.save(teacher);

		Student s = createStudentWithId(null, null);
		s.setTeacher(savedTeacher);
		Student saved = studentRepository.save(s);

		// when
		mockMvc.perform(delete("/api/v1/students/{id}", saved.getId()))
				.andExpect(status().isNoContent());

		// then
		Optional<Student> result = studentRepository.findById(saved.getId());
		assertThat(result).isPresent();
		assertThat(result.get().isActive()).isFalse();
	}

	@Test
	void changeTeacher_updatesStudentTeacher() throws Exception {
		Teacher t1 = teacherRepository.save(createTeacherWithId(null));
		Teacher t2 = teacherRepository.save(createTeacherWithId(null));

		Student s = createStudentWithId(null, null);
		s.setTeacher(t1);
		Student savedStudent = studentRepository.save(s);

		ChangeTeacherCommand cmd = new ChangeTeacherCommand(t2.getId());
		String body = toJson(cmd);

		// when
		mockMvc.perform(put("/api/v1/students/change-teacher/{id}", savedStudent.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.teacherId").value(t2.getId().intValue()));

		// then
		Optional<Student> updated = studentRepository.findById(savedStudent.getId());
		assertThat(updated).isPresent();
		assertThat(updated.get().getTeacher()).isNotNull();
		assertThat(updated.get().getTeacher().getId()).isEqualTo(t2.getId());
	}

	@Test
	void changeTeacher_raceCondition_concurrentUpdates() throws Exception {
		// given
		Teacher t1 = teacherRepository.save(createTeacherWithId(null));
		Teacher t2 = teacherRepository.save(createTeacherWithId(null));
		Teacher t3 = teacherRepository.save(createTeacherWithId(null));

		Student s = createStudentWithId(null, null);
		s.setTeacher(t1);
		Student savedStudent = studentRepository.save(s);

		ChangeTeacherCommand cmdToT2 = new ChangeTeacherCommand(t2.getId());
		ChangeTeacherCommand cmdToT3 = new ChangeTeacherCommand(t3.getId());
		String bodyT2 = toJson(cmdToT2);
		String bodyT3 = toJson(cmdToT3);

		ExecutorService ex = Executors.newFixedThreadPool(2);

		// when
		Callable<Integer> call1 = () -> {
			mockMvc.perform(put("/api/v1/students/change-teacher/{id}", savedStudent.getId())
							.contentType(MediaType.APPLICATION_JSON)
							.content(bodyT2))
					.andExpect(status().isOk());
			return 1;
		};
		Callable<Integer> call2 = () -> {
			mockMvc.perform(put("/api/v1/students/change-teacher/{id}", savedStudent.getId())
							.contentType(MediaType.APPLICATION_JSON)
							.content(bodyT3))
					.andExpect(status().isOk());
			return 1;
		};

		try {
			List<Future<Integer>> futures = ex.invokeAll(List.of(call1, call2));
			for (Future<Integer> f : futures) {
				f.get(5, TimeUnit.SECONDS);
			}
		} finally {
			ex.shutdownNow();
		}

		//then
		Optional<Student> updatedOpt = studentRepository.findById(savedStudent.getId());
		assertThat(updatedOpt).isPresent();
		Student updated = updatedOpt.get();

		assertThat(updated.getTeacher()).isNotNull();
		Long finalTeacherId = updated.getTeacher().getId();
		assertThat(finalTeacherId).isIn(t2.getId(), t3.getId());
	}
}
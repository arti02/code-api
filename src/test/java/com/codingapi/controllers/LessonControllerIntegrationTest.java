package com.codingapi.controllers;

import com.codingapi.common.BaseIntegrationTest;
import com.codingapi.dto.commands.ChangeLessonDateCommand;
import com.codingapi.dto.commands.CreateLessonCommand;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.LessonRepository;
import com.codingapi.repositories.StudentRepository;
import com.codingapi.repositories.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class LessonControllerIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LessonRepository lessonRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@BeforeEach
	void cleanup() {
		lessonRepository.deleteAll();
		studentRepository.deleteAll();
		teacherRepository.deleteAll();
	}

	@Test
	void getAll_returnsLessonsWithActiveParticipants() throws Exception {
		Teacher savedTeacher = teacherRepository.save(createTeacherWithId(null));
		Student student = createStudentWithId(null, null);
		student.setTeacher(savedTeacher);
		Student savedStudent = studentRepository.save(student);

		LocalDateTime date1 = LocalDateTime.now().plusDays(1);
		LocalDateTime date2 = date1.plusDays(1);

		Lesson l1 = new Lesson();
		l1.setTeacher(savedTeacher);
		l1.setStudent(savedStudent);
		l1.setDate(date1);
		Lesson l2 = new Lesson();
		l2.setTeacher(savedTeacher);
		l2.setStudent(savedStudent);
		l2.setDate(date2);

		lessonRepository.save(l1);
		lessonRepository.save(l2);

		// when / then
		mockMvc.perform(get("/api/v1/lessons"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].teacher.id").isNotEmpty())
				.andExpect(jsonPath("$[0].student.id").isNotEmpty());
	}

	@Test
	void addLesson_createsLessonAndReturnsCreated() throws Exception {
		// given
		Teacher savedTeacher = teacherRepository.save(createTeacherWithId(null));
		Student student = createStudentWithId(null, null);
		student.setTeacher(savedTeacher);
		Student savedStudent = studentRepository.save(student);

		LocalDateTime date = LocalDateTime.now().plusDays(2);

		CreateLessonCommand cmd = new CreateLessonCommand(savedTeacher.getId(), savedStudent.getId(), date);
		String body = toJson(cmd);

		// when / then
		mockMvc.perform(post("/api/v1/lessons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.teacher.id").value(savedTeacher.getId().intValue()))
				.andExpect(jsonPath("$.student.id").value(savedStudent.getId().intValue()))
				.andExpect(jsonPath("$.date").isNotEmpty());

		// and persisted
		List<Lesson> all = lessonRepository.findLessonsWithActiveParticipants();
		assertThat(all).hasSize(1);
		Lesson persisted = all.get(0);
		assertThat(persisted.getTeacher().getId()).isEqualTo(savedTeacher.getId());
		assertThat(persisted.getStudent().getId()).isEqualTo(savedStudent.getId());
		assertThat(persisted.getDate()).isNotNull();
	}

	@Test
	void deleteLesson_removesLesson() throws Exception {
		// given
		Teacher savedTeacher = teacherRepository.save(createTeacherWithId(null));
		Student student = createStudentWithId(null, null);
		student.setTeacher(savedTeacher);
		Student savedStudent = studentRepository.save(student);

		Lesson lesson = new Lesson();
		lesson.setTeacher(savedTeacher);
		lesson.setStudent(savedStudent);
		lesson.setDate(LocalDateTime.now().plusDays(3));
		Lesson saved = lessonRepository.save(lesson);

		// when
		mockMvc.perform(delete("/api/v1/lessons/{id}", saved.getId()))
				.andExpect(status().isNoContent());

		// then
		Optional<Lesson> maybe = lessonRepository.findById(saved.getId());
		assertThat(maybe).isEmpty();
	}

	@Test
	void changeDate_updatesLessonDate() throws Exception {
		// given
		Teacher savedTeacher = teacherRepository.save(createTeacherWithId(null));
		Student student = createStudentWithId(null, null);
		student.setTeacher(savedTeacher);
		Student savedStudent = studentRepository.save(student);

		Lesson lesson = new Lesson();
		lesson.setTeacher(savedTeacher);
		lesson.setStudent(savedStudent);
		lesson.setDate(LocalDateTime.now().plusDays(3));
		Lesson saved = lessonRepository.save(lesson);

		LocalDateTime newDate = LocalDateTime.now().plusDays(5);
		ChangeLessonDateCommand cmd = new ChangeLessonDateCommand(newDate);
		String body = toJson(cmd);

		//when
		mockMvc.perform(put("/api/v1/lessons/change-date/{id}", saved.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(saved.getId().intValue()))
				.andExpect(jsonPath("$.date").isNotEmpty());

		//then
		Lesson updated = lessonRepository.findById(saved.getId()).orElseThrow();
		assertThat(updated.getDate())
				.isCloseTo(newDate, within(1, ChronoUnit.MINUTES));
	}
}

package com.codingapi.controllers;

import com.codingapi.common.BaseIntegrationTest;
import com.codingapi.common.CodingApiTestHelper;
import com.codingapi.dto.commands.CreateTeacherCommand;
import com.codingapi.mappers.TeacherMapper;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.MediaType;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.codingapi.enums.Language.EN;
import static com.codingapi.enums.Language.PL;
import static java.nio.file.Files.readString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class TeacherControllerIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TeacherRepository teacherRepository;

	@AfterEach
	void cleanup() {
		teacherRepository.deleteAll();
	}

	@Test
	void getAll_returnsTeachersWithLanguages() throws Exception {
		//given
		Teacher teacher1 = createTeacherWithId(null);
		Teacher teacher2 = createTeacherWithId(null);
		teacherRepository.save(teacher1);
		teacherRepository.save(teacher2);
		String expectedJson = readString(Paths.get("src/test/resources/teachers.json"));

		//then
		mockMvc.perform(get("/api/v1/teachers"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void addTeacher_createsTeacherAndReturnsCreated() throws Exception {
		//given
		CreateTeacherCommand cmd = new CreateTeacherCommand("John", "Doe", Set.of(EN, PL));
		String body = toJson(cmd);

		String expectedJson = readString(Paths.get("src/test/resources/single_teacher.json"));

		//when
		mockMvc.perform(post("/api/v1/teachers").contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(expectedJson));

		//then
		List<Teacher> all = teacherRepository.findTeachersWithLanguages();
		assertThat(all).hasSize(1);
		assertThat(all.get(0)).satisfies(teacher -> {
			assertThat(teacher.getId()).isNotNull();
			assertThat(teacher.getFirstName()).isEqualTo(cmd.firstName());
			assertThat(teacher.getLastName()).isEqualTo(cmd.lastName());
			assertThat(teacher.getLanguages()).isEqualTo(cmd.languages());
		});
	}

	@Test
	void deleteTeacher_setActiveFalse() throws Exception {
		//given
		Teacher t = createTeacherWithId(null);
		Teacher saved = teacherRepository.save(t);

		//when
		mockMvc.perform(delete("/api/v1/teachers/{id}", saved.getId()))
				.andExpect(status().isNoContent());

		//then
		Optional<Teacher> result = teacherRepository.findById(saved.getId());
		assertThat(result)
				.isPresent()
				.get()
				.extracting(Teacher::isActive)
				.isEqualTo(false);
	}
}

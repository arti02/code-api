package com.codingapi.common;

import com.codingapi.enums.Language;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

@Testcontainers
public class CodingApiTestHelper {

	public static final LocalDateTime CURRENT_DATE = LocalDateTime.of(2020, 1, 1, 12, 0);

	public Teacher createTeacherWithId(Long id){
		Teacher teacher = new Teacher();
		teacher.setId(id);
		teacher.setFirstName("John");
		teacher.setLastName("Doe");
		teacher.setLanguages(Set.of(Language.EN, Language.PL));
		return teacher;
	}

	public Student createStudentWithId(Long studentId, Long teacherId){
		Student student = new Student();
		student.setId(studentId);
		student.setFirstName("Jane");
		student.setLastName("Smith");
		student.setLanguage(Language.EN);
		student.setTeacher(createTeacherWithId(teacherId));
		return student;
	}

	public Lesson createLessonWithId(Long id, Long studentId, Long teacherId) {
		Lesson lesson = new Lesson();
		lesson.setId(id);
		lesson.setTeacher(createTeacherWithId(teacherId));
		lesson.setStudent(createStudentWithId(studentId, teacherId));
		lesson.setDate(CURRENT_DATE);
		return lesson;
	}

	public String toJson(Object value) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			return mapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public String readJson(String path) throws Exception {
		return Files.readString(Path.of(path));
	}

}

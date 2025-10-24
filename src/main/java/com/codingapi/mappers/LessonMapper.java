package com.codingapi.mappers;

import com.codingapi.dto.LessonDTO;
import com.codingapi.dto.commands.CreateLessonCommand;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public record LessonMapper(
		StudentMapper studentMapper,
		TeacherMapper teacherMapper) {

	public LessonDTO getDTO(Lesson lesson) {
		return new LessonDTO(
				studentMapper.getDTO(lesson.getStudent()),
				teacherMapper.getDTO(lesson.getTeacher()),
				lesson.getLessonDate());
	}

	public Lesson getEntity(Teacher teacher, Student student, LocalDateTime lessonDate) {
		Lesson lesson = new Lesson();
		lesson.setTeacher(teacher);
		lesson.setStudent(student);
		lesson.setLessonDate(lessonDate);
		return lesson;
	}
}

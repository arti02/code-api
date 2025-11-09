package com.codingapi.mappers;

import com.codingapi.dto.LessonDTO;
import com.codingapi.dto.StudentDTO;
import com.codingapi.dto.TeacherDTO;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class LessonMapper {

	public static LessonDTO toDto(Lesson lesson) {
		StudentDTO studentDto = StudentMapper.toDto(lesson.getStudent());
		TeacherDTO teacherDto = TeacherMapper.toDto(lesson.getTeacher());
		return new LessonDTO(lesson.getId(), studentDto, teacherDto, lesson.getDate());
	}

	public static Lesson toEntity(Teacher teacher, Student student, LocalDateTime lessonDate) {
		Lesson lesson = new Lesson();
		lesson.setTeacher(teacher);
		lesson.setStudent(student);
		lesson.setDate(lessonDate);
		return lesson;
	}
}
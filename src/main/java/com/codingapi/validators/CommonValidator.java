package com.codingapi.validators;

import com.codingapi.exceptions.CodingApiException;

import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommonValidator {

	private final LessonRepository lessonRepository;

	public void validate(Teacher teacher, Student student, LocalDateTime date) {
		validateLessonDateNotInThePast(date);
		validateLanguageCompatibility(teacher, student);
		validateTeacherAvailability(teacher.getId(), date);
		validateStudentAvailability(student.getId(), date);
	}

	private void validateTeacherAvailability(Long teacherId, LocalDateTime date) {
		LocalDateTime from = date.minusHours(1);
		LocalDateTime to = date.plusHours(1);
		if (lessonRepository.existsConflictLessonForTeacher(teacherId, from, to)) {
			throw new CodingApiException("Lesson already exists for this teacher at the given date and time");
		}
	}

	private void validateStudentAvailability(Long studentId, LocalDateTime date) {
		LocalDateTime from = date.minusHours(1);
		LocalDateTime to = date.plusHours(1);
		if (lessonRepository.existsConflictLessonForStudent(studentId, from, to)) {
			throw new CodingApiException("Lesson already exists for this student at the given date and time");
		}
	}

	public void validateLessonDateNotInThePast(LocalDateTime lessonDate) {
		if (lessonDate.isBefore(LocalDateTime.now().plusHours(1))) {
			throw new CodingApiException("Lesson in the past");
		}
	}

	public void validateLanguageCompatibility(Teacher teacher, Student student) {
		if (!teacher.getLanguages().contains(student.getLanguage())) {
			throw new CodingApiException("Teacher does not speak the student's language");
		}
	}
}

package com.codingapi.validators;

import com.codingapi.model.BasePersonEntity;

import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.LessonRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public record LessonValidator(LessonRepository lessonRepository) {

	public void validate(Teacher teacher, Student student, LocalDateTime lessonDate) {
		validateActive(teacher, student);
		validateLanguageCompatibility(teacher, student);
		validateLessonDateNotInThePast(lessonDate);
		isAlreadyExist(teacher.getId(), lessonDate);
	}

	private void isAlreadyExist(Long teacherId, LocalDateTime lessonDate) {
		if (lessonRepository.existsByTeacherIdAndLessonDate(teacherId, lessonDate)) {
			throw new ValidationException("Lesson already exists for this teacher at the given date and time");
		}
	}

	public void validateLessonDateNotInThePast(LocalDateTime lessonDate) {
		if (lessonDate.isBefore(LocalDateTime.now())) {
			throw new ValidationException("Lesson date cannot be in the past");
		}
	}

	private void validateActive(BasePersonEntity... entities) {
		for (BasePersonEntity entity : entities) {
			if (!entity.isActive()) {
				throw new ValidationException(entity.getEntityType() + " is not active");
			}
		}
	}

	public void validateLanguageCompatibility(Teacher teacher, Student student) {
		if (!teacher.getLanguages().contains(student.getLanguage())) {
			throw new ValidationException("Teacher does not speak the student's language");
		}
	}
}

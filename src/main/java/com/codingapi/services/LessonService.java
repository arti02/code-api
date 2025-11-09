package com.codingapi.services;

import com.codingapi.dto.LessonDTO;
import com.codingapi.dto.commands.ChangeLessonDateCommand;
import com.codingapi.dto.commands.CreateLessonCommand;
import com.codingapi.exceptions.CodingApiException;
import com.codingapi.mappers.LessonMapper;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.LessonRepository;
import com.codingapi.repositories.StudentRepository;
import com.codingapi.repositories.TeacherRepository;
import com.codingapi.validators.CommonValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.PessimisticLockException;
import org.hibernate.exception.LockTimeoutException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final CommonValidator commonValidator;

	public List<LessonDTO> getAll() {
		List<Lesson> lessons = lessonRepository.findLessonsWithActiveParticipants();
		return lessons.stream().map(LessonMapper::toDto).toList();
	}

	@Transactional
	public void deleteLesson(Long lessonId) {
		Lesson lesson = lessonRepository.findById(lessonId)
				.orElseThrow(() -> new CodingApiException("Lesson not found with id: " + lessonId));
		commonValidator.validateLessonDateNotInThePast(lesson.getDate());
		lessonRepository.delete(lesson);
	}

	@Transactional
	public LessonDTO addLesson(CreateLessonCommand cmd) {
		Teacher teacher = getTeacher(cmd.teacherId());
		Student student = getStudent(cmd.studentId());
		commonValidator.validate(teacher, student, cmd.date());
		Lesson savedLesson = LessonMapper.toEntity(teacher, student, cmd.date());
		try {
			Lesson saved = lessonRepository.save(savedLesson);
			return LessonMapper.toDto(saved);
		} catch (PessimisticLockingFailureException | PessimisticLockException | LockTimeoutException e) {
			throw new CodingApiException("Could not acquire lock on Teacher or Student, please retry");
		}
	}

	private Student getStudent(Long studentId) {
		return studentRepository.findByIdWithLock(studentId)
				.orElseThrow(() -> new CodingApiException("Student not found with id: " + studentId));
	}

	private Teacher getTeacher(Long teacherId) {
		return teacherRepository.findByIdWithLock(teacherId)
				.orElseThrow(() -> new CodingApiException("Teacher not found with id: " + teacherId));
	}

	@Transactional
	public LessonDTO changeAndValidateDate(Long lessonId, ChangeLessonDateCommand cmd) {
		return lessonRepository.findByIdWithLock(lessonId)
				.map(lesson -> changeAndValidateDate(lesson, cmd))
				.orElseThrow(() -> new CodingApiException("Lesson not found with id: " + lessonId));
	}

	public LessonDTO changeAndValidateDate(Lesson lesson, ChangeLessonDateCommand cmd) {
		Teacher teacher = getTeacher(lesson.getTeacher().getId());
		Student student = getStudent(lesson.getStudent().getId());
		commonValidator.validate(teacher, student, cmd.date());
		lesson.setDate(cmd.date());
		try {
			Lesson result = lessonRepository.save(lesson);
			return LessonMapper.toDto(result);
		} catch (PessimisticLockingFailureException | PessimisticLockException | LockTimeoutException e) {
			throw new CodingApiException("Could not acquire lock, please retry");
		}
	}
}
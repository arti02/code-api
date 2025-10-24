package com.codingapi.services;

import com.codingapi.dto.LessonDTO;
import com.codingapi.dto.commands.ChangeTeacherCommand;
import com.codingapi.dto.commands.CreateLessonCommand;
import com.codingapi.mappers.LessonMapper;
import com.codingapi.model.Lesson;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.LessonRepository;
import com.codingapi.validators.LessonValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;
	private final LessonMapper lessonMapper;
	private final StudentService studentService;
	private final TeacherService teacherService;
	private final LessonValidator lessonValidator;

	public List<LessonDTO> getAll() {
		List<Lesson> lessons = lessonRepository.findLessonsWithActiveParticipants();
		return lessons.stream().map(lessonMapper::getDTO).toList();
	}

	@Transactional
	public void deleteLesson(Long lessonId) {
		lessonRepository.findById(lessonId).ifPresent(lesson -> {
			lessonValidator.validateLessonDateNotInThePast(lesson.getLessonDate());
			lessonRepository.deleteById(lessonId);
		});
	}

	@Transactional
	public LessonDTO addLesson(CreateLessonCommand createLessonCommand) {
		Student student = studentService.findById(createLessonCommand.studentId());
		Teacher teacher = teacherService.findById(createLessonCommand.teacherId());
		lessonValidator.validate(teacher, student, createLessonCommand.lessonDate());
		Lesson savedLesson = lessonRepository.save(lessonMapper.getEntity(teacher, student, createLessonCommand.lessonDate()));
		try {
			Lesson saved = lessonRepository.save(savedLesson);
			return lessonMapper.getDTO(saved);
		} catch (DataIntegrityViolationException ex) {
			throw new RuntimeException("Teacher already has a lesson at this date");
		}
	}

	@Transactional
	public LessonDTO changeTeacher(ChangeTeacherCommand changeTeacherCommand) {
		return lessonRepository.findById(changeTeacherCommand.lessonId())
				.map(lesson -> changeTeacher(changeTeacherCommand, lesson))
				.orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + changeTeacherCommand.lessonId()));
	}

	private LessonDTO changeTeacher(ChangeTeacherCommand changeTeacherCommand, Lesson lesson) {
		Teacher newTeacher = teacherService.findById(changeTeacherCommand.newTeacherId());
		lessonValidator.validate(newTeacher, lesson.getStudent(), lesson.getLessonDate());
		lesson.setTeacher(newTeacher);
		try {
			Lesson result = lessonRepository.save(lesson);
			return lessonMapper.getDTO(result);
		} catch (ObjectOptimisticLockingFailureException | OptimisticLockException ex) {
			throw new RuntimeException("Lesson was concurrently modified, please retry");
		} catch (DataIntegrityViolationException ex) {
			throw new RuntimeException("New teacher already has a lesson at this term");
		}
	}
}
package com.codingapi.repositories;

import com.codingapi.model.Lesson;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

	@EntityGraph(attributePaths = {"teacher","student"})
	@Query("SELECT DISTINCT l FROM Lesson l WHERE l.teacher.isActive = true AND l.student.isActive = true")
	List<Lesson> findLessonsWithActiveParticipants();

	@Query("SELECT count(l) > 0 FROM Lesson l WHERE l.teacher.id = :teacherId "
			+ "AND l.date > :from AND l.date < :to")
	boolean existsConflictLessonForTeacher(Long teacherId, LocalDateTime from, LocalDateTime to);

	@Query("SELECT count(l) > 0 FROM Lesson l WHERE l.student.id = :studentId "
			+ "AND l.date > :from AND l.date < :to")
	boolean existsConflictLessonForStudent(Long studentId, LocalDateTime from, LocalDateTime to);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT l FROM Lesson l WHERE l.id = :id")
	Optional<Lesson> findByIdWithLock(Long id);

}
package com.codingapi.repositories;

import com.codingapi.model.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

	@EntityGraph(attributePaths = {"teacher","student"})
	@Query("SELECT l FROM Lesson l WHERE l.teacher.isActive = true AND l.student.isActive = true")
	List<Lesson> findLessonsWithActiveParticipants();

	boolean existsByTeacherIdAndLessonDate(Long teacherId, LocalDateTime lessonDate);

}
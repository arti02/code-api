package com.codingapi.repositories;

import com.codingapi.model.Teacher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	@EntityGraph(attributePaths = "languages")
	@Query("SELECT t FROM Teacher t where t.isActive")
	List<Teacher> findTeachersWithLanguages();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from Teacher t where t.id = :id and t.isActive")
	Optional<Teacher> findByIdWithLock(Long id);

	Optional<Teacher> findByIdAndIsActiveTrue(Long id);
}

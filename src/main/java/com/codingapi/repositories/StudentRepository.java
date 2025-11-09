package com.codingapi.repositories;

import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

	@Query("SELECT s FROM Student s where s.isActive")
	List<Student> findStudents();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Student s where s.id = :id and s.isActive")
	Optional<Student> findByIdWithLock(Long id);
}
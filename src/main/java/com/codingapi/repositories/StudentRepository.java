package com.codingapi.repositories;

import com.codingapi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

	@Query("SELECT s FROM Student s where s.isActive")
	List<Student> findStudents();
}
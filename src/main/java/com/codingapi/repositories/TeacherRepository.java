package com.codingapi.repositories;

import com.codingapi.enums.Language;
import com.codingapi.model.Teacher;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	@EntityGraph(attributePaths = "languages")
	@Query("SELECT t FROM Teacher t where t.isActive")
	List<Teacher> findTeachersWithLanguages();

	@Query("SELECT t FROM Teacher t JOIN t.languages l WHERE l IN :languages")
	List<Teacher> findTeachersByLanguagesIn(@Param("languages") Set<Language> languages);
}

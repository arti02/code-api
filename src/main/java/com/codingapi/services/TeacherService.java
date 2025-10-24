package com.codingapi.services;

import com.codingapi.dto.TeacherDTO;
import com.codingapi.dto.commands.CreateTeacherCommand;
import com.codingapi.mappers.TeacherMapper;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final TeacherRepository teacherRepository;
	private final TeacherMapper teacherMapper;

	public List<Teacher> getAll() {
		return teacherRepository.findTeachersWithLanguages();
	}

	@Transactional
	public TeacherDTO addTeacher(CreateTeacherCommand createTeacherCommand) {
		Teacher teacher = new Teacher();
		teacher.setFirstName(createTeacherCommand.firstName());
		teacher.setFirstName(createTeacherCommand.lastName());
		teacher.setLanguages(createTeacherCommand.languages());
		return teacherMapper.getDTO(teacherRepository.save(teacher));
	}

	@Transactional
	public void deleteTeacher(Long id) {
		teacherRepository.findById(id).ifPresent(teacher -> {
			teacher.setActive(false);
			teacherRepository.save(teacher);
		});
	}

	public Teacher findById(Long id) {
		return teacherRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
	}

}
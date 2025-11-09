package com.codingapi.services;

import com.codingapi.dto.TeacherDTO;
import com.codingapi.dto.commands.CreateTeacherCommand;
import com.codingapi.mappers.TeacherMapper;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final TeacherRepository teacherRepository;

	public List<TeacherDTO> getAll() {
		return teacherRepository.findTeachersWithLanguages().stream().map(TeacherMapper::toDto).toList();
	}

	@Transactional
	public TeacherDTO addTeacher(CreateTeacherCommand createTeacherCommand) {
		Teacher teacher = new Teacher();
		teacher.setFirstName(createTeacherCommand.firstName());
		teacher.setLastName(createTeacherCommand.lastName());
		teacher.setLanguages(createTeacherCommand.languages());
		return TeacherMapper.toDto(teacherRepository.save(teacher));
	}

	@Transactional
	public void deleteTeacher(Long id) {
		teacherRepository.deleteById(id);
	}

}
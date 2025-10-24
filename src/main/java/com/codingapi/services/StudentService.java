package com.codingapi.services;

import com.codingapi.dto.StudentDTO;
import com.codingapi.dto.commands.CreateStudentCommand;
import com.codingapi.mappers.StudentMapper;
import com.codingapi.model.Student;
import com.codingapi.repositories.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StudentService{

	private final StudentRepository studentRepository;
	private final StudentMapper studentMapper;;

	public List<StudentDTO> getAll() {
		return studentRepository.findStudents().stream()
				.map(studentMapper::getDTO)
				.toList();
	}

	@Transactional
	public StudentDTO addStudent(CreateStudentCommand createStudentCommand) {
		Student student = new Student();
		student.setFirstName(createStudentCommand.firstName());
		student.setFirstName(createStudentCommand.lastName());
		student.setLanguage(createStudentCommand.language());
		return studentMapper.getDTO(studentRepository.save(student));
	}

	@Transactional
	public void deleteStudent(Long id) {
		studentRepository.findById(id).ifPresent(student -> {
			student.setActive(false);
			studentRepository.save(student);
		});
	}

	public Student findById(Long id) {
		return studentRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
	}
}
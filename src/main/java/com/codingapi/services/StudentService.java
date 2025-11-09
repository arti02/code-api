package com.codingapi.services;

import com.codingapi.dto.StudentDTO;
import com.codingapi.dto.commands.ChangeTeacherCommand;
import com.codingapi.dto.commands.CreateStudentCommand;
import com.codingapi.exceptions.CodingApiException;
import com.codingapi.mappers.StudentMapper;
import com.codingapi.model.Student;
import com.codingapi.model.Teacher;
import com.codingapi.repositories.StudentRepository;
import com.codingapi.repositories.TeacherRepository;
import com.codingapi.validators.CommonValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService{

	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final CommonValidator commonValidator;

	public List<StudentDTO> getAll() {
		return studentRepository.findStudents().stream()
				.map(StudentMapper::toDto)
				.toList();
	}

	@Transactional
	public StudentDTO addStudent(CreateStudentCommand cmd) {
		Teacher teacher = getTeacher(cmd.teacherId());
		Student student = new Student();
		student.setFirstName(cmd.firstName());
		student.setLastName(cmd.lastName());
		student.setLanguage(cmd.language());
		commonValidator.validateLanguageCompatibility(teacher, student);
		student.setTeacher(teacher);
		return StudentMapper.toDto(studentRepository.save(student));
	}

	private Teacher getTeacher(Long teacherId) {
		return teacherRepository.findByIdAndIsActiveTrue(teacherId)
				.orElseThrow(() -> new CodingApiException("Teacher not found with id: " + teacherId));
	}

	@Transactional
	public void deleteStudent(Long id) {
		studentRepository.deleteById(id);
	}

	@Transactional
	public StudentDTO changeTeacher(Long id, ChangeTeacherCommand cmd) {
		Teacher newTeacher = getTeacher(cmd.newTeacherId());
		Student student = studentRepository.findByIdWithLock(id)
				.orElseThrow(()-> new CodingApiException("Student not exist with id: " + id));
		commonValidator.validateLanguageCompatibility(newTeacher, student);
		student.setTeacher(newTeacher);
		try {
			return StudentMapper.toDto(studentRepository.save(student));
		} catch (DataIntegrityViolationException | ConstraintViolationException e) {
			throw new CodingApiException("This teacher is already assigned to another student");
		}
	}
}
package com.codingapi.controllers;

import com.codingapi.dto.StudentDTO;
import com.codingapi.dto.commands.CreateStudentCommand;
import com.codingapi.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@GetMapping
	public ResponseEntity<List<StudentDTO>> getAll() {
		List<StudentDTO> result = studentService.getAll();
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<StudentDTO> addStudent(@Valid @RequestBody CreateStudentCommand command) {
		StudentDTO created = studentService.addStudent(command);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
		studentService.deleteStudent(id);
		return ResponseEntity.noContent().build();
	}
}
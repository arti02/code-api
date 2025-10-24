package com.codingapi.controllers;

import com.codingapi.dto.TeacherDTO;
import com.codingapi.dto.commands.CreateTeacherCommand;
import com.codingapi.model.Teacher;
import com.codingapi.services.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {
	private final TeacherService teacherService;

	@GetMapping
	public ResponseEntity<List<Teacher>> getAllTeachers() {
		return ResponseEntity.ok(teacherService.getAll());
	}

	@PostMapping
	public ResponseEntity<TeacherDTO> addTeacher(@Valid @RequestBody CreateTeacherCommand command) {
		TeacherDTO teacherDTO = teacherService.addTeacher(command);
		return ResponseEntity.status(HttpStatus.CREATED).body(teacherDTO);
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
		teacherService.deleteTeacher(id);
		return ResponseEntity.noContent().build();
	}
}
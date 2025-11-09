package com.codingapi.controllers;

import com.codingapi.dto.LessonDTO;
import com.codingapi.dto.commands.ChangeLessonDateCommand;
import com.codingapi.dto.commands.CreateLessonCommand;
import com.codingapi.services.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

	private final LessonService lessonService;

	@GetMapping
	public ResponseEntity<List<LessonDTO>> getAllLessons() {
		return ResponseEntity.ok(lessonService.getAll());
	}

	@PostMapping
	public ResponseEntity<LessonDTO> create(@Valid @RequestBody CreateLessonCommand command) {
		return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.addLesson(command));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		lessonService.deleteLesson(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/change-date/{id}")
	public ResponseEntity<LessonDTO> changeDate(@PathVariable("id") Long id,
			@Valid @RequestBody ChangeLessonDateCommand cmd) {
		return ResponseEntity.ok(lessonService.changeAndValidateDate(id, cmd));
	}

}

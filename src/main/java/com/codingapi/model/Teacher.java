package com.codingapi.model;

import com.codingapi.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Teacher extends BasePersonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ToString.Include
	private String firstName;

	@ToString.Include
	private String lastName;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "teacher_languages", joinColumns = @JoinColumn(name = "teacher_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "language", nullable = false)
	private Set<Language> languages = new HashSet<>();

	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
	private List<Lesson> lessons = new ArrayList<>();
}
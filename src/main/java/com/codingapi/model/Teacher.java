package com.codingapi.model;

import com.codingapi.enums.Language;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SoftDelete;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@SQLDelete(sql = "UPDATE teacher SET is_active = false WHERE id = ? AND version = ?")
public class Teacher extends BasePersonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ToString.Include
	private String firstName;

	@ToString.Include
	private String lastName;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "teacher_languages", joinColumns = @JoinColumn(name = "teacher_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "language", nullable = false)
	private Set<Language> languages = new HashSet<>();

	@OneToMany(mappedBy = "teacher")
	private Set<Lesson> lessons;

	@OneToMany(mappedBy = "teacher")
	private Set<Student> students;
}
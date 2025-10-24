package com.codingapi.model;

import com.codingapi.enums.Language;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Student extends BasePersonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ToString.Include
	private String firstName;

	@ToString.Include
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@ToString.Include
	private Language language;

	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	private List<Lesson> lessons = new ArrayList<>();

}

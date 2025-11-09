package com.codingapi.model;

import com.codingapi.enums.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table
@SQLDelete(sql = "UPDATE student SET is_active = false WHERE id = ? AND version = ?")
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

	@ManyToOne(optional = false)
	@JoinColumn(name = "teacher_id", nullable = false)
	private Teacher teacher;

	@OneToMany(mappedBy = "student")
	private Set<Lesson> lessons;

}

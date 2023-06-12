package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class Artist {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank
	private String  name;
	@NotBlank
	private String  surname;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfDeath;
	private String  nationality;
	
	@OneToMany(mappedBy = "director", fetch = FetchType.EAGER)
	private Set<Movie> directedMovies; 
	
	@ManyToMany(mappedBy = "actors", fetch = FetchType.EAGER)
	private Set<Movie> starredMovies;
	
	@OneToOne(cascade = CascadeType.REMOVE)
	private Image profilePicture;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public Set<Movie> getDirectedMovies() {
		return directedMovies;
	}
	public void setDirectedMovies(Set<Movie> directedMovies) {
		this.directedMovies = directedMovies;
	}
	public Set<Movie> getStarredMovies() {
		return starredMovies;
	}
	public void setStarredMovies(Set<Movie> starredMovies) {
		this.starredMovies = starredMovies;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public LocalDate getDateOfDeath() {
		return dateOfDeath;
	}
	public void setDateOfDeath(LocalDate dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public Image getProfilePicture() {
		return this.profilePicture;
	}
	public void setProfilePicture(Image profilePicture) {
		this.profilePicture = profilePicture;
	}
	@Override
	public int hashCode() {
		return Objects.hash(name, surname);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(name, other.name) && Objects.equals(surname, other.surname);
	}
	
	
}

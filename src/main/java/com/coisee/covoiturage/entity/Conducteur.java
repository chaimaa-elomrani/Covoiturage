package com.coisee.covoiturage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(
		name = "conducteurs",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_conducteurs_email", columnNames = "email"),
				@UniqueConstraint(name = "uk_conducteurs_numero_permis", columnNames = "numero_permis")
		}
)
public class Conducteur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 100)
	@Column(name = "prenom", nullable = false, length = 100)
	private String prenom;

	@NotBlank
	@Size(max = 100)
	@Column(name = "nom", nullable = false, length = 100)
	private String nom;

	@NotBlank
	@Email
	@Size(max = 254)
	@Column(name = "email", nullable = false, length = 254)
	private String email;

	@NotBlank
	@Size(max = 50)
	@Column(name = "numero_permis", nullable = false, length = 50)
	private String numeroPermis;

	protected Conducteur() {
	}

	public Conducteur(String prenom, String nom, String email, String numeroPermis) {
		this.prenom = prenom;
		this.nom = nom;
		this.email = email;
		this.numeroPermis = numeroPermis;
	}

	public Long getId() {
		return id;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumeroPermis() {
		return numeroPermis;
	}

	public void setNumeroPermis(String numeroPermis) {
		this.numeroPermis = numeroPermis;
	}
}

package com.coisee.covoiturage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "passagers")
public class Passager {

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
	@Column(name = "email", nullable = false, unique = true, length = 254)
	private String email;

	@NotBlank
	@Size(max = 20)
	@Column(name = "telephone", nullable = false, length = 20)
	private String telephone;

	protected Passager() {
	}

	public Passager(String prenom, String nom, String email, String telephone) {
		this.prenom = prenom;
		this.nom = nom;
		this.email = email;
		this.telephone = telephone;
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

	public String getTelephone() {
		return telephone;
	}

}

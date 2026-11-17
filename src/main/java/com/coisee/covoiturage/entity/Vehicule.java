package com.coisee.covoiturage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
		name = "vehicules",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_vehicules_immatriculation",
				columnNames = "immatriculation"
		)
)
public class Vehicule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 80)
	@Column(name = "marque", nullable = false, length = 80)
	private String marque;

	@NotBlank
	@Size(max = 100)
	@Column(name = "modele", nullable = false, length = 100)
	private String modele;

	@NotBlank
	@Size(max = 20)
	@Column(name = "immatriculation", nullable = false, length = 20)
	private String immatriculation;

	@NotBlank
	@Size(max = 50)
	@Column(name = "couleur", nullable = false, length = 50)
	private String couleur;

	@NotNull
	@Min(1)
	@Max(8)
	@Column(name = "capacite", nullable = false)
	private Integer capacite ;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "conducteur_id", nullable = false)
	private Conducteur conducteur;

	protected Vehicule() {
	}

	public Vehicule(
			String marque,
			String modele,
			String immatriculation,
			String couleur,
			Integer capacite ,
			Conducteur conducteur
	) {
		this.marque = marque;
		this.modele = modele;
		this.immatriculation = immatriculation;
		this.couleur = couleur;
		this.capacite  = capacite ;
		this.conducteur = conducteur;
	}

	public Long getId() {
		return id;
	}

	public String getMarque() {
		return marque;
	}

	public void setMarque(String marque) {
		this.marque = marque;
	}

	public String getModele() {
		return modele;
	}

	public void setModele(String modele) {
		this.modele = modele;
	}

	public String getImmatriculation() {
		return immatriculation;
	}

	public void setImmatriculation(String immatriculation) {
		this.immatriculation = immatriculation;
	}

	public String getCouleur() {
		return couleur;
	}

	public void setCouleur(String couleur) {
		this.couleur = couleur;
	}

	public Integer getCapacite () {
		return capacite ;
	}

	public void setCapacite (Integer capacite ) {
		this.capacite  = capacite ;
	}

	public Conducteur getConducteur() {
		return conducteur;
	}

	public void setConducteur(Conducteur conducteur) {
		this.conducteur = conducteur;
	}
}

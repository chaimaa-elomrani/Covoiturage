package com.coisee.covoiturage.entity;

import com.coisee.covoiturage.enums.StatutTrajet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trajets")
public class Trajet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "conducteur_id", nullable = false)
	private Conducteur conducteur;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vehicule_id", nullable = false)
	private Vehicule vehicule;

	@NotNull
	@Min(1)
	@Column(name = "places_proposees", nullable = false)
	private Integer placesProposees;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "statut", nullable = false, length = 20)
	private StatutTrajet statut = StatutTrajet.PUBLIE;

	@Valid
	@NotEmpty
	@Size(min = 2)
	@OneToMany(mappedBy = "trajet", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("ordre ASC")
	private List<PointArret> pointsArret = new ArrayList<>();

	protected Trajet() {
	}

	public Trajet(Conducteur conducteur, Vehicule vehicule, Integer placesProposees) {
		this.conducteur = conducteur;
		this.vehicule = vehicule;
		this.placesProposees = placesProposees;
		this.statut = StatutTrajet.PUBLIE;
	}

	public Long getId() {
		return id;
	}

	public Conducteur getConducteur() {
		return conducteur;
	}

	public void setConducteur(Conducteur conducteur) {
		this.conducteur = conducteur;
	}

	public Vehicule getVehicule() {
		return vehicule;
	}

	public void setVehicule(Vehicule vehicule) {
		this.vehicule = vehicule;
	}

	public Integer getPlacesProposees() {
		return placesProposees;
	}

	public void setPlacesProposees(Integer placesProposees) {
		this.placesProposees = placesProposees;
	}

	public StatutTrajet getStatut() {
		return statut;
	}

	public void setStatut(StatutTrajet statut) {
		this.statut = statut;
	}

	public List<PointArret> getPointsArret() {
		return pointsArret;
	}



}

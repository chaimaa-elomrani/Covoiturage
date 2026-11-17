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
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "points_arret",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_points_arret_trajet_ordre", columnNames = {"trajet_id", "ordre"}),
				@UniqueConstraint(name = "uk_points_arret_trajet_ville", columnNames = {"trajet_id", "ville"})
		}
)
public class PointArret {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "trajet_id", nullable = false)
	private Trajet trajet;

	@NotBlank
	@Size(max = 120)
	@Column(name = "ville", nullable = false, length = 120)
	private String ville;

	@NotNull
	@PositiveOrZero
	@Column(name = "ordre", nullable = false)
	private Integer ordre;

	@NotNull
	@Column(name = "heure_passage", nullable = false)
	private LocalDateTime heurePassage;

	@NotNull
	@DecimalMin("0.00")
	@Column(name = "prix_depuis_depart", nullable = false, precision = 6, scale = 2)
	private BigDecimal prixDepuisDepart;

	protected PointArret() {
	}

	public PointArret(String ville, Integer ordre, LocalDateTime heurePassage, BigDecimal prixDepuisDepart) {
		this.ville = ville;
		this.ordre = ordre;
		this.heurePassage = heurePassage;
		this.prixDepuisDepart = prixDepuisDepart;
	}

	public Long getId() {
		return id;
	}

	public Trajet getTrajet() {
		return trajet;
	}

	public void setTrajet(Trajet trajet) {
		this.trajet = trajet;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public Integer getOrdre() {
		return ordre;
	}

	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}

	public LocalDateTime getHeurePassage() {
		return heurePassage;
	}

	public void setHeurePassage(LocalDateTime heurePassage) {
		this.heurePassage = heurePassage;
	}

	public BigDecimal getPrixDepuisDepart() {
		return prixDepuisDepart;
	}

	public void setPrixDepuisDepart(BigDecimal prixDepuisDepart) {
		this.prixDepuisDepart = prixDepuisDepart;
	}
}

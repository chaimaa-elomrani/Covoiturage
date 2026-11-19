package com.coisee.covoiturage.entity;

import com.coisee.covoiturage.enums.StatutReservation;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "passager_id", nullable = false)
	private Passager passager;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "trajet_id", nullable = false)
	private Trajet trajet;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "point_montee_id", nullable = false)
	private PointArret pointMontee;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "point_descente_id", nullable = false)
	private PointArret pointDescente;

	@NotNull
	@Min(1)
	@Column(name = "nombre_places", nullable = false)
	private Integer nombrePlaces;

	@NotNull
	@DecimalMin("0.01")
	@Digits(integer = 8, fraction = 2)
	@Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal montantTotal;

	@NotNull
	@Column(name = "date_reservation", nullable = false, updatable = false)
	private LocalDateTime dateReservation;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "statut", nullable = false, length = 20)
	private StatutReservation statut = StatutReservation.CONFIRMEE;

	protected Reservation() {
	}

	public Reservation(Passager passager, Trajet trajet, PointArret pointMontee, PointArret pointDescente, Integer nombrePlaces, BigDecimal montantTotal) {
		this.passager = passager;
		this.trajet = trajet;
		this.pointMontee = pointMontee;
		this.pointDescente = pointDescente;
		this.nombrePlaces = nombrePlaces;
		this.montantTotal = montantTotal;
		this.statut = StatutReservation.CONFIRMEE;
	}

	@PrePersist
	protected void initialiserDateReservation() {
		if (dateReservation == null) {
			dateReservation = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public Passager getPassager() {
		return passager;
	}

	public void setPassager(Passager passager) {
		this.passager = passager;
	}

	public Trajet getTrajet() {
		return this.trajet;
	}

	public void setTrajet(Trajet trajet) {
		this.trajet = trajet;
	}

	public PointArret getPointMontee() {
		return pointMontee;
	}

	public void setPointMontee(PointArret pointMontee) {
		this.pointMontee = pointMontee;
	}

	public PointArret getPointDescente() {
		return pointDescente;
	}

	public void setPointDescente(PointArret pointDescente) {
		this.pointDescente = pointDescente;
	}

	public Integer getNombrePlaces() {
		return nombrePlaces;
	}

	public void setNombrePlaces(Integer nombrePlaces) {
		this.nombrePlaces = nombrePlaces;
	}

	public BigDecimal getMontantTotal() {
		return montantTotal;
	}

	public void setMontantTotal(BigDecimal montantTotal) {
		this.montantTotal = montantTotal;
	}

	public LocalDateTime getDateReservation() {
		return dateReservation;
	}

	public StatutReservation getStatut() {
		return statut;
	}

	public void setStatut(StatutReservation statut) {
		this.statut = statut;
	}
}

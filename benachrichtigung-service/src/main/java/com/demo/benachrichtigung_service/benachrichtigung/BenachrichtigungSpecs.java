package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

public class BenachrichtigungSpecs {

	private BenachrichtigungSpecs() {
		// Utility-Klasse
	}

	public static Specification<Benachrichtigung> hatTyp(BenachrichtigungTyp typ) {
		return (root, query, cb) -> typ == null
				? cb.conjunction()
				: cb.equal(root.get("typ"), typ);
	}

    public static Specification<Benachrichtigung> hatKontoId(Long kontoId) {
        return (root, query, cb) -> kontoId == null
                ? cb.conjunction()
                : cb.equal(root.get("kontoId"), kontoId);
    }

	public static Specification<Benachrichtigung> hatBuchungId(Long buchungId) {
		return (root, query, cb) -> buchungId == null
				? cb.conjunction()
				: cb.equal(root.get("buchungId"), buchungId);
	}

	public static Specification<Benachrichtigung> hatTransaktionId(Long transaktionId) {
		return (root, query, cb) -> transaktionId == null
				? cb.conjunction()
				: cb.equal(root.get("transaktionId"), transaktionId);
	}

	public static Specification<Benachrichtigung> hatQuelleKontoId(Long quelleKontoId) {
		return (root, query, cb) -> quelleKontoId == null
				? cb.conjunction()
				: cb.equal(root.get("quelleKontoId"), quelleKontoId);
	}

	public static Specification<Benachrichtigung> hatZielKontoId(Long zielKontoId) {
		return (root, query, cb) -> zielKontoId == null
				? cb.conjunction()
				: cb.equal(root.get("zielKontoId"), zielKontoId);
	}
    
	public static Specification<Benachrichtigung> hatIban(String iban) {
		return (root, query, cb) -> (iban == null || iban.isBlank())
				? cb.conjunction()
				: cb.equal(root.get("iban"), iban);
	}

    public static Specification<Benachrichtigung> hatAktion(AktionTyp aktion) {
        return (root, query, cb) -> aktion == null
                ? cb.conjunction()
                : cb.equal(root.get("aktion"), aktion);
    }

	public static Specification<Benachrichtigung> hatInhaber(String inhaber) {
		return (root, query, cb) -> (inhaber == null || inhaber.isBlank())
				? cb.conjunction()
				: cb.equal(root.get("inhaber"), inhaber);
	}

	public static Specification<Benachrichtigung> hatQuelleInhaber(String quelleInhaber) {
		return (root, query, cb) -> (quelleInhaber == null || quelleInhaber.isBlank())
				? cb.conjunction()
				: cb.equal(root.get("quelleInhaber"), quelleInhaber);
	}

	public static Specification<Benachrichtigung> hatZielInhaber(String zielInhaber) {
		return (root, query, cb) -> (zielInhaber == null || zielInhaber.isBlank())
				? cb.conjunction()
				: cb.equal(root.get("zielInhaber"), zielInhaber);
	}

	public static Specification<Benachrichtigung> hatBetrag(Double betrag) {
		return (root, query, cb) -> betrag == null
				? cb.conjunction()
				: cb.equal(root.get("betrag"), betrag);
	}

	public static Specification<Benachrichtigung> hatQuelleIban(String quelleIban) {
		return (root, query, cb) -> (quelleIban == null || quelleIban.isBlank())
				? cb.conjunction()
				: cb.equal(root.get("quelleIban"), quelleIban);
	}

	public static Specification<Benachrichtigung> hatZielIban(String zielIban) {
		return (root, query, cb) -> (zielIban == null || zielIban.isBlank())
				? cb.conjunction()
				: cb.equal(root.get("zielIban"), zielIban);
	}

	public static Specification<Benachrichtigung> abZeitpunkt(LocalDateTime von) {
		return (root, query, cb) -> von == null
				? cb.conjunction()
				: cb.greaterThanOrEqualTo(root.get("timestamp"), von);
	}

	public static Specification<Benachrichtigung> bisZeitpunkt(LocalDateTime bis) {
		return (root, query, cb) -> bis == null
				? cb.conjunction()
				: cb.lessThanOrEqualTo(root.get("timestamp"), bis);
	}

	public static Specification<Benachrichtigung> mitFiltern(
			BenachrichtigungTyp typ,
			Long kontoId,
			Long buchungId,
			Long transaktionId,
			Long quelleKontoId,
			Long zielKontoId,
			String iban,
			String quelleIban,
			String zielIban,
			String inhaber,
			String quelleInhaber,
			String zielInhaber,
			AktionTyp aktion,
			Double betrag,
			LocalDateTime von,
			LocalDateTime bis
	) {
		return Specification.where(hatTyp(typ))
				.and(hatIban(iban))
				.and(hatAktion(aktion))
				.and(hatKontoId(kontoId))
				.and(hatBuchungId(buchungId))
				.and(hatTransaktionId(transaktionId))
				.and(hatQuelleKontoId(quelleKontoId))
				.and(hatZielKontoId(zielKontoId))
				.and(hatQuelleIban(quelleIban))
				.and(hatZielIban(zielIban))
				.and(hatInhaber(inhaber))
				.and(hatQuelleInhaber(quelleInhaber))
				.and(hatZielInhaber(zielInhaber))
				.and(hatBetrag(betrag))
				.and(abZeitpunkt(von))
				.and(bisZeitpunkt(bis));
	}
}

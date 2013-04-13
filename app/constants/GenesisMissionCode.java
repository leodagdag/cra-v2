package constants;

/**
 * @author f.patin
 */
public enum GenesisMissionCode {
	AV("Avant-vente"),
	CP("Congé payé"),
	RTTS("RTT Salarié"),
	RTTE("RTT Employeur"),
	TP("Temps partiel"),
	CSS("Congé sans solde"),
	TI("Travaux interne"),
	F("Formation"),
	IC("Inter contrat"),
	MM("Maladie Maternité"),
	AE("Absence exceptionnelle");

	public final String label;

	GenesisMissionCode(final String label) {
		this.label = label;
	}
}

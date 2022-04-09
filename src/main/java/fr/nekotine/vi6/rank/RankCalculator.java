package fr.nekotine.vi6.rank;

import fr.nekotine.vi6.database.DBPlayer;

public class RankCalculator {
	
	public static final int LP_PER_SECURED = 20;

	public static void calculate(int totalStealed, int totalSecured, int totalArtefact, double moySecu, DBPlayer p) {
		int flatMoySecu = (int) moySecu;
		int change = ((totalSecured-flatMoySecu)*LP_PER_SECURED)+(totalStealed*(LP_PER_SECURED/3));
		switch (p.getTeam()) {
		case GARDE:
			p.setLpGarde(p.getLpGarde()-change);
			break;
		case VOLEUR:
			p.setLpVoleur(p.getLpVoleur()+change);
			break;
		}
	}
}

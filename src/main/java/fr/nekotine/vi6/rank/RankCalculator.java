package fr.nekotine.vi6.rank;

import fr.nekotine.vi6.database.DBPlayer;

public class RankCalculator {
	
	public static final float LP_PER_SECURED = 20.0f;

	public static void calculate(int totalStealed, int totalSecured, int totalArtefact, double moySecu, DBPlayer p) {
		int change = (int)((totalSecured-moySecu)*LP_PER_SECURED);
		switch (p.getTeam()) {
		case GARDE:
			p.setLpGain(-change);
			p.setLpGarde(p.getLpGarde()-change);
			break;
		case VOLEUR:
			p.setLpGain(change);
			p.setLpVoleur(p.getLpVoleur()+change);
			break;
		}
	}
}

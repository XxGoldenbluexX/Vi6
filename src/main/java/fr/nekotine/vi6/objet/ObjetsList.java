package fr.nekotine.vi6.objet;

import fr.nekotine.vi6.enums.Team;

public enum ObjetsList {
	LANTERN(Team.VOLEUR, 1000);
	private final Team team;
	private final int cost;
	ObjetsList(Team team, int cost){
		this.team=team;
		this.cost=cost;
	}
	public Team getTeam() {
		return team;
	}
	public int getCost() {
		return cost;
	}
}

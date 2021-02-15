package fr.nekotine.vi6.objet;

import fr.nekotine.vi6.enums.Team;

public enum ObjetsList {
	LANTERN(Team.GARDE, 5, 1000);
	private final Team team;
	private final int limit;
	private final int cost;
	ObjetsList(Team team,  int limit, int cost){
		this.team=team;
		this.limit = limit;
		this.cost=cost;
	}
	public Team getTeam() {
		return team;
	}
	public int getCost() {
		return cost;
	}
	public int getLimit() {
		return limit;
	}
}

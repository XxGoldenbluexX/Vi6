package fr.nekotine.vi6.objet;

import org.bukkit.Material;

import fr.nekotine.vi6.enums.Team;

public enum ObjetsList {
	Lanterne("Lanterne",Material.LANTERN,Team.VOLEUR);
	private final String objetName;
	private final Material material;
	private final Team team;
	ObjetsList(String objetName, Material material, Team team) {
		this.objetName=objetName;
		this.material=material;
		this.team=team;
	}
	public String getName() {
		return objetName;
	}
	public Material getMaterial() {
		return material;
	}
	public Team getTeam() {
		return team;
	}
}

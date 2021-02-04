package fr.nekotine.vi6.objet;

import org.bukkit.Material;

public enum ObjetsSkins {
	LanterneBleue(ObjetsList.Lanterne, ObjetsList.Lanterne.getMaterial());
	
	
	private final ObjetsList objet;
	private final Material material;
	ObjetsSkins(ObjetsList objet, Material material) {
		this.objet=objet;
		this.material=material;
	}
	public ObjetsList getObjet() {
		return objet;
	}
	public Material getMaterial() {
		return material;
	}
}

package fr.nekotine.vi6.objet;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;

public enum ObjetsSkins {
	Lantern(ObjetsList.LANTERN,
			SkinType.DEFAULT,
			ChatColor.RED+"Lanterne",
			Material.LANTERN,
			new String[]{"Ceci est la rune Lanterne (skin de base de la Lanterne)"},
			Material.LANTERN),
	Lantern0(ObjetsList.LANTERN,
			SkinType.DEFAULT,
			ChatColor.RED+"Lanterne0",
			Material.LANTERN,
			new String[]{"Ceci est la rune Lanterne (skin de base de la Lanterne)"},
			Material.LANTERN),
	BlueLantern(Lantern.getObjet(),
			SkinType.SKIN,
			ChatColor.BLUE+"LanterneBleue",
			Material.SOUL_LANTERN,
			new String[]{"Ceci est le skin LanterneBleue"},
			Material.SOUL_LANTERN),
	SeaLantern(Lantern.getObjet(),
			SkinType.SKIN,
			ChatColor.BLUE+"Lanterne Des profondeurs",
			Material.SOUL_LANTERN,
			new String[]{"Ceci est un skin  de Lanterne"},
			Material.SEA_LANTERN);
	
	////////////////////////////////
	private final ObjetsList objet;
	private final SkinType skinType;
	private final String objetName;
	private final Material material;
	private final String[] lore;
	private final Material skinMaterial;
	////////////////////////////////
	ObjetsSkins(ObjetsList objet, SkinType skinType, /* Param�tres itemStack du shop ->*/String objetName, Material material, String[] lore , 
			Material skinMaterial/*, autres parametres spécifiques à certaines runes*/) {
		this.objet = objet;
		this.skinType = skinType;
		this.objetName=objetName;
		this.material=material;
		this.lore = lore;
		this.skinMaterial=skinMaterial;
	}
	////////////////////////////////
	
	public ObjetsList getObjet() {
		return objet;
	}
	public String getName() {
		return objetName;
	}
	public Material getMaterial() {
		return material;
	}
	public SkinType getSkinType() {
		return skinType;
	}
	public String[] getLore() {
		return lore;
	}
	public static Objet createObjet(Vi6Main main, ObjetsSkins skin, Player player, Game game) {
		switch(skin.getObjet()) {
		case LANTERN:
			return new Lantern(main, skin.getObjet(), player, game, skin.getSkinMaterial());
		default:
			return null;
		}
	}
	public static List<ObjetsSkins> getDefaultSkins(Team team){
		List<ObjetsSkins> objets = new ArrayList<>();
		for(ObjetsSkins obj : values()) {
			if(obj.getObjet().getTeam()==team && obj.skinType==SkinType.DEFAULT) {
				objets.add(obj);
			}
		}
		return objets;
	}
	public static List<ObjetsSkins> getSkins(Team team){
		List<ObjetsSkins> objets = new ArrayList<>();
		for(ObjetsSkins obj : values()) {
			if(obj.getObjet().getTeam()==team && obj.skinType==SkinType.SKIN) {
				objets.add(obj);
			}
		}
		return objets;
	}

	public Material getSkinMaterial() {
		return skinMaterial;
	}
}

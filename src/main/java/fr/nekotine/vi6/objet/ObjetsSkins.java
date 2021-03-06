package fr.nekotine.vi6.objet;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.nekotine.vi6.enums.Team;

public enum ObjetsSkins {
	INVI_SNEAK_SKIN(ObjetsList.INVISNEAK,
			ChatColor.GOLD+"Invisible",
			Material.GLASS_PANE,
			new String[]{ChatColor.LIGHT_PURPLE+"C'est un skin pose pas de question"}),
	CHAMP_DE_FORCE_SLIME(ObjetsList.INVISNEAK,
			ChatColor.GOLD+"Slime",
			Material.SLIME_BLOCK,
			new String[]{ChatColor.LIGHT_PURPLE+"Un champ de force composé de slime"});
	
	////////////////////////////////
	
	private final ObjetsList objet;
	private final String inShopName;
	private final Material inShopMaterial;
	private final String[] inShopLore;
	////////////////////////////////
	ObjetsSkins(ObjetsList objet, String inShopName, Material inShopMaterial, String... inShopLore) {
		this.objet = objet;
		this.inShopName=inShopName;
		this.inShopMaterial=inShopMaterial;
		this.inShopLore=inShopLore;
	}
	////////////////////////////////
	
	public ObjetsList getObjet() {
		return objet;
	}

	public String getInShopName() {
		return inShopName;
	}

	public Material getInShopMaterial() {
		return inShopMaterial;
	}

	public String[] getInShopLore() {
		return inShopLore;
	}
	
	public static List<ObjetsSkins> getSkinsForTeam(Team team){
		List<ObjetsSkins> objets = new ArrayList<>();
		for(ObjetsSkins obj : values()) {
			if(obj.getObjet().getTeam()==team) {
				objets.add(obj);
			}
		}
		return objets;
	}
}

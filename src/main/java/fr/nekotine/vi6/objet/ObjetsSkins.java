package fr.nekotine.vi6.objet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.enums.Team;

public enum ObjetsSkins {
	Lantern(ObjetsList.LANTERN,
			SkinType.DEFAULT,
			Team.VOLEUR,
			ChatColor.RED+"Lanterne",
			Material.LANTERN,
			new String[]{"Ceci est la rune Lanterne (skin de base de la Lanterne)"}),
	BlueLantern(Lantern.getObjet(),
			SkinType.SKIN,
			Lantern.getTeam(),
			ChatColor.BLUE+"LanterneBleue",
			Material.SOUL_LANTERN,
			new String[]{"Ceci est le skin LanterneBleue"});
	
	////////////////////////////////
	private final ObjetsList objet;
	private final SkinType skinType;
	private final Team team;
	private final String objetName;
	private final Material material;
	private final String[] lore;
	////////////////////////////////
	ObjetsSkins(ObjetsList objet, SkinType skinType, Team team, /* Paramètres itemStack du shop ->*/String objetName, Material material, String[] lore /*, autres parametres spécifiques à certaines runes*/) {
		this.objet = objet;
		this.skinType = skinType;
		this.objetName=objetName;
		this.material=material;
		this.team=team;
		this.lore = lore;
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
	public Team getTeam() {
		return team;
	}
	public SkinType getSkinType() {
		return skinType;
	}
	public String[] getLore() {
		return lore;
	}
	public Objet createObjet(ObjetsSkins skin, Player player, Game game) {
		switch(skin.objet) {
		case LANTERN:
			return new Lantern(objet, player, game);
		default:
			return null;
		}
	}
}

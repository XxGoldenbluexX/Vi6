package fr.nekotine.vi6.objet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.list.BuissonFurtif;
import fr.nekotine.vi6.objet.list.Invisneak;
import fr.nekotine.vi6.objet.list.MatraqueDeTheo;
import fr.nekotine.vi6.objet.utils.Objet;

public enum ObjetsList {
	
	INVISNEAK(Invisneak.class,
			Team.VOLEUR,
			1000,
			1,
			ChatColor.GOLD+"Invisible",
			Material.GLASS_PANE,
			ChatColor.LIGHT_PURPLE+"Devenez invisible en vous accroupissant"),
	BUISSON_FURTIF(BuissonFurtif.class,Team.VOLEUR,1000,1,
			ChatColor.GREEN+"Buisson Furtif",Material.OAK_LEAVES,ChatColor.LIGHT_PURPLE+"Devenez invisible et insondable dans les buissons."),
	MATRAQUE_DE_THEO(MatraqueDeTheo.class,Team.GARDE,1000,1,
			ChatColor.DARK_RED+"Matraque de théo",Material.OAK_LEAVES,ChatColor.LIGHT_PURPLE+"Tuez en un coup.",ChatColor.GRAY+"Faites rager les amateurs du bain de fumée.");
	
	////////////////////////////////
	private final Class<?> objetClass;
	private final Team team;
	private final int cost;
	private final int limit;
	private final String inShopName;
	private final Material inShopMaterial;
	private final String[] inShopLore;
	
	
	////////////////////////////////
	
	ObjetsList(Class<?> objetClass, Team team, int cost, int limit, String inShopName, Material inShopMaterial, String... inShopLore){
		this.objetClass=objetClass;
		this.team=team;
		this.cost=cost;
		this.limit = limit;
		this.inShopName=inShopName;
		this.inShopMaterial=inShopMaterial;
		this.inShopLore=inShopLore;
	}
	
	////////////////////////////////
	
	public Team getTeam() {
		return team;
	}
	public int getCost() {
		return cost;
	}
	public int getLimit() {
		return limit;
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
	public Class<?> getObjetClass() {
		return objetClass;
	}
	
	////////////////////////////////

	public static List<ObjetsList> getObjetsForTeam(Team team){
		List<ObjetsList> objets = new ArrayList<>();
		for(ObjetsList obj : values()) {
			if(obj.getTeam()==team) {
				objets.add(obj);
			}
		}
		return objets;
	}
	
	////////////////////////////////
	
	public static Objet createObjet(Vi6Main main, ObjetsList objet, Player player, Game game) {
		try {
			return (Objet)objet.getObjetClass().getConstructor(Vi6Main.class, ObjetsList.class, ObjetsSkins.class, Player.class, Game.class).newInstance(main, objet, game.getWrapper(player).getSelectedSkin(objet), player, game);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}

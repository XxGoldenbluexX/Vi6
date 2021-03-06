package fr.nekotine.vi6.objet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.list.*;
import fr.nekotine.vi6.objet.utils.Objet;

public enum ObjetsList {
	
	INVISNEAK(Invisneak.class,
			Team.VOLEUR,
			100,
			1,
			ChatColor.GOLD+"Invisible",
			Material.GLASS_PANE,
			ChatColor.LIGHT_PURPLE+"Devenez invisible en vous accroupissant"),
	CACTUS(Cactus.class,
			Team.GARDE,
			100,
			1,
			ChatColor.DARK_GREEN+"Cactus",
			Material.CACTUS,
			ChatColor.LIGHT_PURPLE+"Piquez tous les voleurs quand vous en tapez un"),
	SURCHARGE(Surcharge.class,
			Team.GARDE,
			100,
			1,
			ChatColor.RED+"Surcharge",
			Material.FIREWORK_ROCKET,
			ChatColor.LIGHT_PURPLE+"Grand bonus en vitesse et force sur utilisation"),
	BOTTES7LIEUES(Bottes7Lieues.class,
			Team.GARDE,
			100,
			0,
			ChatColor.GREEN+"Bottes de 7 lieues",
			Material.LEATHER_BOOTS,
			ChatColor.LIGHT_PURPLE+"Vitesse accrue de 20%"),
	BUISSON_FURTIF(BuissonFurtif.class,Team.VOLEUR,100,1,
			ChatColor.GREEN+"Buisson Furtif",Material.OAK_LEAVES,ChatColor.LIGHT_PURPLE+"Devenez invisible et insondable dans les buissons."),
	DOUBLE_SAUT(DoubleSaut.class,Team.VOLEUR,100,1,
			ChatColor.YELLOW+"Bottes de propultion",Material.GOLDEN_BOOTS,ChatColor.LIGHT_PURPLE+"Vous pouvez effectuer un double saut."),
	BARRICADE(Barricade.class,Team.GARDE,0,1,
			ChatColor.DARK_PURPLE+"Barricade",Material.BRICKS,ChatColor.LIGHT_PURPLE+"Vous pouvez boucher un passage avec."),
	TELEPORTEUR(Teleporteur.class,Team.GARDE,100,0,
			ChatColor.DARK_BLUE+"Téléporteur",Material.LIGHT_GRAY_SHULKER_BOX,ChatColor.LIGHT_PURPLE+"Vous pouvez placer un téléporteur monodirectionel"),
	CHAMP_DE_FORCE(ChampDeForce.class,Team.GARDE,100,0,
			ChatColor.GRAY+"Champ de force",Material.LIGHT_GRAY_STAINED_GLASS,ChatColor.LIGHT_PURPLE+"Vous pouvez placer une porte ou seul les gardes peuvent passer"),
	LANTERNE(Lanterne.class,Team.GARDE,0,1,
			ChatColor.GOLD+"Lanterne",Material.BRICKS,ChatColor.LIGHT_PURPLE+"Permet de poser jusqu'a deux lanternes que vos aliés peuvent prendre pour se téléporter a vous."),
	MATRAQUE_DE_THEO(MatraqueDeTheo.class,Team.GARDE,100,1,
			ChatColor.DARK_RED+"Matraque de théo",Material.NETHERITE_SWORD,ChatColor.LIGHT_PURPLE+"Tuez en un coup.",ChatColor.GRAY+"Faites rager les amateurs du bain de fumée.");
	
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
	public ItemStack makeInShopItem() {
		ItemStack itm = new ItemStack(inShopMaterial);
		return itm;
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

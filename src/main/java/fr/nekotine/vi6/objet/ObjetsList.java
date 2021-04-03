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
import fr.nekotine.vi6.objet.list.Armure;
import fr.nekotine.vi6.objet.list.BainDeFumee;
import fr.nekotine.vi6.objet.list.Barricade;
import fr.nekotine.vi6.objet.list.Bottes7Lieues;
import fr.nekotine.vi6.objet.list.BuissonFurtif;
import fr.nekotine.vi6.objet.list.Cactus;
import fr.nekotine.vi6.objet.list.Campeur;
import fr.nekotine.vi6.objet.list.ChampDeForce;
import fr.nekotine.vi6.objet.list.DeadRinger;
import fr.nekotine.vi6.objet.list.DoubleSaut;
import fr.nekotine.vi6.objet.list.Invisneak;
import fr.nekotine.vi6.objet.list.Lanterne;
import fr.nekotine.vi6.objet.list.MatraqueDeTheo;
import fr.nekotine.vi6.objet.list.Ombre;
import fr.nekotine.vi6.objet.list.SixiemeSens;
import fr.nekotine.vi6.objet.list.Sonar;
import fr.nekotine.vi6.objet.list.Surcharge;
import fr.nekotine.vi6.objet.list.Teleporteur;
import fr.nekotine.vi6.objet.list.Dephasage;
import fr.nekotine.vi6.objet.list.Pecheur;
import fr.nekotine.vi6.objet.list.GPS;
import fr.nekotine.vi6.objet.list.GlobeVoyant;
import fr.nekotine.vi6.objet.list.PiegeADents;
import fr.nekotine.vi6.objet.list.BrouilleurRadio;
import fr.nekotine.vi6.objet.list.OmniCapteur;
import fr.nekotine.vi6.objet.list.Tazer;
import fr.nekotine.vi6.objet.list.PiegeCapteur;
import fr.nekotine.vi6.objet.list.IEM;
import fr.nekotine.vi6.objet.list.PiegeCollant;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public enum ObjetsList {
	
	INVISNEAK(Invisneak.class,
			Team.VOLEUR,
			100,
			1,
			ChatColor.GOLD+"Invisible",
			Material.GLASS_PANE,
			ChatColor.LIGHT_PURPLE+"Devenez invisible en vous accroupissant"),
	OMBRE(Ombre.class,
			Team.VOLEUR,
			100,
			0,
			ChatColor.GRAY+"Ombre",
			Material.WITHER_SKELETON_SKULL,
			ChatColor.LIGHT_PURPLE+"Placez une ombre pour revenir dessus plus tard",
			ChatColor.LIGHT_PURPLE+" et mourrez si un garde la trouve!"),
	SIXIEME_SENS(SixiemeSens.class,
			Team.VOLEUR,
			100,
			1,
			ChatColor.AQUA+"Sixième Sens",
			Material.HEART_OF_THE_SEA,
			ChatColor.LIGHT_PURPLE+"Vous voyez les gardes proche"),
	ARMURE(Armure.class,
			Team.VOLEUR,
			100,
			0,
			ChatColor.WHITE+"Armure",
			Material.IRON_CHESTPLATE,
			ChatColor.LIGHT_PURPLE+"Obtenez "+Armure.getHealthBonus()/2+" cœurs supplémentaires"),
	CAMPEUR(Campeur.class,
			Team.VOLEUR,
			100,
			0,
			ChatColor.GREEN+"Campeur",
			Material.CAMPFIRE,
			ChatColor.LIGHT_PURPLE+"Régénerez-vous après un certain temps sans prendre de dégâts"),
	DEADRINGER(DeadRinger.class,
			Team.VOLEUR,
			100,
			0,
			ChatColor.RED+"Dead Ringer",
			Material.CLOCK,
			ChatColor.LIGHT_PURPLE+"Quand vous allez mourrir, feint votre mort et devenez invisible"),
	DEPHASAGE(Dephasage.class,
			Team.VOLEUR,
			100,
			1,
			ChatColor.GOLD+"Déphasage",
			Material.IRON_NUGGET,
			ChatColor.LIGHT_PURPLE+"Toutes les "+Math.round(Dephasage.getDelay()/20)+" secondes gagnez "+Math.round(Dephasage.getDuration()/20)+" secondes d'invisibilité"),
	PECHEUR_VOLEUR(Pecheur.class,
			Team.VOLEUR,
			100,
			0,
			ChatColor.AQUA+"Pêcheur",
			Material.FISHING_ROD,
			ChatColor.LIGHT_PURPLE+"Pêchez des objets avec cette canne à pêche révolutionnaire!"),
	BUISSON_FURTIF(BuissonFurtif.class,Team.VOLEUR,100,1,
			ChatColor.GREEN+"Buisson Furtif",Material.OAK_LEAVES,ChatColor.LIGHT_PURPLE+"Devenez invisible et insondable dans les buissons."),
	DOUBLE_SAUT(DoubleSaut.class,Team.VOLEUR,100,1,
			ChatColor.YELLOW+"Bottes de propultion",Material.GOLDEN_BOOTS,ChatColor.LIGHT_PURPLE+"Vous pouvez effectuer un double saut."),
	BAIN_DE_FUMEE(BainDeFumee.class,Team.VOLEUR,100,1,
			ChatColor.GRAY+"Bain de fumée",Material.FIREWORK_STAR,ChatColor.LIGHT_PURPLE+"Vous pouvez faire une flaque de fumé."),
	LANTERNE(Lanterne.class,Team.VOLEUR,0,1,
			ChatColor.GOLD+"Lanterne",Material.LANTERN
			,ChatColor.LIGHT_PURPLE+"Permet de poser jusqu'a deux lanternes"
			,ChatColor.LIGHT_PURPLE+"que vos aliés peuvent prendre pour se téléporter a vous."),
	GPS(GPS.class,
		Team.VOLEUR,
		100,
		0,
		ChatColor.GOLD+"Flèche GPS",
		Material.CROSSBOW,
		ChatColor.LIGHT_PURPLE+"Tire une flèche.",
		ChatColor.LIGHT_PURPLE+"Obtenez une boussole qui pointe dans la direction du garde touché"),
	BROUILLEUR_RADIO(BrouilleurRadio.class,
		Team.VOLEUR,
		100,
		0,
		ChatColor.GRAY+"Brouilleur Radio",
		Material.JUKEBOX,
		ChatColor.LIGHT_PURPLE+"Assourdissez l'ensemble des gardes pendant une courte durée"),
	IEM_(IEM.class,
		Team.VOLEUR,
		100,
		0,
		ChatColor.AQUA+"IEM",
		Material.BEACON,
		ChatColor.LIGHT_PURPLE+"Désactive temporairement les objets des gardes",
		ChatColor.LIGHT_PURPLE+"Durée: "+ChatColor.AQUA+Math.round(IEM.getJamDurationTicks()/20)+ChatColor.LIGHT_PURPLE+" secondes"),
	//----------------------------------------------------------------------------------------------------------------------------------------
	MATRAQUE_DE_THEO(MatraqueDeTheo.class,Team.GARDE,100,1,
			ChatColor.DARK_RED+"Matraque de théo",Material.NETHERITE_SWORD,ChatColor.LIGHT_PURPLE+"Tuez en un coup.",ChatColor.GRAY+"Faites rager les amateurs du bain de fumée."),
	CHAMP_DE_FORCE(ChampDeForce.class,Team.GARDE,100,0,
			ChatColor.GRAY+"Champ de force",Material.LIGHT_GRAY_STAINED_GLASS,
			ChatColor.LIGHT_PURPLE+"Vous pouvez placer une porte où",
			ChatColor.LIGHT_PURPLE+"seul les gardes peuvent passer"),
	BARRICADE(Barricade.class,Team.GARDE,0,1,
			ChatColor.DARK_PURPLE+"Barricade",Material.BRICKS,ChatColor.LIGHT_PURPLE+"Vous pouvez boucher un passage avec."),
	TELEPORTEUR(Teleporteur.class,Team.GARDE,100,0,
			ChatColor.DARK_BLUE+"Téléporteur",Material.LIGHT_GRAY_SHULKER_BOX,ChatColor.LIGHT_PURPLE+"Vous pouvez placer un téléporteur monodirectionel"),
	SONAR(Sonar.class,
			Team.GARDE,
			100,
			1,
			ChatColor.GOLD+"Sonar",
			Material.CLOCK,
			ChatColor.LIGHT_PURPLE+"Emet un "+ChatColor.AQUA+ChatColor.ITALIC+"BIP"+ChatColor.LIGHT_PURPLE+" toutes les "+Math.round(Sonar.getDelay()/20)+" secondes",
			ChatColor.LIGHT_PURPLE+"Le son est aigu si un voleur est près de vous"),
	BOTTES7LIEUES(Bottes7Lieues.class,
			Team.GARDE,
			100,
			8,
			ChatColor.GREEN+"Bottes de 7 lieues",
			Material.LEATHER_BOOTS,
			ChatColor.LIGHT_PURPLE+"Vitesse accrue de "+(Bottes7Lieues.getSpeedMultiplier()-1)*100+"%"),
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
	PECHEUR_GARDE(Pecheur.class,
			Team.GARDE,
			100,
			0,
			ChatColor.AQUA+"Pêcheur",
			Material.FISHING_ROD,
			ChatColor.LIGHT_PURPLE+"Pêchez des objets avec cette canne à pêche révolutionnaire!"),
	PIEGE_A_DENTS(PiegeADents.class,Team.GARDE,100,0,
			ChatColor.DARK_GRAY+"Piège à dents",Material.DRAGON_HEAD,
			ChatColor.LIGHT_PURPLE+"Vous pouvez placer un piège a dents",
			ChatColor.LIGHT_PURPLE+"Il inflige "+ChatColor.RED+"3❤"+ChatColor.LIGHT_PURPLE+" de dégats."),
	GLOBE_VOYANT(GlobeVoyant.class,
			Team.GARDE,
			100,
			0,
			ChatColor.GREEN+"Globe Voyant",
			Material.ENDER_EYE,
			ChatColor.LIGHT_PURPLE+"S'attache au plus proche artéfacts.",ChatColor.LIGHT_PURPLE+"Indique quand celui-ci est volé.",
			ChatColor.WHITE+"Délai du message: "+Math.round(GlobeVoyant.getMessageDelayTicks()/20)+"s"),
	OMNICAPTEUR(OmniCapteur.class,
			Team.GARDE,
			100,
			0,
			ChatColor.RED+"OmniCapteur",
			Material.REDSTONE_TORCH,
			ChatColor.LIGHT_PURPLE+"Pose une balise qui affiche en surbrillance les voleurs à proximité.",
			ChatColor.LIGHT_PURPLE+"Portée: "+ChatColor.AQUA+Math.sqrt(OmniCapteur.getSquaredBlockRange())+ChatColor.LIGHT_PURPLE+" blocks"),
	TAZER(Tazer.class,
			Team.GARDE,
			100,
			0,
			ChatColor.AQUA+"Tazer",
			Material.SHEARS,
			ChatColor.LIGHT_PURPLE+"Tire un projectile qui paralyse le voleur touché.",
			ChatColor.LIGHT_PURPLE+"Temps de recharge: "+ChatColor.AQUA+Math.round(Tazer.getCooldown()/20)+ChatColor.LIGHT_PURPLE+" secondes"),
	PIEGE_COLLANT(PiegeCollant.class,
			Team.GARDE,
			100,
			0,
			ChatColor.GOLD+"Piège capteur",
			Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
			ChatColor.LIGHT_PURPLE+"Un piège visible qui envoie un signal quand activé."),
	PIEGE_CAPTEUR(PiegeCapteur.class,
			Team.GARDE,
			100,
			0,
			ChatColor.GOLD+"Piège collant",
			Material.SLIME_BALL,
			ChatColor.LIGHT_PURPLE+"Un piège invisible qui ralentit sa cible.");
	private final Class<?> objetClass;

	private final Team team;

	private final int cost;

	private final int limit;

	private final String inShopName;

	private final Material inShopMaterial;

	private final String[] inShopLore;

	ObjetsList(Class<?> objetClass, Team team, int cost, int limit, String inShopName, Material inShopMaterial,
			String... inShopLore) {
		this.objetClass = objetClass;
		this.team = team;
		this.cost = cost;
		this.limit = limit;
		this.inShopName = inShopName;
		this.inShopMaterial = inShopMaterial;
		this.inShopLore = inShopLore;
	}

	public Team getTeam() {
		return this.team;
	}

	public int getCost() {
		return this.cost;
	}

	public int getLimit() {
		return this.limit;
	}

	public String getInShopName() {
		return this.inShopName;
	}

	public Material getInShopMaterial() {
		return this.inShopMaterial;
	}

	public String[] getInShopLore() {
		return this.inShopLore;
	}

	public Class<?> getObjetClass() {
		return this.objetClass;
	}

	public static List<ObjetsList> getObjetsForTeam(Team team) {
		List<ObjetsList> objets = new ArrayList<>();
		for (ObjetsList obj : values()) {
			if (obj.getTeam() == team)
				objets.add(obj);
		}
		return objets;
	}

	public static Objet createObjet(Vi6Main main, ObjetsList objet, Game game, Player player, PlayerWrapper wrapper) {
		try {
			return (Objet)objet.getObjetClass().getConstructor(Vi6Main.class,ObjetsList.class,ObjetsSkins.class,Game.class,Player.class,PlayerWrapper.class)
					.newInstance(main, objet, game.getWrapper(player).getSelectedSkin(objet), game, player,wrapper);
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

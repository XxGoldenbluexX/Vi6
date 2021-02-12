package fr.nekotine.vi6;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import fr.nekotine.vi6.commands.Vi6commandMaker;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.map.Entree;
import fr.nekotine.vi6.map.Gateway;
import fr.nekotine.vi6.map.Passage;
import fr.nekotine.vi6.map.Sortie;
import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.YamlWorker;

/**
 * Main class of the minecraft plugin
 * 
 * @author XxGoldenbluexX
 * @author Hyez
 * 
 *
 */

public class Vi6Main extends JavaPlugin {
	
	private PluginManager pmanager;
	private static List<Game> gameList = new ArrayList<Game>(1);
	
	@Override
	public void onLoad() {
		super.onLoad();
		CommandAPI.onLoad(false);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		//Register Serializables
		ConfigurationSerialization.registerClass(Entree.class, "Entree");
		ConfigurationSerialization.registerClass(Sortie.class, "Sortie");
		ConfigurationSerialization.registerClass(Passage.class, "Passage");
		ConfigurationSerialization.registerClass(Gateway.class, "Gateway");
		ConfigurationSerialization.registerClass(Artefact.class, "Artefact");
		ConfigurationSerialization.registerClass(Carte.class, "Carte");
		ConfigurationSerialization.registerClass(DetectionZone.class, "DetectionZone");
		pmanager=Bukkit.getPluginManager();//getting pmanager reference
		CommandAPI.onEnable(this);//enable CommandAPI
		Vi6commandMaker.makevi6(this).register();//registering commands
		//File creation
		saveDefaultConfig();//making config.yml
		if (getDataFolder().exists()) {//making dataFolder
			getDataFolder().mkdir();
		}
		File mapf = new File(getDataFolder(),"Maps");//making map Folder
		if (!mapf.exists()){
				mapf.mkdir();
		}
		Carte.setMapFolder(mapf);
		SQLInterface.load(this);
		YamlWorker.load(this);
	}
	
	
	/**
	 * Give the game of the name given
	 * @param name Name of the game
	 * @return the first game with that name, null if it doesent exist.
	 */
	public static Game getGame(String name) {
		for (Game g : gameList) {
			if (g.getName().equals(name)) return g;
		}
		return null;
	}
	
	public boolean createGame(String name) {
		if (gameExist(name)) return false;
		gameList.add(new Game(this,name));
		return true;
	}
	
	public boolean gameExist(String name) {
		for (Game g : gameList) {
			if (g.getName().equals(name)) return true;
		}
		return false;
	}

	public static List<Game> getGameList() {
		return gameList;
	}

	public static void setGameList(List<Game> list) {
		gameList = list;
	}

	public PluginManager getPmanager() {
		return pmanager;
	}
	
	/**
	 * Used to find PlayerWrapper for a Player
	 * @nullable
	 * @param p Player to find
	 * @return PlayerWrapper for the player, null if the player is not in a game
	 */
	public PlayerWrapper getPlayerWrapper(Player p) {
		for (Game g : gameList) {
			Map<Player,PlayerWrapper> map = g.getPlayerMap();
			if (map.containsKey(p)) return map.get(p);
			getPlayerWrapper(p);
		}
		return null;
	}
	
}

package fr.nekotine.vi6;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

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
	private List<Game> gameList = new ArrayList<Game>(1);
	
	@Override
	public void onLoad() {
		super.onLoad();
		//CommandAPI.onLoad(false);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		//CommandAPI.onEnable(this);
		pmanager=Bukkit.getPluginManager();
		//Vi6commandMaker.makevi6().register();
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		SQLInterface.load(getDataFolder().getAbsolutePath());
	}
	
	
	/**
	 * Give the game of the name given
	 * @param name Name of the game
	 * @return the first game with that name, null if it doesent exist.
	 */
	public Game getGame(String name) {
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

	public List<Game> getGameList() {
		return gameList;
	}

	public void setGameList(List<Game> gameList) {
		this.gameList = gameList;
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

package fr.nekotine.vi6;

import java.util.HashMap;

import org.bukkit.entity.Player;

import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.wrappers.GuardWrapper;
import fr.nekotine.vi6.wrappers.ThiefWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class Game {
	
	private final String name;
	private boolean isRanked=true;
	private GameState state = GameState.Waiting;
	private final HashMap<Player,Team> playerList = new HashMap<Player,Team>(10);
	private final HashMap<Player,GuardWrapper> guards = new HashMap<Player,GuardWrapper>(4);
	private final HashMap<Player,ThiefWrapper> thiefs = new HashMap<Player,ThiefWrapper>(4);
	
	public Game(String name) {
		this.name=name;
	}

	public boolean isRanked() {
		return isRanked;
	}

	public void setRanked(boolean isRanked) {
		this.isRanked = isRanked;
	}

	public String getName() {
		return name;
	}

	public GameState getState() {
		return state;
	}
	
	public boolean addPlayer(Player p) {
		if (!playerList.keySet().contains(p)) {
			playerList.put(p, Team.GARDE);
			guards.put(p, new GuardWrapper());
			thiefs.put(p, new ThiefWrapper());
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(String.format(DisplayTexts.getMessage("game.join"), p.getName()));
			}
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(Player p) {
		if (playerList.keySet().contains(p)) {
			playerList.remove(p);
			guards.remove(p);
			thiefs.remove(p);
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(String.format(DisplayTexts.getMessage("game.leave"), p.getName()));
			}
			return true;
		}
		return false;
	}

	public HashMap<Player, Team> getPlayerList() {
		return playerList;
	}

	public HashMap<Player,GuardWrapper> getGuardsMap() {
		return guards;
	}

	public HashMap<Player,ThiefWrapper> getThiefsMap() {
		return thiefs;
	}
	
}

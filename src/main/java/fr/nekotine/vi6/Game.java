package fr.nekotine.vi6;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameStartEvent;
import fr.nekotine.vi6.sql.PlayerGame;
import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.wrappers.GuardWrapper;
import fr.nekotine.vi6.wrappers.ThiefWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class Game implements Listener{
	private final Vi6Main main;
	
	private final String name;
	private boolean isRanked=true;
	private GameState state = GameState.Waiting;
	private final HashMap<Player,Team> playerList = new HashMap<Player,Team>(10);
	private final HashMap<Player,GuardWrapper> guards = new HashMap<Player,GuardWrapper>(4);
	private final HashMap<Player,ThiefWrapper> thiefs = new HashMap<Player,ThiefWrapper>(4);
	
	private String mapName;
	private int money;
	public Game(Vi6Main main, String name) {
		this.main=main;
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
			guards.get(p).destroy();
			guards.remove(p);
			thiefs.get(p).destroy();
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
	//je met ça là, tu y mettra à la fin au moment où on commence la game
	public void gameStart() {
		SQLInterface sql = new SQLInterface(main.getDataFolder().getAbsolutePath());
		int idPartie = sql.addPartie(Date.valueOf(LocalDate.now()), null, money, isRanked, mapName);
		for(Player guard : guards.keySet()) {
			new PlayerGame(name, sql, guard.getUniqueId(), idPartie, Team.GARDE);
		}
		for(Player thief : thiefs.keySet()) {
			new PlayerGame(name, sql, thief.getUniqueId(), idPartie, Team.VOLEUR);
		}
	}
}

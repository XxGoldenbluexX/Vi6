package fr.nekotine.vi6;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.sql.PlayerGame;
import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.wrappers.GuardWrapper;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class Game implements Listener{
	private final Vi6Main main;
	private int idPartie;
	private String startTime;
	
	private final String name;
	private boolean isRanked=true;
	private GameState state = GameState.Waiting;
	private final HashMap<Player,PlayerWrapper> playerList = new HashMap<Player,PlayerWrapper>();
	
	private String mapName;
	private int money;
	
	public Game(Vi6Main main, String name) {
		this.main=main;
		this.name=name;
		Bukkit.getPluginManager().registerEvents(this, main);
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
			playerList.put(p, new PlayerWrapper(p));
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
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(String.format(DisplayTexts.getMessage("game.leave"), p.getName()));
			}
			return true;
		}
		return false;
	}

	public HashMap<Player, PlayerWrapper> getPlayerList() {
		return playerList;
	}
	
	public Team getPlayerTeam(Player p) {
		PlayerWrapper w = playerList.get(p);
		if (w!=null) return w.getTeam();
		return null;
	}
	
	//je met ça là, tu y mettra à la fin au moment où on commence la game!
	public void gameStart() {
		idPartie = SQLInterface.addPartie(Date.valueOf(LocalDate.now()), null, money, isRanked, mapName);
		for(Entry<Player, PlayerWrapper> playerAndTeam : playerList.entrySet()) {
			Bukkit.getPluginManager().registerEvents(new PlayerGame(name, playerAndTeam.getKey().getUniqueId(), idPartie, playerAndTeam.getValue().getTeam()), main);
		}
		startTime = LocalTime.now().toString();
	}
	
	public void gameEnd() {
		try {
			SQLInterface.updatePartie(idPartie, new Time(SQLInterface.getTimeFormat().parse(LocalTime.now().toString()).getTime() - SQLInterface.getTimeFormat().parse(startTime).getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}

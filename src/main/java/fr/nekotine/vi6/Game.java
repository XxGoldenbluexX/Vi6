package fr.nekotine.vi6;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.events.IsRankedChangeEvent;
import fr.nekotine.vi6.events.MapChangeEvent;
import fr.nekotine.vi6.events.MoneyChangedEvent;
import fr.nekotine.vi6.events.PlayerJoinGameEvent;
import fr.nekotine.vi6.events.PlayerLeaveGameEvent;
import fr.nekotine.vi6.interfaces.inventories.GameMoneyAnvil;
import fr.nekotine.vi6.interfaces.inventories.GameSettingsInventory;
import fr.nekotine.vi6.interfaces.inventories.MapSelectionInventory;
import fr.nekotine.vi6.interfaces.items.OpenWaitingItem;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.sql.PlayerGame;
import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.utils.MessageFormater;
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
	
	private MapSelectionInventory mapInterface;
	private GameSettingsInventory settingsInterface;
	
	public Game(Vi6Main main, String name) {
		this.main=main;
		this.name=name;
		new OpenWaitingItem(main, this);
		settingsInterface = new GameSettingsInventory(main, this);
		mapInterface = new MapSelectionInventory(main, this);
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	public boolean isRanked() {
		return isRanked;
	}
	
	public void openSettings(Player player) {
		player.openInventory(settingsInterface.inventory);
	}
	
	public GameSettingsInventory getSettingsInterface() {
		return settingsInterface;
	}
	
	public void openMoney(Player player) {
		new GameMoneyAnvil(main,this, player);
	}
	
	public void showCaptureMessage(Artefact a,PlayerWrapper p) {
		for (PlayerWrapper w : playerList.values()) {
			if (w.getTeam()==Team.GARDE) {
				w.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('§',DisplayTexts.getMessage("game*artefact*steal*guard")),"", 5, 20, 20);
			}else {
				w.getPlayer().sendTitle(MessageFormater.formatWithColorCodes('&',DisplayTexts.getMessage("game*artefact*steal*voleur"),
						new MessageFormater("&v",a.getName()),new MessageFormater("&p",p.getPlayer().getName())),"", 5, 20, 20);
			}
		}
	}
	
	public void openMapSelection(Player player) {
		player.openInventory(mapInterface.inventory);
	}
	
	public void setRanked(boolean isRanked) {
		this.isRanked = isRanked;
		Bukkit.getPluginManager().callEvent(new IsRankedChangeEvent(this,isRanked));
	}

	public String getName() {
		return name;
	}

	public GameState getState() {
		return state;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void setMoney(int money) {
		this.money=money;
		Bukkit.getPluginManager().callEvent(new MoneyChangedEvent(this,money));
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public void setMapName(String mapName) {
		this.mapName=mapName;
		Bukkit.getPluginManager().callEvent(new MapChangeEvent(mapName, this));
	}
	
	public boolean startGame() {
		for(PlayerWrapper wrapper : playerList.values()) {
			if(!wrapper.isReady()) return false;
		}
		for(PlayerWrapper w : playerList.values()) {
			w.clearStatusEffects();
			w.getStealedArtefactList().clear();
		}
		return true;
	}
	
	public boolean addPlayer(Player p) {
		if (!playerList.keySet().contains(p)) {
			playerList.put(p, new PlayerWrapper(p));
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(MessageFormater.formatWithColorCodes('&',DisplayTexts.getMessage("game*join"),new MessageFormater("&p",p.getName())));
			}
			p.getInventory().clear();
			Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(this, p));
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(Player p) {
		if (playerList.keySet().contains(p)) {
			playerList.remove(p);
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(String.format(DisplayTexts.getMessage("game*leave"), p.getName()));
			}
			Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, p));
			return true;
		}
		return false;
	}

	public HashMap<Player, PlayerWrapper> getPlayerMap() {
		return playerList;
	}
	
	public Set<Player> getPlayerList(){
		return playerList.keySet();
	}
	
	public Team getPlayerTeam(Player p) {
		PlayerWrapper w = playerList.get(p);
		if (w!=null) return w.getTeam();
		return null;
	}
	
	//je met ï¿½a lï¿½, tu y mettra ï¿½ la fin au moment oï¿½ on commence la game!
	public void gameStart() {
		for(Entry<Player, PlayerWrapper> playerAndTeam : playerList.entrySet()) {
			Bukkit.getPluginManager().registerEvents(new PlayerGame(name, playerAndTeam.getKey().getUniqueId(), idPartie, playerAndTeam.getValue().getTeam()), main);
		}
		startTime = LocalTime.now().toString();
	}
	
	public PlayerWrapper getWrapper(Player p) {
		return playerList.get(p);
	}
	
	public void gameEnd() {
		try {
			idPartie = SQLInterface.addPartie(Date.valueOf(LocalDate.now()), new Time(SQLInterface.getTimeFormat().parse(LocalTime.now().toString()).getTime() - SQLInterface.getTimeFormat().parse(startTime).getTime()), money, isRanked, mapName);
			Bukkit.getPluginManager().callEvent(new GameEndEvent(this, idPartie));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}

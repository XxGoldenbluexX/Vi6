package fr.nekotine.vi6;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.events.GameEnterInGamePhaseEvent;
import fr.nekotine.vi6.events.GameEnterPreparationPhaseEvent;
import fr.nekotine.vi6.events.IsRankedChangeEvent;
import fr.nekotine.vi6.events.MapChangeEvent;
import fr.nekotine.vi6.events.MoneyChangedEvent;
import fr.nekotine.vi6.events.PlayerJoinGameEvent;
import fr.nekotine.vi6.events.PlayerLeaveGameEvent;
import fr.nekotine.vi6.interfaces.inventories.GameMoneyAnvil;
import fr.nekotine.vi6.interfaces.inventories.GameSettingsInventory;
import fr.nekotine.vi6.interfaces.inventories.MapSelectionInventory;
import fr.nekotine.vi6.interfaces.items.OpenPreparationItem;
import fr.nekotine.vi6.interfaces.items.OpenWaitingItem;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.objet.Objet;
import fr.nekotine.vi6.sql.PlayerGame;
import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class Game implements Listener{
	private static final int DEFAULT_RANKED_MONEY = 1000;
	private static final int DEFAULT_PREPARATION_SECONDS = 2*60;
	
	private final Vi6Main main;
	private int idPartie;
	private String startTime;
	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective scoreboardSidebar;
	private final String name;
	private boolean isRanked=true;
	private GameState state = GameState.Waiting;
	private final HashMap<Player,PlayerWrapper> playerList = new HashMap<Player,PlayerWrapper>();
	
	private String mapName;
	private Carte map;
	private int money=DEFAULT_RANKED_MONEY;
	
	private MapSelectionInventory mapInterface;
	private GameSettingsInventory settingsInterface;
	
	private final ArrayList<Integer> nbtCompteur = new ArrayList<>();
	private final ArrayList<Objet> objetsList = new ArrayList<>();
	
	private final BossBar bb = Bukkit.createBossBar(ChatColor.GOLD+"Temps restant"+ChatColor.WHITE+": "+ChatColor.AQUA+DEFAULT_PREPARATION_SECONDS/60+ChatColor.WHITE+":"+
			ChatColor.AQUA+DEFAULT_PREPARATION_SECONDS%60, BarColor.BLUE, BarStyle.SOLID);
	private BukkitTask ticker;
	public Game(Vi6Main main, String name) {
		this.main=main;
		this.name=name;
		new OpenWaitingItem(main, this);
		settingsInterface = new GameSettingsInventory(main, this);
		mapInterface = new MapSelectionInventory(main, this);
		nbtCompteur.add(0);
		Bukkit.getPluginManager().registerEvents(this, main);
		scoreboardSidebar = scoreboard.registerNewObjective("sidebar", "dummy", ChatColor.GOLD+name);
		scoreboardSidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		
	}
	
	public boolean isRanked() {
		return isRanked;
	}
	
	public int getNBT() {
		if(nbtCompteur.size()>1) {
			return nbtCompteur.remove(1);
		}
		nbtCompteur.set(0, nbtCompteur.get(0)+1);
		return nbtCompteur.get(0);
	}
	
	public void addObjet(Objet obj) {
		objetsList.add(obj);
	}
	
	public void removeObjet(Objet obj) {
		nbtCompteur.add(obj.itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, getName()+"ObjetNBT"), PersistentDataType.INTEGER));
		Collections.sort(nbtCompteur.subList(1, nbtCompteur.size()));
		objetsList.remove(obj);
	}
	
	public Objet getObjet(ItemStack item) {
		for(Objet obj : objetsList) {
			if(item.equals(obj.itemStack)) {
				return obj;
			}
		}
		return null;
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
			String message="";
			if (w.getTeam()==Team.GARDE) {
				message = ChatColor.translateAlternateColorCodes('§',DisplayTexts.getMessage("game_artefact_steal_guard"));
				w.getPlayer().sendTitle(message,"", 5, 20, 20);
				w.getPlayer().sendMessage(message);
			}else {
				message = MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_artefact_steal_thief"),
						new MessageFormater("§v",a.getDisplayName()),new MessageFormater("§p",p.getPlayer().getName()));
				w.getPlayer().sendTitle(message,"", 5, 20, 20);
				w.getPlayer().sendMessage(message);
			}
		}
	}
	
	public void openMapSelection(Player player) {
		player.openInventory(mapInterface.inventory);
	}
	
	public void setRanked(boolean isRanked) {
		this.isRanked = isRanked;
		if(isRanked) {
			setMoney(DEFAULT_RANKED_MONEY);
		}
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
	
	public static int getDefaultRankedMoney() {
		return DEFAULT_RANKED_MONEY;
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
	
	public void destroy() {
		endGame();
		if (map!=null) {map.unload();map=null;}
		HandlerList.unregisterAll(this);
		scoreboardSidebar.unregister();
		scoreboard=null;
	}
	
	public boolean isEveryoneReady() {
		for(PlayerWrapper wrapper : playerList.values()) {
			if(!wrapper.isReady()) return false;
		}
		return true;
	}
	public void setReady(Player player, boolean isReady) {
		PlayerWrapper wrap = getWrapper(player);
		if(wrap==null) return;
		wrap.setReady(isReady);
		if(isReady && state==GameState.Preparation) {
			if(isEveryoneReady()) {
				enterInGamePhase();
			}
		}
	}
	
	public boolean enterPreparationPhase() {//START------------------
		if(!isEveryoneReady()) return false;
		if (map!=null) {map.unload();map=null;}
		map = Carte.load(mapName);
		if (map==null) return false;
		map.setGame(this);
		map.enable(main);
		map.start();
		state=GameState.Preparation;
		new OpenPreparationItem(main, this);
		for(Entry<Player, PlayerWrapper> playerAndWrapper : playerList.entrySet()) {
			Player player = playerAndWrapper.getKey();
			PlayerWrapper wrapper = playerAndWrapper.getValue();
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false, false));
			wrapper.setReady(false);
			wrapper.setMoney(money);
			wrapper.clearStatusEffects();
			wrapper.getStealedArtefactList().clear();
			bb.addPlayer(playerAndWrapper.getKey());
			if (wrapper.getTeam()==Team.GARDE) {
				player.teleport(map.getGuardSpawn());
			}else {
				player.teleport(map.getMinimapSpawn());
			}
			Bukkit.getPluginManager().registerEvents(new PlayerGame(name, playerAndWrapper.getKey().getUniqueId(), idPartie, playerAndWrapper.getValue().getTeam()), main);
		}
		ticker = new BukkitRunnable() {
			int seconds = DEFAULT_PREPARATION_SECONDS;
			@Override
			public void run() {
				seconds--;
				bb.setProgress(seconds / (double)DEFAULT_PREPARATION_SECONDS);
				bb.setTitle(ChatColor.GOLD+"Temps restant"+ChatColor.WHITE+": "+ChatColor.AQUA+seconds/60+"m"+ChatColor.WHITE+":"+
						ChatColor.AQUA+seconds%60+"s");
				if(seconds==0) {
					enterInGamePhase();
				}
			}
		}.runTaskTimer(main, 0, 20);
		Bukkit.getPluginManager().callEvent(new GameEnterPreparationPhaseEvent(this));
		return true;
	}
	
	public void enterInGamePhase() {
		bb.removeAll();
		ticker.cancel();
		startTime = LocalTime.now().toString();
		for(Entry<Player, PlayerWrapper> playerAndWrapper : playerList.entrySet()) {
			Player player = playerAndWrapper.getKey();
			PlayerWrapper wrapper = playerAndWrapper.getValue();
			player.teleport(wrapper.getThiefSpawnPoint());
		};
		state=GameState.Ingame;
		ticker = new BukkitRunnable() {
			@Override
			public void run() {
				for(Objet obj : objetsList) {
					obj.tick();
				}
				for(Artefact art : map.getArtefactList()) {
					art.tick(Game.this);
				}
			}
		}.runTaskTimer(main, 0, 1);
		Bukkit.getPluginManager().callEvent(new GameEnterInGamePhaseEvent(this));
	}
	
	
	public boolean endGame() {
		if (state==GameState.Waiting) return false;
		ticker.cancel();
		state=GameState.Waiting;
		return false;
	}
	
	public boolean addPlayer(Player p) {
		if (state==GameState.Waiting && !playerList.keySet().contains(p)) {
			p.setScoreboard(scoreboard);
			playerList.put(p, new PlayerWrapper(p));
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_join"),new MessageFormater("§p",p.getName())));
			}
			p.getInventory().clear();
			Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(this, p));
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(Player p) {
		if (playerList.keySet().contains(p)) {
			endGame();
			playerList.remove(p);
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(String.format(DisplayTexts.getMessage("game_leave"), p.getName()));
			}
			p.getInventory().clear();
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
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
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		removePlayer(e.getPlayer());
	}
}
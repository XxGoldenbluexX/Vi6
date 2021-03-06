package fr.nekotine.vi6;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.PlayerState;
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
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.sql.PlayerGame;
import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.statuseffects.ItemHider;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.util.Ticks;

public class Game implements Listener{
	private static int DEFAULT_RANKED_MONEY;
	private static int DEFAULT_PREPARATION_TIME;
	private static int DEFAULT_CAPTURE_DELAY;
	private final Vi6Main main;
	private int idPartie;
	private String startTime;
	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective scoreboardSidebar;
	private final String name;
	private boolean isRanked=true;
	private boolean canCapture=true;
	private int canCaptureRest=0;
	private GameState state = GameState.Waiting;
	private final HashMap<Player,PlayerWrapper> playerList = new HashMap<Player,PlayerWrapper>();
	
	private String mapName;
	private Carte map;
	private int money;
	
	private MapSelectionInventory mapInterface;
	private GameSettingsInventory settingsInterface;
	
	public static final ItemStack GUARD_SWORD = new ItemStack(Material.DIAMOND_SWORD);
	static{
		ItemMeta meta = GUARD_SWORD.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,new AttributeModifier("pvp_1.8",1000,Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,new AttributeModifier("diamondSwordDamages",7,Operation.ADD_NUMBER));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_UNBREAKABLE);
		meta.displayName(Component.text("Épée de garde").color(NamedTextColor.GOLD));
		GUARD_SWORD.setItemMeta(meta);
	}
	
	private final ArrayList<Integer> nbtCompteur = new ArrayList<>();
	private final ArrayList<Objet> objetsList = new ArrayList<>();
	
	private final BossBar bb = Bukkit.createBossBar(ChatColor.GOLD+"Temps restant"+ChatColor.WHITE+": "+ChatColor.AQUA+DEFAULT_PREPARATION_TIME/60+ChatColor.WHITE+":"+
			ChatColor.AQUA+DEFAULT_PREPARATION_TIME%60, BarColor.BLUE, BarStyle.SOLID);
	private BukkitTask bossBarTicker;
	private BukkitTask gameTicker;
	private int scanTime;
	private int defaultScanTime;
	private int scanTimer=0;
	public Game(Vi6Main main, String name) {
		this.main=main;
		this.name=name;
		DEFAULT_RANKED_MONEY = main.getConfig().getInt("rankedMoney",1000);
		DEFAULT_PREPARATION_TIME = main.getConfig().getInt("preparationTime",2*60);
		DEFAULT_CAPTURE_DELAY = main.getConfig().getInt("delayBetweenCapture",20*30);
		money=DEFAULT_RANKED_MONEY;
		new OpenWaitingItem(main, this);
		settingsInterface = new GameSettingsInventory(main, this);
		mapInterface = new MapSelectionInventory(main, this);
		nbtCompteur.add(0);
		Bukkit.getPluginManager().registerEvents(this, main);
		scoreboardSidebar = scoreboard.registerNewObjective("sidebar", "dummy", Component.text(name).color(NamedTextColor.GOLD));
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
		nbtCompteur.add(obj.getItemStack().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, getName()+"ObjetNBT"), PersistentDataType.INTEGER));
		Collections.sort(nbtCompteur.subList(1, nbtCompteur.size()));
		objetsList.remove(obj);
	}
	
	public Objet getObjet(ItemStack item) {
		for(Objet obj : objetsList) {
			if(item.equals(obj.getItemStack())) {
				return obj;
			}
		}
		return null;
	}
	
	public ArrayList<Objet> getObjets(){
		return objetsList;
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
		final TextComponent msgGuard = MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_artefact_steal_guard"));
		final TextComponent msgVoleur = MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_artefact_steal_thief"),
				new MessageFormater("§a",a.getDisplayName()),new MessageFormater("§p",p.getPlayer().getName()));
		Times titleTimes = Title.Times.of(Ticks.duration(5), Ticks.duration(20), Ticks.duration(20));
		Title titleGuard = Title.title(msgGuard, Component.text(""),titleTimes);
		Title titleVoleur = Title.title(msgVoleur, Component.text(""),titleTimes);
		for (PlayerWrapper w : playerList.values()) {
			if (w.getTeam()==Team.GARDE) {
				w.getPlayer().showTitle(titleGuard);
				w.getPlayer().sendMessage(msgGuard);
			}else {
				w.getPlayer().showTitle(titleVoleur);
				w.getPlayer().sendMessage(msgVoleur);
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
	
	public static int getDefaultPreparationSeconds() {
		return DEFAULT_PREPARATION_TIME;
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
		HandlerList.unregisterAll(mapInterface);
		HandlerList.unregisterAll(settingsInterface);
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
			wrapper.setState(PlayerState.PREPARATION);
			bb.addPlayer(playerAndWrapper.getKey());
			if (wrapper.getTeam()==Team.GARDE) {
				player.teleport(map.getGuardSpawn());
				PlayerInventory inv = player.getInventory();
				inv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
				inv.setItem(0, GUARD_SWORD);
			}else {
				player.teleport(map.getMinimapSpawn());
				for(Entry<Player, PlayerWrapper> p : playerList.entrySet()) {
					if(p.getValue().getTeam()==Team.GARDE) {
						p.getKey().hidePlayer(main, player);
					}
				}
			}
			playerAndWrapper.getKey().sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_preparation_start")));
		}
		bossBarTicker = new BukkitRunnable() {
			int seconds = DEFAULT_PREPARATION_TIME;
			@Override
			public void run() {
				seconds--;
				bb.setProgress(seconds / (double)DEFAULT_PREPARATION_TIME);
				bb.setTitle(ChatColor.GOLD+"Temps restant"+ChatColor.WHITE+": "+ChatColor.AQUA+seconds/60+"m"+ChatColor.WHITE+":"+
						ChatColor.AQUA+seconds%60+"s");
				if(seconds==0) {
					enterInGamePhase();
				}
			}
		}.runTaskTimer(main, 0, 20);
		gameTicker = new BukkitRunnable() {
			@Override
			public void run() {
				ingameTick();
			}
		}.runTaskTimer(main, 0, 1);
		Bukkit.getPluginManager().callEvent(new GameEnterPreparationPhaseEvent(this));
		return true;
	}
	
	@Nullable
	public Carte getMap() {
		return map;
	}
	
	public void enterInGamePhase() {
		bb.removeAll();
		bossBarTicker.cancel();
		startTime = LocalTime.now().toString();
		scoreboardSidebar.setDisplaySlot(null);
		defaultScanTime=main.getConfig().getInt("scanDelay", 600);
		scanTime=main.getConfig().getInt("reducedScanDelay", defaultScanTime);
		for(Entry<Player, PlayerWrapper> playerAndWrapper : playerList.entrySet()) {
			Player player = playerAndWrapper.getKey();
			PlayerWrapper wrapper = playerAndWrapper.getValue();
			wrapper.clearStatusEffects();
			wrapper.getStealedArtefactList().clear();
			if(wrapper.getTeam()==Team.VOLEUR){
				wrapper.setState(PlayerState.ENTERING);
				if(wrapper.getThiefSpawnPoint()==null) wrapper.setThiefSpawnPoint(map.getThiefSpawnsList().get(0).getMapLocation());
				player.teleport(wrapper.getThiefSpawnPoint());
				playerAndWrapper.getKey().sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_ingame_start_voleur")));
				
			}else {
				wrapper.setState(PlayerState.INSIDE);
				ItemHider.get().hideFromPlayer(player);
				playerAndWrapper.getKey().sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_ingame_start_garde")));
			}
			Bukkit.getPluginManager().registerEvents(new PlayerGame(name, playerAndWrapper.getKey().getUniqueId(), playerAndWrapper.getValue().getTeam()), main);
		}
		state=GameState.Ingame;
		Bukkit.getPluginManager().callEvent(new GameEnterInGamePhaseEvent(this));
	}
	
	public void ingameTick() {
		Iterator<Objet> ite = objetsList.iterator();
		while (ite.hasNext()) {
			ite.next().ticks();
		}
		if(state==GameState.Ingame) {
			if(!canCapture) {
				canCaptureRest--;
				if(canCaptureRest<=0) {
					canCapture=true;
					for(Entry<Player, PlayerWrapper> p : playerList.entrySet()) {
						if(p.getValue().getTeam()==Team.VOLEUR && p.getValue().isCanCapture()) {
							p.getKey().sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_player_canCapture")));
						}
					}
				}
			}
			for(Artefact art : map.getArtefactList()) {
				art.tick(this);
			}
			scanTimer++;
			if (scanTimer>=scanTime) {
				scan();
				scanTimer=0;
			}
		}
	}
	
	public void setScanTime(int scanTime) {
		this.scanTime=scanTime;
	}
	
	public int getDefaultScanTime() {
		return defaultScanTime;
	}
	
	public int getScanTime() {
		return scanTime;
	}
	
	public void scan() {
		ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
		ArrayList<Integer> idList = new ArrayList<>();
		for (Player p : playerList.keySet()) {
			if (playerList.get(p).getTeam()==Team.GARDE) {
				int entityID = (int)(Math.random() * Integer.MAX_VALUE);
				idList.add(entityID);
				///CREATE PACKET
				Location pLoc = p.getLocation();
				PacketContainer createPacket = pmanager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
				// Entity ID
				createPacket.getIntegers().write(0, entityID);
				createPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
		        // Entity Type
				createPacket.getIntegers().write(6, 78);
		        // Set optional velocity (/8000)
				createPacket.getIntegers().write(1, 0);
				createPacket.getIntegers().write(2, 0);
				createPacket.getIntegers().write(3, 0);
		        // Set yaw pitch
				createPacket.getIntegers().write(4, (int)pLoc.getPitch());
				createPacket.getIntegers().write(5, (int)pLoc.getYaw());
		        // Set object data
				createPacket.getIntegers().write(6, 0);
		        // Set location
				createPacket.getDoubles().write(0, pLoc.getX());
				createPacket.getDoubles().write(1, pLoc.getY());
				createPacket.getDoubles().write(2, pLoc.getZ());
		        // Set UUID
				createPacket.getUUIDs().write(0, UUID.randomUUID());
				///METADATA PACKET
				PacketContainer metadataPacket = pmanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
				//Entity ID
				metadataPacket.getIntegers().write(0, entityID);
				//Watcher
				WrappedDataWatcher watcher = new WrappedDataWatcher();
				Serializer serializer = Registry.get(Byte.class);
				watcher.setObject(0, serializer,(byte)(0x40|0x20));
				watcher.setObject(14, serializer,(byte)(0x08|0x04));
				metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
				///EQUIPMENT PACKET
				PacketContainer equipPacket = pmanager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
				equipPacket.getIntegers().write(0, entityID);
				List<Pair<ItemSlot, ItemStack>> pairList = new ArrayList<>();
				pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, new ItemStack(Material.NETHERITE_CHESTPLATE)));
				equipPacket.getSlotStackPairLists().write(0, pairList);
				sendPacketToTeam(Team.VOLEUR, createPacket, metadataPacket,equipPacket);
			}
			p.playSound(p.getLocation().add(0, 3, 0), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1, 1);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				packet.getIntegerArrays().write(0, idList.stream().mapToInt(Integer::intValue).toArray());
				for (Player p : playerList.keySet()) {
					if (playerList.get(p).getTeam()==Team.VOLEUR) {
						try {
							pmanager.sendServerPacket(p, packet);
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.runTaskLater(main, 140);
	}
	
	private void sendPacketToTeam(Team team,PacketContainer... packet) {
		ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
		for (Player pp : playerList.keySet()) {
			if (playerList.get(pp).getTeam()==team) {
				for (PacketContainer p : packet) {
					try {
						pmanager.sendServerPacket(pp, p);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public boolean endGame() {
		if (state==GameState.Waiting) return false;
		if(state==GameState.Ingame) {
			try {
				idPartie = SQLInterface.addPartie(Date.valueOf(LocalDate.now()), new Time(SQLInterface.getTimeFormat().parse(LocalTime.now().toString()).getTime() - SQLInterface.getTimeFormat().parse(startTime).getTime()), money, isRanked, mapName);
			} catch (ParseException e) {
				idPartie=-1;
				e.printStackTrace();
			}
		}
		for(Objet obj : objetsList) {
			obj.gameEnd();
		}
		scoreboardSidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		if(bb.getPlayers().size()>0) bb.removeAll();
		if(main.isEnabled()) new OpenWaitingItem(main, this);
		int totalVole=0;
		for(Entry<Player, PlayerWrapper> p : playerList.entrySet()) {
			p.getKey().setGameMode(GameMode.SPECTATOR);
			p.getValue().setReady(false);
			p.getValue().setState(PlayerState.WAITING);
			p.getValue().clearStatusEffects();
			p.getValue().setCanCapture(false);
			p.getValue().setCanEscape(false);
			p.getValue().setThiefSpawnPoint(null);
			p.getKey().setWalkSpeed(0.2f);
			p.getKey().getInventory().clear();
			if (p.getValue().getTeam()==Team.GARDE) {
				ItemHider.get().unHideFromPlayer(p.getKey());
			}else {
				totalVole+=p.getValue().getStealedArtefactList().size();
			}
		}
		for(Player p : playerList.keySet()) {
			p.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_end"), 
					new MessageFormater("§n", String.valueOf(totalVole))));
		}
		gameTicker.cancel();
		bossBarTicker.cancel();
		state=GameState.Waiting;
		Bukkit.getPluginManager().callEvent(new GameEndEvent(this, idPartie));
		return true;
	}
	
	public boolean addPlayer(Player p) {
		if (main.getPlayerWrapper(p)==null && state==GameState.Waiting && !playerList.keySet().contains(p)) {
			p.setScoreboard(scoreboard);
			playerList.put(p, new PlayerWrapper(this, p));
			playerList.get(p).setState(PlayerState.WAITING);
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_join"),
						new MessageFormater("§p",p.getName()),new MessageFormater("§g",name)));
			}
			p.setWalkSpeed(0.2f);
			p.getInventory().clear();
			Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(this, p));
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(Player p) {
		if (playerList.keySet().contains(p)) {
			endGame();
			for (Player pl : playerList.keySet()) {
				pl.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_leave"),
						new MessageFormater("§p",p.getName()),new MessageFormater("§g",name)));
			}
			playerList.get(p).destroy();
			playerList.remove(p);
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
	
	public HashMap<Player, PlayerWrapper> getPlayerList(){
		return playerList;
	}
	
	public Team getPlayerTeam(Player p) {
		PlayerWrapper w = playerList.get(p);
		if (w!=null) return w.getTeam();
		return null;
	}
	
	public PlayerWrapper getWrapper(Player p) {
		return playerList.get(p);
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		removePlayer(e.getPlayer());
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		if(playerList.containsKey(e.getEntity()) && playerList.get(e.getEntity()).getTeam()==Team.VOLEUR 
				&& playerList.get(e.getEntity()).getState()==PlayerState.INSIDE) {
			playerList.get(e.getEntity()).setState(PlayerState.LEAVED);
			e.getEntity().setGameMode(GameMode.SPECTATOR);
			for(Entry<Player, PlayerWrapper> p : playerList.entrySet()) {
				p.getKey().sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_death"), 
						new MessageFormater("§p", String.valueOf(e.getEntity().getName())), 
						new MessageFormater("§n", String.valueOf(playerList.get(e.getEntity()).getStealedArtefactList().size()))));
			}
			if(!isThiefLeft()) {
				endGame();
			}
		}
	}
	public boolean isThiefLeft() {
		for(PlayerWrapper wrap : playerList.values()) {
			if(wrap.getTeam()==Team.VOLEUR && (wrap.getState()==PlayerState.ENTERING || wrap.getState()==PlayerState.INSIDE)) {
				return true;
			}
		}
		return false;
	}
	public void capture() {
		canCaptureRest=DEFAULT_CAPTURE_DELAY;
		canCapture=false;
	}

	public boolean isCanCapture() {
		return canCapture;
	}
}
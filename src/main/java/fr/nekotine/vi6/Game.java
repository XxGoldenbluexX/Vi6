package fr.nekotine.vi6;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
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
import fr.nekotine.vi6.map.SpawnVoleur;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.sql.PlayerGame;
import fr.nekotine.vi6.sql.SQLInterface;
import fr.nekotine.vi6.statuseffects.ItemHider;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
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
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import ru.xezard.glow.data.glow.Glow;

public class Game implements Listener {
	
	private static int DEFAULT_RANKED_MONEY;
	private static int DEFAULT_PREPARATION_TIME;
	private static int DEFAULT_CAPTURE_DELAY;
	public static final ItemStack GUARD_SWORD = new ItemStack(Material.DIAMOND_SWORD);
	
	private final Vi6Main main;
	private int idPartie;
	private String startTime;
	private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private final Objective scoreboardSidebar;
	private final org.bukkit.scoreboard.Team noNameTag;
	private final String name;
	private boolean isRanked = true;
	private boolean canCapture = true;
	private int canCaptureRest = 0;
	private GameState state = GameState.Waiting;
	private final HashMap<Player, PlayerWrapper> playerList = new HashMap<>();
	private final ArrayList<Objet> objToRemove = new ArrayList<>();
	private String mapName;
	private Carte map;
	private int money;
	private MapSelectionInventory mapInterface;
	private GameSettingsInventory settingsInterface;
	private Glow guardGlow = Glow.builder().animatedColor(new ChatColor[]{ChatColor.BLUE}).name("guardGlow").build();
	private Glow thiefGlow = Glow.builder().animatedColor(new ChatColor[]{ChatColor.RED}).name("thiefGlow").build();
	private final ArrayList<Integer> nbtCompteur = new ArrayList<>();
	private final BossBar bb = Bukkit.createBossBar(
			"" + ChatColor.GOLD + "Temps restant" + ChatColor.GOLD + ": " + ChatColor.WHITE + ChatColor.AQUA
					+ DEFAULT_PREPARATION_TIME / 60 + ":" + ChatColor.WHITE + ChatColor.AQUA,
			BarColor.BLUE, BarStyle.SOLID, new org.bukkit.boss.BarFlag[0]);
	private BukkitTask bossBarTicker;
	private BukkitTask gameTicker;
	private int scanTime;
	private int defaultScanTime;
	private int scanTimer = 0;
	private final ArrayList<Objet> objetsList = new ArrayList<>();

	static {
		ItemMeta meta = GUARD_SWORD.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier("pvp_1.8", 1000.0D, AttributeModifier.Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier("diamondSwordDamages", 7.0D, AttributeModifier.Operation.ADD_NUMBER));
		meta.setUnbreakable(true);
		meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE});
		meta.displayName(Component.text("Épée de garde").color((TextColor) NamedTextColor.GOLD));
		GUARD_SWORD.setItemMeta(meta);
	}
	
	public Game(Vi6Main main, String name) {
		this.main = main;
		this.name = name;
		DEFAULT_RANKED_MONEY = main.getConfig().getInt("rankedMoney", 1000);
		DEFAULT_PREPARATION_TIME = main.getConfig().getInt("preparationTime", 120);
		DEFAULT_CAPTURE_DELAY = main.getConfig().getInt("delayBetweenCapture", 600);
		this.money = DEFAULT_RANKED_MONEY;
		new OpenWaitingItem(main, this);
		this.settingsInterface = new GameSettingsInventory(main, this);
		this.mapInterface = new MapSelectionInventory(main, this);
		this.nbtCompteur.add(Integer.valueOf(0));
		Bukkit.getPluginManager().registerEvents(this, (Plugin) main);
		this.scoreboardSidebar = this.scoreboard.registerNewObjective("sidebar", "dummy",
				Component.text(name).color((TextColor) NamedTextColor.GOLD));
		this.scoreboardSidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.noNameTag = this.scoreboard.registerNewTeam("noNameTag");
		this.noNameTag.setCanSeeFriendlyInvisibles(false);
		this.noNameTag.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY,org.bukkit.scoreboard.Team.OptionStatus.FOR_OTHER_TEAMS);
	}

	public boolean isRanked() {
		return this.isRanked;
	}

	public int getNBT() {
		if (this.nbtCompteur.size() > 1)
			return ((Integer) this.nbtCompteur.remove(1)).intValue();
		this.nbtCompteur.set(0, Integer.valueOf(((Integer) this.nbtCompteur.get(0)).intValue() + 1));
		return ((Integer) this.nbtCompteur.get(0)).intValue();
	}

	public void addObjet(Objet obj) {
		this.objetsList.add(obj);
	}

	public void removeObjet(Objet obj) {
		if (obj.isTickable()) {
			this.objToRemove.add(obj);
		} else {
			this.objetsList.remove(obj);
		}
		this.nbtCompteur.add((Integer) obj.getDisplayedItem().getItemMeta().getPersistentDataContainer()
				.get(new NamespacedKey((Plugin) this.main, getName() + "ObjetNBT"), PersistentDataType.INTEGER));
		Collections.sort(this.nbtCompteur.subList(1, this.nbtCompteur.size()));
	}

	public Objet getObjet(ItemStack item) {
		for (Objet obj : this.objetsList) {
			if (item.equals(obj.getDisplayedItem()))
				return obj;
		}
		return null;
	}

	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			PlayerWrapper w = getWrapper(p);
			if (w != null) {
				Objet o = getObjet(event.getItem().getItemStack());
				if (o != null)
					o.setNewOwner(p, w);
			}
		}
	}

	public ArrayList<Objet> getObjets() {
		return this.objetsList;
	}

	public void openSettings(Player player) {
		player.openInventory(this.settingsInterface.inventory);
	}

	public GameSettingsInventory getSettingsInterface() {
		return this.settingsInterface;
	}

	public void openMoney(Player player) {
		new GameMoneyAnvil(this.main, this, player);
	}

	public void showCaptureMessage(Artefact a, PlayerWrapper p) {
		TextComponent msgGuard = MessageFormater.formatWithColorCodes('§',
				DisplayTexts.getMessage("game_artefact_steal_guard"), new MessageFormater[0]);
		TextComponent msgVoleur = MessageFormater.formatWithColorCodes('§',
				DisplayTexts.getMessage("game_artefact_steal_thief"),
				new MessageFormater[]{new MessageFormater("§a", a.getDisplayName()),
						new MessageFormater("§p", (p != null) ? p.getPlayer().getName() : "inconnu")});
		Title.Times titleTimes = Title.Times.of(Ticks.duration(5L), Ticks.duration(20L), Ticks.duration(20L));
		Title titleGuard = Title.title((Component) msgGuard, (Component) Component.text(""), titleTimes);
		Title titleVoleur = Title.title((Component) msgVoleur, (Component) Component.text(""), titleTimes);
		for (PlayerWrapper w : this.playerList.values()) {
			if (w.getTeam() == Team.GARDE) {
				w.getPlayer().showTitle(titleGuard);
				w.getPlayer().sendMessage((Component) msgGuard);
				continue;
			}
			w.getPlayer().showTitle(titleVoleur);
			w.getPlayer().sendMessage((Component) msgVoleur);
		}
	}

	public void openMapSelection(Player player) {
		player.openInventory(this.mapInterface.inventory);
	}

	public void setRanked(boolean isRanked) {
		this.isRanked = isRanked;
		if (isRanked)
			setMoney(DEFAULT_RANKED_MONEY);
		Bukkit.getPluginManager().callEvent(new IsRankedChangeEvent(this, isRanked));
	}

	public String getName() {
		return this.name;
	}

	public GameState getState() {
		return this.state;
	}

	public int getMoney() {
		return this.money;
	}

	public static int getDefaultRankedMoney() {
		return DEFAULT_RANKED_MONEY;
	}

	public static int getDefaultPreparationSeconds() {
		return DEFAULT_PREPARATION_TIME;
	}

	public void setMoney(int money) {
		this.money = money;
		Bukkit.getPluginManager().callEvent(new MoneyChangedEvent(this, money));
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
		Bukkit.getPluginManager().callEvent(new MapChangeEvent(mapName, this));
	}

	public void destroy() {
		endGame(true);
		if (this.map != null) {
			this.map.unload();
			this.map = null;
		}
		HandlerList.unregisterAll(this.mapInterface);
		HandlerList.unregisterAll(this.settingsInterface);
		HandlerList.unregisterAll(this);
		this.noNameTag.unregister();
		this.scoreboardSidebar.unregister();
	}

	public boolean isEveryoneReady() {
		for (PlayerWrapper wrapper : this.playerList.values()) {
			if (!wrapper.isReady())
				return false;
		}
		return true;
	}

	public void setReady(Player player, boolean isReady) {
		PlayerWrapper wrap = getWrapper(player);
		if (wrap == null)
			return;
		wrap.setReady(isReady);
		if (isReady && this.state == GameState.Preparation && isEveryoneReady())
			enterInGamePhase();
	}

	public boolean enterPreparationPhase() {
		if (!isEveryoneReady())
			return false;
		ArrayList<Objet> temp = new ArrayList<>(this.objetsList);
		for (Objet o : temp)
			o.destroy();
		if (this.map == null) {
			this.map = Carte.load(this.mapName);
			this.map.setGame(this);
			this.map.enable(this.main);
		}
		if (this.map.getName() != this.mapName) {
			this.map.unload();
			this.map = Carte.load(this.mapName);
			this.map.setGame(this);
			this.map.enable(this.main);
		}
		if (this.map == null)
			return false;
		this.map.setGame(this);
		this.map.start();
		this.state = GameState.Preparation;
		new OpenPreparationItem(this.main, this);
		for (Map.Entry<Player, PlayerWrapper> playerAndWrapper : this.playerList.entrySet()) {
			Player player = playerAndWrapper.getKey();
			PlayerWrapper wrapper = playerAndWrapper.getValue();
			for (PotionEffectType effect : PotionEffectType.values())
				player.removePotionEffect(effect);
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2147483647, 0, false, false, false));
			this.noNameTag.addEntry(player.getName());
			wrapper.setReady(false);
			wrapper.setMoney(this.money);
			wrapper.setState(PlayerState.PREPARATION);
			this.bb.addPlayer(playerAndWrapper.getKey());
			if (wrapper.getTeam() == Team.GARDE) {
				player.teleport(this.map.getGuardSpawn());
				PlayerInventory inv = player.getInventory();
				inv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
				inv.setItem(0, GUARD_SWORD);
				this.guardGlow.addHolders(new Entity[]{(Entity) player});
				this.guardGlow.display(new Player[]{player});
			} else {
				player.addPotionEffect(
						new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, false, false, false));
				player.teleport(this.map.getMinimapSpawn());
				for (Map.Entry<Player, PlayerWrapper> p : this.playerList.entrySet()) {
					if (((PlayerWrapper) p.getValue()).getTeam() == Team.GARDE)
						((Player) p.getKey()).hidePlayer((Plugin) this.main, player);
				}
			}
			((Player) playerAndWrapper.getKey()).sendMessage((Component) MessageFormater.formatWithColorCodes('§',
					DisplayTexts.getMessage("game_preparation_start"), new MessageFormater[0]));
		}
		this

				.bossBarTicker = (new BukkitRunnable() {
					int seconds = Game.DEFAULT_PREPARATION_TIME;

					public void run() {
						this.seconds--;
						Game.this.bb.setProgress(this.seconds / Game.DEFAULT_PREPARATION_TIME);
						Game.this.bb.setTitle("" + ChatColor.GOLD + "Temps restant" + ChatColor.GOLD + ": "
								+ ChatColor.WHITE + ChatColor.AQUA + "m" + this.seconds / 60 + ":" + ChatColor.WHITE
								+ ChatColor.AQUA + "s");
						if (this.seconds == 0)
							Game.this.enterInGamePhase();
					}
				}).runTaskTimer((Plugin) this.main, 0L, 20L);
		this

				.gameTicker = (new BukkitRunnable() {
					public void run() {
						Game.this.ingameTick();
					}
				}).runTaskTimer((Plugin) this.main, 0L, 1L);
		Bukkit.getPluginManager().callEvent(new GameEnterPreparationPhaseEvent(this));
		return true;
	}

	@Nullable
	public Carte getMap() {
		return this.map;
	}

	public void enterInGamePhase() {
		this.bb.removeAll();
		this.bossBarTicker.cancel();
		this.startTime = LocalTime.now().toString();
		this.scoreboardSidebar.setDisplaySlot(null);
		this.defaultScanTime = this.main.getConfig().getInt("scanDelay", 600);
		this.scanTime = this.main.getConfig().getInt("reducedScanDelay", this.defaultScanTime);
		for (Map.Entry<Player, PlayerWrapper> playerAndWrapper : this.playerList.entrySet()) {
			Player player = playerAndWrapper.getKey();
			PlayerWrapper wrapper = playerAndWrapper.getValue();
			wrapper.clearStatusEffects();
			wrapper.getStealedArtefactList().clear();
			if (wrapper.getTeam() == Team.VOLEUR) {
				this.thiefGlow.addHolders(new Entity[]{(Entity) player});
				this.thiefGlow.display(new Player[]{player});
				wrapper.setState(PlayerState.ENTERING);
				if (wrapper.getThiefSpawnPoint() == null)
					wrapper.setThiefSpawnPoint(((SpawnVoleur) this.map.getThiefSpawnsList().get(0)).getMapLocation());
				player.teleport(wrapper.getThiefSpawnPoint());
				((Player) playerAndWrapper.getKey()).sendMessage((Component) MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("game_ingame_start_voleur"), new MessageFormater[0]));
			} else {
				wrapper.setState(PlayerState.INSIDE);
				ItemHider.get().hideFromPlayer(player);
				((Player) playerAndWrapper.getKey()).sendMessage((Component) MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("game_ingame_start_garde"), new MessageFormater[0]));
			}
			Bukkit.getPluginManager()
					.registerEvents(new PlayerGame(this.name, ((Player) playerAndWrapper.getKey()).getUniqueId(),
							((PlayerWrapper) playerAndWrapper.getValue()).getTeam()), (Plugin) this.main);
		}
		this.state = GameState.Ingame;
		Bukkit.getPluginManager().callEvent(new GameEnterInGamePhaseEvent(this));
	}

	public void ingameTick() {
		for (Objet o : this.objToRemove)
			this.objetsList.remove(o);
		for (Objet o : this.objetsList) {
			if (o.isTickable())
				o.ticks();
		}
		if (this.state == GameState.Ingame) {
			if (!this.canCapture) {
				this.canCaptureRest--;
				if (this.canCaptureRest <= 0) {
					this.canCapture = true;
					for (Map.Entry<Player, PlayerWrapper> p : this.playerList.entrySet()) {
						if (((PlayerWrapper) p.getValue()).getTeam() == Team.VOLEUR
								&& ((PlayerWrapper) p.getValue()).isCanCapture())
							((Player) p.getKey()).sendMessage((Component) MessageFormater.formatWithColorCodes('§',
									DisplayTexts.getMessage("game_player_canCapture"), new MessageFormater[0]));
					}
				}
			}
			for (Artefact art : this.map.getArtefactList())
				art.tick(this);
			this.scanTimer++;
			if (this.scanTimer >= this.scanTime) {
				scan();
				this.scanTimer = 0;
			}
		}
	}

	public void setScanTime(int scanTime) {
		this.scanTime = scanTime;
	}

	public int getDefaultScanTime() {
		return this.defaultScanTime;
	}

	public int getScanTime() {
		return this.scanTime;
	}

	public void scan() {
		final ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
		final ArrayList<Integer> idList = new ArrayList<>();
		for (Player p : this.playerList.keySet()) {
			if (((PlayerWrapper) this.playerList.get(p)).getTeam() == Team.GARDE) {
				int entityID = (int) (Math.random() * 2.147483647E9D);
				idList.add(Integer.valueOf(entityID));
				Location pLoc = p.getLocation();
				PacketContainer createPacket = pmanager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
				createPacket.getIntegers().write(0, Integer.valueOf(entityID));
				createPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
				createPacket.getIntegers().write(6, Integer.valueOf(78));
				createPacket.getIntegers().write(1, Integer.valueOf(0));
				createPacket.getIntegers().write(2, Integer.valueOf(0));
				createPacket.getIntegers().write(3, Integer.valueOf(0));
				createPacket.getIntegers().write(4, Integer.valueOf((int) pLoc.getPitch()));
				createPacket.getIntegers().write(5, Integer.valueOf((int) pLoc.getYaw()));
				createPacket.getIntegers().write(6, Integer.valueOf(0));
				createPacket.getDoubles().write(0, Double.valueOf(pLoc.getX()));
				createPacket.getDoubles().write(1, Double.valueOf(pLoc.getY()));
				createPacket.getDoubles().write(2, Double.valueOf(pLoc.getZ()));
				createPacket.getUUIDs().write(0, UUID.randomUUID());
				PacketContainer metadataPacket = pmanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
				metadataPacket.getIntegers().write(0, Integer.valueOf(entityID));
				WrappedDataWatcher watcher = new WrappedDataWatcher();
				WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
				watcher.setObject(0, serializer, Byte.valueOf((byte) 96));
				watcher.setObject(14, serializer, Byte.valueOf((byte) 12));
				metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
				PacketContainer equipPacket = pmanager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
				equipPacket.getIntegers().write(0, Integer.valueOf(entityID));
				List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = new ArrayList<>();
				pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, new ItemStack(Material.NETHERITE_CHESTPLATE)));
				equipPacket.getSlotStackPairLists().write(0, pairList);
				sendPacketToTeam(Team.VOLEUR, new PacketContainer[]{createPacket, metadataPacket, equipPacket});
			}
			p.playSound(p.getLocation().add(0.0D, 3.0D, 0.0D), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER,
					1.0F, 1.0F);
		}
		(new BukkitRunnable() {
			public void run() {
				PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				packet.getIntegerArrays().write(0, idList.stream().mapToInt(Integer::intValue).toArray());
				for (Player p : Game.this.playerList.keySet()) {
					if (((PlayerWrapper) Game.this.playerList.get(p)).getTeam() == Team.VOLEUR)
						try {
							pmanager.sendServerPacket(p, packet);
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
				}
			}
		}).runTaskLater((Plugin) this.main, 140L);
	}

	private void sendPacketToTeam(Team team, PacketContainer... packet) {
		ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
		for (Map.Entry<Player, PlayerWrapper> pp : this.playerList.entrySet()) {
			if (((PlayerWrapper) pp.getValue()).getTeam() == team)
				for (PacketContainer p : packet) {
					try {
						pmanager.sendServerPacket(pp.getKey(), p);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
		}
	}

	public boolean endGame(boolean forced) {
		if (this.state == GameState.Waiting)
			return false;
		if (this.state == GameState.Ingame && !forced)
			try {
				this.idPartie = SQLInterface.addPartie(Date.valueOf(LocalDate.now()),
						new Time(SQLInterface.getTimeFormat().parse(LocalTime.now().toString()).getTime()
								- SQLInterface.getTimeFormat().parse(this.startTime).getTime()),
						this.money, this.isRanked, this.mapName);
			} catch (ParseException e) {
				this.idPartie = -1;
				e.printStackTrace();
			}
		this.scoreboardSidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		if (this.bb.getPlayers().size() > 0)
			this.bb.removeAll();
		if (this.main.isEnabled())
			new OpenWaitingItem(this.main, this);
		int totalVole = 0;
		for (Map.Entry<Player, PlayerWrapper> p : this.playerList.entrySet()) {
			((Player) p.getKey()).setGameMode(GameMode.SPECTATOR);
			((PlayerWrapper) p.getValue()).setReady(false);
			((PlayerWrapper) p.getValue()).setState(PlayerState.WAITING);
			((PlayerWrapper) p.getValue()).clearStatusEffects();
			((PlayerWrapper) p.getValue()).setCanCapture(false);
			((PlayerWrapper) p.getValue()).setCanEscape(false);
			((PlayerWrapper) p.getValue()).setThiefSpawnPoint(null);
			this.noNameTag.removeEntry(((Player) p.getKey()).getName());
			((Player) p.getKey()).setWalkSpeed(0.2F);
			((Player) p.getKey()).getInventory().clear();
			if (((PlayerWrapper) p.getValue()).getTeam() == Team.GARDE) {
				this.guardGlow.removeHolders(new Entity[]{(Entity) p.getKey()});
				ItemHider.get().unHideFromPlayer(p.getKey());
				continue;
			}
			((Player) p.getKey()).removePotionEffect(PotionEffectType.NIGHT_VISION);
			this.thiefGlow.removeHolders(new Entity[]{(Entity) p.getKey()});
			for (Player player : this.playerList.keySet())
				player.showPlayer((Plugin) this.main, p.getKey());
			totalVole += ((PlayerWrapper) p.getValue()).getStealedArtefactList().size();
		}
		for (Player p : this.playerList.keySet()) {
			p.sendMessage((Component) MessageFormater.formatWithColorCodes('§', DisplayTexts.getMessage("game_end"),
					new MessageFormater[]{new MessageFormater("§n", String.valueOf(totalVole))}));
		}
		this.guardGlow.hideFromEveryone();
		this.thiefGlow.hideFromEveryone();
		this.gameTicker.cancel();
		this.bossBarTicker.cancel();
		this.state = GameState.Waiting;
		Bukkit.getPluginManager().callEvent(new GameEndEvent(this, this.idPartie, forced));
		return true;
	}

	public boolean addPlayer(Player p) {
		if (this.main.getPlayerWrapper(p) == null && this.state == GameState.Waiting
				&& !this.playerList.keySet().contains(p)) {
			p.setScoreboard(this.scoreboard);
			this.playerList.put(p, new PlayerWrapper(this, p));
			((PlayerWrapper) this.playerList.get(p)).setState(PlayerState.WAITING);
			for (Player pl : this.playerList.keySet()) {
				pl.sendMessage((Component) MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("game_join"), new MessageFormater[]{
								new MessageFormater("§p", p.getName()), new MessageFormater("§g", this.name)}));
			}
			p.setWalkSpeed(0.2F);
			p.getInventory().clear();
			Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(this, p));
			return true;
		}
		return false;
	}

	public boolean removePlayer(Player p) {
		if (this.playerList.keySet().contains(p)) {
			endGame(true);
			for (Player pl : this.playerList.keySet()) {
				pl.sendMessage((Component) MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("game_leave"), new MessageFormater[]{
								new MessageFormater("§p", p.getName()), new MessageFormater("§g", this.name)}));
			}
			((PlayerWrapper) this.playerList.get(p)).destroy();
			this.playerList.remove(p);
			p.getInventory().clear();
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, p));
			return true;
		}
		return false;
	}

	public HashMap<Player, PlayerWrapper> getPlayerMap() {
		return this.playerList;
	}

	public Team getPlayerTeam(Player p) {
		PlayerWrapper w = this.playerList.get(p);
		if (w != null)
			return w.getTeam();
		return null;
	}

	public PlayerWrapper getWrapper(Player p) {
		return this.playerList.get(p);
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		removePlayer(e.getPlayer());
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		if (this.playerList.containsKey(e.getEntity())
				&& ((PlayerWrapper) this.playerList.get(e.getEntity())).getTeam() == Team.VOLEUR
				&& ((PlayerWrapper) this.playerList.get(e.getEntity())).getState() == PlayerState.INSIDE) {
			((PlayerWrapper) this.playerList.get(e.getEntity())).setState(PlayerState.LEAVED);
			e.getEntity().setGameMode(GameMode.SPECTATOR);
			for (Map.Entry<Player, PlayerWrapper> p : this.playerList.entrySet()) {
				((Player) p.getKey()).sendMessage((Component) MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("game_death"),
						new MessageFormater[]{new MessageFormater("§p", String.valueOf(e.getEntity().getName())),
								new MessageFormater("§n",
										String.valueOf(((PlayerWrapper) this.playerList.get(e.getEntity()))
												.getStealedArtefactList().size()))}));
			}
			if (!isThiefLeft())
				endGame(false);
		}
	}

	public boolean isThiefLeft() {
		for (PlayerWrapper wrap : this.playerList.values()) {
			if (wrap.getTeam() == Team.VOLEUR
					&& (wrap.getState() == PlayerState.ENTERING || wrap.getState() == PlayerState.INSIDE))
				return true;
		}
		return false;
	}

	public void capture() {
		this.canCaptureRest = DEFAULT_CAPTURE_DELAY;
		this.canCapture = false;
	}

	public boolean isCanCapture() {
		return this.canCapture;
	}

	@EventHandler
	public void hitEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			PlayerWrapper damager = this.playerList.get(e.getDamager());
			PlayerWrapper damaged = this.playerList.get(e.getEntity());
			if (damager != null && damaged != null && damager.getTeam() == Team.GARDE
					&& damaged.getTeam() == Team.GARDE)
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (this.playerList.keySet().contains(e.getWhoClicked()) && e.getSlotType() == InventoryType.SlotType.ARMOR)
			e.setCancelled(true);
	}
}
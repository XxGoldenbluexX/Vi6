package fr.nekotine.vi6;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import fr.nekotine.vi6.commands.Vi6commandMaker;
import fr.nekotine.vi6.database.DatabaseManager;
import fr.nekotine.vi6.majordom.Majordom;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Camera;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.map.Entree;
import fr.nekotine.vi6.map.Gateway;
import fr.nekotine.vi6.map.Passage;
import fr.nekotine.vi6.map.Sortie;
import fr.nekotine.vi6.map.SpawnVoleur;
import fr.nekotine.vi6.statuseffects.ItemHider;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ExplosionCanceler;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

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
	public static Logger log;
	private static List<Game> gameList = new ArrayList<Game>(1);
	public static Vi6Main main;
	@Override
	public void onLoad() {
		super.onLoad();
		CommandAPI.onLoad(new CommandAPIConfig());
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		main=this;
		log = getLogger();
		//Register Serializables
		ConfigurationSerialization.registerClass(Entree.class, "Entree");
		ConfigurationSerialization.registerClass(Sortie.class, "Sortie");
		ConfigurationSerialization.registerClass(Passage.class, "Passage");
		ConfigurationSerialization.registerClass(Gateway.class, "Gateway");
		ConfigurationSerialization.registerClass(Artefact.class, "Artefact");
		ConfigurationSerialization.registerClass(Carte.class, "Carte");
		ConfigurationSerialization.registerClass(DetectionZone.class, "DetectionZone");
		ConfigurationSerialization.registerClass(SpawnVoleur.class, "SpawnVoleur");
		ConfigurationSerialization.registerClass(Camera.class, "Camera");
		pmanager=Bukkit.getPluginManager();//getting pmanager reference
		ProtocolManager promanager = ProtocolLibrary.getProtocolManager();
		pmanager.registerEvents(new Majordom(this),this);
		new ItemHider(promanager,this);
		new ExplosionCanceler(pmanager,this);
		//GLOW FOR TEAMS
		log.severe("UPDATE TEST SIGNAL");
		log.severe("UPDATE TEST SIGNAL 2");
		promanager.addPacketListener(new PacketAdapter(this,PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Player receiver = event.getPlayer();
				Player thrower = null;
				int hideid = packet.getIntegers().read(0);
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getEntityId()==hideid) {
						thrower = p;
						break;
					}
				}
				if (thrower==null || thrower==receiver) return;
				PlayerWrapper receiverWrapper = getPlayerWrapper(receiver);
				PlayerWrapper throwerWrapper = getPlayerWrapper(thrower);
				if (throwerWrapper!=null && receiverWrapper!=null && throwerWrapper.getTeam()==receiverWrapper.getTeam()) {
					PacketContainer newPacket = promanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
					WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(thrower).deepClone();
					WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
					byte bytemask = dataWatcher.getByte(0);
					bytemask |= 0x40;
					dataWatcher.setObject(0, byteSerializer, bytemask);
					newPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
					newPacket.getIntegers().write(0, thrower.getEntityId());
					event.setPacket(newPacket);
					/*
					List<WrappedWatchableObject> watchableObjectList = packet.getWatchableCollectionModifier().read(0);
					for (WrappedWatchableObject metadata : watchableObjectList) {
						if (metadata.getIndex() == 0) {
							byte b = (byte) metadata.getValue();
							byte before = b;
							boolean glow = false;
							if (throwerWrapper.getGlowTokens().stream().anyMatch((t)->{return t.viewer==receiverWrapper;})) {
								b |= 0b01000000;
								glow = true;
							}else {
								b &= ~(0b01000000);
							}
							byte after=b;
							metadata.setValue(b);
							Bukkit.broadcast(Component.text(receiver.getName()+" received "+thrower.getName()+" before="+before+" after="+after, glow?TextColor.color(255, 50, 50):TextColor.color(50, 50, 255)));
						}
					}*/
				}
			}
		});
		//File creation
		saveDefaultConfig();//making config.yml
		if (getDataFolder().exists()) {//making dataFolder
			getDataFolder().mkdir();
		}
		File mapf = new File(getDataFolder(),"Maps");//making map Folder
		if (!mapf.exists()){
				mapf.mkdir();
		}
		File dbfile = new File(main.getDataFolder(),"databaseConfig.yml");
		if (!dbfile.exists()) {
			main.saveResource("databaseConfig.yml", false);
		}
		DatabaseManager.loadConfig(dbfile);
		Carte.setMapFolder(mapf);
		DisplayTexts.instance.load(this);
		CommandAPI.onEnable(this);//enable CommandAPI
		Vi6commandMaker.makevi6(this).register();//registering commands
		
		createGame("Vi6");
		getGame("Vi6").setRanked(true);
		
	}
	
	public File getFolder() {
		return getFile();
	}
	
	@Override
	public void onDisable() {
		for(Game game : gameList) {
			game.destroy();
		}
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
		Game g = new Game(this,name);
		g.setTest(name.contentEquals("test"));
		gameList.add(g);
		return true;
	}
	
	public boolean removeGame(Game game) {
		if (!gameList.contains(game)) return false;
		game.destroy();
		gameList.remove(game);
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
		}
		return null;
	}
}

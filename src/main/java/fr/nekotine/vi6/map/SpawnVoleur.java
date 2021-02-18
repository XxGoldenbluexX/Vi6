package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.server.PluginDisableEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.yml.DisplayTexts;

@SerializableAs("SpawnVoleur")
public class SpawnVoleur implements ConfigurationSerializable,Listener{
	private static final String yamlPrefix = "spawnVoleur_";
	private String name;
	private String displayName;
	private Location mapLocation;
	private Location minimapLocation;
	
	private Vi6Main mainref;
	private ArmorStand armorStand;
	public SpawnVoleur(String name, String displayName, Location mapLocation, Location minimapLocation) {
		this.setName(name);
		this.setDisplayName(displayName);
		this.setMapLocation(mapLocation);
		this.setMinimapLocation(minimapLocation);
	}
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("displayName", displayName);
		map.put("mapLocation", mapLocation);
		map.put("minimapLocation", minimapLocation);
		return map;
	}
	public static SpawnVoleur deserialize(Map<String, Object> map) {
		return new  SpawnVoleur((String)map.get("name"), (String)map.get("displayName"), (Location)map.get("mapLocation"), (Location)map.get("minimapLocation"));
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Location getMapLocation() {
		return mapLocation;
	}
	public void setMapLocation(Location mapLocation) {
		this.mapLocation = mapLocation;
	}
	public Location getMinimapLocation() {
		return minimapLocation;
	}
	public void setMinimapLocation(Location minimapLocation) {
		this.minimapLocation = minimapLocation;
	}
	public static String getYamlPrefix() {
		return yamlPrefix;
	}
	public void destroy() {
		if(armorStand!=null) {
			armorStand.remove();
		}
		HandlerList.unregisterAll(this);
	}
	public void enable(Vi6Main mainref) {
		this.mainref=mainref;
		armorStand = (ArmorStand)minimapLocation.getWorld().spawnEntity(minimapLocation, EntityType.ARMOR_STAND);
		armorStand.setVisible(false);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setFireTicks(Game.getDefaultPreparationSeconds()*20);
		Bukkit.getPluginManager().registerEvents(this, mainref);
	}
	@EventHandler
	public void playerHitEntity(PlayerInteractAtEntityEvent e) {
		if(armorStand.equals(e.getRightClicked()) && mainref.getPlayerWrapper(e.getPlayer()).getTeam()==Team.VOLEUR) {
			mainref.getPlayerWrapper(e.getPlayer()).setThiefSpawnPoint(mapLocation);
			e.getPlayer().sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_thiefSpawnPoint_selected"),new MessageFormater("§e", displayName)));
		}
	}
	@EventHandler
	public void onDisable(PluginDisableEvent e) {
		if(e.getPlugin().getClass()==mainref.getClass()&&armorStand!=null) armorStand.remove();
	}
}

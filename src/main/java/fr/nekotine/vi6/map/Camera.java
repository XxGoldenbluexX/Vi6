package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.utils.CameraState;
import fr.nekotine.vi6.utils.IsCreator;

public class Camera implements ConfigurationSerializable, Listener{
	private static final String yamlPrefix = "Camera_";
	private static final String idleURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MzNmVjNTMyM2Y3NjVmZjU5ZjkxNmQyZDFhMWVjNzQ5Njg1NGNmN2JkMjZkZDJmMmNiYWRjM2RkNDkyNzljOCJ9fX0=";
	private static final String startingURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA2MTA3ZGVlN2Y4ZDI5OGE0NDg2ZmQ0ZDYwNDRiOTA5MjJlNjNiZDI5ZjA2OWZkZmJmMjZmNTRmZDRlYmVjNSJ9fX0=";
	private static final String activeURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE1ZTQ3MDgwNWQ1ZmM2MTFjNDRiODdjMzllN2U0ZGZkNDc0MDQ5YjI0ZjNmNmJiYTIzMGUyNTBmOWI0Yjg3YyJ9fX0=";
	private static final int STARTING_TICK_DELAY = 20;
	
	private CameraState state;
	private Location camLoc;
	private String camName;
	private String camDisplayName;
	private Material camMaterial;
	private ChatColor camColor;
	private int camPosition;
	
	private Vi6Main mainref;
	private Creeper creeperCam;
	
	private final ItemStack idleHead;
	private final ItemStack startingHead;
	private final ItemStack activeHead;
	public Camera(String camName, String camDisplayName, Location camLoc, int camPosition, Material camMaterial, ChatColor camColor) {
		this.camLoc = camLoc;
		this.camName =camName;
		this.camDisplayName = camDisplayName;
		this.camMaterial = camMaterial;
		this.camColor = camColor;
		this.camPosition = camPosition;
		
		idleHead = IsCreator.createSkull(idleURL);
		startingHead = IsCreator.createSkull(startingURL);
		activeHead = IsCreator.createSkull(activeURL);
	}
	public String getName() {
		return camName;
	}
	public String getDisplayName() {
		return camDisplayName;
	}
	public Location getLocation() {
		return camLoc;
	}
	public Material getMaterial() {
		return camMaterial;
	}
	public int getPosition() {
		return camPosition;
	}
	public ChatColor getColor() {
		return camColor;
	}
	public void spectate(Player player) {
		if(creeperCam!=null) {
			player.setGameMode(GameMode.SPECTATOR);
			player.setSpectatorTarget(creeperCam);
		}
	}
	public CameraState getState() {
		return state;
	}
	public void setState(CameraState state) {
		this.state = state;
	}
	
	
	public void setName(String name) {
		camName = name;
	}
	public void setDisplayName(String name) {
		camDisplayName = name;
	}
	public void setMaterial(Material mat) {
		camMaterial = mat;
	}
	public void setColor(ChatColor color) {
		camColor = color;
	}
	public void setPosition(int position) {
		camPosition = position;
	}
	public void setLocation(Location location) {
		camLoc = location;
	}
	
	
	public static final String getYamlPrefix() {
		return yamlPrefix;
	}
	
	public void enable(Vi6Main mainref) {
		this.mainref = mainref;
		
		creeperCam = (Creeper)camLoc.getWorld().spawnEntity(camLoc, EntityType.CREEPER);
		creeperCam.setAI(false);
		creeperCam.getEquipment().setHelmet(idleHead);
		creeperCam.setCollidable(false);
		creeperCam.setInvisible(true);
		creeperCam.setSilent(true);
		creeperCam.setInvulnerable(true);
		creeperCam.setGravity(false);
		
		camLoc.getWorld().getBlockAt(camLoc).setType(Material.BARRIER);
		
		Bukkit.getPluginManager().registerEvents(this, mainref);
	}
	public void destroy() {
		if(creeperCam!=null) {
			creeperCam.remove();
		}
		camLoc.getWorld().getBlockAt(camLoc).setType(Material.AIR);
		HandlerList.unregisterAll();
	}
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", camName);
		map.put("displayName", camDisplayName);
		map.put("location", camLoc);
		map.put("material", camMaterial);
		map.put("color", camColor);
		map.put("position", camPosition);
		return map;
	}
	public static Camera deserialize(Map<String, Object> map) {
		return new Camera((String)map.get("name"), (String)map.get("displayName"), (Location)map.get("location"), (int)map.get("position"),
				(Material)map.get("material"),(ChatColor)map.get("color"));
	}
}

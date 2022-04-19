package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.PlayerLeaveCamera;
import fr.nekotine.vi6.utils.CameraState;
import fr.nekotine.vi6.utils.IsCreator;

@SerializableAs("Camera")
public class Camera implements ConfigurationSerializable, Listener{
	private static final String yamlPrefix = "Camera_";
	private static final String idleURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MzNmVjNTMyM2Y3NjVmZjU5ZjkxNmQyZDFhMWVjNzQ5Njg1NGNmN2JkMjZkZDJmMmNiYWRjM2RkNDkyNzljOCJ9fX0=";
	private static final String startingURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA2MTA3ZGVlN2Y4ZDI5OGE0NDg2ZmQ0ZDYwNDRiOTA5MjJlNjNiZDI5ZjA2OWZkZmJmMjZmNTRmZDRlYmVjNSJ9fX0=";
	private static final String activeURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE1ZTQ3MDgwNWQ1ZmM2MTFjNDRiODdjMzllN2U0ZGZkNDc0MDQ5YjI0ZjNmNmJiYTIzMGUyNTBmOWI0Yjg3YyJ9fX0=";
	private static final int STARTING_TICK_DELAY = 20;
	private int delay_left = STARTING_TICK_DELAY;
	private static final PotionEffect startingEffect = new PotionEffect(PotionEffectType.BLINDNESS, STARTING_TICK_DELAY, 0, false, false, false);
	
	private HashMap<Player,ArmorStand> viewers = new HashMap<>();
	
	private CameraState state;
	private Location camLoc;
	private String camName;
	private String camDisplayName;
	private Material camMaterial;
	private int camPosition;
	
	private ArmorStand asCam;
	
	private final ItemStack idleHead;
	private final ItemStack startingHead;
	private final ItemStack activeHead;
	public Camera(String camName, String camDisplayName, Location camLoc, int camPosition, Material camMaterial) {
		this.camLoc = camLoc;
		this.camName =camName;
		this.camDisplayName = camDisplayName;
		this.camMaterial = camMaterial;
		this.camPosition = camPosition;
		
		idleHead = IsCreator.createSkull(idleURL);
		startingHead = IsCreator.createSkull(startingURL);
		activeHead = IsCreator.createSkull(activeURL);
	}
	@EventHandler
	public void onCrouch(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if(viewers.containsKey(e.getPlayer())) {
			removeViewer(p);
		}
	}
	public void tick() {
		if (state==CameraState.STARTING) {
			delay_left--;
			if(delay_left<=0) {
				setState(CameraState.ACTIVE);
				delay_left = STARTING_TICK_DELAY;
			}
		}
	}
	
	public void addViewer(Player player) {
		if(!viewers.containsKey(player)) {
			viewers.put(player, createArmorStand(player));
			switch (state) {
			case IDLE:
				setState(CameraState.STARTING);
				break;
			case STARTING:
				startingEffect.withDuration(delay_left).apply(player);
			default:
				break;
			}
			spectate(player);
		}
	}
	public ArmorStand createArmorStand(Player player) {
		ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
		as.setInvulnerable(true);
		as.setArms(true);
		as.setBasePlate(false);
		as.setCollidable(false);
		as.setMarker(true);
		
		as.setHeadPose(new EulerAngle(30, 0, 0));
		as.setLeftArmPose(new EulerAngle(300, 30, 0));
		
		as.setItem(EquipmentSlot.OFF_HAND, activeHead);
		as.setItem(EquipmentSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
		as.addDisabledSlots(EquipmentSlot.CHEST,EquipmentSlot.FEET,EquipmentSlot.HAND,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFF_HAND);
		
		return as;
	}
	public void removeViewer(Player player) {
		if(viewers.containsKey(player)) {
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(viewers.get(player));
			viewers.get(player).remove();
			viewers.remove(player);
			Bukkit.getPluginManager().callEvent(new PlayerLeaveCamera(this, player));
		}
		if(viewers.size()==0) {
			setState(CameraState.IDLE);
		}
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
	public void spectate(Player player) {
		if(asCam!=null) {
			player.setGameMode(GameMode.SPECTATOR);
			player.setSpectatorTarget(asCam);
		}
	}
	public CameraState getState() {
		return state;
	}
	public void setState(CameraState state) {
		this.state = state;
		switch(state) {
		case IDLE:
			asCam.getEquipment().setHelmet(idleHead);
			break;
		case STARTING:
			for(Player player : viewers.keySet()) {
				startingEffect.apply(player);
			}
			asCam.getEquipment().setHelmet(startingHead);
			break;
		case ACTIVE:
			asCam.getEquipment().setHelmet(activeHead);
			break;
		}
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
		
		asCam = (ArmorStand)camLoc.getWorld().spawnEntity(camLoc, EntityType.ARMOR_STAND);
		asCam.setAI(false);
		asCam.setCollidable(false);
		asCam.setInvisible(true);
		asCam.setSilent(true);
		asCam.setInvulnerable(true);
		asCam.setGravity(false);
		
		setState(CameraState.IDLE);
		
		camLoc.getWorld().getBlockAt(camLoc.add(0,1,0)).setType(Material.BARRIER);
		
		Bukkit.getPluginManager().registerEvents(this, mainref);
	}
	public void destroy() {
		if(asCam!=null) {
			asCam.remove();
			asCam=null;
		}
		camLoc.getWorld().getBlockAt(camLoc.add(0,1,0)).setType(Material.AIR);
		HandlerList.unregisterAll(this);
	}
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", camName);
		map.put("displayName", camDisplayName);
		map.put("location", camLoc);
		map.put("material", camMaterial.toString());
		map.put("position", camPosition);
		return map;
	}
	public static Camera deserialize(Map<String, Object> map) {
		return new Camera((String)map.get("name"), (String)map.get("displayName"), (Location)map.get("location"), (int)map.get("position"),
				Material.valueOf((String)map.get("material")));
	}
}

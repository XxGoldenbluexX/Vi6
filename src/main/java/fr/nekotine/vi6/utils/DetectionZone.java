package fr.nekotine.vi6.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.map.Carte;

public class DetectionZone implements ConfigurationSerializable,Listener {

	private final double x1;
	private final double y1;
	private final double z1;
	private final double x2;
	private final double y2;
	private final double z2;
	private final ArrayList<Player> playersInside = new ArrayList<Player>();
	
	public DetectionZone(Vi6Main mainref,double x1, double y1, double z1, double x2, double y2, double z2) {
		this.x1=Math.min(x1,x2);
		this.y1=Math.max(y1,y2);
		this.z1=Math.max(z1,z2);
		this.x2=Math.max(x1,x2);
		this.y2=Math.max(y1,y2);
		this.z2=Math.max(z1,z2);
		mainref.getPmanager().registerEvents(this, mainref);
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		return map;
	}
	
	public static Carte deserialize(Map<String, Object> args) {
		return new Carte();
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		
	}

	public double getZ2() {
		return z2;
	}

	public double getY2() {
		return y2;
	}

	public double getX2() {
		return x2;
	}

	public double getZ1() {
		return z1;
	}

	public double getY1() {
		return y1;
	}

	public double getX1() {
		return x1;
	}
	
	public boolean isInside(double x, double y, double z) {
		return ( (x>=x1 && x<=x2) && (y>=y1 && y<=y2) && (z>=z1 && z<=z2) );
	}
	
}

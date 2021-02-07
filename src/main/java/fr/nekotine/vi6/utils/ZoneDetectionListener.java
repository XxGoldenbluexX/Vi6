package fr.nekotine.vi6.utils;

import org.bukkit.entity.Player;

import fr.nekotine.vi6.Vi6Main;

public interface ZoneDetectionListener {
	
	public boolean playerEnterZone(Player player,DetectionZone zone, Vi6Main mainref);
	public boolean playerLeaveZone(Player player,DetectionZone zone, Vi6Main mainref);
	
}

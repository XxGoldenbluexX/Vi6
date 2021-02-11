package fr.nekotine.vi6.map;

<<<<<<< HEAD
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.events.GameTickEvent;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Artefact implements ZoneDetectionListener,Listener{
	
	private String name;
	private DetectionZone zone;
	private int nbVoleurInside = 0;
	private int nbGuardInside = 0;
	
	public Artefact(String name,DetectionZone zone) {
		
	}
	
	public String getName() {
		return name;
	}
	
	public void destroy() {
		zone.destroy();
	}
	
	public void enable(Vi6Main mainref) {
		zone.enable(mainref);
	}

	@Override
	public boolean playerEnterZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (zone.equals(zone)) {
			PlayerWrapper w = mainref.getPlayerWrapper(player);
			if (w!=null && (w.getState()==PlayerState.INSIDE || w.getState()==PlayerState.INSIDE)) {
				switch (w.getTeam()) {
				case GARDE:
					nbGuardInside++;
					break;
				case VOLEUR:
					nbVoleurInside++;
					break;
				default:
					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean playerLeaveZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (zone.equals(zone)) {
			PlayerWrapper w = mainref.getPlayerWrapper(player);
			if (w!=null && (w.getState()==PlayerState.INSIDE || w.getState()==PlayerState.INSIDE)) {
				switch (w.getTeam()) {
				case GARDE:
					nbGuardInside--;
					break;
				case VOLEUR:
					nbVoleurInside--;
					break;
				default:
					break;
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onGameTick(GameTickEvent e) {
		e.getGame()
	}
}

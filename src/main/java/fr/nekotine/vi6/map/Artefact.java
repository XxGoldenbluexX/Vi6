package fr.nekotine.vi6.map;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Artefact implements ZoneDetectionListener,Listener{
	
	public enum CaptureState{
		STEALABLE,//l'objet peut etre volé
		CARRIED,//l'objet est porté par un voleur
		STEALED//un voleur c'est enfuit avec cet objet
	}
	
	private String name;
	private DetectionZone zone;
	private int nbVoleurInside = 0;
	private int nbGuardInside = 0;
	private int captureLevel = 0;
	private int maxCaptureLevel=200;
	private CaptureState status=CaptureState.STEALABLE;
	private Vi6Main mainref;
	
	public Artefact(String name,DetectionZone zone) {
		this.name=name;
		this.zone=zone;
	}
	
	public String getName() {
		return name;
	}
	
	public void destroy() {
		zone.destroy();
	}
	
	public void enable(Vi6Main mainref) {
		zone.enable(mainref);
		maxCaptureLevel=mainref.getConfig().getInt("captureTime",200);
		this.mainref = mainref;
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
	public void tick(Game g) {
		if (nbVoleurInside>0) {
			captureLevel+=nbGuardInside>0?0:nbVoleurInside;
			if (captureLevel>=maxCaptureLevel) {
				g.showCaptureMessage(this,capture(zone.getPlayerInsideList()));
			}
		}else {
			captureLevel-=captureLevel>0?1:0;
		}
	}
	
	public PlayerWrapper capture(ArrayList<Player> list) {
		for (Player p : list) {
			PlayerWrapper w = mainref.getPlayerWrapper(p);
			if (w!=null && w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE) {
				w.getStealedArtefactList().add(this);
				return w;
			}
		}
		return null;
	}

	public CaptureState getStatus() {
		return status;
	}

	public void setStatus(CaptureState status) {
		this.status = status;
	}
}

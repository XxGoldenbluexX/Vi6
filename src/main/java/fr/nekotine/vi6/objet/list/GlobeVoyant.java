package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.PlayerStealEvent;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Artefact.CaptureState;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class GlobeVoyant extends Objet{
	private static final int MESSAGE_DELAY_TICKS=100;
	private Artefact attached;
	public GlobeVoyant(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
	}

	@Override
	public void leaveMap() {
	}

	@Override
	public void action(Action var1) {
		if(var1!=Action.PHYSICAL) {
			double nearestD = 0;
			Artefact nearestA = null;
			for(Artefact artefact : getGame().getMap().getArtefactList()) {
				if(artefact.getStatus()==CaptureState.STEALABLE) {
					double distance = getOwner().getLocation().distanceSquared(artefact.getBlockLoc());
					if(nearestA==null || distance<nearestD) {
						nearestA=artefact;
						nearestD=distance;
					}
				}
			}
			if(nearestA!=null) {
				attached=nearestA;
				//message
			}else {
				//message tout est pris ou pas aurtefact
			}
			consume();
		}
	}

	@Override
	public void drop() {
	}

	public static int getMessageDelayTicks() {
		return MESSAGE_DELAY_TICKS;
	}
	@EventHandler
	public void artefactSteal(PlayerStealEvent e) {
		if(e.getGame().equals(getGame()) && e.getArtefact().equals(attached)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					//message
				}
			}.runTaskLater(getMain(), MESSAGE_DELAY_TICKS);
			destroy();
		}
	}
}

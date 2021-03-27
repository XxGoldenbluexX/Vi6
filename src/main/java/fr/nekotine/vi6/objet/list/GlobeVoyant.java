package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerStealEvent;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Artefact.CaptureState;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class GlobeVoyant extends Objet{
	private static final int MESSAGE_DELAY_TICKS=100;
	private int delay=MESSAGE_DELAY_TICKS;
	private Artefact attached;
	private boolean stolen=false;
	public GlobeVoyant(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
		if(stolen) {
			delay--;
			if(delay==0) {
				for(Entry<Player, PlayerWrapper> player : getGame().getPlayerMap().entrySet()) {
					if(player.getValue().getTeam()==Team.GARDE) {
						player.getKey().playSound(Sound.sound(Key.key("entity.vindicator.celebrate"), Sound.Source.VOICE, 0.5f, 1.5f));
					}
				}
				//message
				destroy();
			}
		}
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
			ArrayList<Artefact> artefactList = getGame().getMap().getArtefactList();
			for(Objet obj : super.getGame().getObjets()) {
				if(obj instanceof GlobeVoyant) {
					GlobeVoyant gv = (GlobeVoyant)obj;
					if(gv.getAttached()!=null) artefactList.remove(gv.getAttached());
				}
			}
			double nearestD = 0;
			Artefact nearestA = null;
			for(Artefact artefact : artefactList) {
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
				super.getOwner().getWorld().playSound(attached.getBlockLoc(), "entity.slime.jump", 1, 2);
				super.getOwner().getWorld().playSound(attached.getBlockLoc(), "entity.shulker_bullet.hit", 0.5f, 2);
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
		if(e.getGame().equals(getGame()) && e.getArtefact().equals(attached)) stolen=true;
	}
	public Artefact getAttached() {
		return attached;
	}
}

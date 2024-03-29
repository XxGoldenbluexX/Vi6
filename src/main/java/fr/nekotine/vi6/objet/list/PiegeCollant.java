package fr.nekotine.vi6.objet.list;

import java.util.Map.Entry;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class PiegeCollant extends Objet {

	private static final double SQUARED_TRIGGER_RANGE = 0.7;
	private boolean placed = false;
	private Location loc;
	
	public PiegeCollant(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,PlayerWrapper wrapper) {
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
	public void action(PlayerInteractEvent e) {
		tryPlace();
	}

	@Override
	public void drop() {
		tryPlace();
	}
	
	private void tryPlace() {
		if (onGround() && !placed) {
			placed=true;
			loc = getOwner().getLocation().clone();
			consume();
		}
	}
	
	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (placed && !getOwnerWrapper().haveEffect(Effects.Jammed)) {
			if (getGame().getPlayerMap().entrySet().stream().anyMatch(new Predicate<Entry<Player,PlayerWrapper>>() {
				@Override
				public boolean test(Entry<Player, PlayerWrapper> t) {
					PlayerWrapper w = t.getValue();
					return t.getKey().equals(event.getPlayer()) && w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE && !w.haveEffect(Effects.Fantomatique);
				}
			}
			)) {
				if (event.getFrom().distanceSquared(loc)<=SQUARED_TRIGGER_RANGE) {
					Player p = event.getPlayer();
					int amplitude=0;
					PotionEffect e = p.getPotionEffect(PotionEffectType.SLOW);
					if (e!=null) amplitude = e.getAmplifier()+1;
					p.removePotionEffect(PotionEffectType.SLOW);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,amplitude,false,false,true));
					Vi6Sound.PIEGE_COLLANT.playForPlayer(p);
					placed=false;
				}
			}
		}
	}

}

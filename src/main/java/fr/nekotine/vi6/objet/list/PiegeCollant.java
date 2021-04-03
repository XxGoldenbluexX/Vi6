package fr.nekotine.vi6.objet.list;

import java.util.Map.Entry;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
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
	public void action(Action var1) {
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
		}
	}
	
	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (placed) {
			if (getGame().getPlayerMap().entrySet().stream().anyMatch(new Predicate<Entry<Player,PlayerWrapper>>() {
				@Override
				public boolean test(Entry<Player, PlayerWrapper> t) {
					PlayerWrapper w = t.getValue();
					return t.getKey().equals(event.getPlayer()) && w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE && !w.haveEffect(Effects.Fantomatique);
				}
			}
			)) {
				if (event.getFrom().distanceSquared(loc)<=SQUARED_TRIGGER_RANGE) {
					//TRIGGERED
				}
			}
		}
	}

}

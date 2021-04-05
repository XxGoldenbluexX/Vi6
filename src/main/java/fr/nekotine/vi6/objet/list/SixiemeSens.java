package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class SixiemeSens extends Objet{
	private static int SQUARED_BLOCK_DISTANCE=36;
	private ArrayList<Player> glowed = new ArrayList<>();
	public SixiemeSens(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
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
		disable();
	}

	@Override
	public void leaveMap() {
		disable();
	}

	@Override
	public void action(PlayerInteractEvent e) {
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(getOwner().equals(e.getPlayer())) {
			for(Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
				if(p.getValue().getTeam()==Team.GARDE) {
					updateGlow(p.getKey());
				}
			}
		}else if(getGame().getPlayerTeam(e.getPlayer())==Team.GARDE) {
			updateGlow(e.getPlayer());
		}
	}
	private void updateGlow(Player guard) {
		if(glowed.contains(guard)) {
			if(getOwner().getLocation().distanceSquared(guard.getLocation())>SQUARED_BLOCK_DISTANCE) {
				getGame().unglowPlayer(getOwner(), guard);
				glowed.remove(guard);
			}
		}else if(getOwner().getLocation().distanceSquared(guard.getLocation())<=SQUARED_BLOCK_DISTANCE) {
			getGame().glowPlayer(getOwner(), guard);
			glowed.add(guard);
		}
	}
	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
	}
	public void disable() {
		super.disable();
	}
}

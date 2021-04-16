package fr.nekotine.vi6.objet.list;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class DeadRinger extends Objet{
	public static final int INVISIBILITY_DURATION_TICK=60;
	public DeadRinger(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,PlayerWrapper wrapper) {
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
	}

	@Override
	public void drop() {
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(getOwner().equals(e.getEntity()) && (getOwner().getInventory().contains(getDisplayedItem())) 
		&& getOwner().getHealth()-e.getFinalDamage()<=0) {
			consume();
			disable();
			e.setDamage(0.01);
			for (Map.Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
				if(p.getValue().getTeam()==Team.GARDE) {
					p.getKey().hidePlayer(getMain(), getOwner());
					p.getKey().sendMessage(MessageFormater.formatWithColorCodes('§', DisplayTexts.getMessage("game_death"),
					new MessageFormater("§p", String.valueOf(getOwner().getName())),
					new MessageFormater("§n", String.valueOf(getOwnerWrapper().getStealedArtefactList().size()))));
				}else {
					p.getKey().sendMessage(MessageFormater.formatWithColorCodes('§', DisplayTexts.getMessage("game_fake_death"),
							new MessageFormater("§p", String.valueOf(getOwner().getName()))));
				}
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Map.Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
						if(p.getValue().getTeam()==Team.GARDE) {
							p.getKey().showPlayer(getMain(), getOwner());
						}
					}
					Location loc = getOwner().getLocation();
					Vi6Sound.DEAD_RINGER.playAtLocation(loc);
				}
			}.runTaskLater(getMain(), INVISIBILITY_DURATION_TICK);
		}
	}
}

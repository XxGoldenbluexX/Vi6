package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class BrouilleurRadio extends Objet{
	public BrouilleurRadio(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
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
		disable();
	}

	@Override
	public void leaveMap() {
		disable();
	}

	@Override
	public void action(PlayerInteractEvent e) {
		if(e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) use();
	}

	@Override
	public void drop() {
		use();
	}
	private void use() {
		Vi6Sound.SUCCESS.playForPlayer(getOwner());
		ArrayList<Player> guards = new ArrayList<>();
		for(Entry<Player, PlayerWrapper> player : getGame().getPlayerMap().entrySet()) {
			if(player.getValue().getTeam()==Team.GARDE) {
				guards.add(player.getKey());
				Vi6Sound.BROUILLEUR_0.playForPlayer(player.getKey());
			}
		}
		new BukkitRunnable() {
			byte repeatCount=0;
			@Override
			public void run() {
				switch(repeatCount) {
				case 0:
					guards.forEach((p)-> Vi6Sound.BROUILLEUR_1.playForPlayer(p));
					break;
				case 1:
					guards.forEach((p)-> Vi6Sound.BROUILLEUR_2.playForPlayer(p));
					break;
				case 2:
					guards.forEach((p)-> Vi6Sound.BROUILLEUR_3.playForPlayer(p));
					break;
				case 3:
					this.cancel();
					break;
				default:
					break;
				}
				repeatCount++;
			}
		}.runTaskTimer(getMain(), 8, 0);
		destroy();
	}
}

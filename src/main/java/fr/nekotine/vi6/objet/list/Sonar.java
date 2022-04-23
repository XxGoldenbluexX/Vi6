package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

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

public class Sonar extends Objet{
	private static int DELAY_IN_TICKS = 80;
	private static int SQUARED_BLOCK_RANGE = 25;
	private int delay=DELAY_IN_TICKS;
	public Sonar(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
		if(getOwner().getGameMode()!=GameMode.SPECTATOR) {
			delay--;
			if(delay<=0) {
				delay=DELAY_IN_TICKS;
				ArrayList<Player> thievesNear = new ArrayList<Player>();
				for(Entry<Player, PlayerWrapper> player : super.getGame().getPlayerMap().entrySet()) {
					if(player.getValue().getTeam()==Team.VOLEUR 
						&& !player.getValue().haveEffect(Effects.Insondable)
						&& player.getValue().getState()==PlayerState.INSIDE
						&& super.getOwner().getLocation().distanceSquared(player.getKey().getLocation())<=SQUARED_BLOCK_RANGE){
						thievesNear.add(player.getKey());
					}
				}
				if(thievesNear.size()>0) {
					Vi6Sound.SONAR_DETECT.playForPlayer(getOwner());
					for(Player thief : thievesNear) {
						Vi6Sound.SONAR_DETECT.playForPlayer(thief);
					}
				}else {
					Vi6Sound.SONAR_NOBODY.playForPlayer(getOwner());
				}
			}
		}
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
	public static int getDelay() {
		return DELAY_IN_TICKS;
	}
}

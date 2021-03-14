package fr.nekotine.vi6.objet.list;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import ru.xezard.glow.data.glow.Glow;

public class SixiemeSens extends Objet{
	private static int SQUARED_BLOCK_DISTANCE=36;
	private Glow glow = Glow.builder().animatedColor(new ChatColor[]{ChatColor.BLUE}).name("sixiemeSensGlow").build();
	public SixiemeSens(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		glow.render(player);
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
	public void action(Action var1) {
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(getGame().getPlayerTeam(e.getPlayer())==Team.GARDE) {
			if(glow.getHolders().contains(e.getPlayer())) {
				if(getOwner().getLocation().distanceSquared(e.getPlayer().getLocation())>SQUARED_BLOCK_DISTANCE) glow.removeHolders(e.getPlayer());
			}else {
				if(getOwner().getLocation().distanceSquared(e.getPlayer().getLocation())<=SQUARED_BLOCK_DISTANCE) glow.addHolders(e.getPlayer());
			}
		}
	}
}

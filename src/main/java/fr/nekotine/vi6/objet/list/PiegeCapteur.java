package fr.nekotine.vi6.objet.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.TempBlock;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class PiegeCapteur extends Objet{
	private TempBlock pressure;
	public PiegeCapteur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
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
		if(var1==Action.RIGHT_CLICK_AIR || var1==Action.RIGHT_CLICK_BLOCK) use();
	}

	@Override
	public void drop() {
		use();
	}
	private void use() {
		if(getOwner().getLocation().getBlock().isEmpty() && getOwner().getLocation().subtract(0, 1, 0).getBlock().isSolid()) {
			pressure = new TempBlock(getOwner().getLocation().getBlock(), Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
			Vi6Sound.PIEGECAPTEUR_POSE.playAtLocation(getOwner().getLocation());
			consume();
		}
	}
	public void destroy() {
		super.destroy();
		if(pressure!=null) pressure.reset();
	}
	@EventHandler
	public void onPlayerInterract(PlayerInteractEvent e) {
		if(pressure!=null) {
			if(e.getAction()==Action.PHYSICAL && e.getClickedBlock().equals(pressure.getBlock())) {
				e.setCancelled(true);
				PlayerWrapper wrap = getGame().getWrapper(e.getPlayer());
				if(wrap!=null && wrap.getTeam()==Team.VOLEUR && wrap.getState()==PlayerState.INSIDE) {
					//message
					Vi6Sound.PIEGECAPTEUR_TRIGGER.playAtLocation(pressure.getBlock().getLocation());
					destroy();
				}
			}
		}
	}
}

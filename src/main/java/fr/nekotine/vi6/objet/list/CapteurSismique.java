package fr.nekotine.vi6.objet.list;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.TempBlock;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class CapteurSismique extends Objet{
	private TempBlock sensor;
	public CapteurSismique(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}
	public void destroy() {
		super.destroy();
		if (this.sensor != null)
			this.sensor.reset();
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
		use();
	}

	@Override
	public void drop() {
		use();
	}
	public void use() {
		if(sensor==null) {
			if (!onGround()) {
				Vi6Sound.NO.playForPlayer(getOwner());
			}else {
				sensor = (new TempBlock(getOwner().getLocation().getBlock(), Bukkit.createBlockData(Material.SCULK_SENSOR)))
						.set();
				consume();
			}
			
		}
	}
	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}

}

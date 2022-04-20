package fr.nekotine.vi6.objet.list;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Vibration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.TempBlock;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class CapteurSismique extends Objet{
	private static final int DELAY_BEFORE_RECEIVING = 0;
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
	@EventHandler
	public void whenPowered(BlockRedstoneEvent e) {
		if(e.getBlock().equals(sensor.getBlock())) {
			new Vibration(sensor.getBlock().getLocation(), new Vibration.Destination.EntityDestination(getOwner()), DELAY_BEFORE_RECEIVING);
		}
	}
	public void use() {
		if(sensor==null) {
			sensor = (new TempBlock(getOwner().getLocation().getBlock(), Bukkit.createBlockData(Material.SCULK_SENSOR)))
					.set();
		}
	}

}

package fr.nekotine.vi6.objet.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class DoubleSaut extends Objet {
	
	private final Player player;
	private final static ItemStack jumpBoots = new ItemStack(Material.GOLDEN_BOOTS);
	
	private boolean canJump=false;
	
	public DoubleSaut(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
		this.player = player;
		player.setAllowFlight(true);
		player.getInventory().setBoots(jumpBoots);
	}

	@Override
	public void gameEnd() {
	}

	@Override
	public void tick() {
		if (onGround()) canJump=true;
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void leaveMap(Player holder) {
	}

	@Override
	public void death(Player holder) {
		player.getInventory().remove(jumpBoots);
	}

	@Override
	public void sell(Player holder) {
		player.getInventory().remove(jumpBoots);
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
	}
	
	@EventHandler
	public void onPlayerJump(PlayerJumpEvent event) {
		if (event.getPlayer().equals(player)) player.setAllowFlight(true);
	}
	
	@EventHandler
	public void tryFly(PlayerToggleFlightEvent event) {
		if (event.getPlayer().equals(player)) {
			event.setCancelled(true);
			if (canJump) {
				player.setVelocity(player.getVelocity().setY(0.5));
				player.playSound(Sound.sound(Key.key("item.firecharge.use"),Sound.Source.AMBIENT,0.3f, 1.5f));
				player.playSound(Sound.sound(Key.key("item.hoe.till"),Sound.Source.AMBIENT,1f, 0.1f));
			}
		}
	}
	
	private boolean onGround() {
		return (!player.isFlying() && player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid());
	}

}

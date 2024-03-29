package fr.nekotine.vi6.objet.list;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class DoubleSaut extends Objet {
	private static final ItemStack JUMP_BOOTS = IsCreator.createItemStack(Material.GOLDEN_BOOTS, 1,
			ObjetsList.DOUBLE_SAUT.getInShopName(), ObjetsList.DOUBLE_SAUT.getInShopLore());

	private boolean canJump = false;

	public DoubleSaut(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		getOwner().getInventory().setBoots(JUMP_BOOTS);
	}

	public void disable() {
		super.disable();
		PlayerInventory inv = getOwner().getInventory();
		GameMode gm = getOwner().getGameMode();
		if (getGame().getState()!=GameState.Preparation && (gm==GameMode.SURVIVAL || gm==GameMode.ADVENTURE)) getOwner().setAllowFlight(false);
		if (JUMP_BOOTS.isSimilar(inv.getBoots()))
			inv.setBoots(null);
	}

	public void tick() {
		if(getGame().getState()!=GameState.Preparation) {
			if (onGround() && getOwner().getGameMode() == GameMode.ADVENTURE) {
				canJump = true;
				getOwner().setAllowFlight(true);
			}
		}
	}

	public void cooldownEnded() {
	}

	public void leaveMap() {
		disable();
	}

	public void death() {
		disable();
	}

	public void action(PlayerInteractEvent e) {
	}

	public void drop() {
	}

	@EventHandler
	public void onPlayerJump(PlayerJumpEvent event) {
		if(getGame().getState()!=GameState.Preparation) {
			if (event.getPlayer().equals(getOwner()) && !getOwnerWrapper().haveEffect(Effects.Jammed))
				getOwner().setAllowFlight(true);
			}
	}

	@EventHandler
	public void tryFly(PlayerToggleFlightEvent event) {
		if(getGame().getState()!=GameState.Preparation) {
			if (event.getPlayer().equals(getOwner()) && getOwner().getGameMode() == GameMode.ADVENTURE) {
				event.setCancelled(true);
				if (canJump) {
					getOwner().setVelocity(getOwner().getVelocity().setY(0.5D));
					Vi6Sound.DOUBLE_SAUT.playForPlayer(getOwner());
					canJump = false;
					getOwner().setAllowFlight(false);
				}
			}
		}
	}

	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}
}
package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class OmniCapteur extends Objet{
	private static final float SQUARED_BLOCK_RANGE=16;
	private final ArrayList<Player> glowed = new ArrayList<>();
	private final StatusEffect glowEffect = new StatusEffect(Effects.Glow);
	private ArmorStand omni;
	public OmniCapteur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
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
		omni = (ArmorStand)getOwner().getWorld().spawnEntity(getOwner().getLocation(), EntityType.ARMOR_STAND);
		omni.setArms(true);
		omni.setMarker(true);
		omni.setBasePlate(false);
		omni.setSmall(true);
		omni.getEquipment().setHelmet(new ItemStack(Material.REDSTONE_BLOCK));
		getOwner().getWorld().playSound(getOwner().getLocation(), "entity.vex.hurt", 1, 0.1f);
		getOwner().getWorld().playSound(getOwner().getLocation(), "item.flintandsteel.use", 1, 0.1f);
		consume();
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(omni!=null) {
			if(getGame().getPlayerTeam(e.getPlayer())==Team.GARDE) {
				boolean isInRange = omni.getLocation().distanceSquared(e.getTo())<=SQUARED_BLOCK_RANGE;
				if(glowed.contains(e.getPlayer())) {
					if(!isInRange) getGame().getWrapper(e.getPlayer()).removeStatusEffect(glowEffect);
					glowed.remove(e.getPlayer());
				}else {
					if(isInRange) getGame().getWrapper(e.getPlayer()).addStatusEffect(glowEffect);
					glowed.add(e.getPlayer());
				}
			}
		}
	}

	public static float getSquaredBlockRange() {
		return SQUARED_BLOCK_RANGE;
	}
	public void disable() {
		super.disable();
		if(omni!=null) omni.remove();
		for(Player guard : glowed) {
			getGame().getWrapper(guard).removeStatusEffect(glowEffect);
		}
	}
}

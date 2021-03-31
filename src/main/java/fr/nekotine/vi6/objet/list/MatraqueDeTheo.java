package fr.nekotine.vi6.objet.list;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MatraqueDeTheo extends Objet {
	
	private static final ItemStack MATRAQUE = new ItemStack(Material.NETHERITE_SWORD);

	static {
		ItemMeta meta = MATRAQUE.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier("pvp_1.8", 1000.0D, AttributeModifier.Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier("UltimateSwordDamages", 2048.0D, AttributeModifier.Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier("noKnockback", 1.0D, AttributeModifier.Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK,
				new AttributeModifier("dealKnockback", 3.0D, AttributeModifier.Operation.ADD_NUMBER));
		meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE});
		meta.setUnbreakable(true);
		meta.displayName(Component.text("Matraque de théo").color(TextColor.color(255, 0, 0)));
		MATRAQUE.setItemMeta(meta);
	}

	public MatraqueDeTheo(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		setItem(MATRAQUE);
	}

	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		getOwner().getInventory().removeItem(Game.GUARD_SWORD);
		setItem(MATRAQUE);
	}

	public void disable() {
		super.disable();
		PlayerInventory inv = getOwner().getInventory();
		int slot = inv.first(MATRAQUE);
		if (slot >= 0) {
			inv.setItem(slot, Game.GUARD_SWORD);
		} else {
			inv.addItem(Game.GUARD_SWORD);
		}
	}

	public void drop() {
		Vi6Sound.WOLOLO.playAtLocation(getOwner().getLocation());
	}

	public void cooldownEnded() {
	}

	public void tick() {
	}

	public void death() {
	}

	public void leaveMap() {
	}

	public void action(Action action) {
	}
}
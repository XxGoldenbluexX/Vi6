package fr.nekotine.vi6.objet.list;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MatraqueDeTheo extends Objet {
	
	private static final ItemStack MATRAQUE = new ItemStack(Material.NETHERITE_SWORD);
	static{
		ItemMeta meta = MATRAQUE.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,new AttributeModifier("pvp_1.8",1000,Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,new AttributeModifier("UltimateSwordDamages",2048,Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,new AttributeModifier("noKnockback",1,Operation.ADD_NUMBER));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK,new AttributeModifier("dealKnockback",3,Operation.ADD_NUMBER));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		meta.displayName(Component.text("Matraque de théo").color(TextColor.color(255, 0, 0)));
		MATRAQUE.setItemMeta(meta);
	}

	public MatraqueDeTheo(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, MATRAQUE, game, player);
		player.getInventory().remove(Game.GUARD_SWORD);
	}

	@Override
	public void gameEnd() {
	}

	@Override
	public void tick() {
	}

	@Override
	public void leaveMap(Player holder) {
	}

	@Override
	public void death(Player holder) {
	}

	@Override
	public void sell(Player holder) {
		holder.getInventory().addItem(Game.GUARD_SWORD);
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
		Location loc = holder.getLocation();
		loc.getWorld().playSound(Sound.sound(Key.key("entity.evoker.prepare_wololo"),Sound.Source.VOICE,1f,1.2f), loc.getX(), loc.getY(), loc.getZ());
	}

}

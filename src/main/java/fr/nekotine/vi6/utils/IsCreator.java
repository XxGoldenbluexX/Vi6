package fr.nekotine.vi6.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import net.kyori.adventure.text.Component;

public class IsCreator {
	public static Enchantment enchant = Enchantment.DURABILITY;
	public static ItemStack createItemStack(Material mat, int quantity, String name, String... lore) {
		ItemStack item = new ItemStack(mat,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.text(name));
		List<Component> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(Component.text(line));
			}
		}
		meta.lore(loreList);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createObjetItemStack(Vi6Main main, String displayName, ObjetsList objet, int quantity, String... lore) {
		ItemStack item = new ItemStack(objet.getInShopMaterial(),quantity);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.text(displayName));
		List<Component> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(Component.text(line));
			}
		}
		meta.getPersistentDataContainer().set(ObjetsListTagType.getNamespacedKey(main), new ObjetsListTagType(), objet);
		meta.lore(loreList);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createSkinItemStack(Vi6Main main, Game game, Player player, ObjetsSkins skin, int quantity, String... lore) {
		ItemStack item = new ItemStack(skin.getInShopMaterial(),quantity);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.text(skin.getInShopName()));
		List<Component> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(Component.text(line));
			}
		}
		meta.getPersistentDataContainer().set(ObjetsSkinsTagType.getNamespacedKey(main), new ObjetsSkinsTagType(), skin);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if(game.getWrapper(player).isSkinsSelected(skin)) {
			item.addUnsafeEnchantment(enchant, 1);
			loreList.add(Component.text(ChatColor.GREEN+"[SÉLECTIONNÉE]"));
		}
		meta.lore(loreList);
		
		item.setItemMeta(meta);
		return item;
	}
}

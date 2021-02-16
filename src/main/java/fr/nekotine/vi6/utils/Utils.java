package fr.nekotine.vi6.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsSkins;

public class Utils {
	public static Enchantment enchant = Enchantment.DURABILITY;
	public static ItemStack createItemStack(Material mat, int quantity, String name, String... lore) {
		ItemStack item = new ItemStack(mat,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		List<String> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(line);
			}
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createObjetItemStack(Vi6Main main, String displayName, ObjetsSkins objet, int quantity, String... lore) {
		ItemStack item = new ItemStack(objet.getMaterial(),quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		List<String> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(line);
			}
		}
		meta.getPersistentDataContainer().set(ObjetsSkinsTagType.getNamespacedKey(main), new ObjetsSkinsTagType(), objet);
		meta.setLore(loreList);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createSkinItemStack(Vi6Main main, Game game, Player player, ObjetsSkins skin, int quantity, String... lore) {
		ItemStack item = new ItemStack(skin.getMaterial(),quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(skin.getName());
		List<String> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(line);
			}
		}
		meta.getPersistentDataContainer().set(ObjetsSkinsTagType.getNamespacedKey(main), new ObjetsSkinsTagType(), skin);
		meta.setLore(loreList);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if(game.getWrapper(player).isSkinsSelected(skin)) item.addUnsafeEnchantment(enchant, 1);
		item.setItemMeta(meta);
		return item;
	}
}

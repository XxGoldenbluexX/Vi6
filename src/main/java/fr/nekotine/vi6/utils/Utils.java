package fr.nekotine.vi6.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsSkins;

public class Utils {
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
	public static ItemStack createItemStack(Vi6Main main, ObjetsSkins objet, int quantity, String... lore) {
		ItemStack item = new ItemStack(objet.getMaterial(),quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(objet.getName());
		List<String> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(line);
			}
		}
		meta.getPersistentDataContainer().set(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType(), objet);
		meta.setLore(loreList);
		item.setItemMeta(meta);
		return item;
	}
}

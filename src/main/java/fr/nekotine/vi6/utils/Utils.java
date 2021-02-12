package fr.nekotine.vi6.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
}

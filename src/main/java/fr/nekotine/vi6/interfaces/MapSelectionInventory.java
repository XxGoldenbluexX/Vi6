package fr.nekotine.vi6.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.yml.YamlWorker;

public class MapSelectionInventory extends BaseSharedInventory{
	private static Enchantment enchant = Enchantment.DURABILITY;
	public MapSelectionInventory(Vi6Main main,Game game) {
		super(game, main);
		inventory = Bukkit.createInventory(null, 9*3);
		inventory.setItem(1, createItemStack(Material.BLACK_STAINED_GLASS,1,"",""));
		inventory.setItem(9, createItemStack(Material.BLACK_STAINED_GLASS,1,"",""));
		inventory.setItem(10, createItemStack(Material.BLACK_STAINED_GLASS,1,"",""));
		inventory.setItem(19, createItemStack(Material.BLACK_STAINED_GLASS,1,"",""));
		inventory.setItem(0, createItemStack(Material.BARRIER,1,ChatColor.RED+"Retour",""));
		byte index=1;
		for(String map : YamlWorker.getMapNameList(main)) {
			index++;
			if(index%9==0) {
				index+=2;
			}
			inventory.setItem(index, createItemStack(Material.PAPER,1,map,""));
			inventory.getItem(index).addItemFlags(ItemFlag.HIDE_ENCHANTS);
			if(game.getMapName()==map) {
				inventory.setItem(19, createItemStack(Material.BOOK,1,ChatColor.WHITE+"Carte s�lectionn�e",ChatColor.UNDERLINE+""+ChatColor.LIGHT_PURPLE+map));
				inventory.getItem(index).addUnsafeEnchantment(enchant, 1);
			}
		}
		for(index+=0;index<=25;index++) {
			index++;
			if(index%9==0) {
				index+=2;
			}
			inventory.setItem(index, createItemStack(Material.BLACK_STAINED_GLASS,1,"",""));
		}
	}

	@Override
	public void itemClicked(Player player, ItemStack itm) {
		switch(itm.getType()) {
		case BARRIER:
			game.openSettings(player);
			return;
		case PAPER:
			for(ItemStack item : inventory.getStorageContents()) {
				if(item.getType()==Material.PAPER&&item.getItemMeta().getDisplayName()==game.getMapName()) {
					item.removeEnchantment(enchant);
					break;
				}
			}
			game.setMapName(itm.getItemMeta().getDisplayName());
			itm.addUnsafeEnchantment(enchant, 1);
			inventory.getItem(19).getItemMeta().setDisplayName(itm.getItemMeta().getDisplayName());
			return;
		default:
			return;
		}
	}

}
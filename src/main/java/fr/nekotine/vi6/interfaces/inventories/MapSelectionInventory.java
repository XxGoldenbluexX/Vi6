package fr.nekotine.vi6.interfaces.inventories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameEnterPreparationPhaseEvent;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.utils.IsCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class MapSelectionInventory extends BaseSharedInventory{
	private static Enchantment enchant = Enchantment.DURABILITY;
	public MapSelectionInventory(Vi6Main main,Game game) {
		super(game, main);
		inventory = Bukkit.createInventory(null, 9*3, Component.text("Carte"));
		inventory.setItem(1, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		inventory.setItem(9, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		inventory.setItem(10, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		inventory.setItem(19, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		inventory.setItem(0, IsCreator.createItemStack(Material.BARRIER,1,ChatColor.RED+"Retour",""));
		byte index=1;
		if(Carte.getMapList().size()>0) game.setMapName(Carte.getMapList().get(0));
		for(String map : Carte.getMapList()) {
			index++;
			if(index%9==0) {
				index+=2;
			}
			inventory.setItem(index, IsCreator.createItemStack(Material.PAPER,1,map,""));
			inventory.getItem(index).addItemFlags(ItemFlag.HIDE_ENCHANTS);
			if(map.equals(game.getMapName())) {
				inventory.getItem(index).addUnsafeEnchantment(enchant, 1);
			}
		}
		inventory.setItem(18, IsCreator.createItemStack(Material.BOOK,1,ChatColor.WHITE+"Carte sélectionnée",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMapName()));
		for(index+=1;index<=26;index++) {
			if(index%9==0) {
				index+=2;
			}
			inventory.setItem(index, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
	}

	@Override
	public void itemClicked(Player player, ItemStack itm) {
		switch(itm.getType()) {
		case BARRIER:
			game.openSettings(player);
			break;
		case PAPER:
			for(ItemStack item : inventory.getStorageContents()) {
				if(item.getType()==Material.PAPER&&((TextComponent)item.getItemMeta().displayName()).content().equals(game.getMapName())) {
					item.removeEnchantment(enchant);
					break;
				}
			}
			game.setMapName(((TextComponent)itm.getItemMeta().displayName()).content());
			itm.addUnsafeEnchantment(enchant, 1);
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+((TextComponent)itm.getItemMeta().displayName()).content());
			
			inventory.getItem(18).setLore(lore);
			break;
		default:
			break;
		}
	}
	@EventHandler
	public void onGameStart(GameEnterPreparationPhaseEvent e) {
		if(e.getGame().equals(game)) {
			inventory.getViewers().forEach(HumanEntity::closeInventory);
		}
	}
}

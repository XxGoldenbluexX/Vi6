package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.utils.IsCreator;

public class CheckListGuardInventory extends CheckListInventory{

	public CheckListGuardInventory(Game game, Vi6Main main) {
		super(game, main);
	}

	@Override
	public void itemClicked(ItemStack itm, int slot) {
		int quotient = Math.floorDiv(slot, 9);
		if(quotient==1 || quotient==4) {
			if(itm.getType()==Material.REDSTONE_BLOCK) {
				inventory.setItem(slot, IsCreator.createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Détenu", ""));
			}else if(itm.getType()==Material.EMERALD_BLOCK) {
				inventory.setItem(slot,IsCreator.createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"Volé", ""));
			}
		}
	}

}

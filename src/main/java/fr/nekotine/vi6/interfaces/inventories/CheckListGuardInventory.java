package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;

public class CheckListGuardInventory extends CheckListInventory{

	public CheckListGuardInventory(Game game, Vi6Main main) {
		super(game, main, Team.GARDE);
	}

	@Override
	public void itemClicked(ItemStack itm, int slot) {
		super.change(slot, itm.getType()==Material.REDSTONE_BLOCK);
	}

}

package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;

public class CheckListThiefInventory extends CheckListInventory{
	public CheckListThiefInventory(Game game, Vi6Main main) {
		super(game, main, Team.VOLEUR);
	}

	@Override
	public void itemClicked(ItemStack itm, int slot) {
	}
}

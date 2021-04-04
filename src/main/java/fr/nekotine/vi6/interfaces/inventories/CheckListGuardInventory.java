package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import net.kyori.adventure.text.TextComponent;

public class CheckListGuardInventory extends CheckListInventory{

	public CheckListGuardInventory(Game game, Vi6Main main) {
		super(game, main, Team.GARDE);
	}

	@Override
	public void itemClicked(ItemStack itm, int slot) {
		int quotient = Math.floorDiv(slot, 9);
		if(quotient==1 || quotient==4) {
			super.change(super.game.getMap().getArtefact(((TextComponent)super.inventory.getItem(slot-9).getItemMeta().displayName()).content()),
			itm.getType()==Material.REDSTONE_BLOCK);
		}
	}

}

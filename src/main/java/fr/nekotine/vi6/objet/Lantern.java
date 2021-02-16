package fr.nekotine.vi6.objet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;

public class Lantern extends Objet{
	public Lantern(Vi6Main main, ObjetsList objet, Player player, Game game, Material material) {
		super(main, objet, new ItemStack(material), game);
		player.getInventory().addItem(itemStack);
	}
	@Override
	public void gameEnd() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void leaveMap() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void death() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void action(Action action) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sell() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drop() {
		// TODO Auto-generated method stub
		
	}
}

package fr.nekotine.vi6.objet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;

public class Lantern extends Objet{
	public Lantern(ObjetsList objet, Player player, Game game) {
		super(objet, new ItemStack(Material.LANTERN), game);
		player.getInventory().addItem(itemStack);
	}
	@Override
	public void gameStart() {
		// TODO Auto-generated method stub
		
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
}

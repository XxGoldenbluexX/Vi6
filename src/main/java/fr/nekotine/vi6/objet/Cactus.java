package fr.nekotine.vi6.objet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.utils.Objet;

public class Cactus extends Objet{

	public Cactus(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, new ItemStack(Material.CACTUS), game, player);
		// TODO Auto-generated constructor stub
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
	public void leaveMap(Player holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void death(Player holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sell(Player holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void action(Action action, Player holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(Player holder) {
		// TODO Auto-generated method stub
		
	}

}
package fr.nekotine.vi6.objet.list;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;

public class Surcharge extends Objet{
	private static int cooldownTicks=20*20;
	private static int buffDurationTicks=5*20;
	public Surcharge(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(ObjetsList.SURCHARGE.getInShopMaterial(), 1, ObjetsList.SURCHARGE.getInShopName(),
				ObjetsList.SURCHARGE.getInShopLore()), game, player);
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
		if(action!=Action.PHYSICAL) cast(holder);
	}

	@Override
	public void drop(Player holder) {
		cast(holder);
	}

	@Override
	public void cooldownEnded() {
		System.out.println("COOLDOWN ENDED");
		updateItem(IsCreator.createItemStack(ObjetsList.SURCHARGE.getInShopMaterial(), 1, ObjetsList.SURCHARGE.getInShopName(),
				ObjetsList.SURCHARGE.getInShopLore()));
	}
	private void cast(Player holder) {
		holder.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, buffDurationTicks, 9, false, false, true));
		holder.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, buffDurationTicks, 1, false, false, true));
		holder.getWorld().playSound(holder.getLocation(),Sound.ENTITY_POLAR_BEAR_WARNING, SoundCategory.MASTER, 0.5f, 0.5f);
		setCooldown(cooldownTicks);
	}
}

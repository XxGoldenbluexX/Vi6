package fr.nekotine.vi6.objet.list;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.TempBlock;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;
import java.util.Arrays;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BuissonFurtif extends Objet {
	
	private static final ItemStack HEADBUSH = IsCreator.createItemStack(Material.OAK_LEAVES, 1,ChatColor.GREEN + "Buisson");
	private static final double DETECTION_RANGE_IN_BLOCKS = 2.0D;
	private static final Material[] BUSHTYPE = {Material.PEONY, Material.TALL_GRASS, Material.LARGE_FERN,Material.LILAC, Material.ROSE_BUSH};
	
	private final StatusEffect INSONDABLE = new StatusEffect(Effects.Insondable);
	private final StatusEffect INVISIBLE = new StatusEffect(Effects.Invisible);
	
	private TempBlock bush1_bot;
	private TempBlock bush1_top;
	private TempBlock bush2_bot;
	private TempBlock bush2_top;
	private byte nbBush = 0;
	private boolean invisibleAdded = false;
	private boolean insondableAdded = false;

	public BuissonFurtif(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void destroy() {
		super.destroy();
		if (this.bush1_bot != null)
			this.bush1_bot.reset();
		if (this.bush1_top != null)
			this.bush1_top.reset();
		if (this.bush2_bot != null)
			this.bush2_bot.reset();
		if (this.bush2_top != null)
			this.bush2_top.reset();
	}

	public void tick() {
		if (Arrays.<Material>stream(BUSHTYPE).anyMatch(e -> (e == getOwner().getLocation().getBlock().getType()))
				&& !isGuardNear(getOwner())) {
			if (!this.insondableAdded) {
				getOwnerWrapper().addStatusEffect(this.INSONDABLE);
				this.insondableAdded = true;
			}
			if (!this.invisibleAdded) {
				getOwnerWrapper().addStatusEffect(this.INVISIBLE);
				this.invisibleAdded = true;
			}
		} else {
			this.INSONDABLE.remove();
			this.INVISIBLE.remove();
			this.insondableAdded = false;
			this.invisibleAdded = false;
		}
	}

	private boolean placeBush(Location loc) {
		Block blockBot = loc.getBlock();
		Block blockTop = loc.clone().add(0.0D, 1.0D, 0.0D).getBlock();
		if (this.nbBush < 2 && onGround() && blockBot.getType() == Material.AIR && blockTop.getType() == Material.AIR) {
			if (this.nbBush == 0) {
				this.bush1_top = (new TempBlock(blockTop, Bukkit.createBlockData(Material.TALL_GRASS, "[half=upper]")))
						.set();
				this.bush1_bot = (new TempBlock(blockBot, Bukkit.createBlockData(Material.TALL_GRASS, "[half=lower]")))
						.set();
			} else {
				this.bush2_top = (new TempBlock(blockTop, Bukkit.createBlockData(Material.TALL_GRASS, "[half=upper]")))
						.set();
				this.bush2_bot = (new TempBlock(blockBot, Bukkit.createBlockData(Material.TALL_GRASS, "[half=lower]")))
						.set();
			}
			this.nbBush = (byte) (this.nbBush + 1);
			return true;
		}
		return false;
	}

	public void leaveMap() {
		disable();
	}

	public void death() {
		disable();
	}

	public void action(Action action) {
	}

	public void drop() {
		if (placeBush(getOwner().getLocation())) {
			getOwner().playSound(Sound.sound(Key.key("block.chorus_flower.grow"), Sound.Source.AMBIENT, 1.0F, 1.0F));
			getOwner().sendMessage((Component) MessageFormater.formatWithColorCodes('§',
					DisplayTexts.getMessage("objet_BuissonFurtif_placeBush"),
					new MessageFormater[]{new MessageFormater("§v", getOwnerWrapper().getCurrentSalle())}));
		} else {
			getOwner().playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1.0F, 1.0F));
		}
	}

	private boolean isGuardNear(Player holder) {
		if (holder != null)
			for (Map.Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
				if (((PlayerWrapper) p.getValue()).getTeam() == Team.GARDE
						&& holder.getLocation().distance(((Player) p.getKey()).getLocation()) <= DETECTION_RANGE_IN_BLOCKS)
					return true;
			}
		return false;
	}

	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}

	public void cooldownEnded() {
	}

	public void disable() {
		super.disable();
		PlayerInventory inv = getOwner().getInventory();
		if (HEADBUSH.isSimilar(inv.getHelmet()))inv.setHelmet(null);
		this.INSONDABLE.remove();
		this.INVISIBLE.remove();
		this.insondableAdded = false;
		this.invisibleAdded = false;
	}

	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		getOwner().getInventory().setHelmet(HEADBUSH);
	}
}
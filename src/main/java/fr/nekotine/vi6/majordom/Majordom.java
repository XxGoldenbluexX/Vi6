package fr.nekotine.vi6.majordom;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Majordom implements Listener{

	public static Majordom instance;
	private static final List<Block> list = new ArrayList<>(10);
	private static final int CLOSE_DELAY=40;
	private Plugin mainRef;
	
	public Majordom(Plugin plugin) {
		mainRef = plugin;
		instance=this;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block b = event.getClickedBlock();
		if (event.getHand()==EquipmentSlot.HAND && b!=null
				&& event.getPlayer().getGameMode()!=GameMode.SPECTATOR && event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			BlockData data = b.getBlockData();
			if (data instanceof Openable) {
				event.setCancelled(true);
				Openable o = (Openable) data;
				if (data instanceof Door) {
					Block bminus = b.getLocation().subtract(0, 1, 0).getBlock();
					BlockData dataminus = b.getBlockData();
					if (dataminus instanceof Openable) {
						onOpenableToggle(bminus,(Openable) dataminus);
					}else {
						onOpenableToggle(b,o);
					}
				}else {
					onOpenableToggle(b, o);
				}
			}
		}
	}
	private void onOpenableToggle(Block b, Openable o) {
		if (list.contains(b)) {
			list.remove(b);
			o.setOpen(!o.isOpen());
		}else {
			o.setOpen(!o.isOpen());
			list.add(b);
			new BukkitRunnable() {
				@Override
				public void run() {
					list.remove(b);
					o.setOpen(!o.isOpen());
					b.setBlockData(o);
				}
			}.runTaskLater(mainRef, CLOSE_DELAY);
		}
		b.setBlockData(o);
	}
}

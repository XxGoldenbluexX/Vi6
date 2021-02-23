package fr.nekotine.vi6.statuseffects;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import fr.nekotine.vi6.Vi6Main;

public class ItemHider {
	
	private static ItemHider instance;
	
	private ProtocolManager mnger;
	private final ArrayList<Player> hiden = new ArrayList<>();
	private final ArrayList<Player> hideFrom = new ArrayList<>();
	
	public ItemHider(ProtocolManager p, Vi6Main main) {
		mnger=p;
		instance=this;
		mnger.addPacketListener(new PacketAdapter(main,PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				super.onPacketSending(event);
				PacketContainer packet = event.getPacket();
				if (!hideFrom.contains(event.getPlayer())) return;
				for (Player p : hiden) {
					if (p.getEntityId()==packet.getIntegers().read(0)) {
						List<Pair<ItemSlot, ItemStack>> pairList = packet.getSlotStackPairLists().read(0);
						if (pairList!=null) {
							pairList.clear();
							for (EnumWrappers.ItemSlot slot : EnumWrappers.ItemSlot.values()) {
								pairList.add(new Pair<>(slot, new ItemStack(Material.AIR)));
							}
							packet.getSlotStackPairLists().write(0, pairList);
						}
						return;
					}
				}
			}
		});
	}
	
	public void hidePlayer(Player p) {
		if (!hiden.contains(p)) {
			hiden.add(p);
			for (Player pp : Bukkit.getServer().getOnlinePlayers()) {
				PacketContainer equipPacket = mnger.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
				equipPacket.getIntegers().write(0, p.getEntityId());
				List<Pair<ItemSlot, ItemStack>> pairList = new ArrayList<>();
				for (EnumWrappers.ItemSlot slot : EnumWrappers.ItemSlot.values()) {
					pairList.add(new Pair<>(slot, new ItemStack(Material.AIR)));
				}
				equipPacket.getSlotStackPairLists().write(0, pairList);
				try {
					mnger.sendServerPacket(pp, equipPacket);
				} catch (InvocationTargetException e) {
				}
			}
		}
	}
	
	public void unHidePlayer(Player p) {
		hiden.remove(p);
	}
	
	public void hideFromPlayer(Player p) {
		if (!hideFrom.contains(p)) {
			//TODO
		}
	}
	
	public void unHideFromPlayer(Player p) {
		
	}
	
	public static ItemHider get() {
		return instance;
	}
}

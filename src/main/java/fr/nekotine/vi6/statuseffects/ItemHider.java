package fr.nekotine.vi6.statuseffects;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;

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
				PacketContainer packet = event.getPacket();
				if (!hideFrom.contains(event.getPlayer())) return;
				for (Player p : hiden) {
					if (p.getEntityId()==packet.getIntegers().read(0)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		});
	}
	
	public void hidePlayer(Player p) {
		if (!hiden.contains(p)) {
			PacketContainer packet = hidePacket(p);
			for (Player pp : hideFrom) {
				try {
					mnger.sendServerPacket(pp,packet);
				}catch(InvocationTargetException e) {
					
				}
			}
			hiden.add(p);
		}
	}
	
	public void unHidePlayer(Player p) {
		if (hiden.contains(p)) {
			hiden.remove(p);
			PacketContainer packet = showPacket(p);
			for (Player pp : hideFrom) {
				try {
					mnger.sendServerPacket(pp,packet);
				}catch(InvocationTargetException e) {
					
				}
			}
		}
	}
	
	public void hideFromPlayer(Player p) {
		if (!hideFrom.contains(p)) {
			for (Player pp : hiden) {
				try {
					mnger.sendServerPacket(p,hidePacket(pp));
				}catch(InvocationTargetException e) {
					
				}
			}
			hideFrom.add(p);
		}
	}
	
	public void unHideFromPlayer(Player p) {
		if (hideFrom.contains(p)) {
			hideFrom.remove(p);
			for (Player pp : hiden) {
				try {
					mnger.sendServerPacket(p,showPacket(pp));
				}catch(InvocationTargetException e) {
					
				}
			}
		}
	}
	
	public static ItemHider get() {
		return instance;
	}
	
	private PacketContainer hidePacket(Player p) {
		PacketContainer equipPacket = mnger.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
		equipPacket.getIntegers().write(0, p.getEntityId());
		List<Pair<ItemSlot, ItemStack>> pairList = new ArrayList<>();
		for (EnumWrappers.ItemSlot slot : EnumWrappers.ItemSlot.values()) {
			pairList.add(new Pair<>(slot, new ItemStack(Material.AIR)));
		}
		equipPacket.getSlotStackPairLists().write(0, pairList);
		return equipPacket;
	}
	
	private PacketContainer showPacket(Player p) {
		PacketContainer equipPacket = mnger.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
		equipPacket.getIntegers().write(0, p.getEntityId());
		List<Pair<ItemSlot, ItemStack>> pairList = new ArrayList<>();
		PlayerInventory inv = p.getInventory();
		for (EnumWrappers.ItemSlot slot : EnumWrappers.ItemSlot.values()) {
			pairList.add(new Pair<>(slot, inv.getItem(getSlot(slot))));
		}
		equipPacket.getSlotStackPairLists().write(0, pairList);
		return equipPacket;
	}
	
	private static final EquipmentSlot getSlot(EnumWrappers.ItemSlot slot) {
		switch (slot) {
		case CHEST:
			return EquipmentSlot.CHEST;
		case FEET:
			return EquipmentSlot.FEET;
		case HEAD:
			return EquipmentSlot.HEAD;
		case LEGS:
			return EquipmentSlot.LEGS;
		case MAINHAND:
			return EquipmentSlot.HAND;
		case OFFHAND:
			return EquipmentSlot.OFF_HAND;
		default:
			return EquipmentSlot.CHEST;
		}
	}
}

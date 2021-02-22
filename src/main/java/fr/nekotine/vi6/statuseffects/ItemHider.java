package fr.nekotine.vi6.statuseffects;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import fr.nekotine.vi6.Vi6Main;

public class ItemHider {
	
	private static ItemHider instance;
	
	private ProtocolManager mnger;
	private ArrayList<Player> players = new ArrayList<>();
	
	public ItemHider(ProtocolManager p, Vi6Main main) {
		mnger=p;
		mnger.addPacketListener(new PacketAdapter(main,PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				super.onPacketSending(event);
				PacketContainer packet = event.getPacket();
				for (Player p : players) {
					if (p.getEntityId()==packet.getIntegers().read(0)) {
						packet.getSlotStackPairLists().read(0).clear();
						return;
					}
				}
			}
		});
	}
	
	public void addPlayer(Player p) {
		if (!players.contains(p)) {
			players.add(p);
		}
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
	}
	
	public static ItemHider get() {
		return instance;
	}
}

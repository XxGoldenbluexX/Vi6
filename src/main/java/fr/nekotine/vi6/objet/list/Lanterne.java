package fr.nekotine.vi6.objet.list;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Lanterne extends Objet {

	private Lant lantern1;
	private Lant lantern2;
	private final ArrayList<Player> toShow = new ArrayList<>();
	
	public Lanterne(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
		for (Entry<Player,PlayerWrapper> e : game.getPlayerList().entrySet()) {
			if (e.getValue().getTeam()==Team.GARDE && !e.getKey().equals(player))toShow.add(e.getKey());
		}
		toShow.trimToSize();
	}
	
	@Override
	public void gameEnd() {
		if (lantern1!=null) {lantern1.destroy();lantern1=null;}
		if (lantern2!=null) {lantern2.destroy();lantern2=null;}
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void leaveMap(Player holder) {
	}

	@Override
	public void death(Player holder) {
	}

	@Override
	public void sell(Player holder) {
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
	}
	
	private class Lant{
		
		private final Location loc;
		private final Vi6Main mainref;
		private final int lanternID;
		private final int guardianID;
		private final ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
		private final PacketContainer maintainPacket;
		private final BukkitRunnable lanternMaintainer = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : toShow) {
					try {
						pmanager.sendServerPacket(p, maintainPacket);
					} catch (InvocationTargetException e) {
					}
				}
			}
		};
		
		private Lant(Location loc, Vi6Main main) {
			this.loc=loc;
			mainref=main;
			lanternID = (int)(Math.random() * Integer.MAX_VALUE);
			guardianID = lanternID+1;
			maintainPacket = pmanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			//SPAWN LANTERN
			//PacketContainer
		}
		
		private void destroy() {
			
		}
		
	}

}

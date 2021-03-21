package fr.nekotine.vi6.objet.list;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class DeadRinger extends Objet{
	private static int INVISIBILITY_DURATION_TICK=60;
	private ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
	public DeadRinger(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
	}

	@Override
	public void leaveMap() {
	}

	@Override
	public void action(Action var1) {
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(getOwner().equals(e.getEntity())
		&& (getDisplayedItem().equals(getOwner().getInventory().getItemInMainHand()) || getDisplayedItem().equals(getOwner().getInventory().getItemInOffHand())) 
		/*&& getOwner().getHealth()-e.getFinalDamage()<=0*/) {
			e.setCancelled(true);
			for (Map.Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
				if(p.getValue().getTeam()==Team.GARDE) {
					fakeDeath(p.getKey());
					p.getKey().sendMessage(MessageFormater.formatWithColorCodes('§', DisplayTexts.getMessage("game_death"),
					new MessageFormater("§p", String.valueOf(getOwner().getName())),
					new MessageFormater("§n", String.valueOf(getOwnerWrapper().getStealedArtefactList().size()))));
				}
			}
			StatusEffect se = new StatusEffect(Effects.Invisible);
			getOwnerWrapper().addStatusEffect(se);
			se.autoRemove(getMain(), INVISIBILITY_DURATION_TICK);
			destroy();
		}
	}
	/*@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		System.out.println("Player ded");
		if(getOwner().equals(e.getEntity())
		&& (getDisplayedItem().isSimilar(getOwner().getInventory().getItemInMainHand()) || getDisplayedItem().isSimilar(getOwner().getInventory().getItemInOffHand()))) {
			System.out.println("He had item");
			e.setCancelled(true);
			for(Entry<Player, PlayerWrapper> player : super.getGame().getPlayerMap().entrySet()) {
				if(player.getValue().getTeam()==Team.GARDE) fakeDeath(player.getKey());
			}
			StatusEffect se = new StatusEffect(Effects.Invisible);
			getOwnerWrapper().addStatusEffect(se);
			se.autoRemove(getMain(), INVISIBILITY_DURATION_TICK);
			destroy();
		}
	}*/
	private void fakeDeath(Player toTrick) {
		System.out.println("Faking death to "+toTrick.getName());
		PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_STATUS);
		packet.getIntegers().write(0, getOwner().getEntityId());
		packet.getBytes().write(1, (byte)3);
		/*WrappedDataWatcher watcher = new WrappedDataWatcher();
		Serializer serializer = Registry.get(Byte.class);
		watcher.setEntity(toTrick);
		watcher.setObject(0, serializer, (byte) 3);
		packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());*/
	    try {
	    	pmanager.sendServerPacket(toTrick, packet);
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    }
	}
}

package fr.nekotine.vi6.objet.list;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerStealEvent;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Artefact.CaptureState;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class GlobeVoyant extends Objet{
	private static final int MESSAGE_DELAY_TICKS=100;
	private int delay=MESSAGE_DELAY_TICKS;
	private Artefact attached;
	private boolean stolen=false;
	private Item eye;
	public GlobeVoyant(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
		if(stolen) {
			delay--;
			if(delay==0) {
				for(Entry<Player, PlayerWrapper> player : getGame().getPlayerMap().entrySet()) {
					if(player.getValue().getTeam()==Team.GARDE) {
						player.getKey().playSound(Sound.sound(Key.key("entity.vindicator.celebrate"), Sound.Source.VOICE, 0.5f, 1.5f));
						player.getKey().sendMessage(MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("objet_globe_triggered"), new MessageFormater("§a", attached.getDisplayName())));
					}
				}
				eye.remove();
				disable();
			}
		}
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
		if(var1!=Action.PHYSICAL) {
			ArrayList<Artefact> artefactList = getGame().getMap().getArtefactList();
			for(Objet obj : super.getGame().getObjets()) {
				if(obj instanceof GlobeVoyant) {
					GlobeVoyant gv = (GlobeVoyant)obj;
					if(gv.getAttached()!=null) artefactList.remove(gv.getAttached());
				}
			}
			double nearestD = 0;
			Artefact nearestA = null;
			for(Artefact artefact : artefactList) {
				if(artefact.getStatus()==CaptureState.STEALABLE) {
					double distance = getOwner().getLocation().distanceSquared(artefact.getBlockLoc());
					if(nearestA==null || distance<nearestD) {
						nearestA=artefact;
						nearestD=distance;
					}
				}
			}
			if(nearestA!=null) {
				attached=nearestA;
				super.getOwner().getWorld().playSound(attached.getBlockLoc(), "entity.slime.jump", 1, 2);
				super.getOwner().getWorld().playSound(attached.getBlockLoc(), "entity.shulker_bullet.hit", 0.5f, 2);
				super.getOwner().playSound(Sound.sound(Key.key("entity.slime.jump"), Sound.Source.VOICE, 1, 2));
				super.getOwner().playSound(Sound.sound(Key.key("entity.shulker_bullet.hit"), Sound.Source.VOICE, 0.5f, 2));
				summonItem();
				getOwner().sendMessage(MessageFormater.formatWithColorCodes('§',
				DisplayTexts.getMessage("objet_globe_placed"), new MessageFormater("§an", attached.getDisplayName())));
			}else {
				getOwner().sendMessage(MessageFormater.formatWithColorCodes('§',
				DisplayTexts.getMessage("objet_globe_noArtefact")));
			}
			consume();
		}
	}

	@Override
	public void drop() {
	}

	public static int getMessageDelayTicks() {
		return MESSAGE_DELAY_TICKS;
	}
	@EventHandler
	public void artefactSteal(PlayerStealEvent e) {
		if(e.getGame().equals(getGame()) && e.getArtefact().equals(attached)) stolen=true;
	}
	public Artefact getAttached() {
		return attached;
	}
	private void summonItem() {
		Location spawnLoc = attached.getBlockLoc().clone();
		spawnLoc.add(0.5, 1, 0.5);
		eye = spawnLoc.getWorld().dropItem(spawnLoc, new ItemStack(Material.ENDER_EYE));
		eye.setCanMobPickup(false);
		eye.setCanPlayerPickup(false);
		eye.setGravity(false);
		eye.setInvulnerable(true);
		eye.setPersistent(true);
		eye.setVelocity(new Vector(0, 0, 0));
		for(Entry<Player, PlayerWrapper> player : getGame().getPlayerMap().entrySet()) {
			if(player.getValue().getTeam()==Team.VOLEUR) {
				PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				packet.getIntegerArrays().write(0, new int[] {eye.getEntityId()});
			    try {
			    	ProtocolLibrary.getProtocolManager().sendServerPacket(player.getKey(), packet);
			    } catch (InvocationTargetException e) {
			        e.printStackTrace();
			    }
			}
		}
	}
	public void disable() {
		super.disable();
		if(eye!=null) eye.remove();
	}
}

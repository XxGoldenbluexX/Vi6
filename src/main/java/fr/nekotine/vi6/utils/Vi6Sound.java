package fr.nekotine.vi6.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public enum Vi6Sound{
		ERROR(new Sound[] {
		Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 1),
		Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 0)
		}),
		SUCCESS(new Sound[] {
		Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 1),
		Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 2)
		}),
		SMOKEPOOL(new Sound[] {
		Sound.sound(Key.key("minecraft:block.fire.extinguish"), Sound.Source.AMBIENT, 1.0F, 0.0F)		
		}),
		NO(new Sound[] {
		Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1.0F, 1.0F)	
		}),
		BARRICADE(new Sound[] {
		Sound.sound(Key.key("block.barrel.close"), Sound.Source.VOICE, 1.0F, 0.0F),
		Sound.sound(Key.key("item.shield.block"), Sound.Source.VOICE, 1.0F, 0.0F)	
		}),
		BUISSON(new Sound[] {
		Sound.sound(Key.key("block.chorus_flower.grow"), Sound.Source.AMBIENT, 1.0F, 1.0F)	
		}),
		CACTUS(new Sound[] {
		Sound.sound(Key.key("entity.bee.sting"), Sound.Source.AMBIENT, 1.0F, 1.0F)		
		}),
		CHAMP_DE_FORCE(new Sound[] {
		Sound.sound(Key.key("block.barrel.close"), Sound.Source.VOICE, 1.0F, 0.0F),
		Sound.sound(Key.key("item.shield.block"), Sound.Source.VOICE, 1.0F, 0.0F)
		}),
		DEAD_RINGER(new Sound[] {
		Sound.sound(Key.key("minecraft:entity.evoker.prepare_summon"), Sound.Source.MASTER, 1.0F, 1.7F),
		Sound.sound(Key.key("minecraft:entity.witch.celebrate"), Sound.Source.MASTER, 1.0F, 0.7F)
		}),
		DOUBLE_SAUT(new Sound[] {
		Sound.sound(Key.key("item.firecharge.use"), Sound.Source.AMBIENT, 0.3F, 1.5F),
		Sound.sound(Key.key("item.hoe.till"), Sound.Source.AMBIENT, 1.0F, 0.1F)
		}),
		GLOBE_VOYANT_POSE(new Sound[] {
		Sound.sound(Key.key("entity.slime.jump"), Sound.Source.VOICE, 1, 2),
		Sound.sound(Key.key("entity.shulker_bullet.hit"), Sound.Source.VOICE, 0.5f, 2)
		}),
		GLOBE_VOYANT_TRIGGER(new Sound[] {
		Sound.sound(Key.key("entity.vindicator.celebrate"), Sound.Source.VOICE, 0.5f, 1.5f)
		}),
		WOLOLO(new Sound[] {
		Sound.sound(Key.key("entity.evoker.prepare_wololo"), Sound.Source.VOICE, 1.0F, 1.2F)
		}),
		OMBRE_KILL(new Sound[] {
		Sound.sound(Key.key("entity.wither.spawn"), Sound.Source.MASTER, 0.5F, 1.0F),
		Sound.sound(Key.key("entity.zombie_villager.cure"), Sound.Source.MASTER, 0.5F, 1.0F)
		}),
		OMBRE_TELEPORT(new Sound[] {
		Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.MASTER, 1.0F, 1.0F)
		}),
		OMNICAPTEUR_POSE(new Sound[] {
		Sound.sound(Key.key("entity.vex.hurt"), Sound.Source.MASTER, 2, 0.1F),
		Sound.sound(Key.key("item.flintandsteel.use"), Sound.Source.MASTER, 2, 0.1F)
		}),
		OMNICAPTEUR_DETECT(new Sound[] {
		Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 0.3f, 0.1F),
		Sound.sound(Key.key("block.note_block.cow_bell"), Sound.Source.MASTER, 2, 0.5F),
		Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 2, 0.1F)
		}),
		PIEGECAPTEUR_POSE(new Sound[] {
		Sound.sound(Key.key("item.flintandsteel.use"), Sound.Source.MASTER, 2, 0),
		}),
		PIEGECAPTEUR_TRIGGER(new Sound[] {
		Sound.sound(Key.key("entity.stray.death"), Sound.Source.MASTER, 0.5f, 0),
		Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.MASTER, 1, 2)
		}),
		SONAR_NOBODY(new Sound[] {
		Sound.sound(Key.key("block.note_block.bit"), Sound.Source.VOICE, 1, 0.5f),
		Sound.sound(Key.key("block.note_block.bit"), Sound.Source.VOICE, 2, 0)
		}),
		SONAR_DETECT(new Sound[] {
		Sound.sound(Key.key("block.note_block.bit"), Sound.Source.VOICE, 1, 2),
		Sound.sound(Key.key("block.note_block.bit"), Sound.Source.VOICE, 2, 1)
		}),
		SURCHARGE(new Sound[] {
		Sound.sound(Key.key("entity.polar_bear.warning"), Sound.Source.MASTER, 0.5f, 0.5f)
		}),
		TAZER_SHOCKING(new Sound[] {
		Sound.sound(Key.key("block.beehive.work"), Sound.Source.MASTER, 2, 0.1f),		
		}),
		TELEPORTEUR_PLACE(new Sound[] {
		Sound.sound(Key.key("block.piston.extend"), Sound.Source.VOICE, 1.0F, 2.0F),
		Sound.sound(Key.key("block.iron_trapdoor.open"), Sound.Source.VOICE, 1.0F, 0.6F)
		}),
		TELEPORTEUR_CREATE_PORTAL(new Sound[] {
		Sound.sound(Key.key("block.respawn_anchor.deplete"), Sound.Source.VOICE, 1.0F,1.4F),
		Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.VOICE, 0.1F, 1.4F),
		}),
		BROUILLEUR_0(new Sound[] {
		Sound.sound(Key.key("entity.creeper.primed"), Sound.Source.VOICE, 1, 2),
		Sound.sound(Key.key("entity.generic.extinguish_fire"), Sound.Source.VOICE, 1, 0)	
		}),
		BROUILLEUR_1(new Sound[] {
		Sound.sound(Key.key("entity.generic.extinguish_fire"), Sound.Source.VOICE, 1, 0.5f)	
		}),
		BROUILLEUR_2(new Sound[] {
		Sound.sound(Key.key("entity.creeper.primed"), Sound.Source.VOICE, 1, 2)
		}),
		BROUILLEUR_3(new Sound[] {
		Sound.sound(Key.key("entity.generic.extinguish_fire"), Sound.Source.VOICE, 1, 1.5f)	
		}),
		DEPHASAGE_ACTIVATING_0(new Sound[] {
		Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1)
		}),
		DEPHASAGE_ACTIVATING_1(new Sound[] {
		Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1.5f)
		}),
		DEPHASAGE_ACTIVATING_2(new Sound[] {
		Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 2),
		Sound.sound(Key.key("block.beacon.activate"), Sound.Source.VOICE, 1, 1.5f),
		Sound.sound(Key.key("entity.illusioner.prepare_blindness"), Sound.Source.VOICE, 1, 1)
		}),
		DEPHASAGE_WARNING_0(new Sound[] {
		Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 2)
		}),
		DEPHASAGE_WARNING_1(new Sound[] {
		Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1.5f)
		}),
		DEPHASAGE_WARNING_2(new Sound[] {
		Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1),
		Sound.sound(Key.key("block.beacon.deactivate"), Sound.Source.VOICE, 1, 1.5f)
		}),
		SCAN(new Sound[] {
		Sound.sound(Key.key("block.beacon.power_select"), Sound.Source.MASTER, 1, 1)
		}),
		LANTERNE_POSE(new Sound[] {
		Sound.sound(Key.key("block.anvil.place"), Sound.Source.MASTER, 1, 2)
		}),
		LANTERNE_PRE_TELEPORT(new Sound[] {
		Sound.sound(Key.key("block.anvil.use"), Sound.Source.MASTER, 1, 1.6f)
		}),
		LANTERNE_POST_TELEPORT(new Sound[] {
		Sound.sound(Key.key("block.anvil.use"), Sound.Source.MASTER, 1, 1.6f),
		Sound.sound(Key.key("entity.shulker.shoot"), Sound.Source.MASTER, 2, 0.1f),
		Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1, 1.5f)
		}),
		INVISNEAK(new Sound[] {
		Sound.sound(Key.key("block.lava.extinguish"), Sound.Source.MASTER, 0.1f, 2)
		}),
		GPS_SHOOT(new Sound[] {
		Sound.sound(Key.key("item.crossbow.shoot"), Sound.Source.MASTER, 1, 1)
		}),
		IEM(new Sound[] {
		Sound.sound(Key.key("block.respawn_anchor.set_spawn"), Sound.Source.MASTER, 0.5f, 2),
		Sound.sound(Key.key("entity.lightning_bolt.thunder"), Sound.Source.MASTER, 0.5f, 1.8f)
		}),
		;
		public Sound[] getSounds() {
			return sounds;
		}
		private final Sound[] sounds;
		private Vi6Sound(Sound[] sounds) {
			this.sounds=sounds;
		}
		public void playAtLocation(Location location) {
			for(Sound advSound : sounds) {
				location.getWorld().playSound(advSound, location.getX(), location.getY(), location.getZ());
			}
		}
		public void playForPlayer(Player player) {
			for(Sound advSound : sounds) {
				player.playSound(advSound);
			}
		}
		public void playForPlayer(Player player, Location location) {
			for(Sound advSound : sounds) {
				player.playSound(advSound, location.getX(), location.getY(), location.getZ());
			}
		}
	}

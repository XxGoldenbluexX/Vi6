package fr.nekotine.vi6.statuseffects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.vi6.wrappers.PlayerWrapper;

public enum Effects {
	
	Invisible((p,w)->{
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, true));
		ItemHider.get().hidePlayer(p);
	},(p,w)->{
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		ItemHider.get().unHidePlayer(p);
	}),
	Decouvert((p,w)->{},(p,w)->{}),
	Sondé((p,w)->{},(p,w)->{}),
	Insondable((p,w)->{},(p,w)->{}),
	Glow((p,w)->{
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false, true));
	},(p,w)->{
		p.removePotionEffect(PotionEffectType.GLOWING);
	}),
	InGlowable((p,w)->{},(p,w)->{}),
	Fantomatique((p,w)->{},(p,w)->{}),
	NoDamage((p,w)->{p.setNoDamageTicks(Integer.MAX_VALUE);},(p,w)->{p.setNoDamageTicks(1);});
	
	private final StatusEffectUpdate enableLambda;
	private final StatusEffectUpdate disableLambda;
	
	private Effects(StatusEffectUpdate enable,StatusEffectUpdate disable) {
		this.enableLambda=enable;
		this.disableLambda=disable;
	}
	
	public void enable(Player p, PlayerWrapper w) {
		enableLambda.run(p, w);
	}
	
	public void disable(Player p, PlayerWrapper w) {
		disableLambda.run(p, w);
	}
	
	public static Effects getCounter(Effects e) {
		switch(e) {
		case Invisible:
			return Decouvert;
		case Sondé:
			return Insondable;
		case Glow:
			return InGlowable;
		default: return null;
		}
	}
	
	public static Effects getCountered(Effects e) {
		switch(e) {
		case Decouvert:
			return Invisible;
		case Insondable:
			return Sondé;
		case InGlowable:
			return Glow;
		default: return null;
		}
	}
	
	public static boolean isCounterable(Effects e) {
		return getCounter(e)!=null;
	}
	
	public static boolean isCountering(Effects e) {
		return getCountered(e)!=null;
	}
	
}


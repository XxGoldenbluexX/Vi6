package fr.nekotine.vi6.statuseffects;

public enum Effects {
	
	Invisible,
	Decouvert,
	Sondé,
	Insondable,
	Glow,
	InGlowable,
	Fantomatique;
	
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
	
	public static boolean isCounterable(Effects e) {
		return getCounter(e)!=null;
	}
	
}

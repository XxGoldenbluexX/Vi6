package fr.nekotine.vi6.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsSkins;

public class ObjetsSkinsTagType implements PersistentDataType<String, ObjetsSkins>{
	public static String namespacedKey = "ObjetSkin";
	public static NamespacedKey getNamespacedKey(Vi6Main main) {
		return new NamespacedKey(main, namespacedKey);
	}
	@Override
	public Class<String> getPrimitiveType() {
		return String.class;
	}

	@Override
	public Class<ObjetsSkins> getComplexType() {
		return ObjetsSkins.class;
	}

	@Override
	public String toPrimitive(ObjetsSkins complex, PersistentDataAdapterContext context) {
		return complex.toString();
	}
	
	@Override
	public ObjetsSkins fromPrimitive(String primitive, PersistentDataAdapterContext context) {
		return ObjetsSkins.valueOf(primitive);
	}
}

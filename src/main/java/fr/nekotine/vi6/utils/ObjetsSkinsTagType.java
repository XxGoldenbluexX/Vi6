package fr.nekotine.vi6.utils;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import fr.nekotine.vi6.objet.ObjetsSkins;

public class ObjetsSkinsTagType implements PersistentDataType<String, ObjetsSkins>{

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

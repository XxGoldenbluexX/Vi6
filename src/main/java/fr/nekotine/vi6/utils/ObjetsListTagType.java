package fr.nekotine.vi6.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;

public class ObjetsListTagType implements PersistentDataType<String, ObjetsList>{
	public static String namespacedKey = "ObjetTag";
	public static NamespacedKey getNamespacedKey(Vi6Main main) {
		return new NamespacedKey(main, namespacedKey);
	}
	@Override
	public Class<String> getPrimitiveType() {
		return String.class;
	}

	@Override
	public Class<ObjetsList> getComplexType() {
		return ObjetsList.class;
	}

	@Override
	public String toPrimitive(ObjetsList complex, PersistentDataAdapterContext context) {
		return complex.toString();
	}
	
	@Override
	public ObjetsList fromPrimitive(String primitive, PersistentDataAdapterContext context) {
		return ObjetsList.valueOf(primitive);
	}
}

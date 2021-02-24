package fr.nekotine.vi6.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageFormater {
	
	private final String key;
	private final String value;
	
	public MessageFormater(String key,String value) {
		this.key=key;
		this.value=value;
	}
	
	public static String format(String text,MessageFormater... f) {
		for (MessageFormater ff : f) {
			if (ff.getKey()!=null && ff.getValue()!=null) text=text.replace(ff.getKey(), ff.getValue());
		}
		return text;
	}
	
	public static @NonNull TextComponent formatWithColorCodes(char colorCode,String text,MessageFormater... f) {
		return LegacyComponentSerializer.legacy(colorCode).deserialize(format(text,f));
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
}

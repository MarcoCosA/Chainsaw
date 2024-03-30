package me.BerylliumOranges.listeners.items.traits.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;

public class TraitSerializationUtils {

	public static String serializeTrait(ItemTrait trait) throws IOException {
		ByteArrayOutputStream io = new ByteArrayOutputStream();
		BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
		os.writeObject(trait);
		os.flush();
		byte[] serializedBytes = io.toByteArray();
		String encoded = Base64.getEncoder().encodeToString(serializedBytes);
		os.close();
		return encoded;
	}

	public static ItemTrait deserializeTrait(String base64) throws IOException, ClassNotFoundException {
		byte[] serializedBytes = Base64.getDecoder().decode(base64);
		ByteArrayInputStream io = new ByteArrayInputStream(serializedBytes);
		BukkitObjectInputStream is = new BukkitObjectInputStream(io);
		ItemTrait trait = (ItemTrait) is.readObject();
		is.close();
		return trait;
	}
}

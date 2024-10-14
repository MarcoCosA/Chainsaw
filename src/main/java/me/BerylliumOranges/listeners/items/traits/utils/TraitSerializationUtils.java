package me.BerylliumOranges.listeners.items.traits.utils;

import java.io.IOException;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;

public class TraitSerializationUtils {

	private static Gson gson = null;

	public static String serializeTrait(ItemTrait trait) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", trait.getClass().getSimpleName()); // Add class type information
		jsonObject.add("data", getGson().toJsonTree(trait));
		return Base64.getEncoder().encodeToString(jsonObject.toString().getBytes());
	}

	public static ItemTrait deserializeTrait(String base64) throws IOException {
		byte[] decodedBytes = Base64.getDecoder().decode(base64);
		String json = new String(decodedBytes);
		return getGson().fromJson(json, ItemTrait.class);
	}

	public static void setupGson() {
		gson = GsonSetup.createGson();
	}

	public static Gson getGson() {
		if (gson == null)
			setupGson();
		return gson;
	}
}

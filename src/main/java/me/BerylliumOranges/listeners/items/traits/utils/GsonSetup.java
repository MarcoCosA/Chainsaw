package me.BerylliumOranges.listeners.items.traits.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSetup {
    public static Gson createGson() {
        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    }
}

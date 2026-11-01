package com.prostudios.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prostudios.client.StudiosClientClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/** Module id -> enabled. */
	public Map<String, Boolean> modules = new LinkedHashMap<>();
	/** HUD element id -> [x, y] screen position. */
	public Map<String, int[]> hudPositions = new LinkedHashMap<>();
	/** How far zoom goes (0.1 - 0.9); lower = stronger zoom. */
	public double zoomFactor = 0.25;

	public boolean isEnabled(String id) {
		return modules.getOrDefault(id, true);
	}

	public void setEnabled(String id, boolean enabled) {
		modules.put(id, enabled);
	}

	public int[] pos(String id, int defX, int defY) {
		return hudPositions.computeIfAbsent(id, k -> new int[]{defX, defY});
	}

	public void setPos(String id, int x, int y) {
		hudPositions.put(id, new int[]{x, y});
	}

	public void resetPositions() {
		hudPositions.clear();
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("studios-client.json");
	}

	public void save() {
		try {
			Files.createDirectories(path().getParent());
			Files.writeString(path(), GSON.toJson(this));
		} catch (IOException e) {
			StudiosClientClient.LOGGER.error("Failed to save studios-client config", e);
		}
	}

	public static ClientConfig load() {
		ClientConfig cfg = new ClientConfig();
		Path p = path();
		if (Files.exists(p)) {
			try {
				ClientConfig read = GSON.fromJson(Files.readString(p), ClientConfig.class);
				if (read != null) cfg = read;
			} catch (Exception e) {
				StudiosClientClient.LOGGER.error("Failed to read studios-client config, using defaults", e);
			}
		}
		return cfg;
	}
}

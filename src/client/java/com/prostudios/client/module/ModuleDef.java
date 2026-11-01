package com.prostudios.client.module;

public record ModuleDef(String id, String name) {
	public static final ModuleDef FPS = new ModuleDef("fps", "FPS Display");
	public static final ModuleDef CPS = new ModuleDef("cps", "CPS Counter");
	public static final ModuleDef KEYSTROKES = new ModuleDef("keystrokes", "Keystrokes");
	public static final ModuleDef COORDS = new ModuleDef("coords", "Coordinates");
	public static final ModuleDef PING = new ModuleDef("ping", "Ping Display");
	public static final ModuleDef ZOOM = new ModuleDef("zoom", "Zoom (hold C)");
	public static final ModuleDef FULLBRIGHT = new ModuleDef("fullbright", "Fullbright");

	public static final ModuleDef[] ALL = {FPS, CPS, KEYSTROKES, COORDS, PING, ZOOM, FULLBRIGHT};
}

package net.askarian.MisterErwin.CTF.API.events;

import java.util.HashMap;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CTFMatchEndEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String mapname;
	private final HashMap<String, PlayerScore> stats;

	public CTFMatchEndEvent(String mapname, HashMap<String, PlayerScore> stats) {
		this.mapname = mapname;
		this.stats = stats;
	}

	public String getMapname() {
		return this.mapname;
	}

	public HashMap<String, PlayerScore> getScores() {
		return this.stats;
	}

	public PlayerScore getScore(String name) {
		if (!this.stats.containsKey(name))
			return null;

		return this.stats.get(name);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}

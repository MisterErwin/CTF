package net.askarian.MisterErwin.CTF.API.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CTFInTickEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final int time;

	public CTFInTickEvent(int time) {
		this.time = time;
	}

	public int getTime() {
		return this.time;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}

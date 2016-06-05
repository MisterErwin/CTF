package net.askarian.MisterErwin.CTF.API.events;

import net.askarian.MisterErwin.CTF.translator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CTFPlayerDeathEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Player killer;
	private String message4Killer = "", message4Player = "";

	public CTFPlayerDeathEvent(Player player, translator trans) {

		this.player = player;
		this.message4Killer = trans.get("youkilled").replace("&P",
				player.getName());
		this.killer = (player.getKiller() != null) ? player.getKiller()
				: player;
		this.message4Player = trans.get("killedby").replace("&P",
				killer.getName());

		if (this.killer.getName().equals(this.player.getName()))
			this.message4Player = trans.get("killedyourself").replace("&P",
					player.getPlayer().getName());
	}

	public String getMessage4Player() {
		return this.message4Player;
	}

	public void setMessage4Player(String m) {
		this.message4Player = m;
		this.getPlayer().getServer().getConsoleSender()
				.sendMessage(ChatColor.AQUA + this.getMessage4Player());
		if (!this.message4Player.equals(m))
			this.getPlayer()
					.getServer()
					.getConsoleSender()
					.sendMessage(
							ChatColor.RED + "Can't set the custom message: "
									+ m + " instead " + this.message4Player);
	}

	public String getMessage4Killer() {
		return this.message4Killer;
	}

	public void setMessage4Killer(String m) {
		this.message4Killer = m;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Player getKiller() {
		return this.killer;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}

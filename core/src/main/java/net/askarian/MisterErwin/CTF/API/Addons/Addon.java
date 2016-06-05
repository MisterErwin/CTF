package net.askarian.MisterErwin.CTF.API.Addons;

import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class Addon implements Listener {

	public abstract String getName();

	public abstract boolean useEvents();

	public abstract String getVersion();

	public abstract List<String> getAuthors();

	public abstract void onEnable(CTF inheritance);

	public abstract void onDisable();

}

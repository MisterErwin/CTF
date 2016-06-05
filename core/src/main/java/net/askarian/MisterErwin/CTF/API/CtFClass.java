package net.askarian.MisterErwin.CTF.API;

import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class CtFClass {
	public CTF plugin = CTF.getPlugin();

	public abstract void enable();

	public abstract void disable();

	public abstract String getVersion();

	public abstract String getName();

	public abstract String getCommand();

	public abstract String getDescription();

	public abstract List<String> getAuthors();

	public abstract boolean givekit(Player p);

	public abstract boolean saytime(Player p, int time);

	public abstract ItemStack getIcon();

}

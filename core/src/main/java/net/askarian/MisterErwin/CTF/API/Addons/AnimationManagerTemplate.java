package net.askarian.MisterErwin.CTF.API.Addons;

import net.askarian.MisterErwin.CTF.CTF;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class AnimationManagerTemplate extends BukkitRunnable {

	public abstract long getPeriod();

	public abstract String getName();

	public abstract void enable(CTF plugin);

}

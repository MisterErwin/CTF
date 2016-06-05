package net.askarian.MisterErwin.CTF.listener;

import net.askarian.MisterErwin.CTF.API.events.CTFPlayerDeathEvent;
import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TrackerListener implements Listener {

	private CTF plugin;

	public TrackerListener(CTF pl) {
		this.plugin = pl;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(CTFPlayerDeathEvent event) {
		plugin.tracker.addDeath(event.getPlayer());

		if (event.getKiller() != null
				&& !event.getKiller().equals(event.getPlayer())) {
			plugin.tracker.addKill(event.getKiller());
			plugin.cm.SendMessage(event.getKiller(), event.getMessage4Killer());
			plugin.cm.SendMessage(event.getPlayer(), event.getMessage4Player());
		} else {
			String a = "";
			if (event.getKiller() == null)
				a = a + "no Killer";
			if (event.getKiller().equals(event.getPlayer()))
				a = a + "Player=Killer";
			plugin.cm.SendMessage(event.getPlayer(),
					plugin.trans.get("killedyourself"));

		}

	}
}

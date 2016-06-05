package net.askarian.MisterErwin.CTF.threads;

import net.askarian.MisterErwin.CTF.CTF;

public class BroadcasterThread implements Runnable {

	private CTF plugin;
	private int broadcastmID = 1;

	public BroadcasterThread(CTF pl) {
		plugin = pl;
	}

	public void run() {
		if (plugin.conm.broadcasts.isEmpty()) {
			plugin.cm.broadcasterMessage = "No broadcast set!";
			plugin.cm.allupdate();
		}
		if (plugin.conm.broadcasts.size() <= broadcastmID) // End of Broadcasts
			broadcastmID = 0;
		plugin.cm.broadcasterMessage = plugin.conm.broadcasts.get(broadcastmID);
		plugin.cm.allupdate();
		broadcastmID++;
	}

}

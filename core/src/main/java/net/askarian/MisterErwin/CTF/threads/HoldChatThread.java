package net.askarian.MisterErwin.CTF.threads;

import net.askarian.MisterErwin.CTF.CTF;

public class HoldChatThread implements Runnable {

	private CTF plugin;

	public HoldChatThread(CTF pl) {
		plugin = pl;
	}

	public void run() {
		plugin.cm.showstats = true;
		plugin.cm.allupdate();
		plugin.cm.showstats = false;
	}
}

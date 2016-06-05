package net.askarian.MisterErwin.CTF;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

public class ConfigManager {

	private CTF plugin;

	public ArrayList<String> broadcasts = new ArrayList<String>();

	public ConfigManager(CTF pl) {
		this.plugin = pl;

		this.broadcasts
				.add("Soldiers can climb walls with right-clicking walls!");
		this.broadcasts
				.add("Archer can give instakills when they 20blocks away player!");
		this.broadcasts.add("Yeay, I'm in the broadcast :3");

	}

	private HashMap<String, String> values = new HashMap<String, String>();

	private HashMap<String, String> registred = new HashMap<String, String>();

	public void register(String adress, String def) {

		if (!this.registred.containsKey(adress)) {
			this.registred.put(adress, def);
		}
	}

	public String get(String adress) {

		if (this.values.containsKey(adress)) {
			String ret = this.values.get(adress);
			// ret.replaceAll("a", ChatColor.)
			return ret;

		}

		return "ERROR " + adress + " is empty!";
	}

	@SuppressWarnings("unchecked")
	public void load() {

		readBroadcast();

		File f = new File("plugins/CTF/CTF.properties");

		HashMap<String, String> ll = new HashMap<String, String>();
		ll.clear();

		String sep = ":";

		String l, a, v;

		try {
			if (!f.exists()) {
				f = new File("plugins/CTF/CTF.properties");
				;
				if (!f.exists()) {
					plugin.log.info("Rewrited!");
					this.write(f, this.registred, sep);
				}
			}
			Scanner snr = new Scanner(f);

			while (snr.hasNext()) {
				try {
					l = snr.nextLine().trim();
					a = l.split(sep)[0];
					v = l.split(sep)[1];

					ll.put(a, v);
					plugin.log.info(a + "=" + v);
				} catch (Exception ignored) {

				}
			}

			// Add it?

			Iterator<Entry<String, String>> itr = ((HashMap<String, String>) registred
					.clone()).entrySet().iterator();

			while (itr.hasNext()) {
				Entry<String, String> itlr = itr.next();
				if (!ll.containsKey(itlr.getKey())) {
					// Remove it
					ll.put(itlr.getKey(), itlr.getValue());
					itr.remove();
				} else
					plugin.log.info(itlr.getKey() + "<========== is there! ("
							+ ll.get(itlr.getKey()));
			}

			snr.close();

			// Write the file to the config

			this.write(f, ll, sep);

			this.values.putAll(ll);

		} catch (Exception ignored) {

		}
	}

	@SuppressWarnings("unchecked")
	private void write(File f, HashMap<String, String> hm, String sep)
			throws IOException {
		f.delete();

		f.createNewFile();

		PrintWriter pw = new PrintWriter(f);

		Iterator<Entry<String, String>> it = ((HashMap<String, String>) hm
				.clone()).entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, String> itl = it.next();
			pw.write(itl.getKey() + sep + itl.getValue() + "\r\n");
			plugin.log.info(itl.getKey() + sep + itl.getValue() + "\r\n");
		}

		pw.close();
	}

	private void readBroadcast() {
		File f = new File("plugins/CTF/broadcast.txt");
		this.broadcasts.clear();
		try {
			if (!f.exists()) {
				try {
					File oldFile = f;
					if (!oldFile.getParentFile().isDirectory()) {
						oldFile.mkdirs();
					}
					oldFile.delete();

					File newFile = f;
					newFile.createNewFile();

					PrintWriter pw = new PrintWriter(f);
					pw.write("Soldiers can climb walls with right-clicking walls with their sword!\r\n");
					pw.write("Medics can heal teammates by hitting them!\r\n");
					pw.write("Engineers can place turrets that auto-fire at enemys!\r\n");
					pw.write("Pyros kills burning enemys by hitting them with their axe!\r\n");
					pw.write("Eat some cake from regenerator for full restoration!\r\n");
					pw.write("Archers make insta-kills to 25m away enemys!\r\n");

					pw.close();
				} catch (Exception e) {
					plugin.log.warning("[CTF] Can't create Broadcast file! "
							+ f.getAbsolutePath());
				}
			}
			Scanner snr = new Scanner(f);
			while (snr.hasNext()) {
				String bc = snr.nextLine().trim();
				this.broadcasts.add(bc);

			}

			snr.close();

		} catch (Exception e) {
			plugin.log.warning("[CTF] Error while loading Broadcast file");
		}
		return;
	}
}

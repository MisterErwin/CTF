package net.askarian.MisterErwin.CTF;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

public class translator {

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

	public void load(String lang) {
		File f = new File("plugins/CTF/" + lang + ".yml");

		HashMap<String, String> ll = new HashMap<String, String>();

		String sep = "°";

		String l, a, v;

		try {
			if (!f.exists()) {
				f = new File("plugins/CTF/en.yml");
				if (!f.exists())
					this.write(f, this.registred, sep);
			}
			Scanner snr = new Scanner(f);

			while (snr.hasNext()) {
				try {
					l = snr.nextLine().trim();
					a = l.split(sep)[0];
					v = l.split(sep)[1];

					ll.put(a, v);
				} catch (Exception ignored) {

				}
			}

			// Add it?

			Iterator<Entry<String, String>> itr = registred.entrySet()
					.iterator();

			while (itr.hasNext()) {
				Entry<String, String> itlr = itr.next();
				if (!ll.containsKey(itlr.getKey())) {
					// Remove it
					ll.put(itlr.getKey(), itlr.getValue());
				}
			}

			snr.close();

			// Write the file to the config

			this.write(f, ll, sep);

			this.values.putAll(ll);

		} catch (Exception ignored) {

		}
	}

	private void write(File f, HashMap hm, String sep) throws IOException {
		f.delete();

		f.createNewFile();

		PrintWriter pw = new PrintWriter(f);

		Iterator<Entry<String, String>> it = hm.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, String> itl = it.next();
			pw.write(itl.getKey() + sep + itl.getValue() + "\r\n");

		}

		pw.close();
	}

}

package net.askarian.MisterErwin.CTF.API.Addons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import net.askarian.MisterErwin.CTF.CTF;
import net.askarian.MisterErwin.CTF.CTF;

public class AddonManager {

	public CTF plugin;

	public List<Addon> Addons = new ArrayList<Addon>();

	private AnimationManagerTemplate am;

	public AddonManager(CTF pl) {
		plugin = pl;
		File dir = new File(this.plugin.getDataFolder() + File.separator
				+ "addons");
		if (!dir.isDirectory())
			dir.mkdirs();

		// ////Load Managers

		// Load AnimationManager
		File f = new File(dir, "AnimationManager.class");
		if (!f.exists()) {
			try {
				plugin.log.info("Coping the default AnimationManager!");
				this.copy(plugin.getResource("AnimationManager.class"), f);
			} catch (IOException e) {
				plugin.log
						.info("Error while coping the default AnimationManager");
				e.printStackTrace();
			}

		}

		try {
			ClassLoader cl = new URLClassLoader(
					new URL[] { f.toURI().toURL() },
					AnimationManagerTemplate.class.getClassLoader());
			Class<?> aclass = cl.loadClass(f.getName().substring(0,
					f.getName().lastIndexOf(".")));
			Object object = aclass.newInstance();
			if (!(object instanceof AnimationManagerTemplate)) {
				plugin.log.warning("Not a valid AnimationManager: "
						+ aclass.getSimpleName());
				f.renameTo(new File(dir, "AnimationManager.class.old"));
				try {
					plugin.log.info("Coping the default AnimationManager!");
					this.copy(plugin.getResource("AnimationManager.class"), f);

					// load the new manager
					cl = new URLClassLoader(new URL[] { f.toURI().toURL() },
							AnimationManagerTemplate.class.getClassLoader());
					aclass = cl.loadClass(f.getName().substring(0,
							f.getName().lastIndexOf(".")));
					object = aclass.newInstance();
					if (!(object instanceof AnimationManagerTemplate)) {
						plugin.log
								.warning("Can't load the default animation Manager! - Something went terrible wrong!");
					}
				} catch (IOException e) {
					plugin.log
							.info("Error while coping the default AnimationManager b/c yours is corrupted!");
					e.printStackTrace();
				}
			}
			this.am = (AnimationManagerTemplate) object;
			this.am.enable(plugin);

			this.am.runTaskTimer(plugin, 20, this.am.getPeriod());

			plugin.log.info("Enabled AnimationManager: " + am.getName());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// LOAD subs
		File subs = new File(dir, "subs");
		if (!subs.isDirectory())
			subs.mkdirs();
		this.Addons.clear();
		this.Addons.addAll(new AddonLoader(this.plugin).load(subs
				.getAbsolutePath()));

		for (Addon a : this.Addons) {
			a.onEnable(plugin);
			String authors = "";
			for (String at : a.getAuthors())
				authors += ", " + at;

			authors.replaceFirst(",", "");
			if (a.useEvents())
				plugin.getServer().getPluginManager().registerEvents(a, plugin);
			plugin.log.info("Enabled Addon: " + a.getName() + " made by "
					+ authors);
		}

		if (this.Addons.size() == 0) {
			f = new File(dir, "readme.yml");
			if (!f.exists()) {
				try {
					f.createNewFile();
					PrintWriter pw = new PrintWriter(f);
					pw.write("As You can see, I've added Addons\r\n");
					pw.write("To learn more about them (or download some):\r\n");
					pw.write("http://dev.bukkit.org/bukkit-plugins/ctf/forum/addons/\r\n");
					pw.write("~MisterErwin");
					pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	private void copy(InputStream input, File target) throws IOException {
		if (target.exists()) {
			throw new IOException("File already exists!");
		}

		File parentDir = target.getParentFile();

		if (!parentDir.exists() && !parentDir.mkdirs()) {
			throw new IOException("Failed at creating directories!");
		}

		if (!parentDir.isDirectory()) {
			throw new IOException("The parent of this file is no directory!?");
		}

		if (!target.createNewFile()) {
			throw new IOException("Failed at creating new empty file!");
		}

		byte[] buffer = new byte[1024];

		OutputStream output = new FileOutputStream(target);

		int realLength;

		while ((realLength = input.read(buffer)) > 0) {
			output.write(buffer, 0, realLength);
		}

		output.flush();
		output.close();
	}

}

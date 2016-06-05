package net.askarian.MisterErwin.CTF.API.Addons;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.askarian.MisterErwin.CTF.CTF;

import org.bukkit.ChatColor;

public class AddonLoader {

	protected CTF plugin;
	private ClassLoader loader;
	private ClassLoader jarloader;

	public AddonLoader(CTF inheritance) {
		this.plugin = inheritance;
	}

	public List<Addon> load(String directory) {

		List<Addon> Addons = new ArrayList<Addon>();

		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdir();
		}

		try {
			loader = new URLClassLoader(new URL[] { dir.toURI().toURL() },
					Addon.class.getClassLoader());
		} catch (MalformedURLException ex) {
			plugin.log.warning("AddonLoader encountered an exception: ");
			ex.printStackTrace();
			return Addons;
		}

		boolean loaded = false;
		String lastnameoa = "";

		for (File file : dir.listFiles()) {
			if (lastnameoa != "") {
				if (loaded)
					plugin.getServer().getConsoleSender()
							.sendMessage(ChatColor.GREEN + lastnameoa);
				else
					plugin.getServer().getConsoleSender()
							.sendMessage(ChatColor.RED + lastnameoa);
			}

			loaded = false;
			lastnameoa = file.getName();

			// /
			if (!file.getName().endsWith(".class")) {
				if (!file.getName().endsWith(".jar")) {
					plugin.getServer().getConsoleSender()
							.sendMessage(ChatColor.GRAY + file.getName());
					lastnameoa = "";
					continue;
				} else {
					plugin.log.info("FOUND A JAR!");
					try {
						JarFile jarFile = new JarFile(file.getAbsolutePath());

						Enumeration<JarEntry> e = jarFile.entries();

						URL[] urls = { new URL("jar:file:"
								+ file.getAbsolutePath() + "!/") };
						jarloader = null;
						jarloader = new URLClassLoader(urls,
								Addon.class.getClassLoader());
						// jarloader = URLClassLoader.newInstance(urls);

						while (e.hasMoreElements()) {
							JarEntry je = e.nextElement();
							if (je.isDirectory()
									|| !je.getName().endsWith(".class")) {
								continue;
							}

							if (je == null || je.getName() == null)
								continue;

							// -6 because of .class
							String className = je.getName().substring(0,
									je.getName().length() - 6);
							className = className.replace('/', '.');
							try {
								plugin.log.info(className + jarFile.getName());
								Class<?> aclass = jarloader
										.loadClass(className);
								Object object = aclass.newInstance();
								if (!(object instanceof Addon)) {
									plugin.log.warning("Not a valid add-on: "
											+ aclass.getSimpleName());
									continue;
								}
								Addon a = (Addon) object;

								if (a == null || a.getName() == null) {
									plugin.getServer()
											.getConsoleSender()
											.sendMessage(
													ChatColor.RED
															+ je.getName()
															+ " is invalid!");
									continue;
								}

								// for (Addon ocl : Addon) {
								// if (ocl.getCommand().equals(a.getCommand())){
								// plugin.log.warning(" CTF-Class " +
								// ocl.getName() + " and " + a.getName() +
								// " collapses in their commands! Class " +
								// a.getName() + " is disabled!");
								// a = null;
								// continue;
								// }
								// }
								Addons.add(a);
								loaded = true;
								plugin.getServer()
										.getConsoleSender()
										.sendMessage(
												ChatColor.BLUE
														+ "Inside .jar: "
														+ a.getName()
														+ ChatColor.GREEN
														+ " loaded!");

								// plugin.log.info("Loaded " + a.getName() +
								// " v"
								// + a.getVersion() + " by "
								// + a.getAuthors().get(0));

								je = null;
							} catch (Exception ex) {
								plugin.log.warning("A "
										+ ex.getLocalizedMessage() + " caused "
										+ className + " to fail to load!");
								ex.printStackTrace();
							} catch (Error ex) {
								plugin.log.warning("A "
										+ ex.getLocalizedMessage() + " caused "
										+ className + " to fail to load!");
								ex.printStackTrace();
							}

						}

						e = null;
						jarloader = null;
						jarFile.close();
						jarFile = null;

					} catch (IOException e) {
						System.err.println("Error: " + e.getMessage());
					}

					loaded = false;

				}
				continue;
			}
			String name = file.getName().substring(0,
					file.getName().lastIndexOf("."));
			try {
				Class<?> aclass = loader.loadClass(name);
				Object object = aclass.newInstance();
				if (!(object instanceof Addon)) {
					plugin.log.warning("Not a valid add-on: "
							+ aclass.getSimpleName());
					continue;
				}
				Addon a = (Addon) object;
				// for (CtFClass ocl : Classes) {
				// if (ocl.getCommand().equals(a.getCommand())){
				// plugin.log.warning(" CTF-Class " + ocl.getName() + " and " +
				// a.getName() + " collapses in their commands! Class " +
				// a.getName() + " is disabled!");
				// a = null;
				// continue;
				// }
				// }
				// a.enable();
				Addons.add(a);
				// a.onEnable(plugin);
				// loaded = true;
				// plugin.log.info("Loaded " + a.getName() + " v"
				// + a.getVersion() + " by "
				// + a.getAuthors().get(0));
			} catch (Exception ex) {
				plugin.log.warning("A " + ex.getLocalizedMessage() + " caused "
						+ name + " to fail to load!");
				ex.printStackTrace();
			} catch (Error ex) {
				plugin.log.warning("A " + ex.getLocalizedMessage() + " caused "
						+ name + " to fail to load!");
				ex.printStackTrace();
			}
		}

		if (Addons.size() == 0) {
			plugin.getServer()
					.getConsoleSender()
					.sendMessage(
							ChatColor.AQUA
									+ "[CTF] No addons were loaded ... Maybe you onna download some!");
			plugin.getServer()
					.getConsoleSender()
					.sendMessage(
							ChatColor.AQUA
									+ "[CTF] Just look into the forum @ dev.bukkit!!!");

		}
		return Addons;
	}
}

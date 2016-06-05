package net.askarian.MisterErwin.CTF.API;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.askarian.MisterErwin.CTF.CTF;
import net.askarian.MisterErwin.CTF.util.CTFUtils;

import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ExClassLoader {

	protected CTF plugin;
	private ClassLoader loader;
	private ClassLoader jarloader;

	public ExClassLoader(CTF inheritance) {
		this.plugin = inheritance;
	}

	public List<CtFClass> load(String directory) {

		List<CtFClass> Classes = new ArrayList<CtFClass>();

		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdir();
		}

		try {
			loader = new URLClassLoader(new URL[] { dir.toURI().toURL() },
					CtFClass.class.getClassLoader());
		} catch (MalformedURLException ex) {
			plugin.log.warning("ClassLoader encountered an exception: ");
			ex.printStackTrace();
			return Classes;
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
					if (!file.getName().endsWith(".yml")) {
						plugin.getServer().getConsoleSender()
								.sendMessage(ChatColor.GRAY + file.getName());
						lastnameoa = "";
						continue;
					} else {
						try {
							plugin.getServer()
									.getConsoleSender()
									.sendMessage(
											ChatColor.GOLD
													+ "Found a yml file!");

							YamlConfiguration c = YamlConfiguration
									.loadConfiguration(file);

							// ///////////////
							YMLClass a = new YMLClass(c.getString("name"),
									c.getString("cmd"), c.getString("desc"),
									c.getString("version"), c.getInt("ItemID"),
									(short) c.getInt("ItemData"));

							// /////////////////

							List<String> l = c.getStringList("Items");

							for (String is : l) {
								String lt[] = is.split("<>");
								String[] sp = lt[1].split("--");
								ItemStack r = new ItemStack(0);
								if (sp.length == 1) {
									r.setTypeId(Integer.valueOf(sp[0].trim()));
								} else if (sp.length == 2) {
									r.setTypeId(Integer.valueOf(sp[0].trim()));
									r.setDurability(Short.valueOf(sp[1].trim()));
								} else if (sp.length == 3) {
									r.setTypeId(Integer.valueOf(sp[0].trim()));
									r.setDurability(Short.valueOf(sp[1].trim()));
									ItemMeta im = r.getItemMeta();
									im.setDisplayName(sp[2]);
									r.setItemMeta(im);
								} else if (sp.length == 4) {
									r.setTypeId(Integer.valueOf(sp[0].trim()));
									r.setDurability(Short.valueOf(sp[1].trim()));
									ItemMeta im = r.getItemMeta();
									im.setDisplayName(sp[2]);
									String[] sp2 = sp[3].trim().split("<br>");

									ArrayList<String> al = new ArrayList<String>(
											Arrays.asList(sp2));

									im.setLore(al);

									r.setItemMeta(im);
								}

								a.addItem(Integer.valueOf(lt[0].trim()), r);

							}

							l = c.getStringList("Effects");

							for (String is : l) {
								String[] sp = is.split(":");

								String types = sp[0];
								PotionEffectType type = null;
								if (CTFUtils.isInteger(types))
									type = PotionEffectType.getById(Integer
											.parseInt(types));
								else
									type = PotionEffectType.getByName(types);

								if (type == null) {
									continue;
								}

								PotionEffect pe = new PotionEffect(type,
										Integer.parseInt(sp[1]),
										Integer.parseInt(sp[2]));

								a.addEffect(pe);

							}

							// ///////////////////

							for (CtFClass ocl : Classes) {
								if (ocl.getCommand().equals(a.getCommand())) {
									plugin.log
											.warning(" CTF-Class "
													+ ocl.getName()
													+ " and "
													+ a.getName()
													+ " collapses in their commands! Class "
													+ a.getName()
													+ " is disabled!");
									a = null;
									continue;
								}
							}
							a.enable();
							Classes.add(a);
							loaded = true;

							plugin.log.info("Loaded " + a.getName() + " v"
									+ a.getVersion() + " by "
									+ a.getAuthors().get(0));

							continue;

						} catch (Exception e) {
							e.printStackTrace();
						}
						loaded = false;

					}
				} else {
					plugin.log.info("FOUND A JAR!");
					try {
						JarFile jarFile = new JarFile(file.getAbsolutePath());

						Enumeration e = jarFile.entries();

						URL[] urls = { new URL("jar:file:"
								+ file.getAbsolutePath() + "!/") };
						jarloader = null;
						jarloader = new URLClassLoader(urls,
								CtFClass.class.getClassLoader());
						// jarloader = URLClassLoader.newInstance(urls);

						while (e.hasMoreElements()) {
							JarEntry je = (JarEntry) e.nextElement();
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
								Class aclass = jarloader.loadClass(className);
								Object object = aclass.newInstance();
								if (!(object instanceof CtFClass)) {
									plugin.log.warning("Not a valid add-on: "
											+ aclass.getSimpleName());
									continue;
								}
								CtFClass a = (CtFClass) object;

								if (a == null || a.getName() == null) {
									plugin.getServer()
											.getConsoleSender()
											.sendMessage(
													ChatColor.RED
															+ je.getName()
															+ " is invalid!");
									continue;
								}

								for (CtFClass ocl : Classes) {
									if (ocl.getCommand().equals(a.getCommand())) {
										plugin.log
												.warning(" CTF-Class "
														+ ocl.getName()
														+ " and "
														+ a.getName()
														+ " collapses in their commands! Class "
														+ a.getName()
														+ " is disabled!");
										a = null;
										continue;
									}
								}
								a.enable();
								Classes.add(a);
								loaded = true;
								plugin.getServer()
										.getConsoleSender()
										.sendMessage(
												ChatColor.BLUE
														+ "Inside .jar: "
														+ a.getName()
														+ ChatColor.GREEN
														+ " loaded!");

								plugin.log.info("Loaded " + a.getName() + " v"
										+ a.getVersion() + " by "
										+ a.getAuthors().get(0));

								je = null;
							} catch (Exception ex) {
								plugin.log.warning("A "
										+ ex.getLocalizedMessage() + " caused "
										+ className + " to fail to load!");
								// ex.printStackTrace();
							} catch (Error ex) {
								plugin.log.warning("A "
										+ ex.getLocalizedMessage() + " caused "
										+ className + " to fail to load!");
								// ex.printStackTrace();
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
				if (!(object instanceof CtFClass)) {
					plugin.log.warning("Not a valid add-on: "
							+ aclass.getSimpleName());
					continue;
				}
				CtFClass a = (CtFClass) object;
				for (CtFClass ocl : Classes) {
					if (ocl.getCommand().equals(a.getCommand())) {
						plugin.log.warning(" CTF-Class " + ocl.getName()
								+ " and " + a.getName()
								+ " collapses in their commands! Class "
								+ a.getName() + " is disabled!");
						a = null;
						continue;
					}
				}
				a.enable();
				Classes.add(a);
				loaded = true;
				plugin.log.info("Loaded " + a.getName() + " v" + a.getVersion()
						+ " by " + a.getAuthors().get(0));
			} catch (Exception ex) {
				plugin.log.warning("A " + ex.getLocalizedMessage() + " caused "
						+ name + " to fail to load!");
				// ex.printStackTrace();
			} catch (Error ex) {
				plugin.log.warning("A " + ex.getLocalizedMessage() + " caused "
						+ name + " to fail to load!");
				// ex.printStackTrace();
			}
		}

		if (Classes.size() == 0) {
			plugin.getServer()
					.getConsoleSender()
					.sendMessage(
							ChatColor.RED
									+ "[CTF] No classes were loaded ... download some!");
			plugin.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "[CTF] Will disable me!!");

			plugin.getServer()
					.getPluginManager()
					.disablePlugin(
							plugin.getServer().getPluginManager()
									.getPlugin("CTF"));

		}
		return Classes;
	}
}

package net.askarian.MisterErwin.CTF;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Iterator;

public class CommandManager implements CommandExecutor {
	private CTF plugin;

	public CommandManager(CTF inheritance) {
		this.plugin = inheritance;
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player)
				&& !(cmd.getName().equalsIgnoreCase("ctf")) && args.length > 0
				&& args[1].equals("start")) {
			sender.sendMessage("You must execute this command as a player!!!");
			return true;
		}
		// if (cmd.getName().equalsIgnoreCase("archer")) {
		// this.plugin.clm.setKit((Player)sender, "archer");
		// return true;
		// }
		if (cmd.getName().equalsIgnoreCase("class")) {
			if (args.length > 0)
				changeclass((Player) sender, args[0]);
			else {
				// plugin.cm.changemode ((Player)sender, "classes");

				plugin.clm.showClassMenu((Player) sender);
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("vote")) {
			// if (args.length > 0)
			// changeclass((Player) sender, args[0]);
			// else{
			// // plugin.cm.changemode ((Player)sender, "classes");

			if (!plugin.Game.after) {
				if (plugin.tm.isinGame((Player) sender))
					plugin.cm.SendMessage((Player) sender, ChatColor.RED
							+ "This is not the time to vote");
				return true;
			}
			if (!plugin.tm.isinGame((Player) sender))
				sender.sendMessage(ChatColor.RED
						+ "You are not in the Game => You can't vote!!!");

			plugin.MM.showVoteMenu((Player) sender);
			// }
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("CTF")) {
			if (args.length == 0) {
				sender.sendMessage("CTF - Commands");

				sender.sendMessage(ChatColor.AQUA
						+ " /CTF join       -  join the game");
				sender.sendMessage(ChatColor.YELLOW
						+ " /CTF leave      -  leave the game");

				if (sender.hasPermission("CTF.admin")) {
					sender.sendMessage(ChatColor.RED
							+ "~~~~~Admin Commands~~~~~");
					sender.sendMessage(ChatColor.AQUA
							+ " /CTF start      -  loads first Map and start Game");
					sender.sendMessage(ChatColor.YELLOW
							+ " /CTF stop      -  stops the game");
					sender.sendMessage(ChatColor.AQUA
							+ " /CTF next       -  loads next Map and starts the Game");
					sender.sendMessage(ChatColor.YELLOW
							+ " /CTF tp         -  teleports into World");
					sender.sendMessage(ChatColor.BLUE
							+ " /CTF set Spawn  -  defines the Spawn for the world");
					sender.sendMessage(ChatColor.GREEN
							+ " /CTF set SpawnA -  defines the Spawn for team A");
					sender.sendMessage(ChatColor.BLUE
							+ " /CTF set SpawnB -  defines the Spawn for team B");
					sender.sendMessage(ChatColor.GREEN
							+ " /CTF set FlagA  -  defines the Flag for team A");
					sender.sendMessage(ChatColor.BLUE
							+ " /CTF set FlagB  -  defines the Flag for team B");
				}
			} else if (args[0].equalsIgnoreCase("join")) {
				Bukkit.dispatchCommand(sender, "class");
				// if (((!this.plugin.Game.running) && (!this.plugin.Game.idle))
				// || (this.plugin.Game.Spawn == null ||
				// this.plugin.Game.Spawn.getWorld() == null)) {
				// sender.sendMessage(ChatColor.RED + "No Game is Running!");
				// return true;
				// }
				// if (this.plugin.Game.idle) {
				// if
				// (!this.plugin.Game.waitingPlayers.containsKey(sender.getName()));
				// this.plugin.Game.waitingPlayers.put(sender.getName(),
				// "heavy");
				// } else {
				// this.plugin.tm.JoinTeam((Player)sender);
				// }this.plugin.Game.tp((Player)sender);
			} else if (args[0].equalsIgnoreCase("leave")) {
				this.plugin.tm.LeaveTeam((Player) sender, false);
			} else if ((args[0].equalsIgnoreCase("start"))
					&& (sender.hasPermission("CTF.admin"))) {
				start(sender);
			} else if ((args[0].equalsIgnoreCase("stop"))
					&& (sender.hasPermission("CTF.admin"))) {
				stop(sender);
			} else if ((args[0].equalsIgnoreCase("next"))
					&& (sender.hasPermission("CTF.admin"))) {
				if (this.plugin.Game.Spawn == null
						|| this.plugin.Game.Spawn.getWorld() == null) {
					sender.sendMessage(ChatColor.RED
							+ "No Map is loaded - use /CTF start");
					return true;
				}
				next(sender);
			} else if ((args[0].equalsIgnoreCase("tp"))
					&& (sender.hasPermission("CTF.admin"))) {
				if (this.plugin.Game.Spawn == null
						|| this.plugin.Game.Spawn.getWorld() == null) {
					sender.sendMessage(ChatColor.RED + "No Map is loaded");
					return true;
				}
				if (this.plugin.Game.FlagA == null
						|| this.plugin.Game.FlagA.getWorld() == null
						|| this.plugin.Game.FlagB == null
						|| this.plugin.Game.FlagB.getWorld() == null) {
					plugin.clm.setKit((Player) sender, plugin.clm.Classes
							.get(0).getCommand());
				}
				this.plugin.Game.tp((Player) sender);
			} else if ((args[0].equalsIgnoreCase("set"))
					&& (sender.hasPermission("CTF.admin"))) {
				if (this.plugin.Game.Spawn == null
						|| this.plugin.Game.Spawn.getWorld() == null) {
					sender.sendMessage(ChatColor.RED + "No Map is loaded");
					return true;
				}
				if (args.length > 1)
					if (args[1].equalsIgnoreCase("spawn")) {
						sender.sendMessage(ChatColor.BLUE
								+ "Spawn Location set!");
						this.plugin.Game.saveSpawnLocation(((Player) sender)
								.getLocation());
					} else if (args[1].equalsIgnoreCase("spawnA")) {
						sender.sendMessage(ChatColor.BLUE
								+ "Spawn Location A set!");
						this.plugin.Game.saveSpawnLocationA(((Player) sender)
								.getLocation());
					} else if (args[1].equalsIgnoreCase("spawnB")) {
						sender.sendMessage(ChatColor.BLUE
								+ "Spawn Location B set!");
						this.plugin.Game.saveSpawnLocationB(((Player) sender)
								.getLocation());
					} else if (args[1].equalsIgnoreCase("FlagA")) {
						this.plugin.ctfu.saveFlagA((Player) sender);
					} else if (args[1].equalsIgnoreCase("FlagB")) {
						this.plugin.ctfu.saveFlagB((Player) sender);
					} else {
						sender.sendMessage(ChatColor.RED
								+ "User /ctf set Spawn or SpawnA or SpawnB");
					}
			} else if ((args[0].equalsIgnoreCase("save"))
					&& (sender.hasPermission("CTF.admin"))) {
				if (this.plugin.Game.Spawn == null
						|| this.plugin.Game.Spawn.getWorld() == null) {
					sender.sendMessage(ChatColor.RED + "No Map is loaded");
					return true;
				}
				this.plugin.Game.Spawn.getWorld().save();
				this.plugin.Game.Spawn.getWorld().setAutoSave(false);
				sender.sendMessage("Map saved...");
			}

			return true;
		}

		return false;
	}

	private void stop(CommandSender sender) {
		if (this.plugin.Game.Spawn == null
				|| this.plugin.Game.Spawn.getWorld() == null) {
			sender.sendMessage(ChatColor.RED + "No Map is loaded");
			return;
		}
		// List players =
		// this.plugin.getServer().getWorld(this.plugin.Game.world.getName()).getPlayers();
		stop(false);
		sender.sendMessage("Map gestoppt...");

	}

	public void stop(boolean next) {
		if (this.plugin.Game.Spawn != null) {
			if (!next) {
				for (String p : plugin.tm.TeamA.keySet()) {
					plugin.tm.LeaveTeam(plugin.getServer().getPlayer(p), false);
				}
				for (String p : plugin.tm.TeamB.keySet()) {
					plugin.tm.LeaveTeam(plugin.getServer().getPlayer(p), false);
				}
			}
			for (Player worldPlayers : this.plugin.Game.Spawn.getWorld()
					.getPlayers()) {
				if (!next)
					plugin.tm.LeaveTeam(worldPlayers, false);
				plugin.Game.PlayerStorage.put(worldPlayers.getName(), true);
				worldPlayers.teleport(this.plugin.getServer().getWorlds()
						.get(0).getSpawnLocation(),
						PlayerTeleportEvent.TeleportCause.PLUGIN);
				this.plugin.ctfu.clear(worldPlayers);
				plugin.InvM.give(worldPlayers);
			}
		}
		this.plugin.Game.FlagItemA = null;
		this.plugin.Game.FlagItemB = null;

		if (this.plugin.Game.Spawn != null) {
			this.plugin.Game.Spawn.getWorld().setAutoSave(false);
			this.plugin.MM.unloadMap(this.plugin.Game.Spawn.getWorld()
					.getName());
		}

		for (World w : this.plugin.getServer().getWorlds()) {
			if (w.getName().startsWith("CTF-MAP_")) {
				plugin.log.info("Another CTF-Map is loaded! - Unload it");
				for (Player wp : w.getPlayers()) {
					wp.teleport(this.plugin.getServer().getWorlds().get(0)
							.getSpawnLocation(),
							PlayerTeleportEvent.TeleportCause.PLUGIN);
					this.plugin.ctfu.clear(wp);
				}
				this.plugin.MM.unloadMap(w.getName());
			}
		}

		this.plugin.Game.running = false;
		this.plugin.Game.idle = false;
		this.plugin.Game.after = false;

		this.plugin.Game.Spawn = null;
		this.plugin.Game.SpawnA = null;

		this.plugin.Game.buildonly = false;

		this.plugin.tracker.reset();

	}

	public void start(CommandSender sender, boolean random) {
		this.plugin.Game.start(sender, false, random);
		this.start();
	}

	public void start(CommandSender sender) {
		this.start(sender, true);
		this.start();
	}

	public void public_start(boolean vote) {
		this.plugin.Game.start(plugin.getServer().getOnlinePlayers().iterator().next(), vote,
				true);
		this.start();
	}

	private void start() {

		Iterator<?> wlist = plugin.Game.PlayerStorage.entrySet().iterator();
		Player p;
		String[] ite;
		String key;
		while (wlist.hasNext()) {

			ite = wlist.next().toString().split("=");

			key = ite[0];
			p = plugin.getServer().getPlayerExact(key);
			if (p == null || !p.isOnline())
				continue;
			this.plugin.Game.tp(p);
			p.sendMessage(ChatColor.DARK_PURPLE
					+ "You are ported in the new Game!");
			p.playSound(p.getEyeLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1, 1);
		}
		if (plugin.Game.Spawn != null)
			for (Entity e : plugin.Game.Spawn.getWorld().getEntities()) {
				if (!(e instanceof Player)) {
					e.setFireTicks(999);
					e.remove();
				}
			}
	}

	public void changeclass(Player p, String kit) {
		if (p == null || !p.isOnline())
			return;
		if (!plugin.Game.idle && !plugin.Game.running) {
			if (Boolean.valueOf(plugin.conm.get("auto"))) {
				plugin.log.info("Starting game!");
				plugin.comm.start(plugin.getServer().getConsoleSender());
			} else {
				p.sendMessage(ChatColor.RED + "No Game is running!");
				return;
			}
		}
		this.plugin.clm.setKit(p, kit);
	}

	public void next() {
		CommandSender cs = plugin.getServer().getOnlinePlayers().iterator().next();
		next(cs);
	}

	public void next(CommandSender sender) {
		stop(true);
		start(sender, false);
	}
}

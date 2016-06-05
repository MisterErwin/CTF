package net.askarian.MisterErwin.CTF;

import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.API.ExClassLoader;
import net.askarian.MisterErwin.CTF.util.IconMenu;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Wool;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClassManager {

	public CTF plugin;

	public List<CtFClass> Classes = new ArrayList<CtFClass>();

	// Vars for the classes can be found in their class

	public ClassManager(CTF pl) {
		plugin = pl;
		this.Classes.addAll(new ExClassLoader(this.plugin).load(this.plugin
				.getDataFolder() + File.separator + "classes"));
	}

	public void showClassMenu(Player p) {

		Integer x = this.Classes.size() + 1;
		Integer y = x % 9;

		if (y != 0) {
			while (y != 0) {
				x++;
				y = x % 9;
			}
		}

		// y = 4*9;

		plugin.imm.create(p, "Class Selector", x,
				new IconMenu.OptionClickEventHandler() {
					public void onOptionClick(IconMenu.OptionClickEvent event) {
						// event.getPlayer().sendMessage("You have chosen " +
						// event.getName());
						plugin.comm.changeclass(event.getPlayer(),
								event.getName());
						event.setWillClose(true);
						event.setWillDestroy(true);
					}
				});

		int i = 0;

		List<CtFClass> noperm = new ArrayList<CtFClass>();

		for (CtFClass cl : this.Classes) {
			if (cl == null || cl.getIcon() == null
					|| cl.getDescription() == null || cl.getName() == null
					|| cl.getCommand() == null)
				plugin.log.info("ERROR");
			else {
				if (!hasperm(p, cl.getCommand())) {
					noperm.add(cl);
				} else {
					// plugin.log.info(i + " " + cl.getIcon()+ " " +
					// cl.getName()+ " " + cl.getCommand()+ " " +
					// cl.getDescription());
					plugin.imm.setOption(p, i, cl.getIcon(), cl.getName(),
							cl.getCommand(), cl.getDescription());
					i++;
				}
			}
		}

		for (CtFClass cl : noperm) {
			if (cl == null || cl.getIcon() == null
					|| cl.getDescription() == null || cl.getName() == null
					|| cl.getCommand() == null)
				plugin.log.info("ERROR");
			else {
				plugin.imm.setOption(p, i, cl.getIcon(),
						ChatColor.RED + cl.getName(), cl.getCommand(),
						cl.getDescription());
				i++;

			}
		}

		noperm.clear();
		noperm = null;

		// menu
		// .setOption(3, new ItemStack(Material.APPLE, 1), "Food",
		// "The food is delicious")
		// .setOption(4, new ItemStack(Material.IRON_SWORD, 1), "Weapon",
		// "Weapons are for awesome people")
		// .setOption(5, new ItemStack(Material.EMERALD, 1), "Money",
		// "Money brings happiness");

		plugin.imm.show(p);
	}

	public void setKit(Player p, String kitname) {
		if (!p.isOnline()) {
			plugin.log.info("Player not online!!!");
			return;
		}

		String kit = "";
		Iterator<CtFClass> it = this.Classes.iterator();
		CtFClass a = null;
		while (it.hasNext()) {
			a = it.next();
			if (a != null && a.getCommand().equalsIgnoreCase(kitname)) {
				kit = kitname;
				continue;
			}
		}

		if (kit.equals("")) {
			plugin.cm.SendMessage(p, ChatColor.RED + "There's no kit named "
					+ kitname);
			return;
		}

		if (!hasperm(p, kit)) {
			notClassYet(p, kit);
			return;
		}

		if (!plugin.tm.isinGameWorld(p))
			plugin.InvM.put(p);

		p.getInventory().clear();

		plugin.ctfu.clear(p);

		if (plugin.Game.idle == true) {
			if (plugin.Game.waitingPlayers.containsKey(p.getName()))
				plugin.Game.waitingPlayers.remove(p.getName());
			plugin.Game.waitingPlayers.put(p.getName(), kit);
			if (kit.startsWith("a") || kit.startsWith("e")
					|| kit.startsWith("i") || kit.startsWith("o")
					|| kit.startsWith("u"))
				p.sendMessage("You will start the game as an " + kit);
			else
				p.sendMessage("You will start the game as a " + kit);
			plugin.Game.tp(p);
		} else {

			if (!plugin.tm.getTeam(p).equals("A") && !plugin.tm.getTeam(p).equals("B")) {
				plugin.tm.JoinTeam(p);
			}
			plugin.tm.setClass(p, kit);

			if (kit.startsWith("a") || kit.startsWith("e")
					|| kit.startsWith("i") || kit.startsWith("o")
					|| kit.startsWith("u"))
				plugin.cm.SendMessage(p, plugin.trans.get("YouarenowAN")
						.replace("&k", kit));
			else
				plugin.cm.SendMessage(p, plugin.trans.get("YouarenowA")
						.replace("&k", kit));
			if (!p.isDead())
				p.setHealth(((Damageable) p).getMaxHealth());
			if (plugin.tm.getTeam(p).equals("A"))
				p.teleport(plugin.Game.SpawnA,
						PlayerTeleportEvent.TeleportCause.PLUGIN);
			else if (plugin.tm.getTeam(p).equals("B"))
				p.teleport(plugin.Game.SpawnB,
						PlayerTeleportEvent.TeleportCause.PLUGIN);

			if (plugin.Game.running)
				giveKit(p);
			plugin.log.info(p.getName() + "is now " + kit);
		}
	}

	private void notClassYet(Player p, String kit) {
		String s = plugin.trans.get("buythatclass");
		s = s.replace("&k", kit);

		if (plugin.tm.isinGame(p)) {
			plugin.cm.SendMessage(p, s);
		} else {
			p.sendMessage(s);
			// p.sendMessage("...");
		}

	}

	public void giveKit(Player p) {
		if (!plugin.Game.running)
			return;
		String cn = plugin.tm.getPClass(p).toLowerCase();
		boolean hasFlag = false;
		Wool wool = null;
		if (plugin.Game.FlagAHolder instanceof Player
				&& ((Player) plugin.Game.FlagAHolder).getName().equals(
						p.getName())) {
			hasFlag = true;
			wool = new Wool(DyeColor.RED);
		}
		if (plugin.Game.FlagBHolder instanceof Player
				&& ((Player) plugin.Game.FlagBHolder).getName().equals(
						p.getName())) {
			hasFlag = true;
			wool = new Wool(DyeColor.BLUE);
		}
		// Wool wool = new Wool(DyeColor.RED);

		p.getInventory().clear();

		boolean done = false;

		for (CtFClass a : this.Classes) {
			if (a.getCommand().equalsIgnoreCase(cn)) {
				a.givekit(p);
				done = true;
				continue;
			}
		}

		if (!done) {
			p.sendMessage("Error: You are in no class!");
			plugin.log.warning("|Player " + p.getName()
					+ " should be in class " + cn + ", but he isn't!!!|");
			setKit(p, cn);
		}
		p.setGameMode(GameMode.SURVIVAL);
		if (hasFlag)
			p.getInventory().addItem(wool.toItemStack(1));

	}

	public boolean hasperm(Player p, String kit) {
		String perm = "CTF." + kit;

		if (plugin.Game.disallowedclasses.contains(kit)) {
			p.sendMessage(ChatColor.RED
					+ "This class is not allowed in this map!");
			plugin.cm.SendMessage(p, ChatColor.RED
					+ "This class is not allowed in this map!");
			plugin.log.info("Player " + p.getName()
					+ " tries to join a disallowed class(" + kit + ")");
			return false;
		}

		if (plugin.Game.freeclasses.contains(kit))
			return true;

		return p.hasPermission(perm);

	}

	public ArrayList<?> freeclasses(Player p) {
		ArrayList<?> fret = plugin.Game.freeclasses;

		if (this.plugin.Game.disallowedclasses.size() == 0)
			return this.plugin.Game.freeclasses;

		// for (String cl : this.plugin.Game.freeclasses) {
		//
		// }

		return fret;
	}

	public void saytime(Player p, int time) {
		String cn = plugin.tm.getPClass(p).toLowerCase();

		boolean done = false;

		for (CtFClass a : this.Classes) {
			if (a.getCommand().equalsIgnoreCase(cn)) {
				a.saytime(p, time);
				done = true;
				continue;
			}
		}

		if (!done) {
			p.sendMessage("Error: You are in no class!");
			plugin.log.warning("|Player " + p.getName()
					+ " should be in class " + cn + ", but he isn't!!!|");
			setKit(p, cn);
		}
	}

}

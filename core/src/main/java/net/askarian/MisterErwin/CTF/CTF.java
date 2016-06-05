package net.askarian.MisterErwin.CTF;

import net.askarian.MisterErwin.CTF.API.Addons.AddonManager;
import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.listener.*;
import net.askarian.MisterErwin.CTF.threads.BroadcasterThread;
import net.askarian.MisterErwin.CTF.threads.HoldChatThread;
import net.askarian.MisterErwin.CTF.threads.lookingtimer;
import net.askarian.MisterErwin.CTF.util.CTFUtils;
import net.askarian.MisterErwin.CTF.util.IconMenuManager;
import net.askarian.MisterErwin.CTF.util.InventoryManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * CTF - Made by MisterErwin
 * 
 * You are allowed to modify the hole plugin, but don't delete this comments!
 * 
 * And if you made a few cool changes, just send them to me and I'll propably
 * implement them into the plugin
 * 
 * If you've modify CTF, you do this on your OWN RISK!
 * 
 * If you onna resell my work, give me at least credits(not only in the source!)
 * and send me a PM via bukkit...
 * 
 * @author MisterErwin
 */

public class CTF extends JavaPlugin {
	public Logger log;
	public TeamManager tm;
	public ChatManager cm;
	public Tracker tracker;
	public PlayerListener pl;
	public ConfigManager conm;
	public CommandManager comm;
	public ClassManager clm;
	public MapManager MM;
	public CTFGame Game;
	public CTFUtils ctfu;
	public translator trans;
	public InventoryManager InvM;
	public IconMenuManager imm;
	public AddonManager am;
	private int BCID;
	private int CHID;
	private int tITID;
	private int LTID;
	private int IATID;
	private static CTF instance;

	Skeleton x;

	@Override
	public void onEnable() {
		instance = this;
		this.log = this.getLogger();

		imm = new IconMenuManager(this);
		trans = new translator();
		tm = new TeamManager(this);
		cm = new ChatManager(this);
		pl = new PlayerListener(this);
		tracker = new Tracker(this);
		conm = new ConfigManager(this);
		comm = new CommandManager(this);
		MM = new MapManager(this);
		Game = new CTFGame(this);
		ctfu = new CTFUtils(this);
		InvM = new InventoryManager();

		am = new AddonManager(this);

		// getCommand("archer").setExecutor(this.comm);
		// getCommand("assassin").setExecutor(this.comm);
		// getCommand("chemist").setExecutor(this.comm);
		// getCommand("dwarf").setExecutor(this.comm);
		// getCommand("engineer").setExecutor(this.comm);
		// getCommand("heavy").setExecutor(this.comm);
		// getCommand("medic").setExecutor(this.comm);
		// getCommand("necro").setExecutor(this.comm);
		// getCommand("ninja").setExecutor(this.comm);
		// getCommand("pyro").setExecutor(this.comm);
		// getCommand("soldier").setExecutor(this.comm);

		trans.register("avaibleclasses", ChatColor.WHITE
				+ "AVAIBLE CLASSES - Avaible for everyone!");
		trans.register("premiumclasses", ChatColor.GREEN
				+ "PREMIUM CLASSES - Order @ ...");
		trans.register("buythatclass", ChatColor.RED
				+ "&k is a premium class - Order @ ...");
		// trans.register("youareinTeam", ChatColor.GRAY + "You are in Team ");
		trans.register("AWon", ChatColor.GOLD + "Team : " + ChatColor.RED
				+ "Red" + ChatColor.GOLD + " won the Game!!!");
		trans.register("BWon", ChatColor.GOLD + "Team : " + ChatColor.BLUE
				+ "Blue" + ChatColor.GOLD + " won the Game!!!");
		trans.register("NoWon", ChatColor.GOLD
				+ "No Team won the Game - Time was over!");
		trans.register("backwith/class", "Go back in the game with /class");
		trans.register("killedby", "You had been killed by &P");
		trans.register("youkilled", "You killed &P");
		trans.register("killedyourself", "You killed yourself :D");

		trans.register("flagmustbeathome", ChatColor.RED
				+ "Your Team's Flag must be at home!");
		trans.register("Acaptured", ChatColor.RED + "&P" + ChatColor.YELLOW
				+ " has captured the " + ChatColor.BLUE + "Blue"
				+ ChatColor.YELLOW + "Team's flag!");
		trans.register("Bcaptured", ChatColor.BLUE + "&P" + ChatColor.YELLOW
				+ " has captured the " + ChatColor.RED + "Red"
				+ ChatColor.YELLOW + "Team's flag!");

		trans.register("Astolen", "&P" + ChatColor.YELLOW + " has stolen the "
				+ ChatColor.RED + "Red" + ChatColor.YELLOW + "Team's flag!");
		trans.register("Bstolen", "&P" + ChatColor.YELLOW + " has stolen the "
				+ ChatColor.BLUE + "Blue" + ChatColor.YELLOW + "Team's flag!");

		trans.register("YouarenowA", "You are now a &k");
		trans.register("YouarenowAN", "You are now an &k");

		trans.register("remember2vote", ChatColor.GREEN
				+ "Don't forgot to vote via /vote");

		conm.register("TeamNameA", ChatColor.RED + "Red");
		conm.register("TeamNameB", ChatColor.BLUE + "Blue");

		conm.register("auto", "false");
		conm.register("minPlayer", "1");
		conm.register("player2start", "2");

		conm.register("broadcastertime", "300");

		conm.register("language", "en");

		conm.register("ChatMode", "HUD");

		conm.register("dontReset", "false");

		conm.load();

		this.cm.cm = ChatManager.ChatMode.valueOf(conm.get("ChatMode"));

		registerHooks();

		// this.conm.loadConfig();

		// this.InvM.load();

		this.getServer().getConsoleSender()
				.sendMessage(ChatColor.RED + "CTF is still in beta!");
		this.getServer()
				.getConsoleSender()
				.sendMessage(
						ChatColor.YELLOW
								+ "If you found a bug, report it: http://dev.bukkit.org/server-mods/ctf/tickets/");

		clm = new ClassManager(this);

		trans.load(conm.get("language"));

		// getCommand("ctf").setExecutor(this.comm);
		getCommand("ctf").setExecutor(new CommandManager(this));
		getCommand("vote").setExecutor(new CommandManager(this));

		// getCommand("class").setTabCompleter(this);
		getCommand("class").setExecutor(new CommandManager(this));

		this.log.info("CTF has been enabled!");

		if (Boolean.valueOf(this.conm.get("auto"))) {
			this.getServer()
					.getConsoleSender()
					.sendMessage(
							ChatColor.BLUE + "CTF is now in automatic mode!");
			this.comm.start(this.getServer().getConsoleSender());
		}

	}

	@Override
	public void onDisable() {
		if (Game.idle || Game.running || Game.after && Game.Spawn != null) {
			for (Player p : Game.Spawn.getWorld().getPlayers()) {
				p.teleport(this.getServer().getWorlds().get(0)
						.getSpawnLocation());
				ctfu.clear(p);
				p.sendMessage(StringUtils.repeat(".\n ", 10));
			}
		}

		Player pp;

		for (String pn : this.tm.TeamA.keySet()) {
			pp = this.getServer().getPlayerExact(pn);
			if (pp == null)
				continue;

			this.InvM.give(pp);
		}

		for (String pn : this.tm.TeamB.keySet()) {
			pp = this.getServer().getPlayerExact(pn);
			if (pp == null)
				continue;

			this.InvM.give(pp);
		}

		tm.TeamA = null;
		tm.TeamB = null;

		try {
			this.MM.unloadMap(this.Game.Spawn.getWorld().getName());
		} catch (Exception localException) {
			this.log.info("Can't unload MAP!");
		}

		HandlerList.unregisterAll(this);

		getServer().getScheduler().cancelTask(this.BCID);
		getServer().getScheduler().cancelTask(this.CHID);
		getServer().getScheduler().cancelTask(this.tITID);
		getServer().getScheduler().cancelTask(this.LTID);
		getServer().getScheduler().cancelTask(this.IATID);

		try {
			if (clm.Classes != null && !clm.Classes.isEmpty())
				for (CtFClass a : clm.Classes) {
					a.disable();
				}
		} catch (Exception e) {
		}

		this.BCID = 0;
		this.CHID = 0;
		this.tITID = 0;
		this.LTID = 0;
		this.log.info("CTF has been disabled.");
	}

	public void registerHooks() {
		getServer().getPluginManager().registerEvents(this.pl, this);
		getServer().getPluginManager()
				.registerEvents(new CListener(this), this);
		getServer().getPluginManager().registerEvents(new CTFListener(this),
				this);
		getServer().getPluginManager().registerEvents(new SpongeListener(this),
				this);

		getServer().getPluginManager().registerEvents(
				new TrackerListener(this), this);

		this.BCID = getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new BroadcasterThread(this), 60L,
				Long.valueOf(this.conm.get("broadcastertime")));
		if (this.cm.cm == ChatManager.ChatMode.HUD || this.cm.cm == ChatManager.ChatMode.SCOREBOARD
				|| this.cm.cm == ChatManager.ChatMode.CHATBOARD) {
			this.CHID = getServer().getScheduler().scheduleSyncRepeatingTask(
					this, new HoldChatThread(this), 60L, 20L);
			this.log.info("Update every second!");
		} else if (this.cm.cm == ChatManager.ChatMode.CHAT)
			this.CHID = getServer().getScheduler().scheduleSyncRepeatingTask(
					this, new HoldChatThread(this), 60L, 20 * 20L);
		// this.tITID =
		// getServer().getScheduler().scheduleSyncRepeatingTask(this,
		// new takeItemThread(this), 60L, 20L);
		this.LTID = getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new lookingtimer(this), 60L, 20L);

	}

	// public List<String> onTabComplete(CommandSender sender, Command command,
	// String alias,
	// String[] args){
	//
	// List<String> al = new ArrayList<String>();
	// for (CtFClass c : this.clm.Classes){
	// if (c != null)
	// al.add(c.getCommand());
	// }
	// return al;
	//
	// }

	public static CTF getPlugin() {
		return instance;
	}
}

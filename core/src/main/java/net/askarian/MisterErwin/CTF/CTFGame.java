package net.askarian.MisterErwin.CTF;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CTFGame {

	private CTF plugin;

	public Location Spawn;
	public Location SpawnA;
	public Location SpawnB;

	public Location FlagA;
	public Location FlagB;

	public Object FlagAHolder; // Home - Player - Location
	public Object FlagBHolder; // no-one - player - droped

	public Item FlagItemA;
	public Item FlagItemB;

	// White wool
	public Item FlagItemAW;
	public Item FlagItemBW;

	// Config
	// #classes
	public ArrayList<String> disallowedclasses = new ArrayList<String>();
	public ArrayList<String> freeclasses = new ArrayList<String>();
	// public ArrayList<String> premiumclasses = new ArrayList<String>();
	// #time
	public int pretime = 90;
	public int posttime = 90;
	// #Game
	public int gametime = 10 * 60;
	public int resettime = 3;
	public int foundtime = 20;
	public int MaxCaptures = 2;
	// #sponges
	public double hboost = 5;
	public double vboost = 5;
	// #worldtime
	public long starttimeticks = 0;
	public int holdtime = 1;
	// #gamemode
	public String flagmode = "item";
	// ConfigEnd
	public int TeamACaptures = 0;
	public int TeamBCaptures = 0;

	// public HashMap<?, ?> bleeds = new HashMap<Object, Object>();
	public HashMap<String, Boolean> PlayerStorage = new HashMap<String, Boolean>();

	public boolean idle = false;
	public boolean running = false;
	public boolean after = false;

	// Before games start
	public HashMap<String, String> waitingPlayers = new HashMap<String, String>();

	// Heal & kill Players at spawn
	public ArrayList<String> spawnPlayersA = new ArrayList<String>();
	public ArrayList<String> spawnPlayersB = new ArrayList<String>();

	// last worldid
	private int lastworld = -1;

	public int FlagAReset = 0;
	public int FlagBReset = 0;

	// pre / post
	public int time2start = 0;
	public int time2play = 0;
	public int time2leave = 0;

	public ArrayList<String> TpPlayers = new ArrayList<String>();

	public String premessage = "";

	public boolean buildonly = false;

	public String lastMap = "";

	public CTFGame(CTF pl) {
		plugin = pl;
	}

	// LOCS
	public Location getFlagA() {
		return this.FlagA;
	}

	public Location getFlagB() {
		return this.FlagB;
	}

	public void setFlagA(Location loc) {
		this.FlagA = loc;
	}

	public void setFlagB(Location loc) {
		this.FlagB = loc;
	}

	// /FUNKTION

	public void startGame() {
		if (this.SpawnA.getWorld() == null || buildonly) {
			running = false;
			idle = true;
			after = false;
			premessage = ChatColor.YELLOW + "Please wait...";
			return;
		}

		// Set new modi
		running = true;
		idle = false;
		after = false;
		// Give every Player a team
		// String lastteam = "A";
		Iterator<?> wlist = waitingPlayers.entrySet().iterator();
		Player p;
		String[] ite;
		String key;
		while (wlist.hasNext()) {
			ite = wlist.next().toString().split("=");
			key = ite[0];
			ite[1] = ite[1].replaceAll("\\s", "");
			p = plugin.getServer().getPlayerExact(key);
			if (p == null || p.isDead())
				continue;
			try {
				p.getInventory().clear();
			} catch (Exception ignored) {
			}
			// JOIN
			plugin.tm.JoinTeam(p);

			// Sets the class
			if (ite.length > 1 && !ite[1].equals("")) {
				// plugin.tm.setClass(p, ite[1]);
				plugin.clm.setKit(p, ite[1]);
			} else
				plugin.clm.setKit(p, plugin.clm.Classes.get(0).getCommand());
			// TP2Spawn
			if (plugin.tm.getTeam(p).equals("A"))
				p.teleport(plugin.Game.SpawnA,
						PlayerTeleportEvent.TeleportCause.PLUGIN);
			else if (plugin.tm.getTeam(p).equals("B"))
				p.teleport(plugin.Game.SpawnB,
						PlayerTeleportEvent.TeleportCause.PLUGIN);

			plugin.clm.giveKit(p);

			// plugin.tm.setTeam(p, lastteam);
			// if (lastteam == "A")
			// lastteam = "B";
			// else
			// lastteam= "A";

		}
		// Spawn Flags
		if (FlagItemA == null)
			FlagItemA = plugin.ctfu.SpawnFlag(FlagA, DyeColor.RED);
		if (FlagItemB == null)
			FlagItemB = plugin.ctfu.SpawnFlag(FlagB, DyeColor.BLUE);

	}

	public void saveFlagLocationA(Location loc) {
		if (!new File("" + loc.getWorld().getName() + "/level.dat").exists()) {
			plugin.log
					.warning("[CTF]Error while flaglocation saving => World not found");
			return;
		}
		File fs = new File("" + loc.getWorld().getName() + "/flaglocA.ctf");
		WriteLocFile(fs, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
				loc.getPitch());
		plugin.Game.Spawn = loc;
	}

	public void saveFlagLocationB(Location loc) {
		if (!new File("" + loc.getWorld().getName() + "/level.dat").exists()) {
			plugin.log
					.warning("[CTF]Error while spawnlocation saving => World not found");
			return;
		}
		File fs = new File("" + loc.getWorld().getName() + "/flaglocB.ctf");
		WriteLocFile(fs, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
				loc.getPitch());
		plugin.Game.Spawn = loc;
	}

	public void saveSpawnLocation(Location loc) {
		if (!new File("" + loc.getWorld().getName() + "/level.dat").exists()) {
			plugin.log
					.warning("[CTF]Error while spawnlocation saving => World not found");
			return;
		}
		File fs = new File("" + loc.getWorld().getName() + "/spawnloc.ctf");
		WriteLocFile(fs, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
				loc.getPitch());
		plugin.Game.Spawn = loc;
	}

	public void saveSpawnLocationA(Location loc) {
		if (!new File("" + loc.getWorld().getName() + "/level.dat").exists()) {
			plugin.log
					.warning("[CTF]Error while spawnlocation saving => World not found");
			return;
		}
		File fs = new File("" + loc.getWorld().getName() + "/spawnlocA.ctf");
		WriteLocFile(fs, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
				loc.getPitch());
		plugin.Game.SpawnA = loc;
	}

	public void saveSpawnLocationB(Location loc) {
		if (!new File("" + loc.getWorld().getName() + "/level.dat").exists()) {
			plugin.log
					.warning("[CTF]Error while spawnlocation saving => World not found");
			return;
		}
		File fs = new File("" + loc.getWorld().getName() + "/spawnlocB.ctf");
		WriteLocFile(fs, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
				loc.getPitch());
		plugin.Game.SpawnB = loc;
	}

	// ###################LOAD LOCATIONS###############

	public Location loadFlagLocationA(String worldname) {
		File f = new File("" + worldname + "/flaglocA.ctf");
		if (!f.exists()) {
			plugin.log.warning(f.getAbsolutePath() + " does not exist");
			this.buildonly = true;
			return null;
		}
		return loadLocationfromFile(f, worldname);
	}

	public Location loadFlagLocationB(String worldname) {
		File f = new File("" + worldname + "/flaglocB.ctf");
		if (!f.exists()) {
			plugin.log.warning(f.getAbsolutePath() + " does not exist");
			this.buildonly = true;
			return null;
		}
		return loadLocationfromFile(f, worldname);
	}

	public Location loadSpawnLocation(String worldname) {
		File f = new File("" + worldname + "/spawnloc.ctf");
		if (!f.exists()) {
			plugin.log.warning(f.getAbsolutePath() + " does not exist");
			this.buildonly = true;
			return null;
		}
		return loadLocationfromFile(f, worldname);
	}

	public Location loadSpawnLocationA(String worldname) {
		File f = new File("" + worldname + "/spawnlocA.ctf");
		if (!f.exists()) {
			plugin.log.warning(f.getAbsolutePath() + " does not exist");
			this.buildonly = true;
			return null;
		}
		return loadLocationfromFile(f, worldname);
	}

	public Location loadSpawnLocationB(String worldname) {
		File f = new File("" + worldname + "/spawnlocB.ctf");
		if (!f.exists()) {
			plugin.log.warning(f.getAbsolutePath() + " does not exist");
			this.buildonly = true;
			return null;
		}
		return loadLocationfromFile(f, worldname);
	}

	@SuppressWarnings("resource")
	public Location loadLocationfromFile(File f, String worldname) {
		try {
			Scanner snr = new Scanner(f);
			World w = plugin.getServer().getWorld(worldname);
			if (w == null) {
				plugin.log.warning("[CTF] Error while loading Location : null");
				return null;
			}
			Location loc = new Location(w, 0, 0, 0);

			loc.setX(Double.parseDouble(snr.nextLine().trim().split("=")[1]));
			loc.setY(Double.parseDouble(snr.nextLine().trim().split("=")[1]));
			loc.setZ(Double.parseDouble(snr.nextLine().trim().split("=")[1]));
			if (snr.hasNext())
				loc.setYaw(Float
						.parseFloat(snr.nextLine().trim().split("=")[1]));
			if (snr.hasNext())
				loc.setPitch(Float
						.parseFloat(snr.nextLine().trim().split("=")[1]));
			snr.close();
			return loc;
		} catch (Exception e) {
			plugin.log.warning("[CTF] Error while loading Location");
		}
		return null;
	}

	public void WriteLocFile(File f, double X, double Y, double Z, float yaw,
			float pitch) {
		if (!f.exists()) {
			try {
				File oldFile = f;
				if (!oldFile.getParentFile().isDirectory()) {
					oldFile.mkdirs();
				}
				oldFile.delete();

				f.createNewFile();

				PrintWriter pw = new PrintWriter(f);
				pw.write("#X=" + X + "\r\n");
				pw.write("#Y=" + Y + "\r\n");
				pw.write("#Z=" + Z + "\r\n");
				pw.write("#yaw=" + yaw + "\r\n");
				pw.write("#pitch=" + pitch + "\r\n");

				pw.close();
			} catch (Exception e) {
				plugin.log.warning("[CTF] Error while writing location file:"
						+ f.getAbsolutePath());
			}
			return;
		}// Ende Create
			// File existiert => Übershreiben
		else {
			PrintWriter pw;
			try {
				pw = new PrintWriter(f);

				pw.write("#X=" + X + "\r\n");
				pw.write("#Y=" + Y + "\r\n");
				pw.write("#Z=" + Z + "\r\n");

				pw.close();
			} catch (FileNotFoundException e) {
				plugin.log.warning("[CTF] Error while writing spawnlocation");
			}
		}
	} // END FUNC

	public void votenext() {
		plugin.log.info("Looking for Votes!");
		plugin.comm.stop(true);
		this.plugin.comm.public_start(true);
	}

	public void start(CommandSender p, boolean vote, boolean random) {
		if (this.Spawn != null && this.Spawn.getWorld() != null) {
			p.sendMessage(ChatColor.RED + "A Map is already loaded");
			return;
		}

		String vm = plugin.MM.getVoteMap();

		if (vote && vm != null) {
			start(p, vm);
			this.lastMap = vm;
			vm = null;
			return;
		}

		File folder = new File("plugins/../");
		if (!folder.exists()) {
			folder.mkdir();
		}

		List<String> Maps = new ArrayList<String>();
		int zl = 0;

		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				String MapName = f.getName();

				if (MapName.startsWith("CTF-MAP_")) {
					Maps.add(f.getName());
					plugin.log.info("Found map " + f.getName());
					zl++;
				}
			}
		}
		if (zl == 0 || Maps.get(0) == null) {
			p.sendMessage(ChatColor.RED + "No Maps found in folder "
					+ folder.getName());
			return;
		}
		if (random) {
			int i = new Random().nextInt(Maps.size());
			lastworld = i;
			String worldname = Maps.get(i);
			start(p, worldname);

			return;
		}
		// plugin.log.info("------------"+r.nextInt());
		// String worldname = Maps.get(r.nextInt());
		plugin.log.info(Maps.size() + "SIZE");
		if (Maps.size() == 1) {
			lastworld = 0;
		} else {
			if (lastworld < Maps.size() - 1)
				lastworld++;
			else
				lastworld = -1;

			if (lastworld < 0)
				lastworld = 0;

			Integer i = lastworld;

			plugin.log.info(Maps.size() + " - " + i);

			// while (i < Maps.size() - 1){
			// plugin.log.info(i + " --> " + Maps.get(i));
			// i++;
			// }

		}

		String worldname = Maps.get(lastworld);
		start(p, worldname);

	}

	public void start(CommandSender p, String mapname) {
		p.sendMessage("Loading World: " + mapname + "(" + this.lastworld);
		World w = plugin.MM.loadMap(mapname);
		this.lastMap = mapname;

		if (w == null) {
			p.sendMessage(ChatColor.RED + "Can't load map " + mapname);
			return;
		}

		this.Spawn = w.getSpawnLocation().clone();

		this.SpawnA = w.getSpawnLocation().clone();

		plugin.log.info("[CTF] Loaded map " + this.SpawnA.getWorld().getName());

		Spawn = loadSpawnLocation(mapname);
		if (Spawn == null) {
			Spawn = w.getSpawnLocation();
			idle = true;
			running = false;
			after = false;
			return;
		}
		SpawnA = loadSpawnLocationA(mapname);
		if (SpawnA == null) {
			SpawnA = w.getSpawnLocation();
			idle = true;
			running = false;
			after = false;
			return;
		}
		SpawnB = loadSpawnLocationB(mapname);

		if (SpawnB == null) {
			SpawnB = w.getSpawnLocation();
			idle = true;
			running = false;
			after = false;
			return;
		}

		FlagA = loadFlagLocationA(mapname);
		FlagB = loadFlagLocationB(mapname);

		FlagAHolder = "Home";
		FlagBHolder = "Home";

		TeamACaptures = 0;
		TeamBCaptures = 0;

		plugin.cm.reset();
		loadConfig(mapname);

		time2start = pretime;

		idle = true;
		running = false;
		after = false;

		plugin.ctfu.removeEntites(this.Spawn.getWorld());

	}

	public void tp(Player p) {
		if (p == null || p.isDead())
			return;
		if (this.Spawn == null) {
			p.teleport(this.Spawn.getWorld().getSpawnLocation(),
					PlayerTeleportEvent.TeleportCause.PLUGIN);
		} else {
			p.teleport(this.Spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
		}

	}

	@SuppressWarnings("resource")
	public void loadConfig(String worldname) {
		File f = new File("" + worldname + "/CTF-MAP.properties");
		try {
			if (!f.exists()) {
				writeConfig(f);
			}
			Scanner snr = new Scanner(f);
			World w = plugin.getServer().getWorld(worldname);
			if (w == null) {
				plugin.log
						.warning("[CTF] Error while loading World config : null");
				return;
			}
			// classes
			// this.disallowedclasses =
			// getListbyString(snr.nextLine().trim().split("=")[1]);
			this.freeclasses = getListbyString(snr.nextLine().trim().split("=")[1]);
			this.disallowedclasses = getListbyString(snr.nextLine().trim()
					.split("=")[1]);
			// time & game
			this.pretime = Integer
					.parseInt(snr.nextLine().trim().split("=")[1]);
			this.posttime = Integer
					.parseInt(snr.nextLine().trim().split("=")[1]);
			this.gametime = Integer
					.parseInt(snr.nextLine().trim().split("=")[1]);
			this.resettime = Integer
					.parseInt(snr.nextLine().trim().split("=")[1]);
			this.foundtime = Integer
					.parseInt(snr.nextLine().trim().split("=")[1]);
			this.MaxCaptures = Integer.parseInt(snr.nextLine().trim()
					.split("=")[1]);
			// Sponges
			this.hboost = Double
					.parseDouble(snr.nextLine().trim().split("=")[1]);
			this.vboost = Double
					.parseDouble(snr.nextLine().trim().split("=")[1]);
			// worldtime
			this.starttimeticks = Long.parseLong(snr.nextLine().trim()
					.split("=")[1]);
			this.holdtime = Integer
					.parseInt(snr.nextLine().trim().split("=")[1]);
			// Gamemode
			this.flagmode = snr.nextLine().trim().split("=")[1];

			snr.close();

		} catch (Exception e) {
			plugin.log.warning("[CTF] Error while loading World config");
		}
		return;
	}

	private void writeConfig(File f) {
		try {
			File oldFile = f;
			if (!oldFile.getParentFile().isDirectory()) {
				oldFile.mkdirs();
			}
			oldFile.delete();

			File newFile = f;
			newFile.createNewFile();

			PrintWriter pw = new PrintWriter(f);
			// Classes
			// pw.write("#allowedclasses={soldier,archer,assassin}\r\n");
			pw.write("#freeclasses={}\r\n");
			pw.write("#disallowedclasses={}\r\n");
			// time & game
			pw.write("#pretime=90\r\n");
			pw.write("#posttime=90\r\n");
			pw.write("#gametime=600\r\n");
			pw.write("#resettime=3\r\n");
			pw.write("#foundtime=20\r\n");
			pw.write("#maxcaptures=3\r\n");
			// Sponges
			pw.write("#hboost=6.5\r\n");
			pw.write("#vboost=2.4\r\n");
			// worldtime
			pw.write("#starttimeticks=0\r\n");
			pw.write("#halttime=1\r\n");
			// Gamemode
			pw.write("#flagmode=item\r\n");

			pw.close();
		} catch (Exception e) {
			plugin.log.warning("[CTF] Can't create World-Config! "
					+ f.getAbsolutePath());
		}
	}

	public ArrayList<String> getListbyString(String s) {
		// ArrayList<?> ret = new ArrayList<Object>();
		Pattern p = Pattern.compile(".*\\{(.*)\\}.*");
		Matcher m = p.matcher(s);
		String sc = "";
		if (m.find()) {
			sc = m.group(1);
		} else {
			plugin.log.info("List was empty-->" + s);
			return null;
		}

		return new ArrayList<String>(Arrays.asList(sc.split(",")));
	}

	public void stopCauseCounter() {
		plugin.Game.idle = false;
		plugin.Game.running = false;
		plugin.Game.after = true;
		// plugin.getServer().broadcastMessage("GAME ENDS - " +
		// plugin.Game.posttime);
		plugin.Game.time2leave = plugin.Game.posttime;
		plugin.cm.allupdate();
		String team = "";
		if (this.TeamACaptures > this.TeamBCaptures)
			team = "A";
		if (this.TeamACaptures < this.TeamBCaptures)
			team = "B";
		plugin.tracker.win(team);

		plugin.MM.Team = team;

		for (String pn : plugin.tm.TeamA.keySet()) {
			Player p = plugin.getServer().getPlayerExact(pn.trim());
			if (p == null)
				continue;

			if (team.equals("A"))
				plugin.cm.SendMessage(p, plugin.trans.get("AWon"));
			else if (team.equals("B"))
				plugin.cm.SendMessage(p, plugin.trans.get("BWon"));
			else
				plugin.cm.SendMessage(p, plugin.trans.get("NoWon"));

			// plugin.cm.SendMessage(p, ChatColor.GREEN +
			// "Don't forgot to vote via /vote");
			plugin.cm.SendMessage(p, plugin.trans.get("remember2vote"));

			plugin.MM.showVoteMenu(p);

		}

		for (String pn : plugin.tm.TeamB.keySet()) {
			Player p = plugin.getServer().getPlayerExact(pn);
			if (p == null)
				continue;
			if (team.equals("A"))
				plugin.cm.SendMessage(p, plugin.trans.get("AWon"));
			else if (team.equals("B"))
				plugin.cm.SendMessage(p, plugin.trans.get("BWon"));
			else
				plugin.cm.SendMessage(p, plugin.trans.get("NoWon"));

			plugin.cm.SendMessage(p, plugin.trans.get("remember2vote"));

			plugin.MM.showVoteMenu(p);

		}

		if (plugin.Game.TeamACaptures > plugin.Game.TeamBCaptures) {
			if (plugin.Game.SpawnA != null)
				for (Map.Entry<String, String> entry : plugin.tm.TeamB
						.entrySet())
					if (entry != null
							&& plugin.getServer().getPlayer(entry.getKey()) != null)
						plugin.Game.SpawnA.getWorld().strikeLightning(
								plugin.getServer().getPlayer(entry.getKey())
										.getLocation());

		} else if (plugin.Game.TeamACaptures < plugin.Game.TeamBCaptures) {
			if (plugin.Game.SpawnA != null)
				for (Map.Entry<String, String> entry : plugin.tm.TeamA
						.entrySet())
					if (entry != null
							&& plugin.getServer().getPlayer(entry.getKey()) != null)
						plugin.Game.SpawnA.getWorld().strikeLightning(
								plugin.getServer().getPlayer(entry.getKey())
										.getLocation());

		}
	}

}

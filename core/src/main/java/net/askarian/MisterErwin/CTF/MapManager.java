package net.askarian.MisterErwin.CTF;

import net.askarian.MisterErwin.CTF.util.IconMenu;
import net.askarian.MisterErwin.CTF.util.ResetBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class MapManager {
	private CTF plugin;
	private World.Environment env;
	private WorldType wt;
	private List<String> Maps = new ArrayList<String>();
	private HashMap<String, Integer> votes = new HashMap<String, Integer>();
	private List<String> vplayer = new ArrayList<String>();
	public String Team = "A";

	private HashMap<Location, ResetBlock> resetBlocks = new HashMap<Location, ResetBlock>();

	public MapManager(CTF pl) {
		this.plugin = pl;

		plugin.trans.register("Vote-GUI-Name", "Map Voter");
	}

	public World loadRandom() {
		return null;
	}

	public World loadbyVote() {
		return null;
	}

	public String getVoteMap() {
		try {
			if (this.votes == null || Maps.size() < 1)
				return null;

			int high = 0;
			String name = Maps.get(0);

			Iterator<Map.Entry<String, Integer>> it = ((Map<String, Integer>) this.votes.clone())
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				plugin.log.info(pairs.getKey() + " gets " + pairs.getValue());
				if ((Integer) pairs.getValue() > high) {
					name = (String) pairs.getKey();
					high = (Integer) pairs.getValue();
				}
				it.remove(); // avoids a ConcurrentModificationException
			}

			plugin.log.info("loaded" + name + " with " + high + " votes!");
			it = null;
			this.Maps.clear();
			this.votes.clear();
			this.vplayer.clear();

			return "CTF-MAP_" + name;
		} catch (Exception e) {
			return null;
		}
	}

	public void showVoteMenu(Player p) {

		File folder = new File("plugins/../");
		if (!folder.exists()) {
			folder.mkdir();
		}

		int zl = 0;
		if (this.Maps.size() < 1) {
			for (File f : folder.listFiles()) {
				if (f.isDirectory()) {
					String MapName = f.getName();

					if (MapName.startsWith("CTF-MAP_")
							&& !MapName.equals(plugin.Game.lastMap)) {
						Maps.add(f.getName().replace("CTF-MAP_", ""));
						zl++;
					}
				}
			}

		} else {
			zl = this.Maps.size();
		}
		if (zl == 0 || Maps.get(0) == null) {
			return;
		}

		Integer x = Maps.size() + 1;
		Integer y = x % 9;

		if (y != 0) {
			while (y != 0) {
				x++;
				y = x % 9;
			}
		}

		plugin.imm.create(p, plugin.trans.get("Vote-GUI-Name"), x,
				new IconMenu.OptionClickEventHandler() {
					public void onOptionClick(IconMenu.OptionClickEvent event) {
						plugin.MM.vote(event.getPlayer(), event.getName());
						event.setWillClose(true);
						event.setWillDestroy(true);
					}
				});

		ItemStack win = new ItemStack(Material.WOOL, 1,
				DyeColor.GRAY.getWoolData());
		String t = "No Team won!";

		if (this.Team.equalsIgnoreCase("A")) {
			win = new ItemStack(Material.WOOL, 1, DyeColor.RED.getWoolData());
			t = ChatColor.RED + "Team Red won!";
		} else if (this.Team.equalsIgnoreCase("B")) {
			win = new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getWoolData());
			t = ChatColor.RED + "Team Blue won!";
		}

		plugin.imm.setOption(p, 0, win, t, "-cancel-", "");

		int i = 1;

		ItemStack map = new ItemStack(Material.MAP);

		for (String n : Maps) {
			if (n == null)
				plugin.log.info("ERROR");
			else {
				plugin.imm.setOption(p, i, map.clone(), "Vote for " + n, n, "");
				i++;
			}
		}

		plugin.imm.show(p);
	}

	public void vote(Player p, String name) {
		if (!this.Maps.contains(name)) {
			p.sendMessage(ChatColor.RED + "You can't vote for Map " + name);
			for (String s : this.Maps)
				p.sendMessage(s);
			return;
		}
		if (this.vplayer.contains(p.getName())) {
			p.sendMessage(ChatColor.RED + "You have already voted!");
			return;
		}
		Integer before = 0;
		if (this.votes.containsKey(name)) {
			before = this.votes.get(name);
			this.votes.remove(name);
		}
		this.votes.put(name, before + 1);
		this.vplayer.add(p.getName());

		plugin.log.info(p.getName() + " voted for " + name);
		p.sendMessage(ChatColor.GREEN + "You have voted for " + name);
	}

	public World loadMap(String MapName) {
		this.votes.clear();
		this.vplayer.clear();
		unloadMap(MapName);
		String worldName = MapName;
		File mapfile = new File("plugins/../" + worldName);
		if (!mapfile.exists()) {
			this.plugin.log.info(mapfile.getAbsolutePath());
			this.plugin.log.warning("MAP " + MapName + " not found!");
			return null;
		}

		if (worldName != null && plugin.getServer().getWorld(worldName) != null) {
			if (plugin.conm.get("dontReset").equalsIgnoreCase("true")) {
				plugin.log.info(MapName + " is already loaded - I'll use it!");
				return this.plugin.getServer().getWorld(worldName);
			}
			this.unloadMap(worldName);
			this.plugin.log.info(mapfile.getAbsolutePath());
			this.plugin.log
					.warning("MAP "
							+ MapName
							+ " is already loaded! Don't use CTF Maps as your default map");
			this.plugin.log.info(".");
			this.plugin.log.info(".");
			this.plugin.log.info(".");
			this.plugin.log.info(".");
			this.plugin.log.info(".");

			return this.plugin.getServer().getWorld(worldName);

		}

		WorldCreator wc = new WorldCreator(worldName);

		World w = this.plugin.getServer().createWorld(wc);
		if (w == null) {
			plugin.log.warning("or ");
			return null;
		}
		w.setAutoSave(false);
		// w.setMetadata(this.plugin.getName(), new
		// FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
		w.setKeepSpawnInMemory(false);
		w.setPVP(true);
		w.setSpawnFlags(false, true);

		return w;
	}

	public void unloadMap(String MapName) {

		if (plugin.conm.get("dontReset").equalsIgnoreCase("true")
				&& this.resetBlocks.size() > 0) {
			for (ResetBlock rb : this.resetBlocks.values())
				rb.reset();

			this.resetBlocks.clear();

			plugin.log.info("trying to save " + MapName);

			for (World w : this.plugin.getServer().getWorlds()) {

				if (!w.getName().equals(MapName))
					continue;
				plugin.log.info("saving  " + MapName);

				w.save();
				return;
			}

			plugin.log
					.info("failed to save " + MapName + " (World not found!)");
			return;

		}
		this.resetBlocks.clear();

		plugin.log.info("trying to unload" + MapName);

		for (World w : this.plugin.getServer().getWorlds()) {

			if (!w.getName().equals(MapName))
				continue;
			plugin.log.info("unloading " + MapName);

			this.plugin.getServer().unloadWorld(w, false);
			return;
		}

		plugin.log.info("failed to unload " + MapName + " (World not found!)");

	}

	public void ChangeBlock(Block b, int m, byte data) {
		if (this.resetBlocks.containsKey(b.getLocation())) {
			if (this.resetBlocks.get(b.getLocation()).getMaterial() == m
					&& this.resetBlocks.get(b.getLocation()).getData() == data) {
				this.resetBlocks.remove(b.getLocation());
				return;
			}
			this.resetBlocks.remove(b.getLocation());

		}

		this.resetBlocks.put(b.getLocation(), new ResetBlock(b, m, data));
	}

}

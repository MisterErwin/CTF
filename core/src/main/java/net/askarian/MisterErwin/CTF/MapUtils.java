package net.askarian.MisterErwin.CTF;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MapUtils {

	private CTF plugin;

	public MapUtils(CTF pl) {
		plugin = pl;
	}

	public boolean deleteTempWorld(World w) {
		removePlayers(w);
		if ((w == null) || (!w.getPlayers().isEmpty())) {
			return false;
		}
		w.setAutoSave(false);
		File folder = new File(w.getName());
		for (Entity e : w.getEntities()) {
			e.remove();
		}

		plugin.getServer().unloadWorld(w, true);
		if (folder.exists()) {
			deleteFolder(folder);
		}
		return true;
	}

	public void removePlayers(World world) {
		Set tempSet = new HashSet();
		for (Player p : world.getPlayers()) {
			World w = getNonTempWorld();
			if (w == null) {
				p.kickPlayer("Could not find a non temporary world to teleport you to.");
			}
			new Location(w, 0.0D, 0.0D, 0.0D).getChunk().load(true);
			p.teleport(new Location(w, 0.0D, 0.0D, 0.0D));
		}

	}

	private World getNonTempWorld() {
		World ntw = plugin.getServer().getWorld("world");
		if (ntw == null) {
			for (World w : plugin.getServer().getWorlds()) {
				if (isCTFWorld(w))
					continue;
				ntw = w;
			}
		}

		return ntw;
	}

	public void deleteTempWorlds() {
		for (World w : plugin.getServer().getWorlds()) {
			if (!isCTFWorld(w))
				continue;
			deleteTempWorld(w);
		}
	}

	private void deleteFolder(File folder) {
		try {
			for (File f : folder.listFiles()) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
			folder.delete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isCTFWorld(World world) {
		String name = world.getName();

		return (name.startsWith("CTF-MAP_")) /*
											 * ||
											 * (name.startsWith("TCEditWorld_"))
											 * ||
											 * (name.startsWith("TCConvertWorld_"
											 * ))
											 */;
	}
}

package net.askarian.MisterErwin.CTF.util;

import net.askarian.MisterErwin.CTF.API.events.Score.ScoreType;
import net.askarian.MisterErwin.CTF.CTF;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;

public class CTFUtils {
	private CTF plugin;

	private int[] fences = { 85, 113, Material.IRON_FENCE.getId() }; // Fence,Netherfence,
																		// Ironfence

	public CTFUtils(CTF inheritance) {
		plugin = inheritance;
	}

	public Item SpawnFlag(Location loca, DyeColor color) {
		Location loc = loca.clone();
		if (loc == null) {
			plugin.log.info("Flag-Location not set");
			return null;
		}
		if (isfence(loc.getBlock()))
			loc = loc.getBlock().getRelative(BlockFace.UP).getLocation();
		// else if (isfence (loc.getBlock().getRelative(BlockFace.DOWN)))
		// loc = loc;
		// e = loc.getWorld().dropItem(loc, new ItemStack(Material.WOOL, 1,
		// color.getData())).setVelocity(new Vector());
		loc.setY(loc.getY() + 1);

		Entity e = loc.getWorld().dropItem(loc,
				new ItemStack(Material.WOOL, 1, color.getWoolData()));
		e.setVelocity(new Vector(0, 0, 0));

		// if (e.getLocation().getBlockX() == loc.getBlockX() &&
		// e.getLocation().getBlockY() == loc.getBlockY() &&
		// e.getLocation().getBlockZ() == loc.getBlockZ() )
		plugin.log.info("Flag placed at " + loc.toString());
		// else
		// plugin.log.info("Flag placed NOT at " + loc.toString());

		if (color == DyeColor.RED)
			e.setMetadata("redflag",
					new FixedMetadataValue(plugin, e.getEntityId()));

		if (color == DyeColor.BLUE)
			e.setMetadata("blueflag",
					new FixedMetadataValue(plugin, e.getEntityId()));
		return (Item) e;
	}

	public void saveFlagA(Player p) {
		Block b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if (b.getType() != Material.FENCE
				&& b.getType() != Material.NETHER_FENCE
				&& b.getType() != Material.IRON_FENCE) {
			p.sendMessage(ChatColor.RED + "You must stand on a fence!");
			return;
		}
		Location loc = b.getLocation();
		loc.setY(loc.getY() + 1.5);
		loc.setX(loc.getX() + 0.5);
		loc.setZ(loc.getZ() + 0.5);
		plugin.Game.saveFlagLocationA(loc);
		p.sendMessage(ChatColor.BLUE + "Flag Location A set!");
	}

	public void saveFlagB(Player p) {
		Block b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if (b.getType() != Material.FENCE
				&& b.getType() != Material.NETHER_FENCE
				&& b.getType() != Material.IRON_FENCE) {
			p.sendMessage(ChatColor.RED + "You must stand on a fence!");
			return;
		}
		Location loc = b.getLocation();
		loc.setY(loc.getY() + 1.5);
		loc.setX(loc.getX() + 0.5);
		loc.setZ(loc.getZ() + 0.5);
		plugin.Game.saveFlagLocationB(loc);
		p.sendMessage(ChatColor.BLUE + "Flag Location B set!");
	}

	public boolean isfence(Block b) {
		return (b.getType() == Material.FENCE
				|| b.getType() == Material.NETHER_FENCE || b.getType() == Material.IRON_FENCE);
	}

	public void pickup(Player p, String teamsflag) {
		if (teamsflag.equals("A")) {
			plugin.log.info("FIRED!");
			// Re-collected
			if (plugin.tm.getTeam(p).equals("A")) {
				plugin.cm.allMessage(ChatColor.RED + p.getName()
						+ ChatColor.DARK_PURPLE + " recaptured the "
						+ ChatColor.RED + "Red " + ChatColor.DARK_PURPLE
						+ "Flag!");
				reset("A");
				plugin.tracker.addScore(p.getName(), ScoreType.Recapture);
				return;
			}
			// Collected other flag
			plugin.Game.FlagAHolder = p;
			plugin.Game.FlagItemA = null;
			plugin.Game.FlagItemAW = SpawnFlag(plugin.Game.getFlagA(),
					DyeColor.WHITE);
			plugin.cm.allMessage(plugin.trans.get("Astolen").replace("&P",
					p.getDisplayName()));
			plugin.Game.getFlagA().getWorld()
					.strikeLightningEffect(plugin.Game.getFlagA());

			plugin.tracker.addScore(p.getName(), ScoreType.Steal);

		} else if (teamsflag.equals("B")) {
			// Re-collected
			if (plugin.tm.getTeam(p).equals("B")) {
				plugin.cm.allMessage(ChatColor.BLUE + p.getName()
						+ ChatColor.DARK_PURPLE + " recaptured the "
						+ ChatColor.BLUE + "Blue " + ChatColor.DARK_PURPLE
						+ "Flag!");
				reset("B");
				plugin.tracker.addScore(p.getName(), ScoreType.Recapture);
				return;
			}
			// Collected other flag
			plugin.Game.FlagBHolder = p;
			plugin.Game.FlagItemB = null;
			plugin.Game.FlagItemBW = SpawnFlag(plugin.Game.getFlagB(),
					DyeColor.WHITE);
			plugin.cm.allMessage(plugin.trans.get("Bstolen").replace("&P",
					p.getDisplayName()));
			plugin.Game.getFlagB().getWorld()
					.strikeLightningEffect(plugin.Game.getFlagB());

			plugin.tracker.addScore(p.getName(), ScoreType.Steal);

		}

	}

	public void drop(Player p, Item i) {
		if (plugin.tm.getTeam(p).equals("A")) {
			plugin.Game.FlagItemB = i;
			plugin.Game.FlagBReset = 3;
			plugin.Game.FlagBHolder = "reset";
			i.remove();
		} else if (plugin.tm.getTeam(p).equals("B")) {
			plugin.Game.FlagItemA = i;
			plugin.Game.FlagAReset = 3;
			plugin.Game.FlagAHolder = "reset";
			i.remove();
		}
	}

	public void despawn(String team) {
		if (team.equals("A")) {
			plugin.Game.FlagAReset = 8;
			plugin.Game.FlagAHolder = "reset";
		} else if (team.equals("B")) {
			plugin.Game.FlagBReset = 8;
			plugin.Game.FlagBHolder = "reset";
		}
	}

	public void die(Player p, String team) {
		if (team.equals("A")) {
			drop(p,
					p.getWorld().dropItemNaturally(
							p.getLocation(),
							new ItemStack(Material.WOOL, 1, DyeColor.RED
									.getData())));
		} else if (team.equals("B")) {
			drop(p,
					p.getWorld().dropItemNaturally(
							p.getLocation(),
							new ItemStack(Material.WOOL, 1, DyeColor.BLUE
									.getData())));
		}

	}

	public void reset(String team) {
		if (team.equals("A")) {
			if (plugin.Game.FlagItemAW != null
					&& !plugin.Game.FlagItemAW.isDead()) {
				for (Entity e : plugin.Game.getFlagA().getWorld().getEntities()) {
					if (!(e instanceof Item))
						continue;
					if (plugin.Game.FlagItemAW != null
							&& e.getUniqueId() == plugin.Game.FlagItemAW
									.getUniqueId()) {
						plugin.Game.FlagItemA = SpawnFlag(
								plugin.Game.getFlagA(), DyeColor.RED);
						e.remove();
						plugin.Game.FlagItemAW = null;
					}

				}
				plugin.Game.FlagAHolder = "Home";
			} else {
				plugin.Game.FlagItemA = SpawnFlag(plugin.Game.getFlagA(),
						DyeColor.RED);
				plugin.Game.FlagItemAW = null;
				plugin.Game.FlagAHolder = "Home";
			}
		} else if (team.equals("B")) {
			if (plugin.Game.FlagItemBW != null
					&& !plugin.Game.FlagItemBW.isDead()) {
				for (Entity e : plugin.Game.getFlagB().getWorld().getEntities()) {
					if (!(e instanceof Item))
						continue;
					if (plugin.Game.FlagItemBW != null
							&& e.getUniqueId() == plugin.Game.FlagItemBW
									.getUniqueId()) {
						plugin.Game.FlagItemB = SpawnFlag(
								plugin.Game.getFlagB(), DyeColor.BLUE);
						e.remove();
						plugin.Game.FlagItemBW = null;
					}

				}
				plugin.Game.FlagBHolder = "Home";
			} else {
				plugin.Game.FlagItemB = SpawnFlag(plugin.Game.getFlagB(),
						DyeColor.BLUE);
				plugin.Game.FlagItemBW = null;
				plugin.Game.FlagBHolder = "Home";
			}
		}
	}

	public void captured(Player p, String team) {
		plugin.log.info("Captured by " + team);
		if (team.equals("A")) {
			// Team A captured
			// Is Flag at Base?
			if (!(plugin.Game.FlagAHolder instanceof String && plugin.Game.FlagAHolder == "Home")) {
				plugin.cm.SendMessage(p, plugin.trans.get("flagmustbeathome"));
				return;
			}
			// Set captures...
			plugin.Game.TeamACaptures++;
			plugin.cm.allMessage(plugin.trans.get("Acaptured").replace("&P",
					p.getDisplayName()));
			plugin.cm.allupdate();
			reset("B");

			if (plugin.Game.FlagItemA != null)
				plugin.Game.FlagItemA.remove();
			reset("A");

			if (plugin.Game.TeamACaptures >= plugin.Game.MaxCaptures) {
				win(team);
				plugin.tracker.addScore(p.getName(), ScoreType.FinalCapture);
			} else
				plugin.tracker.addScore(p.getName(), ScoreType.Capture);

		} else if (team.equals("B")) {
			// Team B captured
			// Is Flag at Base?
			if (!(plugin.Game.FlagBHolder instanceof String && plugin.Game.FlagBHolder == "Home")) {
				plugin.cm.SendMessage(p, plugin.trans.get("flagmustbeathome"));
				return;
			}
			// Set captures...
			plugin.Game.TeamBCaptures++;
			plugin.cm.allMessage(plugin.trans.get("Bcaptured").replace("&P",
					p.getDisplayName()));
			plugin.cm.allupdate();
			reset("A");

			if (plugin.Game.FlagItemB != null)
				plugin.Game.FlagItemB.remove();
			reset("B");

			if (plugin.Game.TeamBCaptures >= plugin.Game.MaxCaptures) {
				win(team);
				plugin.tracker.addScore(p.getName(), ScoreType.FinalCapture);
			} else
				plugin.tracker.addScore(p.getName(), ScoreType.Capture);
		}

	}

	public void win(String team) {
		// if (team == "A"){
		// plugin.cm.allMessage(plugin.conm.TeamNameA + " wins the Game!!!");
		// }
		// if (team == "B"){
		// plugin.cm.allMessage(plugin.conm.TeamNameB + " wins the Game!!!");
		// }

		if (!plugin.Game.running)
			return;

		plugin.Game.stopCauseCounter();

	}

	public void removeEntites(World w) {
		List<Entity> list = w.getEntities();
		Iterator<Entity> entities = list.iterator();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			// if (!(entity instanceof CraftPlayer)) {
			entity.remove();
			// plugin.log.info("REMOVED a(n) " + entity.getClass().toString());
			// }
		}
	}

	public void playNote(Sound s, int v, int pi) {
		for (Player p : this.plugin.getServer().getOnlinePlayers())
			if (plugin.tm.isinGame(p))
				p.playSound(p.getLocation(), s, v, pi);

	}

	public void saytime(int time) {
		for (Player p : this.plugin.getServer().getOnlinePlayers())
			if (plugin.tm.isinGame(p))
				plugin.clm.saytime(p, time);
	}

	public void clear(Player p) {
		if (p == null)
			return;
		p.getInventory().clear();

		// p.getInventory().getHelmet().setDurability((short) 0);
		// p.getInventory().getChestplate().setDurability((short) 0);
		// p.getInventory().getLeggings().setDurability((short) 0);
		// p.getInventory().getBoots().setDurability((short) 0);
		//
		// p.getInventory().getHelmet().setTypeId(0);
		// p.getInventory().getChestplate().setTypeId(0);
		// p.getInventory().getLeggings().setTypeId(0);
		// p.getInventory().getBoots().setTypeId(0);

		p.getInventory().setHelmet(new ItemStack(0));
		p.getInventory().setChestplate(new ItemStack(0));
		p.getInventory().setLeggings(new ItemStack(0));
		p.getInventory().setBoots(new ItemStack(0));

		p.setExp(0);
		p.setLevel(0);
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}
}

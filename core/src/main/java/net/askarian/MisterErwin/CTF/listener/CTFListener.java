package net.askarian.MisterErwin.CTF.listener;

import net.askarian.MisterErwin.CTF.API.events.ChangeAwayFromClassEvent;
import net.askarian.MisterErwin.CTF.CTF;
import net.askarian.MisterErwin.CTF.threads.BlockResetThread;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

//import org.bukkit.event.player.;

public class CTFListener implements Listener {

	private CTF plugin;

	public CTFListener(CTF pl) {
		plugin = pl;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (!plugin.tm.isinGame(event.getPlayer()))
			return;

		Item i = event.getItem();
		if (i.getItemStack().getType() != Material.WOOL) {
			return;
		}
		// Is "white" flag
		if (i.getItemStack().getData().getData() == DyeColor.WHITE.getData()) {
			event.setCancelled(true);
			return;
		}

		// A
		if (plugin.tm.getTeam(event.getPlayer()).equals("A")
				&& plugin.Game.FlagItemB != null
				&& i.getUniqueId() == plugin.Game.FlagItemB.getUniqueId()) {
			plugin.ctfu.pickup(event.getPlayer(), "B");
			return;
		}
		if (plugin.tm.getTeam(event.getPlayer()).equals("A")
				&& i.getItemStack().getData().getData() == DyeColor.RED
						.getData()) {
			if (plugin.Game.FlagAHolder == "Home")
				event.setCancelled(true);
			else {
				plugin.ctfu.pickup(event.getPlayer(), "A");
				i.remove();
				event.setCancelled(true);
			}
		}

		// B
		if (plugin.tm.getTeam(event.getPlayer()).equals("B")
				&& plugin.Game.FlagItemA != null
				&& i.getUniqueId() == plugin.Game.FlagItemA.getUniqueId()) {
			plugin.ctfu.pickup(event.getPlayer(), "A");
			return;
		}
		if (plugin.tm.getTeam(event.getPlayer()).equals("B")
				&& i.getItemStack().getData().getData() == DyeColor.BLUE
						.getData()) {
			if (plugin.Game.FlagBHolder == "Home") {
				event.setCancelled(true);
				return;
			} else {
				plugin.ctfu.pickup(event.getPlayer(), "B");
				i.remove();
				event.setCancelled(true);
			}
		}

		if (i.getItemStack().getAmount() > 1)
			i.getItemStack().setAmount(1);

		if (event.isCancelled())
			return;

		return;

		// plugin.log.info("FIRED-F" + plugin.tm.getTeam(event.getPlayer()) +
		// plugin.Game.FlagAHolder.toString());
	}

	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		if (plugin.Game.FlagItemA == null || plugin.Game.FlagItemB == null
				|| plugin.Game.FlagAHolder == null
				|| plugin.Game.FlagBHolder == null)
			return;

		Item i = event.getEntity();
		if (plugin.Game.FlagItemA != null
				&& i.getUniqueId() == plugin.Game.FlagItemA.getUniqueId()
				&& plugin.Game.FlagAHolder != null
				&& plugin.Game.FlagAHolder == "Home") {
			event.setCancelled(true);
			return;
		} else if (plugin.Game.FlagItemB != null
				&& i.getUniqueId() == plugin.Game.FlagItemB.getUniqueId()
				&& plugin.Game.FlagBHolder != null
				&& plugin.Game.FlagBHolder == "Home") {
			event.setCancelled(true);
			return;
		} else if (i.getUniqueId() == plugin.Game.FlagItemA.getUniqueId())
			plugin.ctfu.despawn("A");
		else if (i.getUniqueId() == plugin.Game.FlagItemB.getUniqueId())
			plugin.ctfu.despawn("B");
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!plugin.tm.getTeam(event.getPlayer()).equals("A")
				&& !plugin.tm.getTeam(event.getPlayer()).equals("B"))
			return;
		if (plugin.Game.FlagAHolder instanceof Player
				&& plugin.Game.FlagAHolder == event.getPlayer()) {
			plugin.ctfu.drop(event.getPlayer(), event.getItemDrop());
		} else if (plugin.Game.FlagBHolder instanceof Player
				&& plugin.Game.FlagBHolder == event.getPlayer())
			plugin.ctfu.drop(event.getPlayer(), event.getItemDrop());
		else if (event.getItemDrop().hasMetadata("allowedDrop")) {
			event.setCancelled(false);
		} else
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onPlayerPlaceFlag(BlockPlaceEvent event) {

		if (!plugin.Game.running)
			return;

		if (event.getBlock().getType() != Material.WOOL)
			return;
		if (plugin.Game.Spawn == null
				|| event.getPlayer().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		if (plugin.tm.getTeam(event.getPlayer()).equals("A")
				&& plugin.Game.FlagBHolder instanceof Player
				&& plugin.Game.FlagBHolder == event.getPlayer()) {
			if (plugin.Game.getFlagA().distance(event.getBlock().getLocation()) < 1.7) {
				if (!(plugin.Game.FlagAHolder instanceof String && plugin.Game.FlagAHolder == "Home")) {
					plugin.cm.SendMessage(event.getPlayer(), ChatColor.RED
							+ "Your Team's Flag must be at home!");
					event.setCancelled(true);
					return;
				}
				plugin.ctfu.captured(event.getPlayer(), "A");
				plugin.MM.ChangeBlock(event.getBlock(), event.getBlock()
						.getTypeId(), event.getBlock().getData());
				event.getBlock().setType(Material.GLOWSTONE);
				this.plugin
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								this.plugin,
								new BlockResetThread(event.getBlock(),
										plugin.MM), 50L);
				event.setCancelled(false);
			} else
				event.setCancelled(true);
		} else if (plugin.tm.getTeam(event.getPlayer()).equals("B")
				&& plugin.Game.FlagAHolder instanceof Player
				&& plugin.Game.FlagAHolder == event.getPlayer()) {
			plugin.log.info("sus");
			if (plugin.Game.getFlagB().distance(event.getBlock().getLocation()) < 1.7) {
				if (!(plugin.Game.FlagBHolder instanceof String && plugin.Game.FlagBHolder == "Home")) {
					plugin.cm.SendMessage(event.getPlayer(), ChatColor.RED
							+ "Your Team's Flag must be at home!");
					event.setCancelled(true);
					return;
				}
				plugin.ctfu.captured(event.getPlayer(), "B");
				plugin.MM.ChangeBlock(event.getBlock(), event.getBlock()
						.getTypeId(), event.getBlock().getData());
				event.getBlock().setType(Material.GLOWSTONE);
				this.plugin
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								this.plugin,
								new BlockResetThread(event.getBlock(),
										plugin.MM), 50L);
				event.setCancelled(false);
			} else
				event.setCancelled(true);
		}
		plugin.log.info(plugin.Game.getFlagA().toString() + " - A");
		plugin.log.info(plugin.Game.getFlagB().toString() + " - B");
	}

	@EventHandler
	public void onPlayerChangeClass(ChangeAwayFromClassEvent event) {
		if (!plugin.tm.getTeam(event.getPlayer()).equals("A")
				&& !plugin.tm.getTeam(event.getPlayer()).equals("B"))
			return;
		if (plugin.Game.FlagAHolder instanceof Player
				&& plugin.Game.FlagAHolder == event.getPlayer()) {
			event.getPlayer().setHealth(0D);
		} else if (plugin.Game.FlagBHolder instanceof Player
				&& plugin.Game.FlagBHolder == event.getPlayer())
			event.getPlayer().setHealth(0D);

	}
}

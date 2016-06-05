package net.askarian.MisterErwin.CTF.listener;

import net.askarian.MisterErwin.CTF.API.events.CTFPlayerDeathEvent;
import net.askarian.MisterErwin.CTF.CTF;
import net.askarian.MisterErwin.CTF.threads.ItemRemoveThread;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	private CTF plugin;

	public PlayerListener(CTF pl) {
		this.plugin = pl;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if ((!this.plugin.tm.getTeam(event.getPlayer()).equals("A"))
				&& (!this.plugin.tm.getTeam(event.getPlayer()).equals("B")))
			return;
		if (this.plugin.Game.running || this.plugin.Game.idle
				|| this.plugin.Game.after) {
			event.setCancelled(this.plugin.cm.PlayerChat(event.getPlayer(),
					event.getMessage()));
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		if (Boolean.valueOf(plugin.conm.get("auto"))) {
			plugin.clm.setKit(event.getPlayer(), plugin.clm.Classes.get(0)
					.getCommand());
			plugin.cm
					.SendMessage(event.getPlayer(), "You are now in the Game!");
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		if (!this.plugin.tm.isinGame(event.getPlayer()))
			return;

		this.plugin.tm.LeaveTeam(event.getPlayer(), true);

		// this.plugin.InvM.give(event.getPlayer());

		if (Integer.valueOf(this.plugin.conm.get("minPlayer")) != 0
				&& this.plugin.tm.TeamA.size() + this.plugin.tm.TeamA.size() < Integer
						.valueOf(this.plugin.conm.get("minPlayer"))) {
			plugin.log
					.info("There are not enought player for the CtF match - 'll stop it!");
			plugin.Game.stopCauseCounter();

		}

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player p = event.getEntity();
			if ((this.plugin.tm.getTeam(p).equals("A"))
					|| (this.plugin.tm.getTeam(p).equals("B"))) {
				event.setDeathMessage("");
				event.getDrops().clear();
				event.setDroppedExp(0);
				event.setKeepLevel(true);
				plugin.getServer()
						.getPluginManager()
						.callEvent(
								new CTFPlayerDeathEvent(
										p, plugin.trans));
				if (((this.plugin.Game.FlagAHolder instanceof Player))
						&& (this.plugin.Game.FlagAHolder == p)) {
					this.plugin.ctfu.die(p, "A");
					// new ColorNames(p.getKiller(), "red");
				} else if (((this.plugin.Game.FlagBHolder instanceof Player))
						&& (this.plugin.Game.FlagBHolder == p)) {
					this.plugin.ctfu.die(p, "B");
					// new ColorNames(p.getKiller(), "blue");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!this.plugin.tm.isinGame(event.getPlayer())
				&& !Boolean.valueOf(plugin.conm.get("auto"))) {
			return;
		} else if (!this.plugin.tm.isinGame(event.getPlayer())
				&& Boolean.valueOf(plugin.conm.get("auto"))) {
			plugin.Game.tp(event.getPlayer());
			plugin.tm.JoinTeam(event.getPlayer());
		}
		Player p = event.getPlayer();

		if (this.plugin.Game.spawnPlayersA
				.contains(event.getPlayer().getName()))
			this.plugin.Game.spawnPlayersA.remove(event.getPlayer().getName());
		if (this.plugin.Game.spawnPlayersB
				.contains(event.getPlayer().getName())) {
			this.plugin.Game.spawnPlayersB.remove(event.getPlayer().getName());
		}
		if (this.plugin.tm.getTeam(p).equals("A")) {
			this.plugin.clm.giveKit(p);
			event.setRespawnLocation(this.plugin.Game.SpawnA);
		} else if (this.plugin.tm.getTeam(p).equals("B")) {
			this.plugin.clm.giveKit(p);
			event.setRespawnLocation(this.plugin.Game.SpawnB);
		} else {
			p.sendMessage("NOPE");
		}
		if (this.plugin.Game.Spawn == null) {
			event.setRespawnLocation(p.getWorld().getSpawnLocation());
			this.plugin.log.warning("Spawn Location not set!");
		}
		if (this.plugin.Game.SpawnA == null) {
			event.setRespawnLocation(p.getWorld().getSpawnLocation());
			this.plugin.log.warning("Spawn Location A not set!");
		}
		if (this.plugin.Game.SpawnB == null) {
			event.setRespawnLocation(p.getWorld().getSpawnLocation());
			this.plugin.log.warning("Spawn Location B not set!");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBuild(BlockPlaceEvent event) {

		if (plugin.Game.Spawn == null
				|| event.getPlayer().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		if (event.getPlayer().isOp()
				&& event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if (plugin.Game.Spawn == null
				|| event.getPlayer().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		if (event.getPlayer().isOp()
				&& event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		if (plugin.Game.Spawn == null
				|| event.getBlock().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		if ((event.getNewState().getType() == Material.FIRE))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockRemovebyFire(BlockBurnEvent event) {
		if (plugin.Game.Spawn == null
				|| event.getBlock().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (plugin.tm.isinGame(event.getPlayer())) {
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
				plugin.cm
						.SendMessage(
								event.getPlayer(),
								ChatColor.RED
										+ "You can't teleport out of the Game! - Use /ctf leave first");
				event.setCancelled(true);
				event.setTo(event.getFrom());
				return;
			}
			// if ( plugin.Game.Spawn == null || event.getTo().getWorld() ==
			// plugin.Game.Spawn.getWorld()) {
			//
			// }else {
			// event.getPlayer().sendMessage(ChatColor.RED +
			// "You can't teleport out of the Game! - Use /ctf leave first");
			// plugin.cm.SendMessage(event.getPlayer(), ChatColor.RED +
			// "You can't teleport out of the Game! - Use /ctf leave first");
			// event.setCancelled(true);
			// event.setTo( event.getFrom() );
			// }

		}

	}

	@EventHandler
	public void onPaintingBreak(HangingBreakByEntityEvent event) {
		if (plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!plugin.tm.isinGame(event.getPlayer()))
			return;

		if (event.getAction() != Action.RIGHT_CLICK_AIR
				&& event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player p = event.getPlayer();
		try {
			if (((Damageable) p).getHealth() == ((Damageable) p).getMaxHealth())
				return;

			if (event.getItem() != null && event.getItem().getTypeId() == 364) {

				plugin.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								plugin,
								new ItemRemoveThread(p, new ItemStack(
										Material.COOKED_BEEF, 1)), 1);
				p.setHealth((((Damageable) p).getHealth() + 8 <= 20) ? ((Damageable) p)
						.getHealth() + 8 : 20);

			}
		} catch (Exception e) {
			plugin.log.info("ERROR :(");
			e.printStackTrace();
		}
	}

}

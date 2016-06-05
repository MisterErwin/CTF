package net.askarian.MisterErwin.CTF.listener;

import net.askarian.MisterErwin.CTF.CTF;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class CListener implements Listener {
	private CTF plugin;

	// Listener
	// private archerlistener archerl = new archerlistener (plugin);

	public CListener(CTF pl) {
		plugin = pl;
	}

	// Generell Listener
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		// Is in World?
		if (plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		// Is player
		if (!(event.getEntity().getShooter() instanceof Player)) {
			event.getEntity().remove();
			return;
		}

	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if (plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != plugin.Game.Spawn.getWorld())
			return;
		if (event.getCause() == DamageCause.FALL
				&& event.getEntity() instanceof Player
				&& event.getEntity().getLocation().getBlock().getType() == Material.SOUL_SAND) {
			event.setCancelled(true);
		}

		if (event.getEntity() instanceof Player && plugin.Game.idle
				&& plugin.tm.isinGameWorld((Player) event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(EntityDamageByEntityEvent event) {

		Player d;

		if (!(event.getEntity() instanceof Player))
			return;

		if (!((Player) event.getEntity()).isOnline())
			return;

		if (!(event.getDamager() instanceof Player)
				&& !(event.getDamager() instanceof Projectile))
			return;

		if (event.getDamager() instanceof Projectile) {
			if (((Projectile) event.getDamager()).getShooter() instanceof Player)
				d = (Player) ((Projectile) event.getDamager()).getShooter();
			else
				return;
		} else
			d = (Player) event.getDamager();

		if (!plugin.tm.isinGame((Player) event.getEntity()))
			return;

		if (plugin.tm.getTeam((Player) event.getEntity()).equals(plugin.tm
				.getTeam(d)))
			event.setCancelled(true);
		// Player on Spawn
		if (plugin.tm.getTeam((Player) event.getEntity()).equals("A"))
			if (plugin.Game.spawnPlayersA.contains(((Player) event.getEntity())
					.getName()))
				event.setCancelled(true);
		if (plugin.tm.getTeam((Player) event.getEntity()).equals("B"))
			if (plugin.Game.spawnPlayersB.contains(((Player) event.getEntity())
					.getName()))
				event.setCancelled(true);
		// Damager in Spawn
		if (event.getDamager() instanceof Player) {
			if (plugin.tm.getTeam(d).equals("A"))
				if (plugin.Game.spawnPlayersA.contains(((Player) event
						.getDamager()).getName()))
					event.setCancelled(true);
			if (plugin.tm.getTeam(d).equals("B"))
				if (plugin.Game.spawnPlayersB.contains(((Player) event
						.getDamager()).getName()))
					event.setCancelled(true);
		}

		// Reset armor && weapon
		if ((event.getEntity() instanceof Player) && plugin.Game.Spawn != null
				&& event.getEntity().getWorld() == plugin.Game.Spawn.getWorld()) {
			Player p = (Player) event.getEntity();
			ItemStack[] armor = p.getInventory().getArmorContents();
			if (armor.length > 0) {
				for (int i = 0; i < armor.length; i++)
					armor[i].setDurability((short) (-armor[i].getType()
							.getMaxDurability()));
			}
		}

		if ((event.getDamager() instanceof Player) && plugin.Game.Spawn != null
				&& event.getEntity().getWorld() == plugin.Game.Spawn.getWorld()) {
			Player o = (Player) event.getDamager();
			if (!plugin.tm.getTeam(o).equals("")) {
				ItemStack handitem = o.getItemInHand();
				if (handitem.getDurability() != handitem.getType()
						.getMaxDurability())
					handitem.setDurability((short) -32768);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onRegainHealth(EntityRegainHealthEvent event) {
		if (((event.getEntity() instanceof Player))
				&& (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
				&& (plugin.tm.getTeam((Player) event.getEntity()).equals("A") || plugin.tm
				.getTeam((Player) event.getEntity()).equals("B"))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onFoodLose(FoodLevelChangeEvent event) {
		if (((event.getEntity() instanceof Player))
				&& (plugin.tm.getTeam((Player) event.getEntity()).equals("A") || plugin.tm
				.getTeam((Player) event.getEntity()).equals("B"))) {
			event.setCancelled(true);
		}
	}

	// @EventHandler
	// public void onBlockPlace (BlockPlaceEvent event) {
	// if ((plugin.Game.running != true && plugin.Game.idle != true) ||
	// event.getPlayer().getWorld() != plugin.Game.world)
	// return;
	// Player p = (Player)event.getPlayer();
	// if (plugin.tm.getTeam(p) == "A" || plugin.tm.getTeam(p) == "B")
	// return;
	// // int ItemID = event.getBlockPlaced().getTypeId();
	// // //allowed to
	// // //medic - cobweb(30)
	// // if (plugin.tm.getPClass(p) == "medic" &&ItemID == 30)
	// // return;
	// // if (plugin.tm.getPClass(p) == "pyro" &&ItemID == 51)
	// // return;
	//
	// event.getBlock().setTypeId(0);
	// event.setCancelled(true);
	// }

	@EventHandler
	public void onBowUse(EntityShootBowEvent ev) {
		if ((ev.getEntity() instanceof Player)) {
			Player p = (Player) ev.getEntity();
			if (plugin.Game.Spawn == null
					|| p.getWorld() != plugin.Game.Spawn.getWorld())
				return;
			if (!plugin.tm.getTeam(p).equals("")) {
				ItemStack handitem = p.getItemInHand();
				if (handitem.getDurability() != handitem.getType()
						.getMaxDurability())
					handitem.setDurability((short) -32768);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (plugin.tm.getTeam(event.getPlayer()).equals("A")
				|| plugin.tm.getTeam(event.getPlayer()).equals("B")) {
			if (!plugin.Game.spawnPlayersA
					.contains(event.getPlayer().getName())) {
				if (event.getPlayer().getLocation().getBlock()
						.getRelative(BlockFace.DOWN).getTypeId() == 87)
					plugin.Game.spawnPlayersA.add(event.getPlayer().getName());
			} else {
				if (event.getPlayer().getLocation().getBlock()
						.getRelative(BlockFace.DOWN).getTypeId() != 87)
					plugin.Game.spawnPlayersA.remove(event.getPlayer()
							.getName());
			}
			if (!plugin.Game.spawnPlayersB
					.contains(event.getPlayer().getName())) {
				if (event.getPlayer().getLocation().getBlock()
						.getRelative(BlockFace.DOWN).getTypeId() == 22)
					plugin.Game.spawnPlayersB.add(event.getPlayer().getName());
			} else {
				if (event.getPlayer().getLocation().getBlock()
						.getRelative(BlockFace.DOWN).getTypeId() != 22)
					plugin.Game.spawnPlayersB.remove(event.getPlayer()
							.getName());
			}
		}

		if (plugin.tm.isinGame(event.getPlayer())) {
			if (event.getPlayer().getInventory().contains(Material.COMPASS)) {
				if (plugin.tm.getTeam(event.getPlayer()).equals("A")) {
					event.getPlayer().setCompassTarget(plugin.Game.FlagB);
					// if (plugin.Game.FlagAHolder instanceof Player)
					// event.getPlayer().setCompassTarget(((Player)plugin.Game.FlagAHolder).getLocation());
					// else if (plugin.Game.FlagAHolder instanceof String &&
					// plugin.Game.FlagAHolder.equals("Home"))
					// event.getPlayer().setCompassTarget(plugin.Game.FlagA);
					// else if (plugin.Game.FlagAHolder instanceof Location)
					// event.getPlayer().setCompassTarget((Location)
					// plugin.Game.FlagAHolder);
				} else if (plugin.tm.getTeam(event.getPlayer()).equals("B")) {
					event.getPlayer().setCompassTarget(plugin.Game.FlagA);

					// if (plugin.Game.FlagBHolder instanceof Player)
					// event.getPlayer().setCompassTarget(((Player)plugin.Game.FlagBHolder).getLocation());
					// else if (plugin.Game.FlagBHolder instanceof String &&
					// plugin.Game.FlagBHolder.equals("Home"))
					// event.getPlayer().setCompassTarget(plugin.Game.FlagB);
					// else if (plugin.Game.FlagBHolder instanceof Location)
					// event.getPlayer().setCompassTarget((Location)
					// plugin.Game.FlagBHolder);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity ent = event.getEntity();

		if (ent instanceof Fireball) {
			((Fireball) ent).setIsIncendiary(false);
			((Fireball) ent).setYield(0F);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		event.setFire(false); // Only really needed for fireballs

		Entity ent = event.getEntity();
		if (ent instanceof Fireball)
			event.setRadius(2); // Increased from default(1), since the fireball
								// now don't cause fire
	}

}

import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.API.events.ChangeAwayFromClassEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Cake;

import java.util.*;

public class engineer extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Engineer-Class";
	private String description = "";
	private String command = "engineer";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.DISPENSER);

	private int ThreadID;

	private HashMap<String, TeleporterPack> portals = new HashMap<String, TeleporterPack>();
	private HashMap<String, Dispenser> turrets = new HashMap<String, Dispenser>();
	private HashMap<String, Location> cakes = new HashMap<String, Location>();

	@Override
	public void disable() {
		PlayerInteractEvent.getHandlerList().unregister(this);
		BlockPlaceEvent.getHandlerList().unregister(this);
		BlockExpEvent.getHandlerList().unregister(this);

		for (TeleporterPack tp : this.portals.values()) {
			tp.reset();
		}

		// plugin.getServer().getScheduler().cancelTask(this.ThreadID);
		// this.ThreadID = 0;
	}

	@Override
	public void enable() {
		authors.add("MisterErwin");
		try {
			/* Register the Listener? */
			plugin.getServer().getPluginManager().registerEvents(this, plugin);

			// //Thread
			// this.ThreadID =
			// plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin,
			// new Runnable() {
			// @Override
			// public void run() {
			// //Nina's Invisible
			// HashMap<String, String> invn = InvNinjas;
			// for (Player p : plugin.getServer().getOnlinePlayers()) {
			// if (invn.containsKey(p.getName())){
			// if (p.getInventory().contains(331) &&
			// p.getItemInHand().getTypeId() == 331){
			// p.getInventory().removeItem(new ItemStack[] { new ItemStack(331,
			// 1) });
			// tooglev (p, false);
			// }else{
			// tooglev (p, true);
			// InvNinjas.remove(p.getName());
			// }
			// }
			// }
			//
			// }
			//
			// private void tooglev (Player p, boolean visible){
			// HashMap<String, String> otherTeamPlayer;
			// if (plugin.tm.getTeam(p) == "A")
			// otherTeamPlayer = plugin.tm.TeamB;
			// else if (plugin.tm.getTeam(p) == "B")
			// otherTeamPlayer = plugin.tm.TeamA;
			// else
			// return;
			// //Hide / Show
			// for (Entry<String, String> otp : otherTeamPlayer.entrySet()) {
			// try{
			// if(!visible){
			// plugin.getServer().getPlayerExact(otp.getKey()).hidePlayer(p);
			// plugin.cm.SendMessage(p, ChatColor.DARK_PURPLE +
			// "You are now invisible");
			// }else{
			// plugin.getServer().getPlayerExact(otp.getKey()).showPlayer(p);
			// if (plugin.cm.GetMessage(p).equalsIgnoreCase("now invisible"))
			// plugin.cm.SendMessage(p, ChatColor.RED +
			// "You are not longer invisible");
			// }
			// }catch (Exception e) {}
			// }
			// }
			// }, 60L, 20L);

		} catch (Exception exe) {
			System.out.println("Enabling of " + this.getClass().getSimpleName()
					+ " has failed due to " + exe.getLocalizedMessage());
		}

	}

	@Override
	public List<String> getAuthors() {
		return authors;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public ItemStack getIcon() {
		return this.icon;
	}

	@Override
	public boolean givekit(Player p) {
		PlayerInventory inv = p.getInventory();
		ItemStack item;
		// Rüstung
		// Helm
		item = new ItemStack(306, 1);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(299, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(300, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(309, 1);
		inv.setBoots(item);
		// Items
		// Pickaxe
		item = new ItemStack(278, 1);
		inv.addItem(item);
		// Essen - Steak
		item = new ItemStack(364, 6);
		inv.addItem(item);
		// Wooden sword
		item = new ItemStack(268, 1);
		inv.addItem(item);
		// Portal
		item = new ItemStack(90, 1);
		inv.addItem(item);
		// Dispenser
		item = new ItemStack(23, 1);
		inv.addItem(item);
		// Cake
		item = new ItemStack(92, 1);
		inv.addItem(item);
		// 64 Pfeile
		item = new ItemStack(262, 64);
		inv.addItem(item);

		return true;
	}

	// Listener
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK
				|| event.getAction() == Action.LEFT_CLICK_AIR) {
			Player p = event.getPlayer();
			try {
				if (event.getItem().getTypeId() == 268
						&& plugin.tm.getPClass(p).equalsIgnoreCase(
								this.getCommand())) {
					// HAs player a turret
					if (this.turrets.containsKey(p.getName())) {
						Dispenser dispenser = this.turrets.get(p.getName());
						if (p.getLocation().distance(dispenser.getLocation()) > 8)
							plugin.cm
									.SendMessage(
											p,
											ChatColor.RED
													+ "You have to be at least 8 blocks away from your turret!");
						else {
							if (!dispenser.getInventory().contains(262))
								plugin.cm.SendMessage(p, ChatColor.RED
										+ "Your turret is out of ammo!");
							else {
								Arrow a = dispenser.getWorld().spawnArrow(
										dispenser.getLocation().add(
												p.getLocation().getDirection()
														.multiply(1.4)),
										p.getLocation()
												.getDirection()
												.add(p.getLocation()
														.getDirection()
														.normalize())
												.multiply(10), 0.8f, 12);
								a.setShooter(p);
								a.setVelocity(a.getVelocity().multiply(3));
								dispenser.getInventory().removeItem(
										new ItemStack(262, 1));
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		// CAKE
		if (event.getAction() == Action.LEFT_CLICK_BLOCK
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			Player p = event.getPlayer();
			try {
				if (event.getClickedBlock().getType() == Material.CAKE_BLOCK) {
					Iterator<?> it = ((HashMap<String, Location>) this.cakes
							.clone()).entrySet().iterator();
					Location cl = event.getClickedBlock().getLocation();
					String ret = "";
					while (it.hasNext()) {
						Map.Entry pairs = (Map.Entry) it.next();
						Location l = (Location) pairs.getValue();
						if (l.distance(cl) < 0.5) {
							ret = (String) pairs.getKey();
							break;
						} else
							plugin.log.info(l.distance(cl) + "");

						it.remove(); // avoids a ConcurrentModificationException
					}

					if (ret.equals("")) {
						p.sendMessage("NOT A VALID CAKE!");
						return;
					}

					if (!plugin.tm.getTeam(p).equals(plugin.tm.getTeam(ret))) {
						plugin.cm
								.SendMessage(
										p,
										ChatColor.RED
												+ "This is not your regenerator! - Destroy it!");
						return;
					}

					Block clb = event.getClickedBlock();
					Cake c = (Cake) clb.getState().getData();
					if (c.getSlicesRemaining() <= 1) {

						plugin.cm.SendMessage(p, ChatColor.RED
								+ "This regenerator is empty!");
						event.setCancelled(true);
						return;
					} else {
						event.setCancelled(false);
						plugin.clm.giveKit(p);
						c.setSlicesRemaining(c.getSlicesRemaining() - 3);
						p.setHealth(20);
						plugin.cm.SendMessage(p, ChatColor.YELLOW
								+ "You were regenerated!");
						return;
					}
				}
			} catch (Exception ignored) {
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPlaceBlock(BlockPlaceEvent event) {
		if (!plugin.tm.isinGame(event.getPlayer()))
			return;
		Player p = event.getPlayer();

		if (plugin.tm.getPClass(p).equalsIgnoreCase(this.getCommand())) {
			if (p.getItemInHand().getTypeId() == 23) {
				if (event.getBlockAgainst().getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).getTypeId() != 0) {
					plugin.cm.SendMessage(p, ChatColor.RED
							+ "You can't place a turret here!"
							+ event.getBlockAgainst().getRelative(BlockFace.UP)
									.getTypeId());
					event.setCancelled(true);
					return;
				}

				if (!this.turrets.containsKey(p.getName())) {
					Block pb = event.getBlock(); // .getRelative(BlockFace.UP);

					if (!(pb.getState() instanceof Dispenser)) {
						plugin.log.info(" Error -" + pb.getTypeId());
						event.setCancelled(true);
						return;
					}
					pb.setType(Material.FENCE);
					pb.getRelative(BlockFace.UP).setType(Material.AIR);
					pb.getRelative(BlockFace.UP).setType(Material.DISPENSER);
					if (!(pb.getRelative(BlockFace.UP).getState() instanceof Dispenser)) {
						plugin.log.info(" Error2 -"
								+ pb.getRelative(BlockFace.UP).getTypeId());
						event.setCancelled(true);
						pb.setType(Material.AIR);
						pb.getRelative(BlockFace.UP).setType(Material.AIR);
						return;
					}
					this.turrets
							.put(p.getName(),
									(Dispenser) pb.getRelative(BlockFace.UP)
											.getState());
					event.setCancelled(false);
				} else {
					plugin.cm.SendMessage(p, ChatColor.RED
							+ "You have already set a turret!");
					event.setCancelled(true);
				}
			}

			// Cake
			if (p.getItemInHand().getTypeId() == 92) {
				if (event.getBlockAgainst().getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP).getTypeId() != 0) {
					plugin.cm.SendMessage(p, ChatColor.RED
							+ "You can't place a Cake here!"
							+ event.getBlockAgainst().getRelative(BlockFace.UP)
									.getTypeId());
					event.setCancelled(true);
					return;
				}

				if (!this.cakes.containsKey(p.getName())) {
					Block pb = event.getBlock(); // .getRelative(BlockFace.UP);

					pb.setType(Material.GLASS);
					pb.getRelative(BlockFace.UP).setType(Material.AIR);
					pb.getRelative(BlockFace.UP).setType(Material.CAKE_BLOCK);

					this.cakes.put(p.getName(), pb.getRelative(BlockFace.UP)
							.getLocation());

					plugin.log.info(this.cakes.size() + "");
					event.setCancelled(false);
					return;
				} else {
					plugin.cm.SendMessage(p, ChatColor.RED
							+ "You have already set a cake!");
					event.setCancelled(true);
				}
			}

			// TELE:
			if (event.getBlockPlaced().getType() == Material.PORTAL) {
				if (event.getBlockAgainst().getType() == Material.SPONGE
						|| event.getBlockAgainst().getType() == Material.NETHERRACK
						|| event.getBlockAgainst().getType() == Material.LAPIS_BLOCK
						|| event.getBlockAgainst().getType() == Material.FENCE
						|| event.getBlockAgainst().getType() == Material.NETHER_FENCE
						|| event.getBlockAgainst().getType() == Material.IRON_FENCE)
					return;
				Boolean buildexit = false;
				if (this.portals.containsKey(p.getName())) {
					if (this.portals.get(p.getName()).getExit() == null)
						buildexit = true;
					else if (this.portals.get(p.getName()).on == false)
						this.portals.remove(p.getName());
					else {
						this.plugin.cm.SendMessage(p,
								"You have already built a portal");
						return;
					}
				}

				if (buildexit == true) {
					plugin.log.info("Build-exit!");
					TeleporterPack telep = this.portals.get(p.getName());
					telep.setExit(p,
							event.getBlockPlaced().getRelative(BlockFace.DOWN)
									.getLocation());
					this.portals.remove(p.getName());
					this.portals.put(p.getName(), telep);
				} else {
					TeleporterPack telep = new TeleporterPack(plugin, p);
					telep.setEntrace(p,
							event.getBlockPlaced().getRelative(BlockFace.DOWN)
									.getLocation());
					this.portals.put(p.getName(), telep);
				}

				event.setCancelled(true);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		if (!plugin.tm.isinGame(event.getPlayer()))
			return;
		Player p = event.getPlayer();

		if (event.getBlock().getTypeId() == 23) {
			if (!(event.getBlock().getState() instanceof Dispenser))
				return;
			Dispenser d = (Dispenser) event.getBlock().getState();
			if (this.turrets.containsValue(d)) {
				this.turrets.remove(p.getName());
				d.getInventory().clear();
				d.getBlock().getRelative(BlockFace.DOWN).setTypeId(0);
				d.getBlock().setTypeId(0);
				event.setCancelled(false);
			} else
				plugin.cm.SendMessage(p, ChatColor.RED + "Unknown turret!");
		}

		// CAKE
		if (event.getBlock().getTypeId() == 20) {

			Block b = event.getBlock();
			if (b.getRelative(BlockFace.UP).getTypeId() == 92) {
				if (this.cakes.containsValue(b.getRelative(BlockFace.UP)
						.getLocation())) {
					String removalKey = null;

					for (Map.Entry entry : this.cakes.entrySet()) {
						if (b.getRelative(BlockFace.UP).getLocation()
								.equals(entry.getValue())) {
							removalKey = (String) entry.getKey();
							break;
						}
					}

					if (removalKey != null) {
						this.cakes.remove(removalKey);
						b.getRelative(BlockFace.UP).setTypeId(0);
						b.setTypeId(0);
						event.setCancelled(false);
					} else
						plugin.cm.SendMessage(p, ChatColor.RED
								+ "Unknown cake! - No entry like me");

				} else
					plugin.cm.SendMessage(p, ChatColor.RED
							+ "Unknown cake! - no contain");
			}
		}

		// PORTAL
		if (event.getBlock().getType() == Material.WORKBENCH) {

			Block b = event.getBlock();
			for (TeleporterPack tp : this.portals.values()) {
				tp.onTeleporterBreak(p, event.getBlock());
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLeaveClass(ChangeAwayFromClassEvent event) {
		removeFromClass(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEngineerDeathEvent(PlayerDeathEvent event) {
		removeFromClass(event.getEntity());
	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

	private void removeFromClass(Player p) {
		Dispenser d = null;
		if (this.turrets.containsKey(p.getName())) {
			d = this.turrets.remove(p.getName());
			d.getInventory().clear();
			d.getBlock().getRelative(BlockFace.DOWN).setTypeId(0);
			d.getBlock().setTypeId(0);
		}

		Location c = null;

		if (this.cakes.containsKey(p.getName())) {
			c = this.cakes.remove(p.getName());
			c.getBlock().getRelative(BlockFace.DOWN).setTypeId(0);
			c.getBlock().setTypeId(0);
		}

		if (this.portals.containsKey(p.getName())) {
			TeleporterPack tp = this.portals.get(p.getName());
			tp.reset();
			tp = null;
			this.portals.remove(p.getName());
		}
	}

}

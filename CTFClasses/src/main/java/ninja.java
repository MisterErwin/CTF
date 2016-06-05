import net.askarian.MisterErwin.CTF.API.CtFClass;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ninja extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Ninja-Class";
	private String description = "";
	private String command = "ninja";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.ENDER_PEARL);

	private int ThreadID;

	private HashMap<String, String> InvNinjas = new HashMap<String, String>();

	@Override
	public void disable() {
		EntityDamageEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		ProjectileHitEvent.getHandlerList().unregister(this);

		plugin.getServer().getScheduler().cancelTask(this.ThreadID);
		this.ThreadID = 0;
	}

	@Override
	public void enable() {
		authors.add("MisterErwin");
		try {
			/* Register the Listener? */
			plugin.getServer().getPluginManager().registerEvents(this, plugin);

			// Thread
			this.ThreadID = plugin.getServer().getScheduler()
					.scheduleSyncRepeatingTask(plugin, new Runnable() {
						public void run() {
							// Nina's Invisible
							HashMap<String, String> invn = InvNinjas;
							for (Player p : plugin.getServer()
									.getOnlinePlayers()) {
								if (invn.containsKey(p.getName())) {
									if (p.getInventory().contains(331)
											&& p.getItemInHand().getTypeId() == 331) {
										p.getInventory()
												.removeItem(
														new ItemStack[] { new ItemStack(
																331, 1) });
										tooglev(p, false);
									} else {
										tooglev(p, true);
										plugin.cm
												.SendMessage(
														p,
														ChatColor.DARK_PURPLE
																+ "You are now invisible");

										InvNinjas.remove(p.getName());
									}
								}
							}

						}

						private void tooglev(Player p, boolean visible) {
							// HashMap<String, String> otherTeamPlayer;
							// if (plugin.tm.getTeam(p) == "A")
							// otherTeamPlayer = plugin.tm.TeamB;
							// else if (plugin.tm.getTeam(p) == "B")
							// otherTeamPlayer = plugin.tm.TeamA;
							// else
							// return;
							// Hide / Show
							if (!visible)
								p.addPotionEffect(new PotionEffect(
										PotionEffectType.INVISIBILITY, 9999, 1));
							else {
								p.removePotionEffect(PotionEffectType.INVISIBILITY);
								plugin.cm.SendMessage(p, ChatColor.RED
										+ "You are not longer invisible");

							}
							// for (Entry<String, String> otp :
							// otherTeamPlayer.entrySet()) {
							// try{
							// if(!visible){
							// plugin.getServer().getPlayerExact(otp.getKey()).hidePlayer(p);
							// // plugin.cm.SendMessage(p, ChatColor.DARK_PURPLE
							// + "You are now invisible");
							// }else{
							// plugin.getServer().getPlayerExact(otp.getKey()).showPlayer(p);
							// if
							// (plugin.cm.GetMessage(p).equalsIgnoreCase("now invisible"))
							// plugin.cm.SendMessage(p, ChatColor.RED +
							// "You are not longer invisible");
							// }
							// }catch (Exception e) {}
							// }
						}
					}, 60L, 20L);

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
		// Keine
		// Items
		// Eisenschwert
		item = new ItemStack(267, 1);
		item.addEnchantment(Enchantment.DAMAGE_ALL, 4);
		inv.addItem(item);
		// Ender Perle
		item = new ItemStack(368, 10);
		inv.addItem(item);
		// Eier
		item = new ItemStack(344, 10);
		ItemMeta im = item.getItemMeta();
		List<String> desc = new ArrayList<String>();
		desc.add(ChatColor.BLUE + "Throw it to confuse your enemies");
		im.setLore(desc);
		im.setDisplayName(ChatColor.YELLOW + "Flashbomb");

		item.setItemMeta(im);
		inv.addItem(item);
		// Redstone -
		item = new ItemStack(331, 64);
		inv.addItem(item);
		// Essen
		item = new ItemStack(364, 4);
		inv.addItem(item);

		// Compass
		item = new ItemStack(Material.COMPASS);
		im = item.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Enemy Flag Locator");
		ArrayList<String> al = new ArrayList<String>();
		al.add(ChatColor.YELLOW + "Locate your enemy flag");
		im.setLore(al);
		item.setItemMeta(im);
		inv.setItem(8, item);

		return true;
	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

	// Listener
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK
				&& event.getAction() != Action.RIGHT_CLICK_AIR)
			return;

		Player p = event.getPlayer();
		try {
			if (event.getItem().getTypeId() == 331
					&& plugin.tm.getPClass(p).equalsIgnoreCase(
							this.getCommand())) {
				this.InvNinjas.put(p.getName(), "");
			}
		} catch (Exception e) {
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player p = (Player) event.getEntity();
		if (event.getDamager() instanceof EnderPearl) {
			if (event.getEntity() instanceof Player) {
				if (plugin.tm.getPClass(p).equalsIgnoreCase(this.getCommand()))
					event.setDamage(5);
			}
		}

		if (plugin.tm.getPClass(p).equalsIgnoreCase(this.getCommand())) {
			if (this.InvNinjas.containsKey(p.getName())) {
				this.InvNinjas.remove(p.getName());
				p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 5);
			}

		}

	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player))
			return;

		if (!plugin.tm.isinGame((Player) event.getEntity().getShooter()))
			return;

		if (!plugin.tm.getPClass((Player) event.getEntity().getShooter())
				.equalsIgnoreCase(this.command))
			return;

		if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
			if (plugin.tm.hasFlag((Player) event.getEntity().getShooter())) {
				event.setCancelled(true);
				event.getEntity().remove();
			}

		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onProjectileHit(ProjectileHitEvent event) {
		// Is player
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		// Is Ninja
		Player player = (Player) event.getEntity().getShooter();

		if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
			if (plugin.tm.hasFlag((Player) event.getEntity().getShooter())) {
				event.getEntity().remove();
			}

		}

		if (plugin.tm.getPClass(player).equalsIgnoreCase(this.getCommand())
				&& event.getEntity() instanceof Egg) {
			plugin.Game.Spawn.getWorld().createExplosion(
					event.getEntity().getLocation(), 0.0F);
			// Set fire
			List<Entity> entitylist = event.getEntity().getNearbyEntities(8, 8,
					8);
			for (int i = 0; i < entitylist.size(); i++) {
				if (entitylist.get(i).getType() == EntityType.PLAYER) {
					if (plugin.tm.getTeam((Player) entitylist.get(i)) != plugin.tm
							.getTeam(player)) {
						((Player) entitylist.get(i))
								.addPotionEffect(new PotionEffect(
										PotionEffectType.BLINDNESS, 200, 2));
						((Player) entitylist.get(i))
								.addPotionEffect(new PotionEffect(
										PotionEffectType.CONFUSION, 200, 2));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onChickenSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Chicken) {
			if (event.getSpawnReason() == SpawnReason.EGG) {
				event.getEntity().remove();
				event.setCancelled(true);
			}
		}
	}

}

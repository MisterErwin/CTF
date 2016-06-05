import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.API.events.CTFPlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class archer extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Archer-Class";
	private String description = "Snipe your enemy away!";
	private String command = "archer";
	private ItemStack icon = new ItemStack(Material.BOW);

	private List<String> authors = new ArrayList<String>();

	private HashMap<String, String> custommessages = new HashMap<String, String>();

	@Override
	public void disable() {
		EntityDamageEvent.getHandlerList().unregister(this);

	}

	@Override
	public void enable() {

		authors.add("MisterErwin");
		super.plugin.trans.register("headshottedby",
				"You had been headshotted by &P");
		super.plugin.trans.register("youheadshotted", "You headshotted &P");

		try {
			/* Register the Listener? */
			plugin.getServer().getPluginManager()
					.registerEvents(this, super.plugin);
		} catch (Exception exe) {
			System.out.println("Enabling of " + this.getClass().getSimpleName()
					+ " has failed due to " + exe.getMessage());
			exe.printStackTrace();
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
		item = new ItemStack(302, 1);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(303, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(304, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(305, 1);
		inv.setBoots(item);
		// Items
		// Steinschwert
		item = new ItemStack(272, 1);
		inv.addItem(item);
		// Essen - Steak
		item = new ItemStack(364, 4);
		inv.addItem(item);
		// Bogen -
		item = new ItemStack(261, 1);
		item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
		inv.addItem(item);
		// Pfeile - 2Stacks
		item = new ItemStack(262, 64);
		inv.addItem(item);
		inv.addItem(item);

		// Compass
		item = new ItemStack(Material.COMPASS);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Enemy Flag Locator");
		ArrayList<String> al = new ArrayList<String>();
		al.add(ChatColor.YELLOW + "Locate your enemy flag");
		im.setLore(al);
		item.setItemMeta(im);
		inv.setItem(8, item);

		return true;
	}

	// Listener
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (super.plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != super.plugin.Game.Spawn
						.getWorld())
			return;
		if (event.getDamager() instanceof Projectile
				&& event.getEntity() instanceof Player) {
			if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
				Player opfer = (Player) event.getEntity();
				Player shooter = (Player) ((Projectile) event.getDamager())
						.getShooter();
				// Same Team?

				if (plugin.tm.getTeam(opfer).equals(plugin.tm.getTeam(shooter))
						|| !plugin.tm.getPClass(shooter).equalsIgnoreCase(
								this.getCommand())) {
					// event.setCancelled(true);
					return;
				}

				if (!plugin.tm.getPClass(shooter)
						.equalsIgnoreCase(this.command))
					return;

				// 20 blöcke weg => gesnipert => instakill
				if (opfer.getLocation().distance(shooter.getLocation()) >= 20) {
					// SOUND
					opfer.getInventory().setItem(9, new ItemStack(270, 1));
					opfer.getInventory().getItem(9).setDurability((short) 100);
					// Add to custom message
					opfer.getWorld().playSound(opfer.getLocation(), Sound.ENTITY_PLAYER_BURP,
							1, 9);
					this.custommessages.put(shooter.getName(), opfer.getName());

					opfer.setHealth(0);
				}

			}
		}
	}

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

		// Is Archer
		Player player = (Player) event.getEntity().getShooter();
		if (plugin.tm.getPClass(player).equals("archer")) {
			event.getEntity().remove();
		}

	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(CTFPlayerDeathEvent event) {
		if (this.custommessages.containsKey(event.getKiller().getName())) {
			if (this.custommessages.get(event.getKiller().getName())
					.equalsIgnoreCase(event.getPlayer().getName())) {
				event.setMessage4Player(plugin.trans.get("headshottedby")
						.replace("&P", event.getKiller().getName()));

				event.setMessage4Killer(plugin.trans.get("youheadshotted")
						.replace("&P", event.getPlayer().getName()));

				this.custommessages.remove(event.getKiller().getName());

			}
		}
	}

}

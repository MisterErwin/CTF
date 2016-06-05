import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.threads.BlockResetThread;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class pyro extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Pyro-Class";
	private String description = "";
	private String command = "pyro";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.FIRE);

	@Override
	public void disable() {
		EntityDamageEvent.getHandlerList().unregister(this);
		ProjectileHitEvent.getHandlerList().unregister(this);
		BlockPlaceEvent.getHandlerList().unregister(this);
	}

	@Override
	public void enable() {
		authors.add("MisterErwin");
		try {
			/* Register the Listener? */
			super.plugin.getServer().getPluginManager()
					.registerEvents(this, super.plugin);
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
		item = new ItemStack(298, 1);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(299, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(300, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(301, 1);
		inv.setBoots(item);
		// Items
		// AXT
		item = new ItemStack(279, 1);
		inv.addItem(item);
		// Essen
		item = new ItemStack(364, 5);
		inv.addItem(item);
		// Feuerzeug
		item = new ItemStack(259, 1);
		inv.addItem(item);
		// Bogen -
		item = new ItemStack(261, 1);
		inv.addItem(item);
		// Pfeile - 20
		item = new ItemStack(262, 20);
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

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

	// Listener
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (((event.getDamager() instanceof Player))
				&& ((event.getEntity() instanceof Player))
				&& (this.plugin.tm.getPClass((Player) event.getDamager())
						.equalsIgnoreCase("pyro"))
				&& (event.getEntity().getFireTicks() > 1 && !((Player) event
						.getEntity())
						.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)))
			((Player) event.getEntity()).setHealth(0);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onProjectileHit(ProjectileHitEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			event.getEntity().remove();
			return;
		}

		Player player = (Player) event.getEntity().getShooter();
		if (this.plugin.tm.getPClass(player).equalsIgnoreCase("pyro")) {
			this.plugin.Game.Spawn.getWorld().createExplosion(
					event.getEntity().getLocation(), 0.0F);

			List<?> entitylist = event.getEntity().getNearbyEntities(8.0D,
					8.0D, 8.0D);
			for (int i = 0; i < entitylist.size(); i++) {
				if ((((Entity) entitylist.get(i)).getType() != EntityType.PLAYER)
						|| (this.plugin.tm.getTeam((Player) entitylist.get(i))
								.equals(this.plugin.tm.getTeam(player)))
						|| ((Player) entitylist.get(i))
								.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
					continue;
				((Entity) entitylist.get(i)).setFireTicks(50);
			}

		}

		event.getEntity().remove();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onBlockPlace(BlockPlaceEvent event) {
		try {
			if (super.plugin.Game.Spawn == null
					|| event.getPlayer().getWorld() != super.plugin.Game.Spawn
							.getWorld())
				return;
			if (super.plugin.tm.getPClass(event.getPlayer()).equalsIgnoreCase(
					this.getCommand())
					&& (event.getBlockPlaced().getTypeId() == 51)) {
				plugin.MM.ChangeBlock(event.getBlockPlaced(), event.getBlock()
						.getTypeId(), event.getBlock().getData());
				this.plugin
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								this.plugin,
								new BlockResetThread(event.getBlock(),
										plugin.MM), 130L);
				event.setCancelled(false);
			}
			return;
		} catch (Exception e) {
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onFirePlace(BlockIgniteEvent event) {
		try {
			if (super.plugin.Game.Spawn == null
					|| event.getPlayer().getWorld() != super.plugin.Game.Spawn
							.getWorld())
				return;
			if (event.getCause() != IgniteCause.FLINT_AND_STEEL) {
				event.setCancelled(true);
				return;
			}
			if (super.plugin.tm.getPClass(event.getPlayer()).equalsIgnoreCase(
					this.getCommand())) {
				if (event.getBlock().getLocation().distance(plugin.Game.SpawnA) < 10
						|| event.getBlock().getLocation()
								.distance(plugin.Game.SpawnB) < 10) {
					event.getPlayer().sendMessage("Too near @ spawn!");
					event.setCancelled(true);
					return;
				}
				plugin.MM.ChangeBlock(event.getBlock()
						.getRelative(BlockFace.UP), event.getBlock()
						.getRelative(BlockFace.UP).getTypeId(), event
						.getBlock().getRelative(BlockFace.UP).getData());
				this.plugin
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								this.plugin,
								new BlockResetThread(event.getBlock()
										.getRelative(BlockFace.UP), plugin.MM),
								130L);
				event.setCancelled(false);
			}
			return;
		} catch (Exception e) {
		}
	}

}

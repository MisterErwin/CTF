import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.threads.BlockResetThread;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class medic extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Medic-Class";
	private String description = "";
	private String command = "medic";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(364);

	@Override
	public void disable() {
		EntityDamageEvent.getHandlerList().unregister(this);

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
		item = new ItemStack(314, 1);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(315, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(316, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(317, 1);
		inv.setBoots(item);
		// Items
		// Schwert
		item = new ItemStack(283, 1);
		inv.addItem(item);
		// Spinnennetz
		item = new ItemStack(30, 10);
		inv.addItem(item);
		// Essen
		item = new ItemStack(364, 6);
		inv.addItem(item);

		// Compass
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
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerClick(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player))
			return;

		if (super.plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != super.plugin.Game.Spawn
						.getWorld())
			return;

		// medic?
		if (plugin.tm.getPClass((Player) event.getDamager()).equalsIgnoreCase(
				"medic")) {
			Player medic = (Player) event.getDamager();
			Player hp = (Player) event.getEntity();
			if (plugin.tm.getTeam(medic).equals(plugin.tm.getTeam(hp))) {
				hp.setHealth(20);
				plugin.clm.giveKit(hp);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (super.plugin.Game.Spawn == null
				|| event.getPlayer().getWorld() != super.plugin.Game.Spawn
						.getWorld())
			return;
		if (plugin.tm.getPClass((Player) event.getPlayer()).equalsIgnoreCase(
				this.getCommand())) {
			if (event.getBlockPlaced().getTypeId() == 30) {// Spinnennetz
				plugin.MM.ChangeBlock(event.getBlockPlaced(), event.getBlock()
						.getTypeId(), event.getBlock().getData());
				this.plugin
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								this.plugin,
								new BlockResetThread(event.getBlock(),
										plugin.MM), 300L);
				event.setCancelled(false);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onRegainHealth(EntityRegainHealthEvent event) {
		if (((event.getEntity() instanceof Player))
				&& (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
				&& (plugin.tm.getTeam((Player) event.getEntity()).equals("A") || plugin.tm
				.getTeam((Player) event.getEntity()).equals("B"))
				&& (plugin.tm.getPClass((Player) event.getEntity())
						.equalsIgnoreCase("medic"))) {
			event.setCancelled(false);
		}
	}

}

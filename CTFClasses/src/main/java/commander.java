import java.util.ArrayList;
import java.util.List;

import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.API.events.ChangeAwayFromClassEvent;
import net.askarian.MisterErwin.CTF.API.events.ChangeToClassEvent;
import net.askarian.MisterErwin.CTF.threads.ItemRemoveThread;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class commander extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Commander-Class";
	private String description = "Go into your boat and have fun!";
	private String command = "commander";
	private ItemStack icon = new ItemStack(Material.BOAT);

	public List<Player> commanders = new ArrayList<Player>();

	private List<String> authors = new ArrayList<String>();
	private int ThreadID;

	@Override
	public void disable() {
		EntityDamageEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);

		plugin.getServer().getScheduler().cancelTask(this.ThreadID);
		this.ThreadID = 0;

	}

	@Override
	public void enable() {

		authors.add("MisterErwin");
		super.plugin.trans.register("headshottedby",
				"You had been headshotted by &P");
		super.plugin.trans.register("youheadshotted", "You headshotted &P");

		// Thread
		this.ThreadID = plugin.getServer().getScheduler()
				.scheduleSyncRepeatingTask(plugin, new Runnable() {
					public void run() {
						// Add EXP
						for (Player c : commanders) {
							if (!c.isInsideVehicle()
									&& !(c.getVehicle() instanceof Boat))
								return;

							if (c.getExp() <= 0.985)
								c.setExp((float) (c.getExp() + 0.058));
							if (c.getExp() > 0.985)
								c.setExp(1);
						}

					}

				}, 60L, 20L);

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
		item = new ItemStack(333);
		inv.addItem(item);
		// Fireballs
		item = new ItemStack(385, 20);
		inv.addItem(item);

		return true;
	}

	// Listener
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (plugin.tm.getPClass(event.getPlayer()).equalsIgnoreCase(
				this.command)
				&& plugin.tm.isinGame(event.getPlayer())) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR) {

				if (!event.getPlayer().isInsideVehicle()
						|| !(event.getPlayer().getVehicle() instanceof Boat)) {
					plugin.cm.SendMessage(event.getPlayer(), ChatColor.YELLOW
							+ "You have to sit inside a boat to fire!");
					return;
				}

				Player p = event.getPlayer();
				if (p.getExp() >= 1.0) {
					p.setExp(0);

					plugin.getServer()
							.getScheduler()
							.scheduleSyncDelayedTask(
									plugin,
									new ItemRemoveThread(p, new ItemStack(385,
											1)), 1);
					p.launchProjectile(Fireball.class);
					p.getWorld().createExplosion(p.getEyeLocation(), 0.0F);
				} else if (p.getExp() <= 0.986) {
					plugin.cm.SendMessage(p,
							ChatColor.YELLOW
									+ "You have to load your cannon first! "
									+ p.getExp() + " to " + p.getExpToLevel());
					p.setExp((float) (p.getExp() + 0.058));
				}

			}
		}
	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

	@EventHandler
	public void onPlayerChangetoClass(ChangeToClassEvent event) {
		this.commanders.add(event.getPlayer());
	}

	@EventHandler
	public void onPlayerChangeAway(ChangeAwayFromClassEvent event) {
		if (this.commanders.contains(event.getPlayer()))
			this.commanders.remove(event.getPlayer());
	}

}

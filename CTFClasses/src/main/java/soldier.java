import net.askarian.MisterErwin.CTF.API.CtFClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class soldier extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Soldier-Class";
	private String description = "";
	private String command = "soldier";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.IRON_HELMET);

	@Override
	public void disable() {
		PlayerInteractEvent.getHandlerList().unregister(this);
		EntityDamageEvent.getHandlerList().unregister(this);
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
		item = new ItemStack(306, 1);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(307, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(308, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(309, 1);
		inv.setBoots(item);
		// Items
		// Schwert
		item = new ItemStack(Material.IRON_SWORD, 1);
		inv.addItem(item);
		// Essen
		item = new ItemStack(364, 4);
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
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (super.plugin.Game.Spawn == null
				|| event.getPlayer().getWorld() != super.plugin.Game.Spawn
						.getWorld())
			return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player p = event.getPlayer();
		if (super.plugin.tm.getPClass(p).equalsIgnoreCase("soldier")) {
			if (event.getPlayer().getItemInHand() != null
					&& event.getPlayer().getItemInHand().getType() == Material.IRON_SWORD) {
				p.setVelocity(new Vector(0, 0.9, 0));
				;
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (super.plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != super.plugin.Game.Spawn
						.getWorld())
			return;
		if (event.getCause() == DamageCause.FALL
				&& event.getEntity() instanceof Player
				&& super.plugin.tm.getPClass((Player) event.getEntity())
						.equalsIgnoreCase("soldier")) {
			event.setCancelled(true);
		}
	}

}

import net.askarian.MisterErwin.CTF.API.CtFClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class assassin extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Assassin-Class";
	private String description = "";
	private String command = "assassin";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.GOLD_BOOTS);

	@Override
	public void disable() {
		PlayerInteractEvent.getHandlerList().unregister(this);

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
		// Schuhe
		item = new ItemStack(317, 1);
		inv.setBoots(item);
		// Items
		// Eisenschwert
		item = new ItemStack(267, 1);
		inv.addItem(item);
		// Redstone -
		item = new ItemStack(331, 1);
		inv.addItem(item);
		// Zucker 4
		item = new ItemStack(353, 4);
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
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK
				&& event.getAction() != Action.RIGHT_CLICK_AIR)
			return;

		Player p = event.getPlayer();
		try {
			if (event.getItem().getTypeId() == 331
					&& plugin.tm.getPClass(p).equalsIgnoreCase("assassin")) {
				p.getInventory().removeItem(
						new ItemStack[] { new ItemStack(331, 1) });
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.DAMAGE_RESISTANCE, 100, 2));
			}
			if (event.getItem().getTypeId() == 353
					&& plugin.tm.getPClass(p).equalsIgnoreCase("assassin")) {
				p.getInventory().removeItem(
						new ItemStack[] { new ItemStack(353, 1) });
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100,
						4));
			}
		} catch (Exception ignored) {
		}
	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

}

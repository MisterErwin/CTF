import net.askarian.MisterErwin.CTF.API.CtFClass;
import net.askarian.MisterErwin.CTF.threads.ItemRemoveThread;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Thrower extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Thrower-Class";
	private String description = "";
	private String command = "Thrower";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.IRON_SWORD);

	private HashMap<Integer, String> shootedSwords = new HashMap<Integer, String>();

	@Override
	public void disable() {

	}

	@Override
	public void enable() {
		authors.add("MisterErwin");

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
		MaterialData md = item.getData();
		md.setData(DyeColor.GRAY.getData());
		item.setData(md);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(303, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(304, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(305, 1);
		md = item.getData();
		md.setData(DyeColor.GRAY.getData());
		item.setData(md);
		inv.setBoots(item);
		// Items
		// Schwert
		item = new ItemStack(267, 3);
		inv.addItem(item);
		// Essen
		item = new ItemStack(364, 3);
		inv.addItem(item);

		return true;
	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerThrowItem(PlayerDropItemEvent event) {
		if (!plugin.tm.getPClass(event.getPlayer()).equalsIgnoreCase(
				this.command))
			return;

		Item ia = event.getItemDrop();

		Item i = ia.getWorld().dropItem(ia.getLocation(), ia.getItemStack());

		if (i.getItemStack().getTypeId() != 267)
			return;

		i.setMetadata("allowedDrop", new FixedMetadataValue(this.plugin,
				Boolean.valueOf(true)));

		Vector dir = event.getPlayer().getTargetBlock((Set<Material>)null, 200).getLocation()
				.toVector().subtract(i.getLocation().toVector()).normalize();

		i.setVelocity(dir.multiply(1.5));

		plugin.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(
						plugin,
						new ItemRemoveThread(event.getPlayer(), new ItemStack(
								267, 1)), 1);

		this.shootedSwords.put(i.getEntityId(), event.getPlayer().getName());

		i.setPickupDelay(4);

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerReceiveSword(PlayerPickupItemEvent event) {
		if (!plugin.tm.isinGame(event.getPlayer()))
			return;

		if (this.shootedSwords.containsKey(event.getItem().getEntityId())) {
			Player pthrower = this.plugin.getServer().getPlayer(
					this.shootedSwords.get(event.getItem().getEntityId()));
			Player rec = event.getPlayer();
			if (pthrower.getName().equalsIgnoreCase(rec.getName())) {
				rec.getInventory().addItem(event.getItem().getItemStack());
				event.setCancelled(true);
				event.getItem().remove();
			} else {
				event.getPlayer().damage(10, pthrower);
				event.setCancelled(true);
				event.getItem().remove();
			}

			this.shootedSwords.remove(event.getItem().getEntityId());
		}

	}
}

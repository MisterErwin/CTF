import net.askarian.MisterErwin.CTF.API.CtFClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class test extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "Test-Class";
	private String description = "Use this to create your own class";
	private String command = "<command>";
	private ItemStack icon = new ItemStack(Material.AIR);

	private List<String> authors = new ArrayList<String>();

	private HashMap<String, String> custommessages = new HashMap<String, String>();

	@Override
	public void disable() {
		EntityDamageEvent.getHandlerList().unregister(this);

	}

	@Override
	public void enable() {

		authors.add("<Your_NAME>");
		super.plugin.trans.register("<Register>", "<String>");

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
		// ARMOR
		// HELMET
		item = new ItemStack(302, 1);
		inv.setHelmet(item);
		// CHESTPLATE
		item = new ItemStack(303, 1);
		inv.setChestplate(item);
		// LEGGINGS
		item = new ItemStack(304, 1);
		inv.setLeggings(item);
		// BOOTS
		item = new ItemStack(305, 1);
		inv.setBoots(item);
		// Items
		// SWORD
		item = new ItemStack(272, 1);
		inv.addItem(item);
		// Steak
		item = new ItemStack(364, 1);
		inv.addItem(item);

		return true;
	}

	// Listener
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (super.plugin.Game.Spawn == null
				|| event.getEntity().getWorld() != super.plugin.Game.Spawn
						.getWorld())
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		if (plugin.tm.getPClass((Player) event.getEntity()).equalsIgnoreCase(
				this.command)) {
			// Do something
		}
	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

}
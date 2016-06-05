import net.askarian.MisterErwin.CTF.API.CtFClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class heavy extends CtFClass implements Listener {

	private String version = "1.0";
	private String name = "heavy-Class";
	private String description = "";
	private String command = "heavy";
	private List<String> authors = new ArrayList<String>();
	private ItemStack icon = new ItemStack(Material.DIAMOND_SWORD);

	@Override
	public void disable() {

	}

	@Override
	public void enable() {
		authors.add("MisterErwin");
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
		item = new ItemStack(310, 1);
		inv.setHelmet(item);
		// Brust
		item = new ItemStack(311, 1);
		inv.setChestplate(item);
		// Hose
		item = new ItemStack(312, 1);
		inv.setLeggings(item);
		// Schuhe
		item = new ItemStack(313, 1);
		inv.setBoots(item);
		// Items
		// Schwert
		item = new ItemStack(276, 1);
		inv.addItem(item);
		// Essen
		item = new ItemStack(364, 3);
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
}

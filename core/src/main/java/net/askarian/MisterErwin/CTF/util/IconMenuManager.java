package net.askarian.MisterErwin.CTF.util;

import net.askarian.MisterErwin.CTF.util.IconMenu.OptionClickEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class IconMenuManager implements Listener {

	private HashMap<Player, IconMenu> menus = new HashMap<Player, IconMenu>();

	private Plugin plugin;

	public IconMenuManager(Plugin pl) {
		this.plugin = pl;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void closeall() {
		for (Player p : this.menus.keySet())
			this.destroy(p);
	}

	public void create(Player p, String name, int size,
			OptionClickEventHandler handler) {
		if (this.menus.containsKey(p))
			this.destroy(p);

		IconMenu im = new IconMenu(name, size, handler, this.plugin);
		this.menus.put(p, im);
	}

	public void show(Player p) {
		if (!this.menus.containsKey(p))
			return;

		this.menus.get(p).open(p);
	}

	public void reopen(Player p) {
		if (!this.menus.containsKey(p))
			return;

		this.menus.get(p).reopen(p);
	}

	public void setOption(Player p, int position, ItemStack icon, String reg) {
		if (!this.menus.containsKey(p))
			return;

		this.menus.get(p).setOption(position, icon, reg);
	}

	public void setOption(Player p, int position, ItemStack icon, String name,
			String reg, String... info) {
		if (!this.menus.containsKey(p))
			return;

		this.menus.get(p).setOption(position, icon, name, reg, info);
	}

	public void destroy(Player p) {
		if (!this.menus.containsKey(p))
			return;

		this.menus.get(p).destroy();
		this.menus.remove(p);

		p.getOpenInventory().close();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (this.menus.containsKey(event.getWhoClicked()))
			this.menus.get(event.getWhoClicked()).onInventoryClick(event);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (this.menus.containsKey(event.getPlayer()))
			this.menus.get(event.getPlayer()).onInventoryClose(event);
	}
}

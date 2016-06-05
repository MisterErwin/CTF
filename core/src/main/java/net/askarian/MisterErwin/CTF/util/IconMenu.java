package net.askarian.MisterErwin.CTF.util;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class IconMenu implements Listener {

	private String name = null;
	private int size;
	private OptionClickEventHandler handler;
	private Plugin plugin;

	private String[] optionNames;
	private String[] optionRegs;
	private ItemStack[] optionIcons;

	private boolean autodestroy = true;

	public IconMenu(String name, int size, OptionClickEventHandler handler,
			Plugin plugin) {
		this.name = name;
		this.size = size;
		this.handler = handler;
		this.plugin = plugin;
		this.optionNames = new String[size];
		this.optionRegs = new String[size];
		this.optionIcons = new ItemStack[size];
		this.autodestroy = true;
	}

	public IconMenu setOption(int position, ItemStack icon, String name,
			String reg, String... info) {
		optionRegs[position] = reg;
		optionNames[position] = name;
		optionIcons[position] = setItemNameAndLore(icon, name, info);
		return this;
	}

	public IconMenu setOption(int position, ItemStack icon, String reg) {
		optionRegs[position] = reg;
		optionNames[position] = name;
		optionIcons[position] = icon;
		return this;
	}

	public void open(Player player) {
		Inventory inventory = Bukkit.createInventory(player, size, name);
		for (int i = 0; i < optionIcons.length; i++) {
			if (optionIcons[i] != null) {
				inventory.setItem(i, optionIcons[i]);
			}
		}
		player.openInventory(inventory);
	}

	public void reopen(Player p) {
		for (int i = 0; i < optionIcons.length; i++) {
			if (optionIcons[i] != null) {
				p.getOpenInventory().setItem(i, optionIcons[i]);
			}
		}
	}

	public void setAutoDestroy(boolean autodestroy) {
		this.autodestroy = autodestroy;
	}

	public void destroy() {
		try {
			handler = null;
			plugin = null;
			optionNames = null;
			optionIcons = null;
			optionRegs = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onInventoryClose(InventoryCloseEvent event) {
		if (!event.getInventory().getName().equals(this.name))
			return;
		if (autodestroy) {
			this.destroy();
		}

	}

	public void onInventoryClick(InventoryClickEvent event) {

		if (!event.getInventory().getName().equals(this.name))
			return;

		event.setCancelled(true);
		int slot = event.getRawSlot();
		if (slot >= 0 && slot < size && optionNames[slot] != null) {
			if (optionRegs[slot].equals("")
					|| optionRegs[slot].trim().equals("-cancel-")) {
				event.setCancelled(true);
				return;
			}
			Plugin plugin = this.plugin;
			OptionClickEvent e = new OptionClickEvent(
					(Player) event.getWhoClicked(), slot, optionRegs[slot],
					this, event.isRightClick(), event.isLeftClick(),
					event.isShiftClick());
			handler.onOptionClick(e);
			if (e.willClose()) {
				final Player p = (Player) event.getWhoClicked();
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						new Runnable() {
							@Override
							public void run() {
								p.closeInventory();
							}
						}, 1);
			}
			if (e.willDestroy()) {
				destroy();
			}

			event.setCancelled(true);

		}

	}

	public interface OptionClickEventHandler {
		public void onOptionClick(OptionClickEvent event);
	}

	public class OptionClickEvent {
		private Player player;
		private int position;
		private String name;
		private boolean close;
		private boolean destroy;
		private IconMenu im;

		private boolean rightClick, leftClick, Shift;

		public OptionClickEvent(Player player, int position, String name,
				IconMenu im, boolean rightClick, boolean leftClick,
				boolean Shift) {
			this.player = player;
			this.position = position;
			this.name = name;
			this.close = true;
			this.destroy = false;
			this.im = im;
			this.rightClick = rightClick;
			this.leftClick = leftClick;
			this.Shift = Shift;

		}

		public Player getPlayer() {
			return player;
		}

		public int getPosition() {
			return position;
		}

		public String getName() {
			return name;
		}

		public IconMenu getIconMenu() {
			return this.im;
		}

		public boolean willClose() {
			return close;
		}

		public boolean willDestroy() {
			return destroy;
		}

		public void setWillClose(boolean close) {
			this.close = close;
		}

		public void setWillDestroy(boolean destroy) {
			this.destroy = destroy;
		}

		public boolean isRightClick() {
			return this.rightClick;
		}

		public boolean isLeftClick() {
			return this.leftClick;
		}

		public boolean isShiftClick() {
			return this.Shift;
		}

	}

	private ItemStack setItemNameAndLore(ItemStack item, String name,
			String[] lore) {
		ItemMeta im = item.getItemMeta();
		if (im == null)
			return item;
		if (name != "")
			im.setDisplayName(name);
		if (lore != null)
			im.setLore(Arrays.asList(lore));
		item.setItemMeta(im);
		return item;
	}

	public int size() {
		return this.size;
	}

}

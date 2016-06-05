package net.askarian.MisterErwin.CTF.API;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class YMLClass extends CtFClass {

	public YMLClass(String name, String cmd, String desc, String version,
			int ItemID, short ItemData) {
		this.name = name;
		this.cmd = cmd;
		this.desc = desc;
		this.version = version;
		this.ItemID = ItemID;
		this.ItemData = ItemData;

		this.authors.add("MisterErwin + YML-Parser");

	}

	private String name;
	private String cmd;
	private String desc;
	private String version;
	private int ItemID;
	private short ItemData;

	private HashMap<Integer, ItemStack> inv = new HashMap<Integer, ItemStack>();
	private ArrayList<String> authors = new ArrayList<String>();
	private ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();

	public void addItem(Integer slot, ItemStack is) {
		this.inv.put(slot, is);
	}

	public void addEffect(PotionEffect e) {
		this.effects.add(e);
	}

	@Override
	public void enable() {

	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getCommand() {
		return this.cmd;
	}

	@Override
	public String getDescription() {
		return this.desc;
	}

	@Override
	public List<String> getAuthors() {
		return this.authors;
	}

	@Override
	public boolean givekit(Player p) {
		p.getInventory().clear();
		for (Entry<Integer, ItemStack> e : this.inv.entrySet())
			try{
				p.getInventory().setItem(e.getKey(), e.getValue());
			}catch(Exception ignored){}

		for (PotionEffect pe : this.effects) {
			p.addPotionEffect(pe);
		}
		return true;
	}

	@Override
	public boolean saytime(Player p, int time) {
		p.setLevel(time);
		return true;
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(this.ItemID, 1, this.ItemData);
	}

}

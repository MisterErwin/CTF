package net.askarian.MisterErwin.CTF.util;

import org.bukkit.block.Block;

public class ResetBlock {

	private final Block b;
	private final int m;
	private final byte data;

	public ResetBlock(Block b, int m, byte data) {
		this.b = b;
		this.m = m;
		this.data = data;
	}

	public void reset() {
		if (data != 0x00)
			b.setTypeIdAndData(m, data, false);
		b.setTypeId(m, false);
	}

	public int getMaterial() {
		return this.m;
	}

	public byte getData() {
		return this.data;
	}

}

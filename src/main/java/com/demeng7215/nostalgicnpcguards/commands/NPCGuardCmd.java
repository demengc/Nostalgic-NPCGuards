package com.demeng7215.nostalgicnpcguards.commands;

import com.demeng7215.demlib.api.Common;
import com.demeng7215.demlib.api.CustomCommand;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.nostalgicnpcguards.NPCGuards;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.util.BlockIterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NPCGuardCmd extends CustomCommand {

	private NPCGuards i;

	public NPCGuardCmd(NPCGuards i) {
		super("npcguard");

		this.i = i;

		setDescription("Main command for Nostalgic-NPCGuards.");
		setAliases(Arrays.asList("npcguards", "nostalgic-npcguard", "nostalgic-npcguards"));
	}

	@Override
	protected void run(CommandSender sender, String[] args) {

		if (args.length < 1) {
			MessageUtils.tellWithoutPrefix(sender, "&6Running Nostalgic-NPCGuards v" +
					Common.getVersion() + ".");
			return;
		}

		switch (args[0]) {

			case "reload":
				if (!checkHasPerm("npcguards.reload", sender,
						i.getSettings().getString("no-permission"))) return;

				i.settingsFile.reloadConfig();

				MessageUtils.tell(sender, i.getSettings().getString("successfully-reloaded"));
				return;

			case "set":
				if (!checkIsPlayer(sender,
						i.getSettings().getString("console"))) return;

				if (!checkHasPerm("npcguards.set", sender,
						i.getSettings().getString("no-permission"))) return;

				final Player p = (Player) sender;

				NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,
						i.getSettings().getString("skin-name"));

				npc.setProtected(true);

				ItemStack crossbow = new ItemStack(Material.CROSSBOW);
				CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
				meta.setChargedProjectiles(Collections.singletonList(new ItemStack(Material.ARROW)));
				crossbow.setItemMeta(meta);

				npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, crossbow);
				npc.data().set(NPC.COLLIDABLE_METADATA, true);
				npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
				npc.data().set(NPC.DAMAGE_OTHERS_METADATA, false);
				npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, true);
				npc.spawn(p.getLocation());

				MessageUtils.tell(sender, i.getSettings().getString("successfully-spawned"));
				return;

			case "remove":

				if (!checkIsPlayer(sender,
						i.getSettings().getString("console"))) return;

				if (!checkHasPerm("npcguards.remove", sender,
						i.getSettings().getString("no-permission"))) return;

				final Player p1 = (Player) sender;

				final Entity e = getTargetEntity(p1);

				if (e == null || !e.hasMetadata("NPC")) {
					MessageUtils.tell(p1, i.getSettings().getString("not-npc"));
					return;
				}

				NPC targetNPC = CitizensAPI.getNPCRegistry().getNPC(e);

				if (targetNPC == null || !targetNPC.isSpawned()) {
					MessageUtils.tell(p1, i.getSettings().getString("not-npc"));
					return;
				}

				targetNPC.destroy();
				MessageUtils.tell(p1, i.getSettings().getString("successfully-removed"));
		}
	}

	private Entity getTargetEntity(Player p) {

		List<Entity> targetList = p.getNearbyEntities(10, 10, 10);
		BlockIterator bi = new BlockIterator(p, 10);
		Entity target = null;

		while (bi.hasNext()) {

			Block b = bi.next();
			int bx = b.getX();
			int by = b.getY();
			int bz = b.getZ();

			if (b.getType().isSolid()) {
				break;
			} else {

				for (Entity e : targetList) {

					Location l = e.getLocation();
					double ex = l.getX();
					double ey = l.getY();
					double ez = l.getZ();

					if ((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5))
						target = e;
				}
			}
		}
		return target;
	}
}
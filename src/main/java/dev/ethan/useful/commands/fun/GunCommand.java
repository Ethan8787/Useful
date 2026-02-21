package dev.ethan.useful.commands.fun;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "gun", permission = "guildwars.fun.gun", description = "Give custom gun item", override = true)
public class GunCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player p)) return;

        ItemStack gun = new ItemStack(Material.FEATHER);
        ItemMeta m = gun.getItemMeta();

        if (m != null) {
            m.setDisplayName(ChatColor.RESET + Messages.VANDAL_NAME);

            List<String> lore = new ArrayList<>();
            lore.add("§7Effective Range: §f80m");
            m.setLore(lore);

            m.addEnchant(Enchantment.UNBREAKING, -32769, true);
            m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

            NamespacedKey key = new NamespacedKey(Main.getInstance(), "range");
            AttributeModifier modifier = new AttributeModifier(key, -5, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.HAND);

            m.addAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE, modifier);
            m.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, modifier);

            gun.setItemMeta(m);
        }

        p.getInventory().addItem(gun);
    }
}

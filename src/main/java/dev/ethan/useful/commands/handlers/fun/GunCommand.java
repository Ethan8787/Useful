package dev.ethan.useful.commands.handlers.fun;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class GunCommand implements CommandHandler {
    @Override
    public boolean handle(Player p, String label, String[] args) {
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
        return true;
    }
}

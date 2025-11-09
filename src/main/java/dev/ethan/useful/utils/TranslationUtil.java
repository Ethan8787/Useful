package dev.ethan.useful.utils;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TranslationUtil {
    private static final Map<EntityType, String> ENTITY_NAME_MAP = new HashMap<>();
    private static final Map<Material, String> ITEM_NAME_MAP = new HashMap<>();

    static {
        ENTITY_NAME_MAP.put(EntityType.ALLAY, "悅靈");
        ENTITY_NAME_MAP.put(EntityType.AXOLOTL, "蠑螈");
        ENTITY_NAME_MAP.put(EntityType.BAT, "蝙蝠");
        ENTITY_NAME_MAP.put(EntityType.BEE, "蜜蜂");
        ENTITY_NAME_MAP.put(EntityType.BLAZE, "烈焰人");
        ENTITY_NAME_MAP.put(EntityType.CAMEL, "駱駝");
        ENTITY_NAME_MAP.put(EntityType.CAT, "貓");
        ENTITY_NAME_MAP.put(EntityType.CAVE_SPIDER, "洞穴蜘蛛");
        ENTITY_NAME_MAP.put(EntityType.CHICKEN, "雞");
        ENTITY_NAME_MAP.put(EntityType.COD, "鱈魚");
        ENTITY_NAME_MAP.put(EntityType.COW, "牛");
        ENTITY_NAME_MAP.put(EntityType.CREEPER, "苦力怕");
        ENTITY_NAME_MAP.put(EntityType.DOLPHIN, "海豚");
        ENTITY_NAME_MAP.put(EntityType.DONKEY, "驢");
        ENTITY_NAME_MAP.put(EntityType.DROWNED, "沉屍");
        ENTITY_NAME_MAP.put(EntityType.ELDER_GUARDIAN, "遠古深海守衛");
        ENTITY_NAME_MAP.put(EntityType.ENDER_DRAGON, "終界龍");
        ENTITY_NAME_MAP.put(EntityType.ENDERMAN, "終界使者");
        ENTITY_NAME_MAP.put(EntityType.ENDERMITE, "終界蟎");
        ENTITY_NAME_MAP.put(EntityType.EVOKER, "喚魔者");
        ENTITY_NAME_MAP.put(EntityType.FOX, "狐狸");
        ENTITY_NAME_MAP.put(EntityType.FROG, "青蛙");
        ENTITY_NAME_MAP.put(EntityType.GHAST, "地域幽靈");
        ENTITY_NAME_MAP.put(EntityType.GIANT, "巨人");
        ENTITY_NAME_MAP.put(EntityType.GLOW_SQUID, "發光魷魚");
        ENTITY_NAME_MAP.put(EntityType.GOAT, "山羊");
        ENTITY_NAME_MAP.put(EntityType.GUARDIAN, "深海守衛");
        ENTITY_NAME_MAP.put(EntityType.HOGLIN, "豬布獸");
        ENTITY_NAME_MAP.put(EntityType.HORSE, "馬");
        ENTITY_NAME_MAP.put(EntityType.HUSK, "屍殼");
        ENTITY_NAME_MAP.put(EntityType.ILLUSIONER, "幻術師");
        ENTITY_NAME_MAP.put(EntityType.IRON_GOLEM, "鐵巨人");
        ENTITY_NAME_MAP.put(EntityType.LLAMA, "駱駝");
        ENTITY_NAME_MAP.put(EntityType.MAGMA_CUBE, "岩漿立方怪");
        ENTITY_NAME_MAP.put(EntityType.MULE, "騾");
        ENTITY_NAME_MAP.put(EntityType.MOOSHROOM, "哞菇");
        ENTITY_NAME_MAP.put(EntityType.OCELOT, "山貓");
        ENTITY_NAME_MAP.put(EntityType.PANDA, "熊貓");
        ENTITY_NAME_MAP.put(EntityType.PARROT, "鸚鵡");
        ENTITY_NAME_MAP.put(EntityType.PHANTOM, "夜魅");
        ENTITY_NAME_MAP.put(EntityType.PIG, "豬");
        ENTITY_NAME_MAP.put(EntityType.PIGLIN, "豬布林");
        ENTITY_NAME_MAP.put(EntityType.PIGLIN_BRUTE, "豬布林蠻兵");
        ENTITY_NAME_MAP.put(EntityType.PILLAGER, "掠奪者");
        ENTITY_NAME_MAP.put(EntityType.POLAR_BEAR, "北極熊");
        ENTITY_NAME_MAP.put(EntityType.PUFFERFISH, "河豚");
        ENTITY_NAME_MAP.put(EntityType.RABBIT, "兔子");
        ENTITY_NAME_MAP.put(EntityType.RAVAGER, "劫毀獸");
        ENTITY_NAME_MAP.put(EntityType.SALMON, "鮭魚");
        ENTITY_NAME_MAP.put(EntityType.SHEEP, "羊");
        ENTITY_NAME_MAP.put(EntityType.SHULKER, "界伏蚌");
        ENTITY_NAME_MAP.put(EntityType.SILVERFISH, "蠹魚");
        ENTITY_NAME_MAP.put(EntityType.SKELETON, "骷髏");
        ENTITY_NAME_MAP.put(EntityType.SKELETON_HORSE, "骷髏馬");
        ENTITY_NAME_MAP.put(EntityType.SLIME, "史萊姆");
        ENTITY_NAME_MAP.put(EntityType.SNIFFER, "嗅探獸");
        ENTITY_NAME_MAP.put(EntityType.SNOW_GOLEM, "雪人");
        ENTITY_NAME_MAP.put(EntityType.SPIDER, "蜘蛛");
        ENTITY_NAME_MAP.put(EntityType.SQUID, "魷魚");
        ENTITY_NAME_MAP.put(EntityType.STRAY, "流髑");
        ENTITY_NAME_MAP.put(EntityType.STRIDER, "熾足獸");
        ENTITY_NAME_MAP.put(EntityType.TADPOLE, "蝌蚪");
        ENTITY_NAME_MAP.put(EntityType.TRADER_LLAMA, "商駝");
        ENTITY_NAME_MAP.put(EntityType.TROPICAL_FISH, "熱帶魚");
        ENTITY_NAME_MAP.put(EntityType.TURTLE, "海龜");
        ENTITY_NAME_MAP.put(EntityType.VEX, "惱鬼");
        ENTITY_NAME_MAP.put(EntityType.VILLAGER, "村民");
        ENTITY_NAME_MAP.put(EntityType.VINDICATOR, "衛道士");
        ENTITY_NAME_MAP.put(EntityType.WARDEN, "伏守者");
        ENTITY_NAME_MAP.put(EntityType.WANDERING_TRADER, "流浪商人");
        ENTITY_NAME_MAP.put(EntityType.WITCH, "女巫");
        ENTITY_NAME_MAP.put(EntityType.WITHER, "凋零怪");
        ENTITY_NAME_MAP.put(EntityType.WITHER_SKELETON, "凋零骷髏");
        ENTITY_NAME_MAP.put(EntityType.WOLF, "狼");
        ENTITY_NAME_MAP.put(EntityType.ZOGLIN, "豬屍獸");
        ENTITY_NAME_MAP.put(EntityType.ZOMBIE, "殭屍");
        ENTITY_NAME_MAP.put(EntityType.ZOMBIE_HORSE, "殭屍馬");
        ENTITY_NAME_MAP.put(EntityType.ZOMBIE_VILLAGER, "殭屍村民");
        ENTITY_NAME_MAP.put(EntityType.ZOMBIFIED_PIGLIN, "殭屍化豬布林");

    }

    public static String getDeathMessageByCause(String prefix, String name, EntityDamageEvent.DamageCause cause) {
        prefix = prefix == null ? "" : prefix;
        name = name == null ? "某人" : name;

        return "§4死亡" + " §7» " + prefix + name + "§f " + switch (cause) {
            case DROWNING -> "被淹死了";
            case FALL -> "摔死了";
            case FIRE, FIRE_TICK -> "被燒死了";
            case LAVA -> "以為他能泡溫泉";
            case VOID -> "以為虛空是天堂";
            case LIGHTNING -> "遭到天譴";
            case SUFFOCATION -> "缺乏生存基本要素 (氧氣)";
            case STARVATION -> "缺乏生存基本要素 (食物)";
            case POISON -> "吸毒吸死了";
            case MAGIC -> "被不知道三小魔術殺死了";
            case WITHER -> "屌零了";
            case FLY_INTO_WALL -> "知道了鞘翅的好玩之處";
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "被炸死了";
            case DRAGON_BREATH -> "被終界龍吐的紫色黏稠口水噁心死了";
            case FALLING_BLOCK -> "被方塊壓成紙片了";
            case PROJECTILE -> "被射死了";
            case FREEZE -> "被凍死了";
            case CRAMMING -> "被擠死了";
            case SONIC_BOOM -> "被伏守者操爛了";
            case SUICIDE -> "想不開";
            default -> "死亡";
        };
    }

    public static String getCustomTranslatedEntityName(Entity entity) {
        return ENTITY_NAME_MAP.getOrDefault(entity.getType(), entity.getName());
    }

    public static String getCustomTranslatedItemName(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            String name = ITEM_NAME_MAP.getOrDefault(item.getType(),
                    (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName())
                            ? item.getItemMeta().getDisplayName() : "");
            return name != null ? name : "";
        }
        return "";
    }

}

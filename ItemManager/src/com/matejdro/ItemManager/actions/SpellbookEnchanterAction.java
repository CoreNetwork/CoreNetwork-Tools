package com.matejdro.ItemManager.actions;

import com.matejdro.ItemManager.ItemProcessingResult;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SpellbookEnchanterAction extends ItemManagerAction
{
    private static final Set<String> bookNames = new HashSet<>();
    static
    {
        bookNames.add("Spell of Growth");
        bookNames.add("Spell of Time");
        bookNames.add("Spell of Deadweight");
        bookNames.add("Spell of Wind");
        bookNames.add("Spell of Decay");
        bookNames.add("Spell of Fusing");
        bookNames.add("Spell of Unsliming");
        bookNames.add("Spell of Peddling");
        bookNames.add("Spell of Forging");
    }

    private int enchantmentId;
    private int enchantmentLevel;


    @Override
    public boolean loadArguments(String[] arguments, int start)
    {
        enchantmentId = Integer.parseInt(arguments[start]);
        enchantmentLevel = Integer.parseInt(arguments[start + 1]);

        return true;
    }

    @Override
    public ItemProcessingResult processItem(CompoundTag item)
    {
        String materialName = item.getString("id");
        if (!materialName.equals("minecraft:book"))
            return ItemProcessingResult.NOTHING_HAPPENED;

        CompoundTag itemNbt = (CompoundTag) item.get("tag");
        if (itemNbt == null)
            return ItemProcessingResult.NOTHING_HAPPENED;

        CompoundTag displayTag = (CompoundTag) itemNbt.get("display");
        if (displayTag == null)
            return ItemProcessingResult.NOTHING_HAPPENED;

        if (!displayTag.contains("Name"))
            return ItemProcessingResult.NOTHING_HAPPENED;

        String name = displayTag.getString("Name");
        if (!bookNames.contains(stripColor(name)))
            return ItemProcessingResult.NOTHING_HAPPENED;

        ListTag enchTag = new ListTag<CompoundTag>("ench");

        CompoundTag singleEnchantment = new CompoundTag();
        singleEnchantment.putShort("id", (short) enchantmentId);
        singleEnchantment.putShort("lvl", (short) enchantmentLevel);

        enchTag.add(singleEnchantment);

        itemNbt.put("ench", enchTag);
        return ItemProcessingResult.ITEM_MODIFIED;
    }


    //Shamelessly stolen strip color method from Bukkit.

    public static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");
    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }


}


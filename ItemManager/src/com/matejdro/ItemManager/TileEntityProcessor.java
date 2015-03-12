package com.matejdro.ItemManager;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import java.util.Iterator;
import java.util.List;

public class TileEntityProcessor
{
    public static ItemProcessingResult processTileEntity(CompoundTag tag)
    {
        String id = tag.getString("id");

        switch (id)
        {
            case "Chest":
            case "Cauldron":
            case "Furnace":
            case "Hopper":
            case "Trap": //Trap = Dispenser
            case "Dropper":
                return processStandardContainer(tag);
        }

        return ItemProcessingResult.NOTHING_HAPPENED;
    }

    public static ItemProcessingResult processStandardContainer(CompoundTag tag)
    {
        List<CompoundTag> items = ((ListTag) tag.getList("Items")).list;
        Iterator<CompoundTag> iterator = items.iterator();

        boolean somethingChanged = false;

        while (iterator.hasNext())
        {
            CompoundTag item = iterator.next();
            ItemProcessingResult result = Main.pickedAction.processItem(item);

            if (result == ItemProcessingResult.ITEM_MODIFIED)
                somethingChanged = true;
            else if (result == ItemProcessingResult.DELETE_ITEM)
            {
                somethingChanged = true;
                iterator.remove();
            }
        }

        return somethingChanged ? ItemProcessingResult.ITEM_MODIFIED : ItemProcessingResult.NOTHING_HAPPENED;
    }
}

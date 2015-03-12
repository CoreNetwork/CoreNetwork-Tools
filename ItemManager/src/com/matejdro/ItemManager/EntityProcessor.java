package com.matejdro.ItemManager;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.Tag;
import java.util.Iterator;
import java.util.List;

public class EntityProcessor
{
    public static ItemProcessingResult processEntity(CompoundTag tag)
    {
        String id = tag.getString("id");

        switch (id)
        {
            case "Item":
                return processItemEntity(tag);
        }


        return processEquipment(tag);
    }

    public static ItemProcessingResult processPlayer(CompoundTag tag)
    {
        List<CompoundTag> items = ((ListTag) tag.getList("Inventory")).list;
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

    public static ItemProcessingResult processItemEntity(CompoundTag tag)
    {
        CompoundTag item = (CompoundTag) tag.get("Item");
        return Main.pickedAction.processItem(item);
    }

    public static ItemProcessingResult processEquipment(CompoundTag tag)
    {
        Tag equipmentTag = tag.get("Equipment");
        if (equipmentTag == null)
            return ItemProcessingResult.NOTHING_HAPPENED;

        boolean somethingChanged = false;

        List<CompoundTag> equipmentList = ((ListTag<CompoundTag>) equipmentTag).list;
        for (int i = 0; i < 5; i++)
        {
            CompoundTag item = equipmentList.get(i);

            boolean empty = !item.contains("id");
            if (empty)
                continue;

            ItemProcessingResult result = Main.pickedAction.processItem(item);
            if (result == ItemProcessingResult.ITEM_MODIFIED)
            {
                somethingChanged = true;
            }
            else if (result == ItemProcessingResult.DELETE_ITEM)
            {
                equipmentList.set(i, new CompoundTag()); //Null equipment items are represented as blank compound tags
                somethingChanged = true;
            }
        }

        return somethingChanged ? ItemProcessingResult.ITEM_MODIFIED : ItemProcessingResult.NOTHING_HAPPENED;
    }

}

package com.matejdro.ItemManager.actions;

import com.matejdro.ItemManager.ItemProcessingResult;
import com.mojang.nbt.CompoundTag;

public abstract class ItemManagerAction
{
    /**
     * @param arguments List of all arguments passed to ItemManager.
     * @param start Position of the first argument meant for this action.
     * @return <code>true</code> if arguments are valid, otherwise <code>false</code>. Application will stop if this returns <code>false</code>.
     */
    public abstract boolean loadArguments(String[] arguments, int start);

    /**
     * @param item Item in NBT format. See <a href="http://minecraft.gamepedia.com/Player.dat_Format#Item_structure">Minecraft Wiki</a>.
     * @return result of this processing.
     */
    public abstract ItemProcessingResult processItem(CompoundTag item);

}

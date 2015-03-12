package com.matejdro.ItemManager;

public enum ItemProcessingResult
{
    /**
     * Return when you did not modify item
     */
    NOTHING_HAPPENED,

    /**
     * Return when you modified item.
     */
    ITEM_MODIFIED,

    /**
     * Return when you want item deleted.
     */
    DELETE_ITEM;
}

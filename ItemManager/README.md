# ItemManager #

A command line tool working on region files (containing chunk data, for processing in-world items) and on player files (for processing player inventory). It can perform various actions on items saved in the world. Currently it effects:

* Items in player inventory.
* Items on the ground.
* Items picked by mobs (equipment).
* Items in chests, brewing stands, dispensers, droppers, furnaces, hoppers.

## Usage ##

```
java -jar ItemManager.jar
    <region folder or players folder> <Action ID> <Action parameters>
```

(To process both players inventory and in-world items, you must run the tool twice, once on region folder and once on players folder).

## Actions ##

1 - Set enchantment of all spellbooks to specific enchantment. Parameters: `<Enchantment ID> <Enchantment Level>`

## Example ##

```
java -jar ItemManager.jar region 1 48 1
```

Above example will work on the `region` folder – the `ItemManager.jar` file was placed in `world` folder (for overworld).

The example will set all enchantments on all spellbooks to Level `1` `Power` enchantment (ID = `48`).

It works on all `.mca` files in the `region` folder so make sure you pre-generate all chunks in the first place. Use [WorldBorder](http://dev.bukkit.org/bukkit-plugins/worldborder/) or [Minecraft Land Generator](https://sites.google.com/site/minecraftlandgenerator/).
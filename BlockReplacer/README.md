# BlockReplacer #

A command line tool working on region files (containing chunk data). It replaces all blocks of specificed types with another specified block type, in defined height range.

## Usage ##

```
java -jar BlockReplacer.jar
    <region folder> <minY> <maxY> <original blocks ID> <replacement block ID>
```

## Example ##

```
java -jar BlockReplacer.jar region 110 117 10,11 9
```

Above example will work on the `region` folder – the `BlockReplacer.jar` file was placed in `DIM-1` folder (Nether).

The example will replace all flowing lava blocks (ID = `10`) and stationary lava blocks (ID = `11`) between Y `110` and `117` (inclusive) with flowing water blocks (ID = `8`).

It works on all `.mca` files in the `region` folder so make sure you pre-generate all chunks in the first place. Use [WorldBorder](http://dev.bukkit.org/bukkit-plugins/worldborder/) or [Minecraft Land Generator](https://sites.google.com/site/minecraftlandgenerator/).
# FillBlocks #

A command line tool working on region files (containing chunk data). It fills empty space with a specified block type, in defined height range.

## Usage ##

```
java -jar FillBlocks.jar
    <region folder> <minY> <maxY> <blockID>
```

## Example ##

```
java -jar FillBlocks.jar region 110 117 11
```

Above example will work on the `region` folder – the `FillBlocks.jar` file was placed in `DIM-1` folder (Nether).

The example will replace all air blocks between `110` and `117` (inclusive) with stationary lava (`11`).

It works on all `.mca` files in the `region` folder so make sure you pre-generate all chunks in the first place. Use [WorldBorder](http://dev.bukkit.org/bukkit-plugins/worldborder/) or [Minecraft Land Generator](https://sites.google.com/site/minecraftlandgenerator/).
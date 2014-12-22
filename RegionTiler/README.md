# RegionTiler #

A command line tool working on region files (containing chunk data). It picks one chunk from a region file and clones it, replacing chunk data in all existing region files.

The fastest way to create and pre-generate a large superflat map or create a foundation for a maze, city or dungeon: just build whatever you want to tile and run the script.

## Usage ##

```
java -jar RegionTiler.jar
    <origin chunk x> <origin chunk z>
    <min region x> <max region x>
    <min region z> <max region z>
```

You have to put the `RegionTiler.jar` file in the respective `region` folder of the world you want to work with.

Use [Dinnerbone’s tools](https://dinnerbone.com/minecraft/tools/coordinates/) to get min/max region coordinates.

## Example ##

```
java -jar RegionTiler.jar 1 1 -5 4 -5 4
```

In above example we’re tiling chunk of coordinates `x=1`, `z=1` from the file `r.0.0.mca` (this chunk needs to be fully generated for script to work).

We’re also looking for the world to span from `x=-2500`, `z=-2500` to `x=2500`, `z=2500` which gives us region files `r.-5,-5.mca` to `r.4,4.mca`. We can use those to fill parameters like above (`-5 4 -5 4`).
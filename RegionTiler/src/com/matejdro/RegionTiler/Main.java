package com.matejdro.RegionTiler;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.world.level.chunk.storage.RegionFile;

/**
 * Created by Matej on 29.5.2014.
 */
public class Main
{
    private static HashSet<String> villagers = new HashSet<String>();

    public static void main(String[] args)
    {
        if (args.length < 5)
        {
            System.out.println("usage: java -jar RegionTiler.jar <origin chunk x> <origin chunk z> <min region x> <max region x> <min region z> <max region z>");
            return;
        }

        int originX = Integer.parseInt(args[0]);
        int originZ = Integer.parseInt(args[1]);
        int minX = Integer.parseInt(args[2]);
        int maxX = Integer.parseInt(args[3]);
        int minZ = Integer.parseInt(args[4]);
        int maxZ = Integer.parseInt(args[5]);

        try
        {
            System.out.println("loading origin chunk...");
            int originRegionX = originX / 32;
            int originRegionZ = originZ / 32;
            int insideOriginChunkX = originX % 32;
            int insideOriginChunkZ = originZ % 32;

            File originRegionFile = new File("r." + originRegionX + "." + originRegionZ + ".mca");
            RegionFile originRegion = new RegionFile(originRegionFile);
            DataInputStream originChunkStream = originRegion.getChunkDataInputStream(insideOriginChunkX, insideOriginChunkZ);
            CompoundTag originChunk = NbtIo.read(originChunkStream);
            originChunkStream.close();
            originRegion.close();

            for (int x = minX; x <= maxX; x++)
            {
                for (int z = minZ; z <= maxZ; z++)
                {
                    System.out.println("writing region " + x + " " + z + "...");
                    File regionFile = new File("r." + x + "." + z + ".mca");
                    RegionFile region = new RegionFile(regionFile);

                    for (int cX = 0; cX < 32; cX++)
                    {
                        for (int cZ = 0; cZ < 32; cZ++)
                        {
                            int actualChunkX = x * 32 + cX;
                            int actualChunkZ = z * 32 + cZ;

                            modifyChunkCoordinates(originChunk, actualChunkX, actualChunkZ);

                            DataOutputStream stream = region.getChunkDataOutputStream(cX, cZ);
                            NbtIo.write(originChunk, stream);
                            stream.close();
                        }
                    }

                    region.close();
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

//    public static void fillChunks(File file, int originChunkX, int originChunkZ)
//    {
//        System.out.println("Tiling Chunk (0,0) over whole origin region ...");
//        try
//        {
//            RegionFile region = new RegionFile(file);
//            DataInputStream middleChunkSteam = region.getChunkDataInputStream(16, 16);
//            CompoundTag middleChunk = NbtIo.read(middleChunkSteam);
//            CompoundTag levelTag = middleChunk.getCompound("Level");
//
//            int baseX = levelTag.getInt("xPos") - 16;
//            int baseZ = levelTag.getInt("zPos") - 16;
//
//

//            for (int x = 0; x < 32; x++)
//            {
//                for (int z = 0; z < 32; z++)
//                {
//                    modifyChunkCoordinates();
//                    DataOutputStream chunkStream = region.getChunkDataOutputStream(x, z);
//                    CompoundTag tag = NbtIo.read(chunkStream);
//                    CompoundTag levelTag = tag.getCompound("Level");
//                    ListTag entitiesTag = (ListTag) levelTag.get("Entities");
//
//                    for (int i = 0; i < entitiesTag.size(); i++)
//                    {
//                        CompoundTag entity = (CompoundTag) entitiesTag.get(i);
//                        String id = entity.getString("id");
//
//                        if (!"Villager".equals(id))
//                            continue;
//
//                        long uuidMost = entity.getLong("UUIDMost");
//                        long uuidLeast = entity.getLong("UUIDLeast");
//
//                        UUID uuid = new UUID(uuidMost, uuidLeast);
//                        villagers.add(uuid.toString());
//                    }
//
//
//                    chunkStream.close();
//                }
//            }
//
//        } catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }



    public static void processRegion(File file)
    {
        System.out.println("    Processing region " + file.getName() + " ...");
        try
        {
            RegionFile region = new RegionFile(file);
            for (int x = 0; x < 32; x++)
            {
                for (int z = 0; z < 32; z++)
                {
                    if (!region.hasChunk(x, z))
                        continue;

                    DataInputStream chunkStream = region.getChunkDataInputStream(x, z);
                    CompoundTag tag = NbtIo.read(chunkStream);
                    CompoundTag levelTag = tag.getCompound("Level");
                    ListTag entitiesTag = (ListTag) levelTag.get("Entities");

                    for (int i = 0; i < entitiesTag.size(); i++)
                    {
                        CompoundTag entity = (CompoundTag) entitiesTag.get(i);
                        String id = entity.getString("id");

                        if (!"Villager".equals(id))
                            continue;

                        long uuidMost = entity.getLong("UUIDMost");
                        long uuidLeast = entity.getLong("UUIDLeast");

                        UUID uuid = new UUID(uuidMost, uuidLeast);
                        villagers.add(uuid.toString());
                    }


                    chunkStream.close();
                 }
            }

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void modifyChunkCoordinates(CompoundTag chunk, int x, int z)
    {
        CompoundTag levelTag = chunk.getCompound("Level");
        levelTag.putInt("xPos", x);
        levelTag.putInt("zPos", z);
    }

    private static void copyFile(File origin, File destination)
    {
        try
        {
            FileInputStream in = new FileInputStream(origin);
            FileOutputStream out = new FileOutputStream(destination);

            byte[] buffer = new byte[100000];

            while (true)
            {
                int read = in.read(buffer);
                if (read == -1)
                    break;

                out.write(buffer, 0, read);
            }

            in.close();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

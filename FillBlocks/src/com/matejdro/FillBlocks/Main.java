package com.matejdro.FillBlocks;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import net.minecraft.world.level.chunk.storage.RegionFile;

import java.io.*;
import java.sql.*;

/**
 * Created by Matej on 29.5.2014.
 */
public class Main
{
    private static int minY;
    private static int maxY;
    private static int blockID;

    public static void main(String[] args)
    {
        if (args.length < 4)
        {
            System.out.println("Usage: java -jar FillBlocks.jar <region folder> <minY> <maxY> <blockID>");
            return;
        }

        File regionFolder = new File(args[0]);
        minY = Integer.parseInt(args[1]);
        maxY = Integer.parseInt(args[2]);
        blockID = Integer.parseInt(args[3]);

        processWorldFolder(regionFolder);
    }
    public static void processWorldFolder(File folder)
    {
        System.out.println("Loading chunks from " + folder.getAbsolutePath() + " ...");

        FileFilter filter = new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                return file.getName().endsWith(".mca");
            }
        };

        for (File file : folder.listFiles(filter))
        {
            processRegion(file);
        }
    }

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
                    CompoundTag chunk = NbtIo.read(chunkStream);
                    chunkStream.close();

                    CompoundTag levelTag = chunk.getCompound("Level");
                    ListTag sectionsTag = levelTag.getList("Sections");

                    boolean chunkChanged = false;

                    for (int i = 0; i < sectionsTag.size(); i++)
                    {
                        CompoundTag section = (CompoundTag) sectionsTag.get(i);
                        int sectionY = section.getByte("Y");
                        byte[] blocksArray = section.getByteArray("Blocks");

                        boolean sectionChanged = false;

                        for (int j = 0; j < blocksArray.length; j++)
                        {
                            //int blockX = j % 16;
                            //int blockZ = (j / 16) % 16;
                            int blockY = (j / 256) % 16;

                           // int test = blockY * 16 * 16 + blockZ * 16 + blockX;

                            blockY += sectionY * 16;

                            if (blockY >= minY && blockY <= maxY && blocksArray[j] == 0)
                            {
                                blocksArray[j] = (byte) blockID;
                                sectionChanged = true;
                            }
                        }

                        if (sectionChanged)
                        {
                            chunkChanged = true;
                            section.putByteArray("Blocks", blocksArray);
                        }
                    }

                    if (chunkChanged)
                    {
                        chunk.put("Sections", sectionsTag);

                        DataOutputStream chunkOutputStream = region.getChunkDataOutputStream(x, z);
                        NbtIo.write(chunk, chunkOutputStream);
                        chunkOutputStream.close();
                    }
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

    private static Connection openConnection(String path)
    {

        try
        {
            Class.forName("org.sqlite.JDBC");
            Connection ret = DriverManager.getConnection("jdbc:sqlite:" + path);
            ret.setAutoCommit(false);
            return ret;

        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}

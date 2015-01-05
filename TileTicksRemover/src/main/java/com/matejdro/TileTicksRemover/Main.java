package com.matejdro.TileTicksRemover;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import java.lang.Runtime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.level.chunk.storage.RegionFile;

import java.io.*;
import java.sql.*;
import java.util.HashSet;

/**
 * Created by Matej on 29.5.2014.
 */
public class Main
{
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("Usage: java -jar TileTicksRemover.jar <region folder>");
            return;
        }

        File regionFolder = new File(args[0]);
        processRegionFolder(regionFolder);
    }

    public static void processRegionFolder(File folder)
    {
        FileFilter filter = new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                return file.getName().endsWith(".mca");
            }
        };

        ExecutorService threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        File[] files = folder.listFiles();
        final int all = files.length;


        for (int i = 0; i < all; i++)
        {
            final File finalFile = files[i];
            final int finalI = i;

            threads.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    int percent = finalI * 100 / all;
                    processRegion(finalFile);
                    System.out.println("    Progress: " + finalI + " / " + all + " (" + percent + "%)");

                }
            });
        }

        try
        {
            threads.awaitTermination(10, TimeUnit.DAYS);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
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
                    CompoundTag tag = NbtIo.read(chunkStream);
                    chunkStream.close();

                    CompoundTag levelTag = tag.getCompound("Level");
                    if (levelTag.contains("TileTicks"))
                    {
                        levelTag.tags.remove("TileTicks");
                        tag.put("Level", levelTag);

                        DataOutputStream chunkOutputStream = region.getChunkDataOutputStream(x, z);
                        NbtIo.write(tag, chunkOutputStream);
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

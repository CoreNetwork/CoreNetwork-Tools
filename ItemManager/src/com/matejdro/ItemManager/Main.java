package com.matejdro.ItemManager;

import com.matejdro.ItemManager.actions.ItemManagerAction;
import com.matejdro.ItemManager.actions.SpellbookEnchanterAction;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.Tag;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.level.chunk.storage.RegionFile;

import java.io.*;
import java.sql.*;

public class Main
{
    public static ItemManagerAction pickedAction;

    private static HashMap<Integer, ItemManagerAction> actions = new HashMap<>();

    private static ExecutorService threads;


    static
    {
        actions.put(1, new SpellbookEnchanterAction());
    }

    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("java -jar java -jar ItemManager.jar <region folder or playerdata folder> <Action ID> <Action parameters>");
            return;
        }

        int actionId = Integer.parseInt(args[1]);
        pickedAction = actions.get(actionId);
        if (pickedAction == null)
        {
            System.out.println("Invalid action ID " + args[1]);
            return;
        }

        if (!pickedAction.loadArguments(args, 2))
            return;


        threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        File regionFolder = new File(args[0]);
        processWorldFolder(regionFolder);
        processPlayersFolder(regionFolder);

        try
        {
            threads.shutdown();
            threads.awaitTermination(10, TimeUnit.DAYS);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        System.out.println("Completed!");
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

        for (final File file : folder.listFiles(filter))
        {
            threads.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    processRegion(file);
                }
            });
        }
    }

    public static void processPlayersFolder(File folder)
    {
        System.out.println("Loading players from " + folder.getAbsolutePath() + " ...");

        FileFilter filter = new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                return file.getName().endsWith(".dat");
            }
        };

        for (final File file : folder.listFiles(filter))
        {
            threads.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    processPlayerFile(file);
                }
            });
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

                    boolean chunkChanged = false;


                    List<CompoundTag> entityList = (List<CompoundTag>) levelTag.getList("Entities").list;
                    Iterator<CompoundTag> iterator = entityList.iterator();
                    while (iterator.hasNext())
                    {
                        CompoundTag entity = iterator.next();
                        ItemProcessingResult result = EntityProcessor.processEntity(entity);

                        if (result != ItemProcessingResult.NOTHING_HAPPENED)
                            System.out.println("result " + result);

                        if (result == ItemProcessingResult.ITEM_MODIFIED)
                            chunkChanged = true;
                        else if (result == ItemProcessingResult.DELETE_ITEM)
                        {
                            chunkChanged = true;
                            iterator.remove();
                        }
                    }

                    List<CompoundTag> tileEntityList = (List<CompoundTag>) levelTag.getList("TileEntities").list;
                    for (CompoundTag tileEntity : tileEntityList)
                    {
                        ItemProcessingResult processingResult = TileEntityProcessor.processTileEntity(tileEntity);
                        if (processingResult == ItemProcessingResult.ITEM_MODIFIED)
                            chunkChanged = true;
                        else if (processingResult == ItemProcessingResult.DELETE_ITEM)
                            throw new IllegalStateException("Deleting tile entities is not supported!");
                    }

                    if (chunkChanged)
                    {
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

    public static void processPlayerFile(File file)
    {
        System.out.println("    Processing player " + file.getName() + " ...");
        try
        {
            FileInputStream playerFileInputStream = new FileInputStream(file);
            CompoundTag playerTag = NbtIo.readCompressed(playerFileInputStream);
            playerFileInputStream.close();

            boolean saveBack = false;
            ItemProcessingResult processingResult = EntityProcessor.processPlayer(playerTag);
            if (processingResult == ItemProcessingResult.ITEM_MODIFIED)
                saveBack = true;
            else if (processingResult == ItemProcessingResult.DELETE_ITEM)
                throw new IllegalStateException("Deleting tile entities is not supported!");

            if (saveBack)
            {
                FileOutputStream playerFileOutputStream = new FileOutputStream(file);
                NbtIo.writeCompressed(playerTag, playerFileOutputStream);
                playerFileOutputStream.close();
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
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

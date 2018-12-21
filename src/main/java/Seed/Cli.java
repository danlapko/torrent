package Seed;


import Seed.Commands.*;
import Seed.Exceptions.ConnectionBrokenException;
import io.airlift.airline.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// list files from tracker `list_tracker`
// list files seeding by me `list_seeding`
// download file from torrent `download fileId`
// upload file to torrent `upload filePath`
// remove file file from seeding by me `remove fileId`
// `store`
// exit session `exit`

public class Cli {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        String trackerHost = "localhost";
        short trackerPort = 8081;
        short myServerPort = 9999;
//        int sentUpdateEvery = 4 * 60 * 1000; // ms
        int sentUpdateEvery = 9 * 1000; // ms
        int blockSize = 10 * 1024 * 1024; // 10M
        String catalogURI = "./catalogSeed.txt";


        @SuppressWarnings("unchecked")
        io.airlift.airline.Cli.CliBuilder<Command> builder = io.airlift.airline.Cli.<Command>builder("MyTorrent")
                .withDescription("My simple torrent")
                .withCommands(
                        CmdListTracker.class,
                        CmdListSeeding.class,
                        CmdDownload.class,
                        CmdStore.class,
                        CmdUpload.class,
                        CmdRemove.class,
                        CmdExit.class
                );
        io.airlift.airline.Cli<Command> parser = builder.build();

        // initialize global context

        GlobalContext globalContext = new GlobalContext(trackerHost, trackerPort, catalogURI, myServerPort, blockSize);

        // start updater
        Thread updaterThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(sentUpdateEvery);
                    globalContext.updateMyFilesAtTracker();
                } catch (ConnectionBrokenException e) {
                    try {
                        globalContext.tryReconnect();
                    } catch (IOException e1) {
                        return;
                    }
                } catch (InterruptedException | IOException e) {
                    return;
                }
            }
        });

        updaterThread.setDaemon(true);
        updaterThread.start();

        // start my server (SeedServer)
        Thread seedServerThread = new Thread(new SeedServer(globalContext));
        seedServerThread.setDaemon(true);
        seedServerThread.start();

        Command cmd;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // processing commands
        while (true) {
            System.out.print("> ");
            args = reader.readLine().split("\\s+");

            try {
                cmd = parser.parse(args);
            } catch (ParseException e) {
                System.err.println("WARNING: parse error! " + e.getMessage());
                continue;
            }

            if (cmd instanceof CmdExit) {
                globalContext.finish();
                return;
            }

            // executing command
            try {
                cmd.execute(globalContext);
            } catch (ConnectionBrokenException e) {
                System.err.println("FATAL: connection broken or expired! " + e.getMessage());
//                updaterThread.interrupt();
//                seedServerThread.interrupt();
                globalContext.finish();
                e.printStackTrace();
                return;
            } catch (IOException e) {
                System.err.println("FATAL: Invalid path! " + e.getMessage());
//                updaterThread.interrupt();
//                seedServerThread.interrupt();
                globalContext.finish();
                e.printStackTrace();
                return;
            } catch (Exception e) {
                System.err.println("FATAL: I don't know what have happened");
//                updaterThread.interrupt();
//                seedServerThread.interrupt();
                globalContext.finish();
                e.printStackTrace();
                return;
            }
        }
    }
}

// // =========== possible command content ============
//        byte message = 1;
//        dataOutputStream.writeByte(message);
//
//        System.err.println("Wrote: " + message);
//
//        int count = dataInputStream.readInt();
//        System.err.print("Read: count=" + count);
//        for (int i = 0; i < count; i++) {
//            int id = dataInputStream.readInt();
//            String name = dataInputStream.readUTF();
//            long size = dataInputStream.readLong();
//            System.err.print(" (" + id + ", " + name + " ," + size + ")");
//        }
//        System.err.println();
//        Thread.sleep(2000);
//        // =========== /possible command content ============





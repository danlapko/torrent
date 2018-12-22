package Seed;


import Seed.Commands.*;
import Seed.Exceptions.ConnectionBrokenException;
import Seed.Exceptions.SeedException;
import io.airlift.airline.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;

// list files from tracker `list_tracker`
// list files seeding by me `list_seeding`
// download file from torrent `download fileId`
// upload file to torrent `upload filePath`
// remove file file from seeding by me `remove fileId`
// `store`
// exit session `exit`

public class Seed {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
//        String trackerHost = Short.valueOf(args[1]);
        String trackerHost = "localhost";
        short trackerPort = 8081;
        short myServerPort = Short.valueOf(args[0]);
        int sentUpdateEvery = 4 * 60 * 1000; // ms
//        int sentUpdateEvery = 9 * 1000; // ms
        int blockSize = 10 * 1024 * 1024; // 10M

        String catalogURI = "./seedCatalog_" + myServerPort + ".bin";


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

            } catch (SeedException e) {
                System.err.println("WARNING:" + e.getMessage());
            } catch (NoSuchFileException | FileNotFoundException e) {
                System.err.println("WARNING: Invalid path! " + e.getMessage());
            } catch (ConnectionBrokenException e) {
                System.err.println("FATAL: connection broken or expired! " + e.getMessage());
                globalContext.finish();
                e.printStackTrace();
                return;
            } catch (IOException e) {
                System.err.println("FATAL: Unknown IO error! " + e.getMessage());
                globalContext.finish();
                e.printStackTrace();
                return;

            } catch (Exception e) {
                System.err.println("FATAL: I don't know what have happened");
                globalContext.finish();
                e.printStackTrace();
                return;
            }
        }
    }
}





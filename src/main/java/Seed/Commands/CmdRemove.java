package Seed.Commands;

import Seed.GlobalContext;
import io.airlift.airline.Arguments;

import java.io.IOException;

@io.airlift.airline.Command(name = "remove", description = "remove file from seeding by me by its id")
public class CmdRemove implements Command {

    @Arguments(description = "file id obtained from tracker", required = true)
    private int fileId;

    @Override
    public int execute(GlobalContext context) throws IOException {
        context.catalog.removeFile(fileId);
        context.updateMyFilesAtTracker();

        System.out.println("File with id=" + fileId + " have been removed!");

        return 0;
    }
}


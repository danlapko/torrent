package Seed.Commands;

import Seed.GlobalContext;
import io.airlift.airline.Arguments;

@io.airlift.airline.Command(name = "remove", description = "remove file from seeding by me by its id")
public class CmdRemove implements Command {

    @Arguments(description = "file id obtained from tracker", required = true)
    private int fileId;

    @Override
    public int execute(GlobalContext context) {
        context.catalog.removeFile(fileId);
        System.err.println("File with id=" + fileId + " have been removed!");

        return 0;
    }
}


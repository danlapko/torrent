package Seed.Commands;

import Seed.GlobalContext;
import Seed.Model.FileMeta;

import java.util.List;

@io.airlift.airline.Command(name = "list_tracker", description = "list files available to download")
public class CmdListTracker implements Command {

    @Override
    public int execute(GlobalContext context) throws Exception {
        List<FileMeta> list = context.getTrackerList();
        System.out.println("Name        Size        Id");
        for (FileMeta file : list) {
            System.out.println(file.name + "\t" + file.size + "\t" + file.id);
        }
        return 0;
    }
}

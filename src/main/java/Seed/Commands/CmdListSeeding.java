package Seed.Commands;

import Seed.GlobalContext;
import Seed.Model.FileMeta;

import java.util.List;

@io.airlift.airline.Command(name = "list_seeding", description = "list files seeding by me")
public class CmdListSeeding implements Command {

    @Override
    public int execute(GlobalContext context) throws Exception {
        List<FileMeta> list = context.getSeedingFiles();
        System.out.println("Name        Size        Id");
        for (FileMeta file : list) {
            System.out.println(file.name + "\t" + file.size + "\t" + file.id);
        }
        return 0;
    }
}

package Seed.Commands;

import Seed.GlobalContext;
import Seed.Model.BlockMeta;
import Seed.Model.FileMeta;
import io.airlift.airline.Arguments;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@io.airlift.airline.Command(name = "store", description = "store downloaded file to filesystem")
public class CmdStore implements Command {

    @Arguments(description = "file id from seeding", required = true)
    private int id;

    @Arguments(description = "file absolute path", required = true)
    private String dirtyFileName;

    @Override
    public int execute(GlobalContext context) throws Exception {

        FileMeta fileMeta = context.catalog.getFile(id);

        byte[] content = new byte[(int) fileMeta.size];
        long numBlocks = (fileMeta.size + context.blockSize - 1) % context.blockSize;
        for (int i = 0; i < numBlocks; i++) {
            if (fileMeta.containsBlock(i)) {
                BlockMeta blockMeta = fileMeta.getBlock(i);
                for (int j = 0; j < context.blockSize; j++) {
                    content[(int) (i * context.blockSize + j)] = blockMeta.data[j];
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(dirtyFileName);
        fos.write(content);
        fos.close();

        System.err.println("File " + fileMeta.name + " whith id=" + fileMeta.id + " have been stored to " + dirtyFileName + "!");

        return 0;
    }
}


package Seed.Commands;

import Seed.Exceptions.NotAvaliableFile;
import Seed.GlobalContext;
import Seed.Model.BlockMeta;
import Seed.Model.FileMeta;
import io.airlift.airline.Arguments;
import io.airlift.airline.Option;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@io.airlift.airline.Command(name = "store", description = "store downloaded file to filesystem")
public class CmdStore implements Command {

    @Arguments(description = "file id obtained from tracker", required = true)
    private int fileId;
    @Option(name = {"-f", "--file"}, description = "file name to store at", required = true)
    private String dirtyFileName;

    @Override
    public int execute(GlobalContext context) throws Exception {

        FileMeta fileMeta = context.catalog.getFile(fileId);
        if (fileMeta == null) {
            throw new NotAvaliableFile(" You have not file with such id: " + fileId + "! To store it first download it.");
        }

        byte[] content = new byte[(int) fileMeta.size];
        long numBlocks = (fileMeta.size + context.blockSize - 1) / context.blockSize;
        for (int i = 0; i < numBlocks; i++) {
            if (fileMeta.containsBlock(i)) {
                BlockMeta block = fileMeta.getBlock(i);
//                System.out.println(i + "/" + numBlocks);
                for (int j = 0; j < block.size; j++) {
                    content[(int) (i * context.blockSize + j)] = block.data[j];
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(dirtyFileName);
        fos.write(content);
        fos.close();

        System.out.println("File " + fileMeta.name + " whith id=" + fileMeta.id + " have been stored into " + dirtyFileName + "!");

        return 0;
    }
}


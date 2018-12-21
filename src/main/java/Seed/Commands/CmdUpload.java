package Seed.Commands;

import Seed.GlobalContext;
import Seed.Messages.Client.ClientRequestUpload;
import Seed.Model.BlockMeta;
import Seed.Model.FileMeta;
import io.airlift.airline.Arguments;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@io.airlift.airline.Command(name = "upload", description = "point tracker that you have new file for seedding")
public class CmdUpload implements Command {

    @Arguments(description = "file absolute path", required = true)
    private String dirtyFileName;

    @Override
    public int execute(GlobalContext context) throws Exception {
        Path path = Paths.get(dirtyFileName);
        byte[] content = Files.readAllBytes(path);

        FileMeta fileMeta = context.uploadTracker(path.getFileName().toString(), content.length);
        for (long pos = 0, id = 0; pos < content.length; pos += context.blockSize, id++) {
            fileMeta.addBlock(new BlockMeta((int) id, content.length, content));
        }

        context.catalog.addFile(fileMeta);
        System.out.println("File " + fileMeta.name + " whith id=" + fileMeta.id + " have been uploaded!");

        return 0;
    }
}


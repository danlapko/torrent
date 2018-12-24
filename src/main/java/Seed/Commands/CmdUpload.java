package Seed.Commands;

import Seed.GlobalContext;
import Seed.Messages.Client.ClientRequestUpload;
import Seed.Model.BlockMeta;
import Seed.Model.FileMeta;
import io.airlift.airline.Arguments;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@io.airlift.airline.Command(name = "upload", description = "point tracker that you have new file for seedding")
public class CmdUpload implements Command {

    @Arguments(description = "file absolute path", required = true)
    private String dirtyFileName;

    @Override
    public int execute(GlobalContext context) throws Exception {
        Path path = Paths.get(dirtyFileName);
        byte[] content = Files.readAllBytes(path);

        FileMeta fileMeta = context.uploadTracker(path.getFileName().toString(), content.length);
        for (int pos = 0, id = 0; pos < content.length; pos += context.blockSize, id++) {
            int rightBound = content.length < pos + context.blockSize ? content.length : pos + context.blockSize;
            byte[] blockContent = Arrays.copyOfRange(content, pos, rightBound);
            fileMeta.addBlock(new BlockMeta((int) id, blockContent.length, blockContent));
        }

        context.catalog.addFile(fileMeta);

        context.updateMyFilesAtTracker();
        System.out.println("File " + fileMeta.name + " whith id=" + fileMeta.id + " have been uploaded!");

        return 0;
    }
}


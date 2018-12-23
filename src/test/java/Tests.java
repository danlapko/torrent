import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class Tests {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private Proc server;

    class Proc {
        final private Process proc;
        final private InputStream in;
        final private InputStream err;
        final private OutputStream out;
        final private String dir;

        Proc(Process proc, String dir) {
            this.proc = proc;

            this.in = proc.getInputStream();
            this.err = proc.getErrorStream();
            this.out = proc.getOutputStream();
            this.dir = dir;
        }
    }

    @Before
    public void createAndStartTracker() throws Exception {

        String dir = "tests/server";
        new ProcessBuilder("bash", "-c",
                "mkdir -p " + dir + "; " +
                        "cd " + dir + "; ").start().waitFor();

        Process process = Runtime.getRuntime().exec("java -jar /home/danila/se_6f/java/torrent/build/libs/tracker-1.0-SNAPSHOT.jar",
                null, new File(dir));
        server = new Proc(process, dir);
        Thread.sleep(1000);
    }

    @After
    public void cleanOut() throws IOException, InterruptedException {
        server.proc.destroyForcibly();
        new ProcessBuilder("bash", "-c", "rm -rf tests").start().waitFor();
    }

    private void execCmd(Proc client, String command) throws Exception {
        command = command + "\n";
        client.out.write(command.getBytes());
        client.out.flush();

        Thread.sleep(500);

        // Client stdout
        System.out.println("> " + command);
        byte bin[] = new byte[client.in.available()];
        client.in.read(bin, 0, bin.length);
        System.out.println(" " + new String(bin));

        // Client stderr
        byte berr[] = new byte[client.err.available()];
        client.err.read(berr, 0, berr.length);
        System.err.println(" " + new String(berr));

//        // Server output
//        byte bserv[] = new byte[server.err.available()];
//        server.err.read(bserv, 0, bserv.length);
//        System.err.println(" " + new String(bserv));
    }


    private Proc createAndStartSeed(int port) throws Exception {

        String dir = "tests/client" + port;
        ProcessBuilder pb_dir = new ProcessBuilder("bash", "-c",
                "mkdir -p " + dir + "; " +
                        "cd " + dir + "; " +
                        "touch f1.txt f2.txt; echo 'f1 content " + dir + "' > f1.txt; " +
                        "echo 'f2 content " + dir + "' > f2.txt;");
        pb_dir.start().waitFor();
        pb_dir = new ProcessBuilder("bash", "-c",
                "mkdir -p " + dir + "; " +
                        "cd " + dir + "; " +
                        "touch f1.txt f2.txt; echo 'f1 content " + dir + "' > f1.txt; " +
                        "echo 'f2 content " + dir + "' > f2.txt;");
        pb_dir.start().waitFor();

        Process proc = Runtime.getRuntime().exec(
                "java -Xmx4096M -jar /home/danila/se_6f/java/torrent/build/libs/seed-1.0-SNAPSHOT.jar " + port,
                null, new File(dir));
        Thread.sleep(1000);
        return new Proc(proc, dir);
    }

    @Test
    public void uploadStore() throws Exception {
        System.out.println("========= uploadStore =========");
        Proc client = createAndStartSeed(10101);

        execCmd(client, "upload f1.txt");
        execCmd(client, "store 0 -f foo.txt");
        execCmd(client, "exit");
        client.proc.waitFor();

        Assert.assertTrue(FileUtils.contentEquals(
                new File(client.dir + "/f1.txt"),
                new File(client.dir + "/foo.txt")));

        System.out.println("========= /uploadStore =========");
    }

    @Test
    public void uploadDownloadStore() throws Exception {
        System.out.println("========= uploadDownloadStore =========");
        Proc client1 = createAndStartSeed(10101);
        Proc client2 = createAndStartSeed(10102);

        execCmd(client1, "upload f1.txt");
        execCmd(client2, "download 0");
        execCmd(client2, "store 0 -f foo.txt");

        execCmd(client1, "exit");
        execCmd(client2, "exit");

        client1.proc.waitFor();
        client2.proc.waitFor();

        Assert.assertTrue(FileUtils.contentEquals(
                new File(client1.dir + "/f1.txt"),
                new File(client2.dir + "/foo.txt")));

        System.out.println("========= /uploadDownloadStore =========");
    }

    @Test
    public void uploadLargeDownloadStore() throws Exception {
        System.out.println("========= uploadLargeDownloadStore =========");
        Proc client1 = createAndStartSeed(10101);
        Proc client2 = createAndStartSeed(10102);


        RandomAccessFile f = new RandomAccessFile(client1.dir + "/large.txt", "rw");
        f.setLength(102 * 1024 * 1024); // 102 mb

        execCmd(client1, "upload large.txt");
        execCmd(client2, "download 0");
        execCmd(client2, "store 0 -f foo.txt");

        execCmd(client1, "exit");
        execCmd(client2, "exit");

        client1.proc.waitFor();
        client2.proc.waitFor();

        Assert.assertTrue(FileUtils.contentEquals(
                new File(client1.dir + "/large.txt"),
                new File(client2.dir + "/foo.txt")));

        System.out.println("========= /uploadLargeDownloadStore =========");
    }

    @Test
    public void keepInformationBetweenSessions() throws Exception {
        System.out.println("========= keepInformationBetweenSessions =========");
        Proc client1 = createAndStartSeed(10101);
        Proc client2 = createAndStartSeed(10102);

        execCmd(client1, "upload f1.txt");
        execCmd(client1, "exit");
        client1.proc.waitFor();

        execCmd(client2, "download 0");
        execCmd(client2, "store 0 -f foo.txt");
        Assert.assertFalse(FileUtils.contentEquals(
                new File(client1.dir + "/f1.txt"),
                new File(client2.dir + "/foo.txt")));

        client1 = createAndStartSeed(10101);
        execCmd(client2, "download 0");
        execCmd(client2, "store 0 -f foo.txt");

        execCmd(client1, "exit");
        execCmd(client2, "exit");
        client1.proc.waitFor();
        client2.proc.waitFor();

        Assert.assertTrue(FileUtils.contentEquals(
                new File(client1.dir + "/f1.txt"),
                new File(client2.dir + "/foo.txt")));

        System.out.println("========= /keepInformationBetweenSessions =========");
    }

    @Test
    public void transitiveDataTransfer() throws Exception {
        System.out.println("========= transitiveDataTransfer =========");
        Proc client1 = createAndStartSeed(10101);
        Proc client2 = createAndStartSeed(10102);
        Proc client3 = createAndStartSeed(10103);

        execCmd(client1, "upload f1.txt");
        execCmd(client2, "download 0");

        execCmd(client1, "exit");
        client1.proc.waitFor();

        execCmd(client3, "download 0");
        execCmd(client3, "store 0 -f foo.txt");

        execCmd(client2, "exit");
        execCmd(client3, "exit");
        client2.proc.waitFor();
        client3.proc.waitFor();

        Assert.assertTrue(FileUtils.contentEquals(
                new File(client1.dir + "/f1.txt"),
                new File(client3.dir + "/foo.txt")));

        System.out.println("========= /transitiveDataTransfer =========");
    }
}

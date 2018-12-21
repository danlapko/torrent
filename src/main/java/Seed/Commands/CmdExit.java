package Seed.Commands;

import Seed.GlobalContext;

@io.airlift.airline.Command(name = "exit", description = "finish session and exit")
public class CmdExit implements Command {

    @Override
    public int execute(GlobalContext context) throws Exception {
        System.err.println("storing ...");

        return 0;
    }
}

package Seed.Commands;


import Seed.GlobalContext;

public interface Command {
    int execute(GlobalContext context) throws Exception;
}

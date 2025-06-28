package io.datadynamics.hdfs;

public class CommandFactory {

    public static Command getCommand(String name) {
        switch (name) {
            case "ls":
                return new LsCommand();
            case "info":
                return new InfoCommand();
            default:
                return new HelpCommand();
        }
    }

}
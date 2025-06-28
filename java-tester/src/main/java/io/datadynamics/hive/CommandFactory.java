package io.datadynamics.hive;

public class CommandFactory {

    public static Command getCommand(String name) {
        switch (name) {
            case "execute":
                return new ExecuteCommand();
            default:
                return new HelpCommand();
        }
    }

}
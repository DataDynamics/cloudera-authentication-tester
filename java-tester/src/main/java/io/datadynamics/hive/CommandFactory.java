package io.datadynamics.hive;

public class CommandFactory {

    public static Command getCommand(String name) {
        switch (name) {
            case "execute":
                return new KerberosExecuteCommand();
            default:
                return new HelpCommand();
        }
    }

}
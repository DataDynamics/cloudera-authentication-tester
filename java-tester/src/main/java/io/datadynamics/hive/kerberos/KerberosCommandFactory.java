package io.datadynamics.hive.kerberos;

public class KerberosCommandFactory {

    public static Command  getCommand(String name) {
        switch (name) {
            case "execute":
                return new KerberosExecuteCommand();
            default:
                return new HelpCommand();
        }
    }

}
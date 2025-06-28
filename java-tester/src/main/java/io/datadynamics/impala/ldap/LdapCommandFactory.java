package io.datadynamics.impala.ldap;

public class LdapCommandFactory {

    public static Command getCommand(String name) {
        switch (name) {
            case "execute":
                return new LdapExecuteCommand();
            default:
                return new HelpCommand();
        }
    }

}
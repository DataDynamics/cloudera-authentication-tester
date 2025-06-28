package io.datadynamics.hive.ldap;

import java.util.List;

public class HelpCommand implements Command {

    @Override
    public void execute(String username, String keytab, String url, String query, List<String> args) throws Exception {
        System.out.println(help());
    }

    @Override
    public String help() {
        String builder = "USAGE:" + "\n" +
                "hive-test --user <User> --pass <Password> --url \"jdbc:hive://...\" --query \"SELECT 1\"" + "\n" +
                "hive-test --user <User> --pass <Password> --url \"jdbc:hive://...\" --queryFile \"user.sql\"" + "\n";
        return builder;
    }

}
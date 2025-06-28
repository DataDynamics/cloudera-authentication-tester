package io.datadynamics.hive.kerberos;

import java.util.List;

public class HelpCommand implements Command {

    @Override
    public void execute(String username, String keytab, String url, String query, List<String> args) throws Exception {
        System.out.println(help());
    }

    @Override
    public String help() {
        String builder = "USAGE:" + "\n" +
                "hive-kerberos --user <User> --keytab <Keytab> --url \"jdbc:hive://...\" --query \"SELECT 1\"" + "\n" +
                "hive-kerberos --user <User> --keytab <Keytab> --url \"jdbc:hive://...\" --queryFile \"user.sql\"" + "\n";
        return builder;
    }

}
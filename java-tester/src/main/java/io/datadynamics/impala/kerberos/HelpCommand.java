package io.datadynamics.impala.kerberos;

import java.util.List;

public class HelpCommand implements Command {

    @Override
    public void execute(String username, String keytab, String url, String query, List<String> args) throws Exception {
        System.out.println(help());
    }

    @Override
    public String help() {
        String builder = "USAGE:" + "\n" +
                "impala-kerberos --user <User> --keytab <Keytab> --url \"jdbc:impala://...\" --query \"SELECT 1\"" + "\n" +
                "impala-kerberos --user <User> --keytab <Keytab> --url \"jdbc:impala://...\" --queryFile \"user.sql\"" + "\n";
        return builder;
    }

}
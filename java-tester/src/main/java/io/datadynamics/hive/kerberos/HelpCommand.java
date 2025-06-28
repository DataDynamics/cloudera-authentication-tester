package io.datadynamics.hive.kerberos;

import java.util.List;

public class HelpCommand implements Command {

    @Override
    public void execute(String username, String keytab, String url, String query, List<String> args) throws Exception {
        System.out.println(help());
    }

    @Override
    public String help() {
        StringBuilder builder = new StringBuilder();
        builder.append("USAGE:").append("\n");
        builder.append("hive-test --user <User> --keytab <Password> --url \"jdbc:hive://...\" --query \"SELECT 1\"").append("\n");
        builder.append("hive-test --user <User> --keytab <Password> --url \"jdbc:hive://...\" --queryFile \"user.sql\"").append("\n");
        return builder.toString();
    }

}
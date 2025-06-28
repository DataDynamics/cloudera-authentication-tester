package io.datadynamics.impala.ldap;

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
        builder.append("impala-test --user <User> --pass <Password> --url \"jdbc:impala://...\" --query \"SELECT 1\"").append("\n");
        builder.append("impala-test --user <User> --pass <Password> --url \"jdbc:impala://...\" --queryFile \"user.sql\"").append("\n");
        return builder.toString();
    }

}
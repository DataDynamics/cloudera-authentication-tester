package io.datadynamics.impala.kerberos;

import java.util.List;

interface Command {

    void execute(String username, String keytab, String url, String query, List<String> args) throws Exception;

    String help();

}
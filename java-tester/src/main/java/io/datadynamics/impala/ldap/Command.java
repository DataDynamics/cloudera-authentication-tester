package io.datadynamics.impala.ldap;

import java.util.List;

interface Command {

    void execute(String username, String password, String url, String query, List<String> args) throws Exception;

    String help();

}
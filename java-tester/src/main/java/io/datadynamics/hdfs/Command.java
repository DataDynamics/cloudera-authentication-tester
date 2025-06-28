package io.datadynamics.hdfs;

import java.util.List;

interface Command {

    void execute(String username, String keytab, List<String> args) throws Exception;

    String help();

}
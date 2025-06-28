package io.datadynamics.hive.ldap;

import io.datadynamics.hive.kerberos.HelpCommand;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LdapTester {

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            printHelp();
            System.exit(1);
        }

        String username = null, password = null, url = null, query = null, queryFile = null;
        int i = 0;
        // 옵션 파싱
        while (i < args.length && args[i].startsWith("--")) {
            switch (args[i]) {
                case "--user":
                    if (++i < args.length) username = args[i++];
                    else printHelpAndExit();
                    break;
                case "--pass":
                    if (++i < args.length) password = args[i++];
                    else printHelpAndExit();
                    break;
                case "--url":
                    if (++i < args.length) url = args[i++];
                    else printHelpAndExit();
                    break;
                case "--query":
                    if (++i < args.length) query = args[i++];
                    else printHelpAndExit();
                    break;
                case "--queryFile":
                    if (++i < args.length) queryFile = args[i++];
                    else printHelpAndExit();
                    break;
                default:
                    printHelpAndExit();
            }
        }

        if (i >= args.length) printHelpAndExit();
        String cmdName = args[i++];
        Command cmd = LdapCommandFactory.getCommand(cmdName);
        if (cmd == null) {
            System.err.println("알 수 없는 명령: " + cmdName);
            printHelpAndExit();
        }

        List<String> cmdArgs = new ArrayList<>();
        while (i < args.length) {
            cmdArgs.add(args[i++]);
        }

        if (username == null || password == null || url == null) {
            printHelpAndExit();
        }

        if (StringUtils.isEmpty(query) && StringUtils.isEmpty(queryFile)) {
            System.err.println("알 수 없는 명령: " + cmdName);
            printHelpAndExit();
        }

        String q = null;
        if (!StringUtils.isEmpty(queryFile) && !new java.io.File(queryFile).exists()) {
            System.err.println("알 수 없는 명령: " + cmdName);
            printHelpAndExit();
        }

        if (!StringUtils.isEmpty(queryFile) && new java.io.File(queryFile).exists()) {
            q = FileUtils.readFileToString(new java.io.File(queryFile), Charset.defaultCharset());
        }

        if (StringUtils.isEmpty(queryFile) && !StringUtils.isEmpty(query)) {
            q = query;
        }


        cmd.execute(username, password, url, q, cmdArgs);
    }

    static void printHelp() {
        System.out.println(new HelpCommand().help());
    }

    static void printHelpAndExit() {
        printHelp();
        System.exit(1);
    }

}

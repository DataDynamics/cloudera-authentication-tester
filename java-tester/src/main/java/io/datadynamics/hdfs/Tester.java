package io.datadynamics.hdfs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tester {

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            printHelp();
            System.exit(1);
        }

        String username = null, keytab = null;
        int i = 0;
        // 옵션 파싱
        while (i < args.length && args[i].startsWith("--")) {
            switch (args[i]) {
                case "--user":
                    if (++i < args.length) username = args[i++];
                    else printHelpAndExit();
                    break;
                case "--keytab":
                    if (++i < args.length) keytab = args[i++];
                    else printHelpAndExit();
                    break;
                default:
                    printHelpAndExit();
            }
        }

        if (i >= args.length) printHelpAndExit();
        String cmdName = args[i++];
        Command cmd = CommandFactory.getCommand(cmdName);
        if (cmd == null) {
            System.err.println("알 수 없는 명령: " + cmdName);
            printHelpAndExit();
        }

        if (new File(keytab).exists() == false) {
            System.err.println("Keytab 파일이 존재하지 않습니다. 파일: " + keytab);
            printHelpAndExit();
        }

        List<String> cmdArgs = new ArrayList<>();
        while (i < args.length) {
            cmdArgs.add(args[i++]);
        }

        if (username == null || keytab == null) {
            printHelpAndExit();
        }

        cmd.execute(username, keytab, cmdArgs);
    }

    static void printHelp() {
        System.out.println(new HelpCommand().help());
    }

    static void printHelpAndExit() {
        printHelp();
        System.exit(1);
    }

}
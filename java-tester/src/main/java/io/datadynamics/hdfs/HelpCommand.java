package io.datadynamics.hdfs;

import java.util.List;

public class HelpCommand implements Command {

    @Override
    public void execute(String username, String keytab, List<String> args) throws Exception {
        System.out.println(help());
    }

    @Override
    public String help() {
        String builder = "USAGE:" + "\n" +
                "hdfs-test --user <KerberosUsername> --keytab <KerberosKeytab> <COMMAND>" + "\n" +
                "<COMMAND>" + "\n" +
                "        ls <DIR>   - 디렉토리 리스트 표시" + "\n" +
                "        info <DIR/FILE> - 파일 정보 표시" + "\n";
        return builder;
    }

}
package io.datadynamics.hdfs;

import java.util.List;

public class HelpCommand implements Command {

    @Override
    public void execute(String username, String keytab, List<String> args) throws Exception {
        System.out.println(help());
    }

    @Override
    public String help() {
        StringBuilder builder = new StringBuilder();
        builder.append("hdfs-test --username <KerberosUsername> --keytab <KerberosKeytab> <COMMAND>").append("\n");
        builder.append("Command:").append("\n");
        builder.append("        ls <DIR>   - 디렉토리 리스트 표시").append("\n");
        builder.append("        info <DIR/FILE> - 파일 정보 표시").append("\n");
        return builder.toString();
    }

}
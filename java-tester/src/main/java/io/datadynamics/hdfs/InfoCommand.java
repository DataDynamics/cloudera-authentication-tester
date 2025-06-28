package io.datadynamics.hdfs;

import io.datadynamics.client.common.DefaultResourceLoader;
import io.datadynamics.client.common.Resource;
import io.datadynamics.client.kerberos.FileSystemHelper;
import io.datadynamics.client.kerberos.KerberosKeytabUser;
import io.datadynamics.client.kerberos.KerberosUser;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class InfoCommand implements Command {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void execute(String username, String keytab, List<String> args) throws Exception {
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Configuration configuration = new Configuration();
        String confDir = System.getProperty("conf.dir");
        Collection<File> files = FileUtils.listFiles(new File(confDir), new String[]{"xml"}, false);
        for (File file : files) {
            Resource resource = defaultResourceLoader.getResource("file://" + file.getAbsolutePath());
            configuration.addResource(resource.getURL());
        }

        KerberosUser kerberosUser = new KerberosKeytabUser(username, keytab);
        FileSystemHelper helper = FileSystemHelper.create(configuration, kerberosUser);
        FileSystem fs = helper.getFs();

        FileStatus fileStatus = fs.getFileStatus(new Path(args.get(0)));

        System.out.println("Path: " + fileStatus.getPath().toString());
        System.out.println("Group: " + fileStatus.getGroup());
        System.out.println("Owner: " + fileStatus.getOwner());
        System.out.println("Size: " + fileStatus.getLen());
        System.out.println("Access Time: " + sdf.format(new Date(fileStatus.getAccessTime())));
        System.out.println("Modification Time: " + sdf.format(new Date(fileStatus.getModificationTime())));
        System.out.println("Replication: " + fileStatus.getReplication());
        System.out.println("Block Size: " + fileStatus.getBlockSize());

        helper.closeFileSystem();

        try {
            helper.logout();
        } catch (Exception e) {
            // Ignored
        }
    }

    @Override
    public String help() {
        return "info <FILE> : File의 정보를 출력합니다.";
    }

}
package io.datadynamics.hdfs;

import io.datadynamics.client.common.DefaultResourceLoader;
import io.datadynamics.client.common.Resource;
import io.datadynamics.client.kerberos.FileSystemHelper;
import io.datadynamics.client.kerberos.KerberosKeytabUser;
import io.datadynamics.client.kerberos.KerberosUser;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.Collection;
import java.util.List;

class LsCommand implements Command {

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

        fs.listFiles(new Path(args.get(0)), false);

        helper.closeFileSystem();

        try {
            helper.logout();
        } catch (Exception e) {
            // Ignored
        }
    }

    @Override
    public String help() {
        return "ls <DIR> : HDFS 디렉토리 목록을 조회합니다.";
    }

}
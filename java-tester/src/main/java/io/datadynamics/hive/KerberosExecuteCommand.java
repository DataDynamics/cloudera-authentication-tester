package io.datadynamics.hive;

import com.cloudera.hive.jdbc.HS2DataSource;
import io.datadynamics.client.common.DefaultResourceLoader;
import io.datadynamics.client.common.Resource;
import io.datadynamics.client.kerberos.KerberosKeytabUser;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.UserGroupInformation;

import javax.sql.DataSource;
import java.io.File;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class KerberosExecuteCommand implements Command {

    @Override
    public void execute(String username, String keytab, String url, String query, List<String> args) throws Exception {

        KerberosKeytabUser kerberosUser = new KerberosKeytabUser(username, keytab);

        HiveConfigurator configurator = new HiveConfigurator();

        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Configuration configuration = new Configuration();
        HiveConf hiveConfig = new HiveConf();
        String confDir = System.getProperty("conf.dir");
        Collection<File> files = FileUtils.listFiles(new File(confDir), new String[]{"xml"}, false);
        for (File file : files) {
            Resource resource = defaultResourceLoader.getResource("file://" + file.getAbsolutePath());
            configuration.addResource(resource.getURL());
            hiveConfig.addResource(resource.getURL());
        }

        UserGroupInformation ugi = configurator.authenticate(hiveConfig, kerberosUser);
        kerberosUser.checkTGTAndRelogin();

        DataSource dataSource = dataSource();
        Connection conn = ugi.doAs((PrivilegedExceptionAction<Connection>) () -> dataSource.getConnection());
        executeQuery(conn, query);

        kerberosUser.logout();


    }

    public static DataSource dataSource() {
        HS2DataSource ds = new HS2DataSource();
        ds.setURL("");
        return ds;
    }

    public static void executeQuery(Connection conn, String query) throws SQLException {
        long startTime = System.currentTimeMillis();

        PreparedStatement psmt = conn.prepareStatement(query);
        ResultSet rs = psmt.executeQuery();
        int rows = 0;
        while (rs.next()) {
            rows++;
        }
        long finishTime = System.currentTimeMillis();
        rs.close();
        psmt.close();
        conn.close();

        System.out.println("Elapsed Time (sec)  : " + (finishTime - startTime) / 1000);
    }

    @Override
    public String help() {
        return "";
    }

}
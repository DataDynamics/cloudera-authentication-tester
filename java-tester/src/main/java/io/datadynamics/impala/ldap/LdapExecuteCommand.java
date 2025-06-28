package io.datadynamics.impala.ldap;

import io.datadynamics.client.ResultSetTablePrinter;
import io.datadynamics.client.common.DefaultResourceLoader;
import io.datadynamics.client.common.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class LdapExecuteCommand implements Command {

    public static void executeQuery(Connection conn, String query) throws SQLException {
        long startTime = System.currentTimeMillis();

        PreparedStatement psmt = conn.prepareStatement(query);
        ResultSet rs = psmt.executeQuery();
        ResultSetTablePrinter.printResultSet(rs);
        rs.close();
        psmt.close();
        conn.close();
        long finishTime = System.currentTimeMillis();

        System.out.println("Elapsed Time (sec)  : " + (finishTime - startTime) / 1000);
    }

    @Override
    public void execute(String username, String password, String url, String query, List<String> args) throws Exception {
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Configuration configuration = new Configuration();
        String confDir = System.getProperty("conf.dir");
        Collection<File> files = FileUtils.listFiles(new File(confDir), new String[]{"xml"}, false);
        for (File file : files) {
            Resource resource = defaultResourceLoader.getResource("file://" + file.getAbsolutePath());
            configuration.addResource(resource.getURL());
        }

        com.cloudera.impala.jdbc.DataSource ds = new com.cloudera.impala.jdbc.DataSource();
        ds.setURL(url);
        ds.setUserID(username);
        ds.setPassword(password);

        Connection conn = ds.getConnection();
        executeQuery(conn, query);
    }

    @Override
    public String help() {
        return "";
    }

}
package io.datadynamics.client;

import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetTablePrinter {

    /**
     * ResultSet 객체를 받아서 inamik-text-tables를 이용해 콘솔에 표로 출력합니다.
     *
     * @param rs 출력할 ResultSet 객체
     * @throws SQLException SQL 처리 중 예외 발생 시
     */
    public static void printResultSet(ResultSet rs) throws SQLException {
        SimpleTable simpleTable = SimpleTable.of();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        SimpleTable headerNext = simpleTable.nextRow();
        for (int i = 1; i <= columnCount; i++) {
            headerNext.nextCell().addLine(metaData.getColumnLabel(i));
        }

        while (rs.next()) {
            SimpleTable row = headerNext.nextRow();
            for (int i = 1; i <= columnCount; i++) {
                row.nextCell().addLine(rs.getString(i));
            }
        }

        Util.print(simpleTable.toGrid());
    }

    public static void printResultSetSimple(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        // 컬럼명 출력
        for (int i = 1; i <= cols; i++) {
            System.out.print(meta.getColumnName(i) + "\t");
        }

        System.out.println();
        // 데이터 출력
        while (rs.next()) {
            for (int i = 1; i <= cols; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println();
        }
    }

}
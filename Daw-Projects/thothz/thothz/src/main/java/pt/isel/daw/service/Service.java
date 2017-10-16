package pt.isel.daw.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;

public class Service {

    final String PAGINATION = " LIMIT %d OFFSET %d";
    private final JdbcTemplate jdbcTemplate;

    public Service(DataSourceTransactionManager transactionManager) {
        try {
            this.jdbcTemplate = new JdbcTemplate(
                    new SingleConnectionDataSource(transactionManager.getDataSource().getConnection(), false)
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    JdbcTemplate getJdbcTemplate(){
        return jdbcTemplate;
    }
}

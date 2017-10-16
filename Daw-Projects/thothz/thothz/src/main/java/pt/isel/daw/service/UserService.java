package pt.isel.daw.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import pt.isel.daw.model.User;
import pt.isel.daw.rowMapper.UserMapper;

import java.util.Base64;
import java.util.List;

@org.springframework.stereotype.Service
public class UserService extends Service{

    private final String SELECT = "SELECT id, name, email, password FROM daw.user";

    public UserService(DataSourceTransactionManager transactionManager){
        super(transactionManager);
    }

    public int countUsers() {
        return getJdbcTemplate()
                .queryForObject("SELECT count(*) FROM daw.user", Integer.class);
    }

    public List<User> getAllUsers(int offset, int limit) {
        return getJdbcTemplate()
                .query(SELECT + String.format(PAGINATION, limit, offset), new UserMapper());
    }

    public User getUser(String email) {
        return getJdbcTemplate()
                .queryForObject(SELECT + " WHERE email = ?", new Object[]{ email}, new UserMapper());
    }


    public int createUser(String name, String email, String password) {
        JdbcTemplate jt = getJdbcTemplate();
        String pass = Base64.getEncoder().encode(password.getBytes()).toString();
        String createUser = "insert into daw.user (name, email, password) values (?,?,?)";
        return jt.update(createUser, name, email, pass);
    }
}
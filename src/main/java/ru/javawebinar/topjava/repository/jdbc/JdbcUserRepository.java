package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        validateUser(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        String createRolesSql = "INSERT INTO user_role (role, user_id) VALUES(?,?)";
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            setRoles(createRolesSql, user);
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        } else {
            jdbcTemplate.update("DELETE FROM user_role Where user_id=?", user.getId());
            setRoles(createRolesSql, user);
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users u left JOIN user_role ur ON ur.user_id = u.id WHERE u.id=?",
                new UserWithRolesResultSetExtractor(), id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users u Left JOIN user_role ur ON ur.user_id = u.id WHERE email=?",
                new UserWithRolesResultSetExtractor(), email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        Map<Integer, Set<Role>> allRoles = jdbcTemplate.query("SELECT * FROM user_role", new RoleResultSetExtractor());
        List<User> allUser = jdbcTemplate.query("SELECT * FROM users ORDER BY name", new UserWithRolesResultSetExtractor(false, allRoles));
        return allUser;
    }

    private static class UserWithRolesResultSetExtractor implements ResultSetExtractor<List<User>> {

        private boolean isJoinQuery = true;
        private Map<Integer, Set<Role>> roles;

        public UserWithRolesResultSetExtractor() {
        }

        public UserWithRolesResultSetExtractor(boolean isJoinQuery, Map<Integer, Set<Role>> roles) {
            super();
            this.isJoinQuery = isJoinQuery;
            this.roles = roles;
        }

        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> users = new LinkedHashMap<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                User user = users.get(id);
                if (user == null) {
                    user = new User();
                    user.setId(id);
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword((rs.getString("password")));
                    user.setRegistered(rs.getDate("registered"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                    user.setRoles(EnumSet.noneOf(Role.class));
                    users.put(id, user);
                }
                String strRole = rs.getString("role");
                if (!(strRole == null)) {
                    if (isJoinQuery) {
                        if (!strRole.equals("")) {
                            Role role = Role.valueOf(strRole);
                            user.getRoles().add(role);
                        }
                    } else {
                        user.setRoles(roles.get(user.getId()));
                    }
                }

            }
            return new ArrayList<>(users.values());
        }
    }

    private static class RoleResultSetExtractor implements ResultSetExtractor<Map<Integer, Set<Role>>> {
        @Override
        public Map<Integer, Set<Role>> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, Set<Role>> roles = new HashMap<>();
            while (rs.next()) {
                Integer id = rs.getInt("user_id");
                Role role = Role.valueOf(rs.getString("role"));
                if (roles.get(id) == null) {
                    roles.put(id, new HashSet<>());
                }
                roles.get(id).add(role);

            }
            return roles;
        }
    }

    private static class RoleBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
        User user;
        List<Role> roles;

        public RoleBatchPreparedStatementSetter(User user) {
            super();
            this.user = user;
            this.roles = new ArrayList<>(user.getRoles());
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            try {
//                if (roles.size() == 0) {
//                    ps.setString(1, "");
//                } else {
                Role role = roles.get(i);
                ps.setString(1, role.name());
                ps.setInt(2, user.getId());

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException ex) {
                throw new SQLException("can`t create with no roles");
            }
        }

        @Override
        public int getBatchSize() {
            return roles.size() == 0 ? 1 : roles.size();
        }
    }

    private void validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
    }

    private void setRoles(String sql, User user) {
        jdbcTemplate.batchUpdate(sql, new RoleBatchPreparedStatementSetter(user));
    }
}
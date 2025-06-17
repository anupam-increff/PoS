package com.increff.pos.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import com.increff.pos.pojo.ClientPojo;

@Repository
public class ClientDao {
    @Autowired
    private JdbcTemplate jdbc;

    private static final String INSERT = "INSERT INTO client (name, email) VALUES (?, ?)";
    private static final String SELECT_ALL = "SELECT id, name, email FROM client";
    private static final String SELECT_BY_ID = "SELECT id, name, email FROM client WHERE id = ?";
    private static final String UPDATE = "UPDATE client SET name=?, email=? WHERE id=?";

    private static RowMapper<ClientPojo> mapper = (rs, i) -> {
        ClientPojo p = new ClientPojo();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setEmail(rs.getString("email"));
        return p;
    };

    public void insert(ClientPojo p) {
        jdbc.update(INSERT, p.getName(), p.getEmail());
    }

    public ClientPojo select(Integer id) {
        return jdbc.queryForObject(SELECT_BY_ID, mapper, id);
    }

    public List<ClientPojo> selectAll() {
        return jdbc.query(SELECT_ALL, mapper);
    }

    public void update(ClientPojo p) {
        jdbc.update(UPDATE, p.getName(), p.getEmail(), p.getId());
    }
}

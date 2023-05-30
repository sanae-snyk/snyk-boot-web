package com.example.snykbootweb.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countCustomers() {

        log.info("Getting CUSTOMERS count from database");

        int result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM CUSTOMERS", Integer.class);

        return result;
    }

    public void bulkInsert () {
        log.info("Performing bulk insert");

        List<Object[]> splitUpNames = Arrays.asList("John Apple", "John Orange", "John Pear", "John Lemon").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
    }

    public List<Customer> getAll () {

        List<Customer> customers = jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers",
                (rs, rowNum) -> new Customer((int) rs.getLong("id"),
                                                   rs.getString("first_name"),
                                                   rs.getString("last_name")));

        return customers;

    }

    public List<Customer> getAllByLastName (String lastName) {
        List<Customer> customers = jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE last_name = '+lastName+'",
                (rs, rowNum) -> new Customer((int) rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")));

        return customers;
    }
}

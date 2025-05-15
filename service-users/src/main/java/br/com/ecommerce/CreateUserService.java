package br.com.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService {
	
	private Connection connection;

	private static final String USER = "root";
	private static final String PASSWORD = "Jhonatan";
	private static final String URL_MYSQL = "jdbc:mysql://localhost:3306/db_users?useTimezone=true&serverTimezone=UTC&characterEncoding=UTF-8"; 
	
	StringBuilder createTableUser = new StringBuilder()
			.append(" CREATE TABLE IF NOT EXISTS Users ( ")
			.append(" uuid VARCHAR(200) PRIMARY KEY, ")
			.append(" email VARCHAR(200))");
	
	CreateUserService() throws SQLException {
		this.connection = DriverManager.getConnection(URL_MYSQL, USER, PASSWORD);
		connection.createStatement().execute(String.valueOf(createTableUser));
	}
	
	public void testConnection() {
        try (Connection conn = DriverManager.getConnection(URL_MYSQL, USER, PASSWORD)) {
            System.out.println("Conexão bem-sucedida!");
        } catch (SQLException e) {
            System.out.println("Falha na conexão: " + e.getMessage());
        }
    }
	
	public static void main(String[] args) throws SQLException {
		var createUserService = new CreateUserService();
		try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER",
				createUserService::parse, Order.class, new HashMap<String, String>())) {
			service.run();
		}
	}

	private void parse(ConsumerRecord<String, Order> record) throws SQLException {
		System.out.println("------------------------------------------");
		System.out.println("Processing new order, checking for user");
		System.out.println(record.value());

		var order = record.value();
		if (isNewUser(order.getEmail())) {
			insertNewUser(order.getEmail());
		}
	}

	private void insertNewUser(String email) throws SQLException {
		var insert = connection.prepareStatement("INSERT INTO Users (uuid, email) VALUES (?,?)");
		insert.setString(1, UUID.randomUUID().toString());
		insert.setString(2, email);
		insert.execute();
		
		System.out.println("Usuário UUID e " + email + ", adicionado.");
	}

	private boolean isNewUser(String email) throws SQLException {
		var exists = connection.prepareStatement("SELECT uuid FROM Users WHERE email = ? LIMIT 1");
		exists.setString(1, email);
		var result = exists.executeQuery();
		return !result.next();
	}

}
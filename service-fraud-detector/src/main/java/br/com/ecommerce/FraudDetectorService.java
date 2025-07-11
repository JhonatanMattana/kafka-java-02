package br.com.ecommerce;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudDetectorService {
	
	public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try (var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                Order.class,
                new HashMap<String, String>())) {
            service.run();
        }
    }
	
	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        var order = record.value();
        if (isFraud(order)) {
        	// pretending that the fraud happens when the amount is >= 4500
        	System.out.println("Order is a fraud, amaunt: " + order.getAmount().toString());
        	orderDispatcher.send("ECOMMERCE_NEW_REJECTED", order.getEmail(), order);
		} else {
			System.out.println("Approved, amaunt: " + order.toString());
			orderDispatcher.send("ECOMMERCE_NEW_APPROVED", order.getEmail(), order);
		}
        
    }

	private boolean isFraud(Order order) {
		return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
	}
	
}
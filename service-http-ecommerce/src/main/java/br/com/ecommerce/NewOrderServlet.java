package br.com.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewOrderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
	private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();
	
	@Override
	public void destroy() {
		super.destroy();
		orderDispatcher.close();
		emailDispatcher.close();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			// We are not caring about any security issues, we are only
			// showing how to use http as a starting point
			var orderId = UUID.randomUUID().toString();
			var email = req.getParameter("email");
			var amount = new BigDecimal(req.getParameter("amount"));
			
			var order = new Order(orderId, amount, email);
			
			orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);
			
			var emailCode = "Thank you for your Order, We are processing your Order!!!";
			emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
			
			System.out.println("New Order send successfully.");
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("New Order send");
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
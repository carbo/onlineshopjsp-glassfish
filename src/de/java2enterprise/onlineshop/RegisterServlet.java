package de.java2enterprise.onlineshop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(
			HttpServletRequest request, 
			HttpServletResponse response) 
			throws ServletException, IOException {
		
		response.setContentType(
				"text/html;charset=UTF-8");
		
		String email = request.getParameter("email");
		String password = 
			request.getParameter("password");
		
		Customer customer = new Customer();
		customer.setEmail(email);
		customer.setPassword(password);
		
		try {
			persist(customer);
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
		
		if(customer.getId() != null) {
			request.setAttribute("message", "Die Registrierung war erfolgreich!");
		} else {
			request.setAttribute("message", "Die Registrierung war erfolglos!");
		}
		
		RequestDispatcher dispatcher = 
			request.getRequestDispatcher("index.jsp");
		dispatcher.forward(request, response);
	}
	
	public void persist(Customer customer) 
			throws Exception {
		String[] autogeneratedKeys = new String[]{"id"};
		Connection con = 
				((DataSource)InitialContext.
				doLookup("jdbc/__default")).
				getConnection();
		PreparedStatement stmt = con.prepareStatement(
			"INSERT INTO onlineshop.customer(" +
				"email, " +
				"password" +
			") VALUES (" +
				"?, " +
				"?)", autogeneratedKeys
			);
		
		stmt.setString(1, customer.getEmail());
		stmt.setString(2, customer.getPassword());
		stmt.executeUpdate();
		
        ResultSet rs = stmt.getGeneratedKeys();
		Long id = null;
		while(rs.next()) {
			id = rs.getLong(1);
			customer.setId(id);
		}
		con.close();
	}
}

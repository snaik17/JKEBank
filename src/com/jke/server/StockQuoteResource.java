package com.jke.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jke.logic.StockQuoteLogic;

public class StockQuoteResource extends HttpServlet {

	public StockQuoteResource() {}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		List<String> paths= new ArrayList<String>();
		StringTokenizer st= new StringTokenizer(request.getRequestURI(), "/", false);
		while (st.hasMoreTokens())
			paths.add(st.nextToken());

		String stockTickerSymbol = getString(paths, 2);

		if (paths.isEmpty() || stockTickerSymbol == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			PrintWriter out= new PrintWriter(response.getOutputStream());
			out.println("Missing the stock ticker symbol in the request url");
			out.close();
			return;
		}
		
		String stockQuoteValue = StockQuoteLogic.getQuote(stockTickerSymbol);
		response.getWriter().write(stockQuoteValue);
	}
	
	private String getString(List<String> strings, int index) {
		try {
			return strings.get(index);
		} catch (IndexOutOfBoundsException e) {
			return e.getMessage();
		}
	}
}

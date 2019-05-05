/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2009, 2010. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:  Use,
 * duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 *******************************************************************************/

package com.jke.server;

import com.ibm.team.json.JSONArray;
import com.ibm.team.json.JSONObject;
import com.jke.beans.AccountBean;
import com.jke.beans.UserBean;
import com.jke.db.data.BeanLoader;
import com.jke.logic.AccountLogic;
import com.jke.logic.JKE_Util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserResource extends HttpServlet {

	public UserResource() {}


	/**
	 * GET ../user/<userid> returns if the user is known, 404 (NOT_FOUND)
	 * otherwise GET ../user/<userid>/accounts returns the accounts known for this
	 * user, or 404 (NOT_FOUND) if the user is not known
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		
		String servleturlname = "user";
		
		List<String> paths= new ArrayList<String>();
		StringTokenizer st= new StringTokenizer(request.getRequestURI(), "/", false);
		while (st.hasMoreTokens())
			paths.add(st.nextToken());

		String userId= getString(paths, paths.indexOf(servleturlname)+1);
		String action= getString(paths, paths.indexOf(servleturlname)+2);


		
		if (paths.isEmpty() || userId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			PrintWriter out= new PrintWriter(response.getOutputStream());
			out.println("Missing the user id in the request url");
			out.close();
			return;
		}

		UserBean user= new UserBean(userId, "", "");

		if (action == null) {

			user= JKE_Util.login(userId, null);
			if (user == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				PrintWriter out= new PrintWriter(response.getOutputStream());
				out.println("Could not find the specified user");
				out.close();
				return;
			}
			user.toJson().serialize(response.getWriter());

		} else if (action.equals("accounts")) {

			AccountLogic logic= new AccountLogic();
			List<AccountBean> accounts= logic.getUserAccounts(user);
			JSONArray accountsObj= new JSONArray();
			for (AccountBean accountBean : accounts) {
				accountsObj.add(accountBean.toJson());
			}
			accountsObj.serialize(response.getWriter());
		}

	}
	
	/**
	 * POST ../users to create a new user or /user/{userId}/accounts with a
	 * JSON payload to create an account
	 */
	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		// break up the path
		List<String> paths = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(request.getRequestURI(), "/", false);
		while (st.hasMoreTokens()) paths.add(st.nextToken());

		String userId = getString(paths, 1);
		String action = getString(paths, 2);

		if (userId == null && action == null) {
			// { "first" : "Jack", "last" : "Johnson", "userId": "jj", "pwd" :
			// "passw0rd" }
			try {
				Reader reader = new InputStreamReader(request.getInputStream());
				UserBean user = new UserBean();

				JSONObject obj = (JSONObject) JSONObject.parse(reader);

				// check if the user exists already
				BeanLoader loader = new BeanLoader();
				String userid = (String) obj.get("userId");
				if (loader.pullUserBean(userid, null) != null) {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					return;
				}

				user.setFirstName((String) obj.get("first"));
				user.setLastName((String) obj.get("last"));
				user.setUserName(userid);

				loader.pushUserBean(user, (String) obj.get("pwd"));

				response.setHeader("Location", "/user/" + userid);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				PrintWriter out = new PrintWriter(response.getOutputStream());
				out.println("Internal Server Error: " + e.getMessage());
				out.close();
			}
		} else if (userId != null && "accounts".equals(action)) {            
			UserBean user = JKE_Util.login(userId, null);
			if (user == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				PrintWriter out = new PrintWriter(response.getOutputStream());
				out.println("Could not find the specified user");
				out.close();
				return;
			}
			Reader reader = new InputStreamReader(request.getInputStream());
			JSONObject obj = (JSONObject) JSONObject.parse(reader);

			AccountBean account = new AccountBean();
			account.fromJson(obj);
			account.setUserName(userId);

			AccountLogic logic = new AccountLogic();
			if (!logic.addUserAccount(account)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				PrintWriter out = new PrintWriter(response.getOutputStream());
				out.println("Account could not be added (Reason: Duplicate account number)");
				out.close();
				return;
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			PrintWriter out = new PrintWriter(response.getOutputStream());
			out.println("Invalid request: " + request.getRequestURI());
			out.close();
			return;
		}
	}

	private String getString(List<String> strings, int index) {
		try {
			return strings.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
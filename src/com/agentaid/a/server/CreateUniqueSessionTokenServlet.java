package com.agentaid.a.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CreateUniqueSessionTokenServlet extends HttpServlet {

	private static final long serialVersionUID = -6399913492947158097L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String state = (String) session.getAttribute("state");
		if (state == null) {
			state = new BigInteger(130, new SecureRandom()).toString(32);
			request.getSession().setAttribute("state", state);
		}
		response.setContentType("text/javascript");
		PrintWriter pw = response.getWriter();
		pw.write("function signinCallback(authResult) {\n");
		pw.write("	if (authResult['code']) {\n");
		pw.write("		$('#signinButton').attr('style', 'display: none');\n");
		pw.write("		$.ajax({\n");
		pw.write("			type : 'POST',\n");
		pw.write("			url : 'signinCallbackServlet?state=");
		pw.write(state);
		pw.write("',\n");
		pw.write("			contentType : 'application/octet-stream; charset=utf-8',\n");
		pw.write("			success : function(result) {\n");
		pw.write("				$('#results').html(result);\n");
		pw.write("			},\n");
		pw.write("			processData : false,\n");
		pw.write("			data : authResult['code']\n");
		pw.write("		});\n");
		pw.write("		$('#header').attr('style', 'visibility: visible');\n");
		pw.write("	} else if (authResult['error']) {\n");
		pw.write("	}\n");
		pw.write("}\n");
		pw.write("\n");
		pw.write("\n");
		pw.write("\n");
		pw.flush();
		pw.close();
	}
}

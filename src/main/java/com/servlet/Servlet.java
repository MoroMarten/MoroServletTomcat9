package com.servlet;

import java.io.*;
import java.util.Enumeration;

import com.synchronize.Communicator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Servlet extends HttpServlet {

    /**
     * handles HTTP POST request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Enumeration<String> paramNames = request.getParameterNames();
        String param = getNextParam(paramNames);
        if (paramNames.hasMoreElements()) handleResponse(response, "Only one request parameter allowed");
        else if (param == null) handleResponse(response, "No request parameters found");
        else if (param.equals("end")) handleEnd(response);
        else {
            try {
                int amount = Integer.parseInt(param);
                handleAddition(response, amount);
            } catch (NumberFormatException nfe) {
                handleResponse(response, "Request parameter has to be numeric and without decimal places");
            }
        }
    }

    private String getNextParam(Enumeration<String> params) {
        if (params.hasMoreElements()) return params.nextElement();
        else return null;
    }

    private void handleEnd(HttpServletResponse response)
            throws IOException {
        Communicator communicator = Communicator.getInstance();
        int sum = communicator.handleEnd();
        handleResponse(response, Integer.toString(sum));
    }

    private void handleAddition(HttpServletResponse response, int amount)
            throws IOException {
        Communicator communicator = Communicator.getInstance();
        int sum = communicator.increaseAndReceiveSum(amount);
        handleResponse(response, Integer.toString(sum));
    }

    private void handleResponse(HttpServletResponse response, String message)
            throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println();
        writer.println("Result: " + message);
        writer.flush();
    }
}
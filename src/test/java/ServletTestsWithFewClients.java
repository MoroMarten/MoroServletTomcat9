import com.servlet.Servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import threads.MockClient;

import java.io.IOException;

public class ServletTestsWithFewClients {

    private Servlet servlet;
    private MockHttpServletRequest paramRequest;
    private MockHttpServletResponse paramResponse;
    private MockHttpServletRequest endRequest;
    private MockHttpServletResponse endResponse;

    @BeforeEach
    public void setup() {
        servlet = new Servlet();
        paramResponse = new MockHttpServletResponse();
        paramRequest = new MockHttpServletRequest();
        endRequest = new MockHttpServletRequest();
        endResponse = new MockHttpServletResponse();
        endRequest.addParameter("end");
    }

    @Test
    public void testNonNumericParam() throws IOException {
        paramRequest.addParameter("justString");
        servlet.doPost(paramRequest, paramResponse);
        Assertions.assertEquals("Result: Request parameter has to be numeric and without decimal places",
                paramResponse.getContentAsString().strip());
    }

    @Test
    public void testDecimalPlacedParam() throws IOException {
        paramRequest.addParameter("10.5");
        servlet.doPost(paramRequest, paramResponse);
        Assertions.assertEquals("Result: Request parameter has to be numeric and without decimal places",
                paramResponse.getContentAsString().strip());
    }

    @Test
    public void testRequestWithoutParams() throws IOException {
        servlet.doPost(paramRequest, paramResponse);
        Assertions.assertEquals("Result: No request parameters found",
                paramResponse.getContentAsString().strip());
    }

    @Test
    public void testMultipleParams() throws IOException {
        paramRequest.addParameter("10");
        paramRequest.addParameter("5");
        servlet.doPost(paramRequest, paramResponse);
        Assertions.assertEquals("Result: Only one request parameter allowed",
                paramResponse.getContentAsString().strip());
    }

    @Test
    public void testSingleRequest() throws IOException, InterruptedException {
        paramRequest.addParameter("10");
        Thread mockParamRequest = new Thread(new MockClient(paramResponse, paramRequest));
        mockParamRequest.start();
        Thread.sleep(1000);
        servlet.doPost(endRequest, endResponse);
        Thread.sleep(1000);
        Assertions.assertEquals("Result: 10", paramResponse.getContentAsString().strip());
        Assertions.assertEquals("Result: 10", endResponse.getContentAsString().strip());
    }

    @Test
    public void testSummingRequests() throws IOException, InterruptedException {
        MockHttpServletRequest paramRequest2 = getMockRequestWithParam("5");
        MockHttpServletResponse paramResponse2 = new MockHttpServletResponse();
        paramRequest.addParameter("10");
        Thread mockParamRequest1 = new Thread(new MockClient(paramResponse, paramRequest));
        Thread mockParamRequest2 = new Thread(new MockClient(paramResponse2, paramRequest2));
        mockParamRequest1.start();
        mockParamRequest2.start();
        Thread.sleep(1000);
        servlet.doPost(endRequest, endResponse);
        Thread.sleep(1000);
        Assertions.assertEquals("Result: 15", paramResponse.getContentAsString().strip());
        Assertions.assertEquals("Result: 15", paramResponse2.getContentAsString().strip());
        Assertions.assertEquals("Result: 15", endResponse.getContentAsString().strip());
    }

    private MockHttpServletRequest getMockRequestWithParam(String param) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(param);
        return request;
    }
}

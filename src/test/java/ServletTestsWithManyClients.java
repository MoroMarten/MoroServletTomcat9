import com.servlet.Servlet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import threads.MockClient;

import java.io.IOException;
import java.util.ArrayList;

public class ServletTestsWithManyClients {

    private Servlet servlet;
    private MockHttpServletRequest endRequest;
    private MockHttpServletResponse endResponse;

    @BeforeEach
    public void setup() {
        servlet = new Servlet();
        endRequest = new MockHttpServletRequest();
        endResponse = new MockHttpServletResponse();
        endRequest.addParameter("end");
    }

    @Test
    public void test20ClientsAndEnd() throws IOException, InterruptedException {
        int size = 20;
        MockHttpServletResponse[] mockResponses = getMockResponsesArray(size);
        MockHttpServletRequest[] mockRequests = getMockRequestArray(size);
        Thread[] mockClients = getMockClientsArray(mockResponses, mockRequests);
        for (int i = 0; i < size; i++) {
            mockClients[i].start();
        }
        Thread.sleep(1000);
        servlet.doPost(endRequest, endResponse);
        for (int i = 0; i < size; i++) {
            mockClients[i].join();
            Assertions.assertEquals("Result: " + size,
                    mockResponses[i].getContentAsString().strip());
        }
        Assertions.assertEquals("Result: " + size,
                endResponse.getContentAsString().strip());
    }

    @Test
    public void testEndInTheMiddle() throws IOException, InterruptedException {
        MockHttpServletResponse[] mockResponses = getMockResponsesArray(20);
        MockHttpServletRequest[] mockRequests = getMockRequestArray(20);
        mockRequests[10] = getMockRequestWithParam("end");
        Thread[] mockClients = getMockClientsArray(mockResponses, mockRequests);
        for (int i = 0; i < 20; i++) {
            mockClients[i].start();
        }
        Thread.sleep(1000);
        servlet.doPost(endRequest, endResponse);
        ArrayList<String> differentResults = getDifferentResultsArrayFromResponses(mockResponses, mockClients);
        String response = endResponse.getContentAsString().strip();
        if (!differentResults.contains(response)) differentResults.add(response);
        Assertions.assertTrue(2 == differentResults.size() || 1 == differentResults.size());
        Assertions.assertEquals(19, extractSumFromResult(differentResults));
    }

    private ArrayList<String> getDifferentResultsArrayFromResponses(MockHttpServletResponse[] mockResponses, Thread[] mockClients)
    throws IOException, InterruptedException {
        ArrayList<String> differentResults = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mockClients[i].join();
            String response = mockResponses[i].getContentAsString().strip();
            if (!differentResults.contains(response)) differentResults.add(response);
        }
        return differentResults;
    }

    private int extractSumFromResult(ArrayList<String> differentResults) {
        int sum = 0;
        for (String result : differentResults) {
            String[] halves = result.strip().split(" ");
            sum += Integer.parseInt(halves[1]);
        }
        return sum;
    }

    private Thread[] getMockClientsArray(MockHttpServletResponse[] mockResponses, MockHttpServletRequest[] mockRequests) {
        int size = mockRequests.length;
        Thread[] mockClients = new Thread[size];
        for (int i = 0; i < size; i++) {
            Thread thread = new Thread(new MockClient(mockResponses[i], mockRequests[i]));
            mockClients[i] = thread;
        }
        return mockClients;
    }

    private MockHttpServletRequest[] getMockRequestArray(int size) {
        MockHttpServletRequest[] mockRequests = new MockHttpServletRequest[size];
        for (int i = 0; i < size; i++) {
            mockRequests[i] = getMockRequestWithParam("1");
        }
        return mockRequests;
    }

    private MockHttpServletResponse[] getMockResponsesArray(int size) {
        MockHttpServletResponse[] mockResponses = new MockHttpServletResponse[size];
        for (int i = 0; i < size; i++) {
            MockHttpServletResponse mockResponse = new MockHttpServletResponse();
            mockResponses[i] = mockResponse;
        }
        return mockResponses;
    }

    private MockHttpServletRequest getMockRequestWithParam(String param) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(param);
        return request;
    }

}

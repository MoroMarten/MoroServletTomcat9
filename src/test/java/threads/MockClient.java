package threads;

import com.servlet.Servlet;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

public class MockClient implements Runnable {

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private Servlet servlet;

    public MockClient(MockHttpServletResponse response, MockHttpServletRequest request) {
        this.response = response;
        this.request = request;
        this.servlet = new Servlet();
    }

    public void run() {
        try {
            servlet.doPost(request, response);
        } catch (IOException io) {}
    }
}

package wei.web.resource;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderResource.class)
public class OrderResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderSender orderSender;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test @Ignore
    public void testNonNumericOrderId() throws Exception {

        String orderIds = "1, 2, e";

        thrown.expectCause(isA(NumberFormatException.class));
        mvc.perform(post("/orders/{orderIds}?abortOrder=false&abortOrderId=", orderIds)).andExpect(MockMvcResultMatchers.status().isOk());
    }


}

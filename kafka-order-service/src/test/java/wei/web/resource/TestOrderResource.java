package wei.web.resource;

import mockit.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestOrderResource {

    @Injectable
    //Dummy dummy;
    OrderSender orderSender;

    @Tested
    OrderResource orderResource;

    @Test
    public void testOrderResource() {

        new Expectations() {{
           orderSender.send((Integer[])any, anyBoolean, anyString); times = 1;
        }};
        orderResource.createOrder("1", false, "");

        new Verifications() {{
            boolean abortOrder;
            orderSender.send(null, abortOrder = withCapture(), null);
            Assert.assertEquals(abortOrder, false);
        }};
    }

    @Test
    public void testOrderRe() {
        new Expectations() {{
            orderSender.send((Integer[])any, anyBoolean, anyString); times = 1;
        }};
        orderResource.createOrder("1", true, "");

        new Verifications() {{
            boolean abortOrder;
            orderSender.send(null, abortOrder = withCapture(), null);
            Assert.assertEquals(abortOrder, true);
        }};
    }
}

package wei.web.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@RestController
@Slf4j
public class OrderResource {

    @Autowired
    //Dummy dummy;
    OrderSender sender;

    @PostMapping("/orders/{orderIds}")
    public void createOrder(@PathVariable String orderIds, @RequestParam boolean abortOrder, @RequestParam String abortOrderId) {
        //try {
            Stream<String> strStream = Arrays.stream(orderIds.split(","));
            Stream<Integer> idStream = strStream.map(Integer::parseInt);
            Integer[] ids = idStream.toArray(Integer[]::new);
        /*} catch (Exception ex) {
            ex.printStackTrace();
        }*/
        //dummy.du();
        sender.send(new Integer[]{1}, abortOrder, "");
        //sender.send(ids, abortOrder, abortOrderId);
        /*ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new Helper(sender, new int[]{1, 2}));*/
        //executor.submit(new Helper(sender, new int[]{101, 200}));
        //executor.submit(new Helper(sender, new int[]{201, 300}));
    }

    private static class Helper implements Runnable {

        private OrderSender orderSender;
        private int[] orderIdRange;

        private Helper(OrderSender orderSender, int[] orderIdRange) {
            this.orderSender = orderSender;
            this.orderIdRange = orderIdRange;
        }

        @Override
        public void run() {
            for(int i=orderIdRange[0]; i<orderIdRange[1]; i++) {
                String abortOrderId = "";
                if(i % 50 == 1) {
                    abortOrderId = "" + 10000;
                }
                orderSender.send(new Integer[]{i}, false, abortOrderId);

                int sleepTime = new Random().nextInt(400);
                try {
                    Thread.sleep((sleepTime));
                } catch (Exception ex) {

                }
            }
        }
    }
}

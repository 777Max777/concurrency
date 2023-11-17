package course.concurrency.m3_shared.immutable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private Map<Long, Order> currentOrders = new HashMap<>();
    private long nextId = 0L;

    private synchronized long nextId() {
        return nextId++;
    }

    public synchronized long createOrder(List<Item> items) {
        long id = nextId();
        Order order = new Order(items);
        order.setId(id);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order oldVal;
        while (true) {
            oldVal = currentOrders.get(orderId);
            if (oldVal.isSealed())
                continue;
            Order.OrderBuilder builder = new Order.OrderBuilder(oldVal)
                    .withPaymentInfo(paymentInfo);
            Order order = builder.build();
            if (order.checkStatus()) {
                order = setDeliveredStatus(builder);
            }
            if (currentOrders.get(orderId).equals(oldVal) && !oldVal.isSealed()) {
                oldVal.seal(true);
                currentOrders.put(orderId, order);
                break;
            }
        }
    }

    public void setPacked(long orderId) {
        Order o;
        while (true) {
            o = currentOrders.get(orderId);
            if (o.isSealed())
                continue;
            o.seal(true);
            Order.OrderBuilder builder = new Order.OrderBuilder(o)
                    .withPacked(true);
            Order order = builder.build();
            if (order.checkStatus()) {
                order = setDeliveredStatus(builder);
            }
            if (currentOrders.get(orderId).equals(o)) {
                currentOrders.put(orderId, order);
                break;
            }
        }
    }

    private synchronized void deliver(Order order) {
        /* ... */
        currentOrders.get(order.getId()).setStatus(Order.Status.DELIVERED);
    }

    private Order setDeliveredStatus(Order.OrderBuilder builder) {
        return builder.withStatus(Order.Status.DELIVERED).build();
    }

    public synchronized boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}

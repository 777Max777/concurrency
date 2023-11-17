package course.concurrency.m3_shared.immutable;

import java.util.List;

import static course.concurrency.m3_shared.immutable.Order.Status.NEW;

public class Order {

    public enum Status { NEW, IN_PROGRESS, DELIVERED }

    private Long id;
    private List<Item> items;
    private PaymentInfo paymentInfo;
    private boolean isPacked;
    private Status status;

    public Order(List<Item> items) {
        this.items = items;
        this.status = NEW;
    }

    public synchronized boolean checkStatus() {
        if (items != null && !items.isEmpty() && paymentInfo != null && isPacked) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
        this.status = Status.IN_PROGRESS;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public void setPacked(boolean packed) {
        isPacked = packed;
        this.status = Status.IN_PROGRESS;
    }

    public Status getStatus() {
        return status;
    }

    public void seal(boolean isSealed) {
        this.isSealed = isSealed;
    }

    public boolean isSealed() {
        return isSealed;
    }

    public static class OrderBuilder {

        private Long id;
        private List<Item> items;
        private PaymentInfo paymentInfo;
        private boolean isPacked;
        private Status status;

        public OrderBuilder(List<Item> items) {
            this.items = items;
            this.status = Status.NEW;
        }

        public Order build() {
            return new Order(
                    this.id,
                    this.items,
                    this.paymentInfo,
                    this.isPacked,
                    this.status);
        }

        public OrderBuilder(Order order) {
            this.id = order.getId();
            this.items = order.getItems();
            this.paymentInfo = order.getPaymentInfo();
            this.isPacked = order.isPacked();
            this.status = order.getStatus();
        }

        public OrderBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public OrderBuilder withItems(List<Item> items) {
            this.items = items;
            return this;
        }

        public OrderBuilder withPaymentInfo(PaymentInfo paymentInfo) {
            this.paymentInfo = paymentInfo;
            this.status = Status.IN_PROGRESS;
            return this;
        }

        public OrderBuilder withPacked(boolean packed) {
            this.isPacked = packed;
            this.status = Status.IN_PROGRESS;
            return this;
        }

        public OrderBuilder withStatus(Status status) {
            this.status = status;
            return this;
        }
    }
}

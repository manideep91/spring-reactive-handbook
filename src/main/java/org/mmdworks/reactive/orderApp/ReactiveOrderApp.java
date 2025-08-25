package org.mmdworks.reactive.orderApp;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public class ReactiveOrderApp {

    public static void main(String[] args) {
        OrderService orderService = new OrderService();

        // 👉 You will implement the logic here
        Mono<OrderReceipt> receiptMono = orderService.placeOrder("user123");

        receiptMono.subscribe(System.out::println);
    }
}

// 📦 --- POJOs ---
class User {
    String userId;
    String name;
    // constructor, getters
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    public String toString() { return "User: " + name; }
}

class Cart {
    String userId;
    double total;
    public Cart(String userId, double total) {
        this.userId = userId;
        this.total = total;
    }
    public String toString() { return "Cart total: " + total; }
}

class PaymentInfo {
    String transactionId;
    public PaymentInfo(String transactionId) {
        this.transactionId = transactionId;
    }
    public String toString() { return "Payment: " + transactionId; }
}

class OrderReceipt {
    User user;
    Cart cart;
    PaymentInfo payment;
    public OrderReceipt(User user, Cart cart, PaymentInfo payment) {
        this.user = user;
        this.cart = cart;
        this.payment = payment;
    }
    public String toString() {
        return "Receipt for " + user.name + ", paid: " + cart.total + ", txn: " + payment.transactionId;
    }
}


class UserService {
    Mono<User> getUser(String userId) {
        return Mono.just(new User(userId, "Manideep"));
    }
}

class CartService {
    Mono<Cart> getCart(String userId) {
        return Mono.just(new Cart(userId, 299.99));
    }
}

class PaymentService {
    Mono<PaymentInfo> charge(User user, Cart cart) {
        return Mono.just(new PaymentInfo("TXN12345"));
    }
}


//Mono<User> → from userService.getUser(userId)
//Mono<Cart> → from cartService.getCart(userId)
//Mono<PaymentInfo> → from paymentService.charge(User, Cart)
//Mono<OrderReceipt> → Combine all 3 and build a receipt


class OrderService {
    UserService userService = new UserService();
    CartService cartService = new CartService();
    PaymentService paymentService = new PaymentService();



    Mono<OrderReceipt> placeOrder(String userId) {

        Mono<User> userMono = userService.getUser(userId);
        Mono<Cart> cartMono = cartService.getCart(userId);

        return userMono.zipWith(cartMono)
                .flatMap(tuple -> {
                    User t1 = tuple.getT1();
                    Cart t2 = tuple.getT2();
                    Mono<PaymentInfo> paymentInfoMono = paymentService.charge(t1, t2);
                    return paymentInfoMono.map(paymentInfo -> new OrderReceipt(t1, t2, paymentInfo));

                });
    }
}


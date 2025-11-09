import java.util.*;

// ===== Class Product =====
class Product {
    String productId;
    String name;
    double price;
    int stock;

    public Product(String productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public boolean reduceStock(int amount) {
        if (stock >= amount) {
            stock -= amount;
            return true;
        }
        return false;
    }
}

// ===== Class OrderItem =====
class OrderItem {
    String orderId;
    String productId;
    int quantity;
    double unitPrice;

    public OrderItem(String orderId, String productId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double calculateTotal() {
        return quantity * unitPrice;
    }
}

// ===== Class Payment =====
class Payment {
    String paymentId;
    String method;
    double amount;
    String status;

    public Payment(String paymentId, String method, double amount) {
        this.paymentId = paymentId;
        this.method = method;
        this.amount = amount;
        this.status = "Pending";
    }

    public String getPaymentMethod() {
        return method;
    }

    public double getAmount() {
        return amount;
    }

    public boolean confirmPayment() {
        status = "Completed";
        return true;
    }
}

// ===== Class Receipt =====
class Receipt {
    String receiptId;
    String orderDetails;
    Date printTime;

    public Receipt(String receiptId, String orderDetails) {
        this.receiptId = receiptId;
        this.orderDetails = orderDetails;
        this.printTime = new Date();
    }

    public void print() {
        System.out.println("\n===== STRUK PEMBELIAN =====");
        System.out.println("Nomor Struk  : " + receiptId);
        System.out.println(orderDetails);
        System.out.println("Waktu Cetak  : " + printTime);
        System.out.println("===========================");
    }
}

// ===== Class Order =====
class Order {
    String orderId;
    Date orderDate;
    double totalAmount;
    String status;
    ArrayList<OrderItem> items = new ArrayList<>();
    Payment payment;

    public Order(String orderId) {
        this.orderId = orderId;
        this.orderDate = new Date();
        this.status = "New";
    }

    public void addItem(Product product, int quantity) {
        if (product.reduceStock(quantity)) {
            OrderItem item = new OrderItem(orderId, product.productId, quantity, product.getPrice());
            items.add(item);
        } else {
            System.out.println("❌ Stok tidak mencukupi untuk produk: " + product.name);
        }
    }

    public double getTotalAmount() {
        totalAmount = 0;
        for (OrderItem item : items) {
            totalAmount += item.calculateTotal();
        }
        return totalAmount;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Receipt generateReceipt() {
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(orderId).append("\n");
        for (OrderItem item : items) {
            details.append(item.productId).append(" x").append(item.quantity)
                   .append(" = Rp ").append(item.calculateTotal()).append("\n");
        }
        details.append("Total: Rp ").append(getTotalAmount()).append("\n");
        details.append("Pembayaran: ").append(payment.method).append("\n");
        return new Receipt("RC" + orderId, details.toString());
    }
}

// ===== Class Kasir =====
class Kasir {
    String employeeId;
    String name;
    String shift;

    public Kasir(String employeeId, String name, String shift) {
        this.employeeId = employeeId;
        this.name = name;
        this.shift = shift;
    }

    public void completeTransaction(Order order, Payment payment) {
        order.setPayment(payment);
        payment.confirmPayment();
        order.status = "Completed";

        Receipt receipt = order.generateReceipt();
        receipt.print();
    }
}

// ===== Main Class =====
public class POSMain {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // Data produk
        Product p1 = new Product("P001", "Roti", 5000, 10);
        Product p2 = new Product("P002", "Susu", 8000, 8);
        Product p3 = new Product("P003", "Kopi", 12000, 5);

        Kasir kasir = new Kasir("K01", "Hilmi", "Pagi");
        Order order = new Order("O001");

        System.out.println("=== Sistem Point of Sale ===");
        System.out.println("Kasir: " + kasir.name);
        System.out.println("Daftar Produk:");
        System.out.println("1. " + p1.name + " - Rp" + p1.price);
        System.out.println("2. " + p2.name + " - Rp" + p2.price);
        System.out.println("3. " + p3.name + " - Rp" + p3.price);

        while (true) {
            System.out.print("\nPilih produk (1-3, 0 untuk selesai): ");
            int pilih = in.nextInt();
            if (pilih == 0) break;

            System.out.print("Jumlah beli: ");
            int qty = in.nextInt();

            switch (pilih) {
                case 1 -> order.addItem(p1, qty);
                case 2 -> order.addItem(p2, qty);
                case 3 -> order.addItem(p3, qty);
                default -> System.out.println("Pilihan tidak valid!");
            }
        }

        double total = order.getTotalAmount();
        System.out.println("\nTotal belanja: Rp " + total);

        System.out.print("Masukkan metode pembayaran (Cash/Debit): ");
        String method = in.next();
        Payment payment = new Payment("PAY001", method, total);

        kasir.completeTransaction(order, payment);
        in.close();
    }
}
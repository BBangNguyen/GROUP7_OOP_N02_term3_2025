import java.util.Scanner;

class User {
    String name;
    String address;
}

class ElectricityBill {
    double units;

    double calculateBill() {
        return units * 1.5; // 1.5 VND mỗi kWh
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        User user = new User();
        System.out.print("Nhập tên khách hàng: ");
        user.name = scanner.nextLine();

        System.out.print("Nhập địa chỉ khách hàng: ");
        user.address = scanner.nextLine();

        ElectricityBill bill = new ElectricityBill();
        System.out.print("Nhập số điện tiêu thụ (kWh): ");
        bill.units = scanner.nextDouble();

        // Xuất hóa đơn
        System.out.println("\n=== Hóa đơn tiền điện ===");
        System.out.println("Tên khách hàng: " + user.name);
        System.out.println("Địa chỉ: " + user.address);
        System.out.println("Số điện tiêu thụ: " + bill.units + " kWh");
        System.out.println("Thành tiền: " + bill.calculateBill() + " VND");

        scanner.close();
    }
}

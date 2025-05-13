import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Assertions;

@TestInstance(Lifecycle.PER_CLASS)
public class JUnitRunner {
    public static void main(String[] args) {
        ElectricityBillManagementTest test = new ElectricityBillManagementTest();
        
        System.out.println("Running test for ElectricityBillManagementTest...");

        // Test tính tiền điện
        test.testCalculateBill();
        System.out.println("Test tính tiền điện: PASSED");

        // Test thông tin người dùng
        test.testUserInfo();
        System.out.println("Test thông tin người dùng: PASSED");
    }
}

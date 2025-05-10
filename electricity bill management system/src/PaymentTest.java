package pure_java_project.test;

public class PaymentTest {package com.groupname.ebms.tests;

import com.groupname.ebms.models.Payment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    @Test
    public void testAmountPaid() {
        Payment payment = new Payment(1, 101, 150.0, "2025-05-10");
        assertEquals(150.0, payment.getAmountPaid());
    }
}

    
}

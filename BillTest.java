package pure_java_project.test;

public class BillTest { package com.groupname.ebms.tests;

import com.groupname.ebms.models.Bill;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BillTest {
    @Test
    public void testCalculateTotalAmount() {
        Bill bill = new Bill(101, 1, 500, 0.2);
        assertEquals(100.0, bill.getTotalAmount(), 0.001);
    }
}
    
}


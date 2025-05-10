package com.groupname.ebms.tests;

import com.groupname.ebms.models.Customer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {
    @Test
    public void testUpdateInfo() {
        Customer customer = new Customer(1, "John", "Address1", "123", "john@mail.com");
        customer.updateInfo("Jane", null, null, null);
        assertEquals("Jane", customer.getName());
    }
}

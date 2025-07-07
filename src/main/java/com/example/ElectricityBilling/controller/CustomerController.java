package com.example.ElectricityBilling.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ElectricityBilling.dto.CustomerRegistrationDTO;
import com.example.ElectricityBilling.dto.CustomerResponseDTO;
import com.example.ElectricityBilling.dto.LoginRequestDTO;
import com.example.ElectricityBilling.dto.LoginResponseDTO;
import com.example.ElectricityBilling.entity.Customer;
import com.example.ElectricityBilling.mapper.CustomerMapper;
import com.example.ElectricityBilling.security.JwtTokenUtil;
import com.example.ElectricityBilling.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerController(CustomerService customerService,
                            AuthenticationManager authenticationManager,
                            JwtTokenUtil jwtTokenUtil,
                            CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.customerMapper = customerMapper;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers().stream()
                .map(customerMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customerMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegistrationDTO registrationDTO) {
        if (customerService.existsByEmail(registrationDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        if (customerService.existsByPhone(registrationDTO.getPhone())) {
            return ResponseEntity.badRequest().body("Phone number already exists");
        }
        
        Customer customer = customerMapper.toEntity(registrationDTO);
        Customer savedCustomer = customerService.registerCustomer(customer);
        return ResponseEntity.ok(customerMapper.toResponseDTO(savedCustomer));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            System.out.println("[CustomerController] Login attempt for email: " + loginRequest.getEmail());
            
            // Kiểm tra xem customer có tồn tại không
            Customer customer = customerService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Customer not found with email: " + loginRequest.getEmail()));
            
            System.out.println("[CustomerController] Customer found: " + customer.getFullName());
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            System.out.println("[CustomerController] Authentication successful");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            // Trả về cả id cho frontend
            LoginResponseDTO response = new LoginResponseDTO(token, customer.getEmail(), customer.getFullName(), customer.getId());
            System.out.println("[CustomerController] Login successful for: " + customer.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("[CustomerController] Login failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PostMapping("/init-sample-data")
    public ResponseEntity<String> initSampleData() {
        try {
            // Kiểm tra xem đã có customer nào chưa
            if (customerService.getAllCustomers().isEmpty()) {
                Customer sampleCustomer = new Customer();
                sampleCustomer.setEmail("bangcm12@gmail.com");
                sampleCustomer.setPassword("123456"); // Password sẽ được encode trong service
                sampleCustomer.setFullName("Nguyen Van A");
                sampleCustomer.setPhone("0123456789");
                sampleCustomer.setLocation("Ha Noi");
                
                customerService.registerCustomer(sampleCustomer);
                return ResponseEntity.ok("Dữ liệu khách hàng mẫu đã được tạo thành công!");
            } else {
                return ResponseEntity.ok("Đã có dữ liệu khách hàng trong hệ thống.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo dữ liệu mẫu: " + e.getMessage());
        }
    }
}
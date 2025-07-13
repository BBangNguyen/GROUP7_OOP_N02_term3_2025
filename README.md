+ Nguyễn Hoàng Giang MSV 22010370
+ Nguyễn Văn Bằng   MSV 22010952Add commentMore actions

 Tiêu đề BTL : Electricity bill management system
 Xây dựng ứng dụng  quản lí tiền điện 

Link 
Yêu cầu:
- Giao diện Java Spring Boot.
- Có chức năng quản lý quản lí tiền điện của khách 

+ Thêm, sửa, xóa khách hàng

+ Liệt kê thông tin về khách hàng, có thể lọc các khách hàng theo loại (hộ dân, doanh nghiệp), theo địa chỉ, theo mã khách hàng
- Có chức năng quản lý chỉ số tiền điện 

+Thêm, sửa, xoá chỉ số điện tiêu thụ theo từng tháng.

- Có chức năng gán chỉ số điện cho khách hàng.

- Dữ liệu được lưu trữ xuống file nhị phân

+ Cần tạo các lớp liên quan đến khách hàng , chỉ số điện  , và hóa đơn  để đọc, ghi xuống 1 hay nhiều file.

- Khi làm việc với dữ liệu trong bộ nhớ, dữ liệu cần được lưu trữ dưới dạng các Collection tùy chọn như ArrayList, LinkedList, Map, ....

- Sinh viên có thể thêm các chức năng vào chương trình để ứng dụng phong phú hơn bằng cách thêm các nghiệp vụ cho bài toán (tùy chọn)
  + Tính tiền điện theo bậc.
  + Tạo hóa đơn
  + Thống kê theo tháng, năm, khách hàng...
 
1.Sơ đố khối yêu cầu
1.1 UML Component Diagram

<img width="954" height="526" alt="image" src="https://github.com/user-attachments/assets/df55b477-7f11-4a4c-92e5-43b2613fc4c6" />


2. Sơ đồ Behavioural Diagram:
2.1 Sequence Diagram
<img width="2379" height="1311" alt="image" src="https://github.com/user-attachments/assets/063f5eca-dadb-483b-b340-ec634b7fde2e" />


2.2  Activity Diagram
<img width="2560" height="1170" alt="image" src="https://github.com/user-attachments/assets/757374e4-53b0-4a6d-857c-857f6285e778" />












Lưu đồ thuật toán : <img width="985" height="1583" alt="ZLPHRnj547w_Np6ObyHjNAT9sf8yK2Ts6jFK2RPR2GcfsjoDxQLtksPlpwsf-eGG0hL0QILKH0Kj89ug-WRK4Yz1_H_k7u2VmExUPNAndIZxaFXcTcQ__RxPsRqSQwPq4eNGPh8gHNTBLZcAcnizBdhjNkBVh8HTgRZkHPxtbkvXXC1knmjICT55Bxx5HPyf5i4UyszrbKn4i253gU3jpKkRxsn" src="https://github.com/user-attachments/assets/13f074cb-9aaf-461e-81c2-3b87f6ad05dd" />






   Yêu cầu số 6-7 :
 Thành viên nhóm
+ Nguyễn Hoàng Giang MSV 22010370
+ Nguyễn Văn Bằng MSV 22010952

 Tiêu đề BTL: Electricity bill management system
Xây dựng ứng dụng quản lý tiền điện 

 Chức năng chính của hệ thống (Yêu cầu số 5)
Quản lý hóa đơn tiền điện: Tạo hóa đơn tiền điện dựa trên chỉ số đồng hồ điện và tính toán chi phí theo biểu giá điện.

 Phân tích thuật toán thành các chức năng nhỏ:
1. Quản lý đồng hồ điện**Thêm, xóa, sửa thông tin đồng hồ điện
3. Tạo hóa đơn Tự động tạo hóa đơn dựa trên chỉ số tiêu thụ
4. Tính toán chi phí: Áp dụng biểu giá điện để tính toán tổng chi phí
5. Thanh toán hóa đơn: Xử lý thanh toán và cập nhật trạng thái hóa đơn

 Yêu cầu số 6: Bắt lỗi (Try-Catch-Finally)
Đã thêm khối bắt lỗi vào tất cả các phần:
-  Backend Controllers (CustomerController, BillingController, MeterController)
-  Service Layer (BillingService, CustomerService, MeterService)
-  Frontend JavaScript (Authentication, API calls, Form handling)

 Yêu cầu số 7: Phân công công việc UI/MVC

 Phân chia công việc cho các thành viên:

 Sinh viên A - Nguyễn Hoàng Giang (MSV 22010370) và Nguyễn Văn Bằng :
Chức năng: Quản lý đồng hồ điện
- Controller: MeterController.java
- View: Giao diện quản lý đồng hồ điện (Meters Tab)
- Model: Meter.java, Consumption.java
- Nhiệm vụ cụ thể:
  - Thêm/xóa/sửa đồng hồ điện
  - Ghi chỉ số tiêu thụ điện hàng tháng
  - Biểu đồ tiêu thụ điện theo thời gian

Sinh viên B - Nguyễn Văn Bằng (MSV 22010952):
Chức năng: Quản lý hóa đơn và thanh toán và ghi chỉ số tiêu thụ
- Controller: BillingController.java
- View: Giao diện quản lý hóa đơn (Bills Tab)
- Model: Billing.java, Payment.java
- Nhiệm vụ cụ thể:
  - Tạo hóa đơn tiền điện tự động
  - Hiển thị danh sách hóa đơn
  - Xử lý thanh toán hóa đơn
  - Xuất báo cáo Excel

   

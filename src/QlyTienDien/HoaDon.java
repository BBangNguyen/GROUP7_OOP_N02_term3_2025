package QlyTienDien;

class HoaDon {
    private String maHoaDon;
    private String ngayLap ;
    private KhachHang khachHang;

    public HoaDon(String maHoaDon, String ngayLap, KhachHang khachHang) {
        this.maHoaDon = maHoaDon;
        this.ngayLap = ngayLap;
        this.khachHang = khachHang;
    }

    // Getters và Setters
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(String ngayLap) {
        this.ngayLap = ngayLap;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    // Phương thức để in hóa đơn
    public void inHoaDon() {
        System.out.println("---- Hóa Đơn Điện Nước ----");
        System.out.println("Mã Hóa Đơn: " + maHoaDon);
        System.out.println("Ngày Lập: " + java.time.LocalDate.now().toString());
        System.out.println("Mã Khách Hàng: " + khachHang.getMaKhachHang());
        System.out.println("Mã Tháng: " + khachHang.getMaThang());
        System.out.println("Chỉ Số Cũ: " + khachHang.getChiSoCu());
        System.out.println("Chỉ Số Mới: " + khachHang.getChiSoMoi());
        System.out.println("Tổng Tiền: " + khachHang.tinhTienDien());
        if (khachHang instanceof KhachHangDoanhNghiep) {
            KhachHangDoanhNghiep dn = (KhachHangDoanhNghiep) khachHang;
            System.out.println("Hệ Số Nhân: " + dn.getHeSoNhan());
        }
        System.out.println("---------------------------");
    }
}

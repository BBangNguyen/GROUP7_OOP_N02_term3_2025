package QlyTienDien;

interface TinhTien {
    double tinhTienDien();
}


abstract class KhachHang implements TinhTien {
    private String maKhachHang;
    private int maThang;
    private int chiSoCu;
    private int chiSoMoi;

    public KhachHang(String maKhachHang, int maThang, int chiSoCu, int chiSoMoi) {
        this.maKhachHang = maKhachHang;
        this.maThang = maThang;
        this.chiSoCu = chiSoCu;
        this.chiSoMoi = chiSoMoi;
    }

    // Getters and Setters
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public int getMaThang() { return maThang; }
    public void setMaThang(int maThang) { this.maThang = maThang; }
    public int getChiSoCu() { return chiSoCu; }
    public void setChiSoCu(int chiSoCu) { this.chiSoCu = chiSoCu; }
    public int getChiSoMoi() { return chiSoMoi; }
    public void setChiSoMoi(int chiSoMoi) { this.chiSoMoi = chiSoMoi; }
}

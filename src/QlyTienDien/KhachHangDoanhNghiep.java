package QlyTienDien;

class KhachHangDoanhNghiep extends KhachHang {
    private double heSoNhan;

    public KhachHangDoanhNghiep(String maKhachHang, int maThang, int chiSoCu, int chiSoMoi, double heSoNhan) {
        super(maKhachHang, maThang, chiSoCu, chiSoMoi);
        this.heSoNhan = heSoNhan;
    }

    public double getHeSoNhan() { return heSoNhan; }
    public void setHeSoNhan(double heSoNhan) { this.heSoNhan = heSoNhan; }

    @Override
    public double tinhTienDien() {
        int soDienTieuThu = getChiSoMoi() - getChiSoCu();
        double tongTien = 0;

        // Tính theo đơn giá doanh nghiệp
        if(soDienTieuThu <= 100){
            tongTien = soDienTieuThu * 2000;
        } else if(soDienTieuThu <= 200){
            tongTien = 100 * 2000 + (soDienTieuThu - 100) * 2200;
        } else {
            tongTien = 100 * 2000 + 100 * 2200 + (soDienTieuThu - 200) * 2500;
        }

        return tongTien * heSoNhan;
    }
}


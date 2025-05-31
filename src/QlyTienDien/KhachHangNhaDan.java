package QlyTienDien;

class KhachHangNhaDan extends KhachHang {
    public KhachHangNhaDan(String maKhachHang, int maThang, int chiSoCu, int chiSoMoi) {
        super(maKhachHang, maThang, chiSoCu, chiSoMoi);
    }

    @Override
    public double tinhTienDien() {
        int soDienTieuThu = getChiSoMoi() - getChiSoCu();
        double tongTien = 0;

        if(soDienTieuThu <= 50){
            tongTien = soDienTieuThu * 1806;
        } else if(soDienTieuThu <= 100){
            tongTien = 50 * 1806 + (soDienTieuThu - 50) * 1866;
        } else if(soDienTieuThu <= 200){
            tongTien = 50 * 1806 + 50 * 1866 + (soDienTieuThu - 100) * 2167;
        } else if(soDienTieuThu <= 300){
            tongTien = 50 * 1806 + 50 * 1866 + 100 * 2167 + (soDienTieuThu - 200) * 2729;
        } else if(soDienTieuThu <= 400){
            tongTien = 50 * 1806 + 50 * 1866 + 100 * 2167 + 100 * 2729 + (soDienTieuThu - 400) * 3050;
        } else {
            tongTien = 50 * 1806 + 50 * 1866 + 100 * 2167 + 100 * 2729 + 100 * 3050 + (soDienTieuThu - 400) * 3151;
        }

        return tongTien;
    }
}


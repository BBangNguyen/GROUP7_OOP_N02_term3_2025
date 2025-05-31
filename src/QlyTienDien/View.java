package QlyTienDien;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

class View {
    private JFrame frame;
    private JTextField MaKhachHang, MaThang, ChiSoCu, ChiSoMoi, heSoNhan, MaHoaDon, NgayLap;
    private JTable table;
    private DefaultTableModel tableModel;
    private ArrayList<KhachHang> KH;
    private ArrayList<HoaDon> hoaDonList;

    View() {
        KH = new ArrayList<>();
        hoaDonList = new ArrayList<>();
        frame = new JFrame("Quản lý chỉ số điện");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);
        frame.getContentPane().setBackground(new Color(245, 245, 245));
        frame.setLayout(null);
        
        Font font = new Font("Arial", Font.PLAIN, 14);
        Font btnFont = new Font("Arial", Font.BOLD, 14);

        // Các label và text field hiện có
        JLabel lblMaKhachHang = new JLabel("Mã khách hàng:");
        lblMaKhachHang.setBounds(30, 30, 120, 25);
        lblMaKhachHang.setFont(font);
        frame.add(lblMaKhachHang);

        MaKhachHang = new JTextField();
        MaKhachHang.setBounds(150, 30, 150, 25);
        MaKhachHang.setFont(font);
        frame.add(MaKhachHang);

        JLabel lblMaThang = new JLabel("Mã tháng:");
        lblMaThang.setBounds(30, 70, 120, 25);
        lblMaThang.setFont(font);
        frame.add(lblMaThang);

        MaThang = new JTextField();
        MaThang.setBounds(150, 70, 150, 25);
        MaThang.setFont(font);
        frame.add(MaThang);

        JLabel lblChiSoCu = new JLabel("Chỉ số cũ:");
        lblChiSoCu.setBounds(30, 110, 120, 25);
        lblChiSoCu.setFont(font);
        frame.add(lblChiSoCu);

        ChiSoCu = new JTextField();
        ChiSoCu.setBounds(150, 110, 150, 25);
        ChiSoCu.setFont(font);
        frame.add(ChiSoCu);

        JLabel lblChiSoMoi = new JLabel("Chỉ số mới:");
        lblChiSoMoi.setBounds(30, 150, 120, 25);
        lblChiSoMoi.setFont(font);
        frame.add(lblChiSoMoi);

        ChiSoMoi = new JTextField();
        ChiSoMoi.setBounds(150, 150, 150, 25);
        ChiSoMoi.setFont(font);
        frame.add(ChiSoMoi);
        
        // Thêm lựa chọn loại khách hàng
        JLabel lblLoaiKhach = new JLabel("Loại khách hàng:");
        lblLoaiKhach.setBounds(30, 190, 120, 25);
        lblLoaiKhach.setFont(font);
        frame.add(lblLoaiKhach);

        String[] loaiKhachOptions = {"Nhà Dân", "Doanh Nghiệp"};
        JComboBox<String> cboLoaiKhach = new JComboBox<>(loaiKhachOptions);
        cboLoaiKhach.setBounds(150, 190, 150, 25);
        frame.add(cboLoaiKhach);
        
        // Nếu là Doanh Nghiệp thì cần nhập hệ số nhân
        JLabel lblHeSoNhan = new JLabel("Hệ số nhân:");
        lblHeSoNhan.setBounds(30, 230, 120, 25);
        lblHeSoNhan.setFont(font);
        frame.add(lblHeSoNhan);

        heSoNhan = new JTextField();
        heSoNhan.setBounds(150, 230, 150, 25);
        heSoNhan.setFont(font);
        heSoNhan.setEnabled(false); // Mặc định không nhập
        frame.add(heSoNhan);
        
        // Thêm sự kiện để bật/tắt trường hệ số nhân
        cboLoaiKhach.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) cboLoaiKhach.getSelectedItem();
                if(selected.equals("Doanh Nghiệp")) {
                    heSoNhan.setEnabled(true);
                } else {
                    heSoNhan.setEnabled(false);
                    heSoNhan.setText("");
                }
            }
        });
        
        // Thêm các trường cho Hóa Đơn
        JLabel lblMaHoaDon = new JLabel("Mã hóa đơn:");
        lblMaHoaDon.setBounds(30, 270, 120, 25);
        lblMaHoaDon.setFont(font);
        frame.add(lblMaHoaDon);

        MaHoaDon = new JTextField();
        MaHoaDon.setBounds(150, 270, 150, 25);
        MaHoaDon.setFont(font);
        frame.add(MaHoaDon);
        
        JLabel lblNgayLap = new JLabel("Ngày lập:");
        lblNgayLap.setBounds(30, 310, 120, 25);
        lblNgayLap.setFont(font);
        frame.add(lblNgayLap);

        NgayLap = new JTextField();
        NgayLap.setBounds(150, 310, 150, 25);
        NgayLap.setFont(font);
        frame.add(NgayLap);
        
        // Các nút hiện có
        JButton btnThem = new JButton("Thêm");
        btnThem.setBounds(330, 30, 120, 35);
        frame.add(btnThem);

        JButton btnSua = new JButton("Sửa");
        btnSua.setBounds(330, 70, 120, 35);
        frame.add(btnSua);

        JButton btnXoa = new JButton("Xóa");
        btnXoa.setBounds(330, 110, 120, 35);
        frame.add(btnXoa);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(330, 150, 120, 35);
        frame.add(btnReset);

        JButton btnDong = new JButton("Đóng");
        btnDong.setBounds(330, 190, 120, 35);
        frame.add(btnDong);

        JButton btnSapXep = new JButton("Sắp xếp");
        btnSapXep.setBounds(470, 30, 120, 35);
        frame.add(btnSapXep);
        
        JButton btnInHoaDon = new JButton("In Hóa Đơn");
        btnInHoaDon.setBounds(470, 70, 120, 35);
        frame.add(btnInHoaDon);

        // Định nghĩa bảng hiển thị
        tableModel = new DefaultTableModel(new Object[]{"Mã KH", "Mã Tháng", "Chỉ Số Cũ", "Chỉ Số Mới", "Loại KH", "Hệ Số Nhân", "Mã Hóa Đơn", "Ngày Lập", "Tổng Tiền"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 360, 920, 300);
        frame.add(scrollPane);

        // Xử lý sự kiện các nút
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRecord(cboLoaiKhach.getSelectedItem().toString());
            }
        });

        btnSua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRecord();
            }
        });

        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRecord();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields(cboLoaiKhach);
            }
        });

        btnDong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        btnSapXep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapXepTheoMaKhachHang();
            }
        });
        
        btnInHoaDon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inHoaDon();
            }
        });

        // Hiển thị frame
        frame.setVisible(true);
    }

    private void sapXepTheoMaKhachHang(){
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);
    }

    private void addRecord(String loaiKhach) {
        String maKhachHang = MaKhachHang.getText().trim();
        String maThangText = MaThang.getText().trim();
        String chiSoCuText = ChiSoCu.getText().trim();
        String chiSoMoiText = ChiSoMoi.getText().trim();
        String heSoNhanText = heSoNhan.getText().trim();
        String maHoaDon = MaHoaDon.getText().trim();
        String ngayLap = NgayLap.getText().trim();

        if(maKhachHang.isEmpty() || maThangText.isEmpty() || chiSoCuText.isEmpty() || chiSoMoiText.isEmpty() || maHoaDon.isEmpty() || ngayLap.isEmpty()){
            JOptionPane.showMessageDialog(frame, "Vui lòng nhập đầy đủ thông tin!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra mã khách hàng đã tồn tại
        for(KhachHang kh : KH){
            if(kh.getMaKhachHang().equals(maKhachHang)){
                JOptionPane.showMessageDialog(frame, "Mã khách hàng này đã tồn tại! Xin hãy nhập mã khác", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Kiểm tra mã hóa đơn đã tồn tại
        for(int i=0; i<tableModel.getRowCount(); i++) {
            String existingMaHoaDon = (String)tableModel.getValueAt(i, 6);
            if(existingMaHoaDon.equals(maHoaDon)){
                JOptionPane.showMessageDialog(frame, "Mã hóa đơn này đã tồn tại! Xin hãy nhập mã khác", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            int maThang = Integer.parseInt(maThangText);
            int chiSoCu = Integer.parseInt(chiSoCuText);
            int chiSoMoi = Integer.parseInt(chiSoMoiText);

            if(maThang < 1 || maThang > 12){
                JOptionPane.showMessageDialog(frame, "Mã tháng phải nằm trong khoảng 1 đến 12", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(chiSoCu < 0 || chiSoMoi < 0){
                JOptionPane.showMessageDialog(frame, "Chỉ số điện phải là số dương!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(chiSoMoi < chiSoCu){
                JOptionPane.showMessageDialog(frame, "Chỉ số mới phải lớn hơn chỉ số cũ!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            KhachHang kh;
            String loaiKH;
            double hsNhan = 1.0;

            if(loaiKhach.equals("Nhà Dân")){
                kh = new KhachHangNhaDan(maKhachHang, maThang, chiSoCu, chiSoMoi);
                loaiKH = "Nhà Dân";
            } else {
                if(heSoNhanText.isEmpty()){
                    JOptionPane.showMessageDialog(frame, "Vui lòng nhập hệ số nhân cho khách hàng doanh nghiệp!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                hsNhan = Double.parseDouble(heSoNhanText);
                kh = new KhachHangDoanhNghiep(maKhachHang, maThang, chiSoCu, chiSoMoi, hsNhan);
                loaiKH = "Doanh Nghiệp";
            }

            // Tạo hóa đơn
            HoaDon hoaDon = new HoaDon(maHoaDon, ngayLap, kh);
            hoaDonList.add(hoaDon);
            KH.add(kh);

            double tongTien = hoaDon.getKhachHang().tinhTienDien();

            if(loaiKhach.equals("Nhà Dân")){
                tableModel.addRow(new Object[]{maKhachHang, maThang, chiSoCu, chiSoMoi, loaiKH, "-", maHoaDon, ngayLap, tongTien});
            } else {
                tableModel.addRow(new Object[]{maKhachHang, maThang, chiSoCu, chiSoMoi, loaiKH, hsNhan, maHoaDon, ngayLap, tongTien});
            }

            JOptionPane.showMessageDialog(frame, "Thêm khách hàng và hóa đơn thành công!", "Thông báo!", JOptionPane.INFORMATION_MESSAGE);
//            resetFields(null);
        } catch(NumberFormatException e){
            JOptionPane.showMessageDialog(frame, "Vui lòng nhập đúng định dạng số!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String maKhachHang = MaKhachHang.getText().trim();
            String maThangText = MaThang.getText().trim();
            String chiSoCuText = ChiSoCu.getText().trim();
            String chiSoMoiText = ChiSoMoi.getText().trim();
            String heSoNhanText = heSoNhan.getText().trim();
            String maHoaDon = MaHoaDon.getText().trim();
            String ngayLap = NgayLap.getText().trim();

            if(maKhachHang.isEmpty() || maThangText.isEmpty() || chiSoCuText.isEmpty() || chiSoMoiText.isEmpty() || maHoaDon.isEmpty() || ngayLap.isEmpty()){
                JOptionPane.showMessageDialog(frame, "Vui lòng nhập đầy đủ thông tin!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int maThang = Integer.parseInt(maThangText);
                int chiSoCu = Integer.parseInt(chiSoCuText);
                int chiSoMoi = Integer.parseInt(chiSoMoiText);

                if(maThang < 1 || maThang > 12){
                    JOptionPane.showMessageDialog(frame, "Mã tháng phải nằm trong khoảng 1 đến 12", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(chiSoCu < 0 || chiSoMoi < 0){
                    JOptionPane.showMessageDialog(frame, "Chỉ số điện phải là số dương!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(chiSoMoi < chiSoCu){
                    JOptionPane.showMessageDialog(frame, "Chỉ số mới phải lớn hơn chỉ số cũ!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra mã hóa đơn đã tồn tại (nếu thay đổi mã hóa đơn)
                String existingMaHoaDon = (String)tableModel.getValueAt(selectedRow, 6);
                if(!existingMaHoaDon.equals(maHoaDon)){
                    for(int i=0; i<tableModel.getRowCount(); i++) {
                        if(i == selectedRow) continue;
                        String tempMaHoaDon = (String)tableModel.getValueAt(i, 6);
                        if(tempMaHoaDon.equals(maHoaDon)){
                            JOptionPane.showMessageDialog(frame, "Mã hóa đơn này đã tồn tại! Xin hãy nhập mã khác", "Lỗi!", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                // Lấy đối tượng KhachHang và HoaDon tương ứng
                KhachHang kh = KH.get(selectedRow);
                HoaDon hoaDon = hoaDonList.get(selectedRow);

                // Cập nhật thông tin khách hàng
                kh.setMaKhachHang(maKhachHang);
                kh.setMaThang(maThang);
                kh.setChiSoCu(chiSoCu);
                kh.setChiSoMoi(chiSoMoi);

                // Cập nhật hóa đơn
                hoaDon.setMaHoaDon(maHoaDon);
                hoaDon.setNgayLap(ngayLap);

                // Cập nhật thông tin trong bảng
                tableModel.setValueAt(maKhachHang, selectedRow, 0);
                tableModel.setValueAt(maThang, selectedRow, 1);
                tableModel.setValueAt(chiSoCu, selectedRow, 2);
                tableModel.setValueAt(chiSoMoi, selectedRow, 3);
                tableModel.setValueAt(maHoaDon, selectedRow, 6);
                tableModel.setValueAt(ngayLap, selectedRow, 7);
                tableModel.setValueAt(kh.tinhTienDien(), selectedRow, 8);

                // Cập nhật hệ số nhân nếu là Doanh Nghiệp
                if(kh instanceof KhachHangDoanhNghiep){
                    double hsNhan = Double.parseDouble(heSoNhanText);
                    ((KhachHangDoanhNghiep) kh).setHeSoNhan(hsNhan);
                    tableModel.setValueAt(hsNhan, selectedRow, 5);
                } else {
                    tableModel.setValueAt("-", selectedRow, 5);
                }

                JOptionPane.showMessageDialog(frame, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch(NumberFormatException e){
                JOptionPane.showMessageDialog(frame, "Vui lòng nhập đúng định dạng số!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Vui lòng chọn khách hàng để sửa!", "Lỗi!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(frame,"Bạn có chắc chắn muốn xóa khách hàng này không?","Xác nhận xóa",JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                KH.remove(selectedRow);
                hoaDonList.remove(selectedRow);
                tableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Vui lòng chọn khách hàng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields(JComboBox<String> cboLoaiKhach) {
        MaKhachHang.setText("");
        MaThang.setText("");
        ChiSoCu.setText("");
        ChiSoMoi.setText("");
        heSoNhan.setText("");
        MaHoaDon.setText("");
        NgayLap.setText("");
        if(cboLoaiKhach != null){
            cboLoaiKhach.setSelectedIndex(0);
            heSoNhan.setEnabled(false);
        }
    }
    
    private void inHoaDon() {
        // Lấy dòng được chọn
        int selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            // Lấy hóa đơn tương ứng từ danh sách hoaDonList
            HoaDon hoaDon = hoaDonList.get(selectedRow);
            if(hoaDon != null){
                // In hóa đơn ra console
                hoaDon.inHoaDon();
                JOptionPane.showMessageDialog(frame, "Đã in hóa đơn cho khách hàng " + hoaDon.getKhachHang().getMaKhachHang(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Không tìm thấy hóa đơn cho khách hàng đã chọn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Vui lòng chọn hóa đơn để in!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
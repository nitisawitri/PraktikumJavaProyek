package frame;

import helpers.ComboBoxItem;
import helpers.Koneksi;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;
import java.sql.*;

public class KecamatanInputFrame extends JFrame {
    private JPanel mainpanel;
    private JTextField idTextField;
    private JTextField namaTextField;
    private JPanel buttonPanel;
    private JButton batalButton;
    private JButton simpanButton;
    private JComboBox KabupatenComboBox;
    private JRadioButton tipeBRadioButton;
    private JRadioButton tipeARadioButton;
    private JTextField populasiTextField;
    private JTextField luasTextField;
    private JLabel luasLabel;
    private ButtonGroup klasifikasiButtonGroup;

    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public void isikomponen() {
        Connection connection = Koneksi.getConnection();
        String findSQL = "SELECT * FROM kecamatan WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(findSQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namaTextField.setText(rs.getString("nama"));
                int KabupatenId = rs.getInt("kabupaten_id");
                for (int i = 0; 1 < KabupatenComboBox.getItemCount(); i++) {
                    KabupatenComboBox.setSelectedIndex(i);
                    ComboBoxItem item = (ComboBoxItem) KabupatenComboBox.getSelectedItem();
                    if (KabupatenId == item.getValue()) {
                        break;
                    }
                }
                String Klasifikasi = rs.getString("Klasifikasi");
                if (Klasifikasi != null) {
                    if (Klasifikasi.equals("TIPE A")) {
                        tipeARadioButton.setSelected(true);
                    } else if (Klasifikasi.equals("TIPE B")) {
                        tipeBRadioButton.setSelected(true);
                    }
                }
                populasiTextField.setText(String.valueOf(rs.getInt("populasi")));
                luasTextField.setText(String.valueOf(rs.getDouble("luas")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public KecamatanInputFrame() {
        batalButton.addActionListener(e -> {
            dispose();
        });
        simpanButton.addActionListener(e -> {
            String nama = namaTextField.getText();
            if (nama.equals("")) {
                JOptionPane.showMessageDialog(null, "Isi Nama Kabupaten", "Validasi data kosong", JOptionPane.WARNING_MESSAGE);
                namaTextField.requestFocus();
                return;
            }
            ComboBoxItem item = (ComboBoxItem) KabupatenComboBox.getSelectedItem();
            int KabupatenId = item.getValue();
            if (KabupatenId == 0) {
                JOptionPane.showMessageDialog(null, "Pilih Kabupaten", "Validasi Combobox", JOptionPane.WARNING_MESSAGE);
                KabupatenComboBox.requestFocus();
                return;
            }
            String Klafikasi = "";
            if (tipeARadioButton.isSelected()) {
                Klafikasi = "TIPE A";
            } else if (tipeBRadioButton.isSelected()) {
                Klafikasi = "TIPE B";
            } else {
                JOptionPane.showMessageDialog(null, "Pilih Klasifikasi", "Validasi Data Kosong", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(populasiTextField.getText().equals("")){
                populasiTextField.setText("0");
            }
            int populasi = Integer.parseInt(populasiTextField.getText());
            if(populasi == 0){
                JOptionPane.showMessageDialog( null, "Isi Populasi","Validasi Data Kosong", JOptionPane.WARNING_MESSAGE );
                populasiTextField.requestFocus();
                return;
            }

            if(luasTextField.getText().equals("")){
                luasTextField.setText("0");
            }
            double luas = Float.parseFloat(luasTextField.getText());
            if(luas == 0){
                JOptionPane.showMessageDialog( null, "Isi Luas", "Validasi Data Kosong", JOptionPane.WARNING_MESSAGE);
                luasTextField.requestFocus();
                return;
            }
            Connection connection = Koneksi.getConnection();
            PreparedStatement ps;
            try {
                if (id == 0) {
                    String cekSQL = "SELECT * FROM kecamatan WHERE nama = ?";
                    ps = connection.prepareStatement(cekSQL);
                    ps.setString(1, nama);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Data sama sudah ada");
                    } else {
                        String insertSQL = "INSERT INTO kecamatan (id, nama, kabupaten_id, klasifikasi) " + "populasi, luas) " + "VALUES (NULL, ?, ?, ?, ?, ?)";
                        ps = connection.prepareStatement(insertSQL);
                        ps.setString(1, nama);
                        ps.setInt(2, KabupatenId);
                        ps.setString(3, Klafikasi);
                        ps.setInt(4, populasi);
                        ps.setDouble(5, luas);
                        ps.executeUpdate();
                        dispose();
                    }
                } else {
                    String cekSQL = "SELECT * FROM kecamatan WHERE nama = ? AND id != ?";
                    ps = connection.prepareStatement(cekSQL);
                    ps.setString(1, nama);
                    ps.setInt(2, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Data sama sudah ada");

                    } else {
                        String updateSQL = "UPDATE kecamatan SET nama = ?, kabupaten_id = ?, klasifikasi = ? " + "populasi = ?, luas = ? WHERE id = ?";
                        ps = connection.prepareStatement(updateSQL);
                        ps.setString(1, nama);
                        ps.setInt(2, KabupatenId);
                        ps.setString(3, Klafikasi);
                        ps.setInt(4, populasi);
                        ps.setDouble(5, luas);
                        ps.setInt(6, id);
                        ps.executeUpdate();
                        dispose();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        batalButton.addActionListener(e -> dispose());
        kustomisasiKomponen();
        init();
    }

    public void init() {
        setContentPane(mainpanel);
        setTitle("Input Kecamatan");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void kustomisasiKomponen() {
        Connection connection = Koneksi.getConnection();
        String selectSQL = "SELECT * FROM kabupaten ORDER BY nama";
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            KabupatenComboBox.addItem(new ComboBoxItem(0, "Pilih Kabupaten"));
            while (rs.next()) {
                KabupatenComboBox.addItem(new ComboBoxItem(rs.getInt("id"), rs.getString("nama")));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    klasifikasiButtonGroup =new ButtonGroup();
    klasifikasiButtonGroup.add(tipeARadioButton);
    klasifikasiButtonGroup.add(tipeBRadioButton);

        luasLabel.setText("Luas (Km\u00B2)");
        populasiTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        populasiTextField.setText("0");
        populasiTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                populasiTextField.setEnabled((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode() == KeyEvent.VK_RIGHT);
            }
        });
        luasTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        luasTextField.setText("0");
        luasTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {

                luasTextField.setEditable((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode() == KeyEvent.VK_RIGHT || ke.getKeyCode() == KeyEvent.VK_PERIOD);
            }
        });

    }
}

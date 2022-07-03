package frame;

import helpers.Koneksi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class KabupatenViewFrame extends JFrame{
    private JPanel mainPanel;
    private JButton cariButton;
    private JTextField cariTextField;
    private JTable viewTable;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JButton cetakButton;
    private JButton tutupButton;

    public KabupatenViewFrame() {
        cariButton.addActionListener(e -> {
            Connection connection = Koneksi.getConnection();
            String keyword = "%" + cariTextField.getText() + "%";
            String searchSQL = "SELECT * FROM Kabupaten WHERE nama like ?";
            try {
            PreparedStatement ps = connection.prepareStatement(searchSQL);
            ps.setString ( 1, keyword);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel dtm = (DefaultTableModel) viewTable.getModel();
            dtm.setRowCount(0);
            Object[] row = new Object[2];
            while (rs.next()){
                row[0] = rs.getInt( "id");
                row[1] = rs.getString( "nama");
                dtm.addRow(row);
            }
        }catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        });

        hapusButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if(barisTerpilih < 0) {
                JOptionPane.showMessageDialog(null, "pilih data dulu");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(null, "Yakin mau hapus", "konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if(pilihan ==0){
                TableModel tm = viewTable.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih, 0). toString());
                Connection connection = Koneksi.getConnection();
                String deleteSQL = "DELETE FROM Kabupaten WHERE id = ? ";
                try {
                    PreparedStatement ps = connection.prepareStatement(deleteSQL);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }catch (SQLException ex){
                    throw new RuntimeException(ex);
                }
            }
        });
        tutupButton.addActionListener(e -> {
            dispose();
        });
        batalButton.addActionListener(e -> {
            isiTabel();
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                isiTabel();
            }
        });
        isiTabel();
        init();

    }
    public void init() {
        setContentPane(mainPanel);
        setTitle("Data Kabupaten");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTabel(){
        Connection connection = Koneksi.getConnection();
        String selectSQl = "SELECT * FROM Kabupaten";
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(selectSQl);
            String header[] = {"id","Nama kabupaten"};
            DefaultTableModel dtm = new DefaultTableModel(header,0);
            viewTable.setModel(dtm);
            Object[] row = new Object[2];
            while (rs.next()){
                row[0] = rs.getInt( "id");
                row[1] = rs.getString(  "nama");
                dtm.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

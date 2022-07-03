package frame;

import helpers.Koneksi;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class KecamatanViewFrame extends JFrame{
    private JPanel mainpanel;
    private JPanel caripanel;
    private JScrollPane viewScrollpanel;
    private JPanel buttonPanel;
    private JTextField cariTextField;
    private JButton cariButton;
    private JTable viewtable;
    private JButton tamahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JButton cetakButton;
    private JButton tutupButton;

    public KecamatanViewFrame() {
        ubahButton.addActionListener(e -> {
            int barisTerpilih = viewtable.getSelectedRow();
            if(barisTerpilih < 0) {
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            TableModel tm = viewtable.getModel();
            int id = Integer.parseInt(tm.getValueAt(barisTerpilih, 0).toString());
            KecamatanInputFrame inputFrame = new KecamatanInputFrame();
            inputFrame.setId(id);
            inputFrame.isikomponen();
            inputFrame.setVisible(true);
        });
        tamahButton.addActionListener(e -> {
            KecamatanInputFrame inputFrame = new KecamatanInputFrame();
            inputFrame.setVisible(true);
        });
        cariButton.addActionListener(e -> {
            if(cariTextField.getText().equals("")){
                JOptionPane.showMessageDialog( null, "Isi kata kunci pencarian", "Validasi kata kunci kosong", JOptionPane.WARNING_MESSAGE);
                cariTextField.requestFocus();
                return;
            }
            Connection connection = Koneksi.getConnection();
            String keyword = "%" + cariTextField.getText() + "%";
            String searchSQL = "SELECT K.*,B.nama AS nama_kabupaten " +"FROM kecamatan K" +"LEFT JOIN kabupaten B ON K.kabupaten_id = B.id" + "WHERE K.nama like ? OR B.nama like?";
            try {
                PreparedStatement ps = connection.prepareStatement(searchSQL);
                ps.setString ( 1, keyword);
                ps.setString(2, keyword);
                ResultSet rs = ps.executeQuery();
                DefaultTableModel dtm = (DefaultTableModel) viewtable.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[6];
                while (rs.next()){
                    row[0] = rs.getInt( "id");
                    row[1] = rs.getString( "nama");
                    row[2] = rs.getString("nama_kabupaten");
                    row[3] = rs.getString("Klasifikasi");
                    row[4] = rs.getInt("populasi");
                    row[5] = rs.getDouble("luas");
                    dtm.addRow(row);
                }
            }catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });

        hapusButton.addActionListener(e -> {
            int barisTerpilih = viewtable.getSelectedRow();
            if(barisTerpilih < 0) {
                JOptionPane.showMessageDialog(null, "pilih data dulu");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(null, "Yakin mau hapus", "konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if(pilihan ==0){
                TableModel tm = viewtable.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih, 0). toString());
                Connection connection = Koneksi.getConnection();
                String deleteSQL = "DELETE FROM kecamatan WHERE id = ? ";
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
        setContentPane(mainpanel);
        setTitle("Data Kecamatan");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTabel(){
        Connection connection = Koneksi.getConnection();
        String selectSQl = "SELECT K.*,B.nama AS nama_kabupaten FROM kecamatan K " + "LEFT JOIN kabupaten B ON K.kabupaten_id = B.id";
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(selectSQl);
            String header[] = {"id","Nama kecamatan", "Nama kabupaten", "Klasifikasi", "Populasi", "Luas"};
            DefaultTableModel dtm = new DefaultTableModel(header,0);
            viewtable.setModel(dtm);
            viewtable.getColumnModel().getColumn(0).setMaxWidth(32);
            viewtable.getColumnModel().getColumn(1).setMinWidth(150);
            viewtable.getColumnModel().getColumn(2).setMinWidth(150);

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            viewtable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
            viewtable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

            Object[] row = new Object[6];
            while (rs.next()){

                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                String rowPopulasi = nf.format(rs.getInt("populasi"));
                String rowLuas = String.format("%, .2f", rs.getDouble("luas"));

                row[0] = rs.getInt( "id");
                row[1] = rs.getString(  "nama");
                row[2] = rs.getString( "nama_kabupaten");
                row[3] = rs.getString("Klasifikasi");
                row[4] = rowPopulasi;
                row[5] = rowLuas;
                dtm.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package frame;

import helpers.Koneksi;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KabupatenInputFrame extends JFrame {
    private JPanel mainpanel;
    private JTextField idtextField1;
    private JTextField namatextField;
    private JPanel buttonPanel;
    private JButton batalButton;
    private JButton simpanButton;

  private int id;
  public void setId(int id){
      this.id = id;
  }

  public void isikomponen(){
      Connection connection = Koneksi.getConnection();
      String findSQL = "SELECT * FROM kabupaten WHERE id = ?";
      PreparedStatement ps = null;
      try {
          ps = connection.prepareStatement(findSQL);
          ps.setInt(1, id);
          ResultSet rs = ps.executeQuery();
          if (rs.next()) {
              idtextField1.setText(String.valueOf(rs.getInt("id")));
              namatextField.setText(rs.getString("nama"));
          }
      }catch (SQLException e) {
          throw new RuntimeException(e);
      }
  }

    public KabupatenInputFrame(){
        batalButton.addActionListener(e -> {
            dispose();
        });
        simpanButton.addActionListener(e -> {
            String nama = namatextField.getText();
            Connection connection = Koneksi.getConnection();
            PreparedStatement ps;
            try {
                if (id == 0) {
                    String insertSQL = "INSERT INTO kabupaten VALUES (NULL, ?)";
                    ps = connection.prepareStatement(insertSQL);
                    ps.setString(1, nama);
                    ps.executeUpdate();
                    dispose();
                } else {
                    String updateSQL = "UPDATE kabupaten SET nama = ? WHERE id = ?";
                    ps = connection.prepareStatement(updateSQL);
                    ps.setString(1, nama);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    dispose();
                }
            }catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        init();
    }
    public void init() {
        setContentPane(mainpanel);
        setTitle("Input Kabupaten");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

}

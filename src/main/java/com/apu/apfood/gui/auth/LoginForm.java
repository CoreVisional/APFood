package com.apu.apfood.gui.auth;

import com.apu.apfood.gui.AdminForm;
import com.apu.apfood.gui.VendorForm;
import com.apu.apfood.gui.RunnerForm;
import com.apu.apfood.gui.CustomerForm;
import com.apu.apfood.helpers.GUIHelper;
import com.apu.apfood.helpers.ImageHelper; // Add this import statement
import com.apu.apfood.db.models.User;
import com.apu.apfood.services.UserService;
import java.awt.Font;
import java.util.Map;
import javax.swing.UIManager;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author Bryan
 */
public class LoginForm extends javax.swing.JFrame {

    private Font font;

    // Instantiate helpers classes
    ImageHelper imageHelper = new ImageHelper();

    /**
     * Creates new form LoginForm
     */
    public LoginForm() {
        initComponents();
        initCustomComponents();
    }

    private void initCustomComponents() {
        imageHelper.setFrameIcon(this, "/icons/apu-logo.png");
        GUIHelper.JFrameSetup(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        signInBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        emailInput = new javax.swing.JTextField();
        emailLabel = new javax.swing.JLabel();
        passwordInput = new javax.swing.JPasswordField();
        showPasswordJCheckBox = new javax.swing.JCheckBox();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("APFood");

        signInBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        signInBtn.setText("Sign in");
        signInBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        signInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signInBtnActionPerformed(evt);
            }
        });

        clearBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clearBtn.setText("Clear");
        clearBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Nirmala UI Semilight", 1, 28)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome to APFood");
        jLabel1.setToolTipText("");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        passwordLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        passwordLabel.setText("Password");

        emailInput.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        emailInput.setToolTipText("example@mail.com");
        emailInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailInputActionPerformed(evt);
            }
        });

        emailLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        emailLabel.setText("Email");

        passwordInput.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        passwordInput.setToolTipText("************");

        showPasswordJCheckBox.setText("Show Password");
        showPasswordJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPasswordJCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 169, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(141, 141, 141))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passwordInput, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailInput, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(signInBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showPasswordJCheckBox, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(238, 238, 238))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74)
                .addComponent(emailLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailInput, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(passwordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordInput, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(showPasswordJCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(signInBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(307, Short.MAX_VALUE))
        );

        passwordInput.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void emailInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailInputActionPerformed

    }//GEN-LAST:event_emailInputActionPerformed

    private void signInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signInBtnActionPerformed
        // Get credentials
        String email = UserService.sanitizeEmail(emailInput.getText());
        char[] password = passwordInput.getPassword();

        User userObject = checkCredentials(email, password);
        if (userObject == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userObject.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "Login success! \nClick OK to continue", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new AdminForm(userObject);
        } else if (userObject.getRole().equals("customer")) {
            JOptionPane.showMessageDialog(this, "Login success! \nClick OK to continue", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new CustomerForm(userObject); // Should pass in userObject
        } else if (userObject.getRole().equals("runner")) {
            JOptionPane.showMessageDialog(this, "Login success! \nClick OK to continue", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new RunnerForm(userObject);
        } else if (userObject.getRole().equals("vendor")) {
            JOptionPane.showMessageDialog(this, "Login success! \nClick OK to continue", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new VendorForm(userObject);
        }
    }//GEN-LAST:event_signInBtnActionPerformed


    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        emailInput.setText("");
        passwordInput.setText("");
    }//GEN-LAST:event_clearBtnActionPerformed

    private void showPasswordJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPasswordJCheckBoxActionPerformed
        if (showPasswordJCheckBox.isSelected()) {
            // If the checkbox is checked, show the password
            passwordInput.setEchoChar((char) 0); // Display characters as is (no masking)
        } else {
            // If the checkbox is unchecked, hide the password
            passwordInput.setEchoChar('*'); // Show password as asterisks (masked)
        }
    }//GEN-LAST:event_showPasswordJCheckBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Checks if user exists in text file
    public static User checkCredentials(String emailInput, char[] passwordInput) {
        try {
            FileReader fr = new FileReader("src\\main\\java\\com\\apu\\apfood\\db\\datafiles\\Users.txt");
            BufferedReader br = new BufferedReader(fr);
            br.readLine(); // Skip the first line
            String row;
            while ((row = br.readLine()) != null) {
                String[] rowArray = row.split("\\| ");
                int userId = Integer.parseInt(rowArray[1]);
                String name = rowArray[2];
                String email = rowArray[3];
                char[] password = rowArray[4].toCharArray();
                String role = rowArray[5];
                if (email.equals(emailInput) && Arrays.equals(passwordInput, password)) {
                    if (role.equals("admin")) {
                        return new User(userId, name, email, password, role);
                    } else if (role.equals("customer")) {
                        return new User(userId, name, email, password, role);
                    } else if (role.equals("runner")) {
                        return new User(userId, name, email, password, role);
                    } else if (role.equals("vendor")) {
                        return new User(userId, name, email, password, role);
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearBtn;
    private javax.swing.JTextField emailInput;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField passwordInput;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JCheckBox showPasswordJCheckBox;
    private javax.swing.JButton signInBtn;
    // End of variables declaration//GEN-END:variables
}

package peridot.GUI.dialog.modulesManager;

import peridot.GUI.GUIUtils;
import peridot.GUI.WrapLayout;
import peridot.GUI.component.Button;
import peridot.GUI.component.Dialog;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.GUI.component.*;
import peridot.Global;
import peridot.script.RModule;
import peridot.script.r.Package;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author pentalpha
 */
public class NewPackageDialog extends Dialog {
    public String name = null;
    public String version = null;
    private boolean successful;
    private Package newPackage;

    public NewPackageDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        successful = false;
    }

    public boolean isSuccessful(){
        return successful;
    }

    public String getPackName(){
        return name;
    }

    public String getPackVersion(){
        return version;
    }

    private boolean nameIsValid(){
        boolean validName = name.length() >= 2;
        char lastChar = name.charAt(name.length()-1);
        validName = validName && lastChar != '.';
        for(int i = 0; i < name.length(); i++){
            if(Character.isLetterOrDigit(name.charAt(i)) == false
                    && name.charAt(i) != '.'){
                validName = false;
                break;
            }
        }

        return validName;
    }

    private boolean versionIsValid(){
        for(int i = 0; i < version.length(); i++){
            if(!(Character.isDigit(version.charAt(i))
                    || version.charAt(i) == '.')){
                return false;
            }
        }
        return true;
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Required Package");
        getContentPane().setLayout(new WrapLayout(FlowLayout.CENTER, 5, 8));
        Dimension dialogSize = new Dimension(380, 145);
        this.setPreferredSize(dialogSize);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        Panel namePanel = new Panel();

        nameTextField = new javax.swing.JTextField();
        nameTextField.setText("name");
        nameTextField.setMinimumSize(new java.awt.Dimension(100, 10));
        nameTextField.setName(""); // NOI18N
        nameTextField.setPreferredSize(new java.awt.Dimension(100, 25));

        instructionsLabel = new Label();
        instructionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        instructionsLabel.setText("Name: letters, numbers and dot.");
        instructionsLabel.setPreferredSize(new java.awt.Dimension(210, 15));

        namePanel.add(instructionsLabel);
        namePanel.add(nameTextField);
        getContentPane().add(namePanel);

        Panel versionPanel = new Panel();

        versionTextField = new javax.swing.JTextField();
        versionTextField.setText("1.0.0");
        versionTextField.setMinimumSize(new java.awt.Dimension(100, 10));
        versionTextField.setName(""); // NOI18N
        versionTextField.setPreferredSize(new java.awt.Dimension(100, 25));

        versionInstructionsLabel = new Label();
        versionInstructionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        versionInstructionsLabel.setText("Version: numbers and dot.");
        versionInstructionsLabel.setPreferredSize(new java.awt.Dimension(180, 15));

        versionPanel.add(versionInstructionsLabel);
        versionPanel.add(versionTextField);
        getContentPane().add(versionPanel);

        okButton = new Button();
        okButton.setText("OK");
        okButton.setPreferredSize(new java.awt.Dimension(70, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton = new Button();
        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(70, 25));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        Panel buttonsPanel = new Panel();
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        getContentPane().add(buttonsPanel);

        Dimension loc = GUIUtils.getCenterLocation(dialogSize.width, dialogSize.height);
        setLocation(loc.width, loc.height);

        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.name = this.nameTextField.getText();
        this.version = this.versionTextField.getText();

        if(!nameIsValid()){
            JOptionPane.showMessageDialog(rootPane, "Only letters, numbers and dot are allowed!",
                    "Invalid name!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!versionIsValid()){
            JOptionPane.showMessageDialog(rootPane, "Only numbers and dot are allowed!",
                    "Invalid version!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        successful = true;
        this.newPackage = new Package(name, version);
        this.setVisible(false);
    }

    private javax.swing.JButton okButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel instructionsLabel, versionInstructionsLabel;
    private javax.swing.JTextField nameTextField, versionTextField;
}

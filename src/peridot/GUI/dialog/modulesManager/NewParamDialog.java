package peridot.GUI.dialog.modulesManager;

import java.awt.Dimension;
import javax.swing.ButtonGroup;
import peridot.GUI.component.Label;
import peridot.GUI.component.Dialog;
import peridot.GUI.component.Button;
import javax.swing.JOptionPane;
import peridot.GUI.component.Panel;
import peridot.GUI.component.RadioButton;
import peridot.Global;
import peridot.script.RScript;

/**
 *
 * @author pentalpha
 */
public class NewParamDialog extends Dialog {
    private String name = null;
    private String type = null;
    private boolean successful;
    
    public NewParamDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        this.nameTextField.setText("[Parameter Name]");
        successful = false;
    }
    
    public boolean isSuccessful(){
        return successful;
    }

    public String getParamName(){
        return name;
    }
    
    public String getParamType(){
        return type;
    }
    
    private boolean textIsValid(){
        return Global.stringIsLettersAndDigits(nameTextField.getText());
    }
    
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Parametter");
        getContentPane().setLayout(new java.awt.FlowLayout());
        this.setPreferredSize(new Dimension(400, 155));
        this.setResizable(false);
        
        Panel namePanel = new Panel();
        
        nameTextField = new javax.swing.JTextField();
        nameTextField.setText("[Parameter Name]");
        nameTextField.setMinimumSize(new java.awt.Dimension(100, 10));
        nameTextField.setName(""); // NOI18N
        nameTextField.setPreferredSize(new java.awt.Dimension(200, 25));
        namePanel.add(nameTextField);
        
        instructionsLabel = new Label();
        instructionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        instructionsLabel.setText("Use only: a-z, A-Z and 1-9");
        instructionsLabel.setPreferredSize(new java.awt.Dimension(180, 15));
        namePanel.add(instructionsLabel);
        
        getContentPane().add(namePanel);
        
        optionsPanel = new Panel();
        this.optionsGroup = new ButtonGroup();
        paramOptions = new RadioButton[RScript.availableParamTypes.keySet().size()];
        int i = 0;
        for(String name : RScript.availableParamTypes.keySet()){
            paramOptions[i] = new RadioButton();
            paramOptions[i].setText(name);
            optionsGroup.add(paramOptions[i]);
            optionsPanel.add(paramOptions[i]);
            i++;
        }
        getContentPane().add(optionsPanel);
        
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
        
        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(!textIsValid()){            
            JOptionPane.showMessageDialog(rootPane, "Not a valid name!", 
                    "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int selected = -1;
        for(int i = 0; i < paramOptions.length; i++){
            if(paramOptions[i].isSelected()){
                selected = i;
                break;
            }
        }
        if(selected == -1){
            JOptionPane.showMessageDialog(rootPane, "No Parametter Type selected",
                    "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.name = this.nameTextField.getText();
        this.type = paramOptions[selected].getText();
        successful = true;
        this.setVisible(false);
    }
    
    private RadioButton[] paramOptions;
    private ButtonGroup optionsGroup;
    private Panel optionsPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JTextField nameTextField;
}

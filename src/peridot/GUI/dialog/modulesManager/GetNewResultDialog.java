package peridot.GUI.dialog.modulesManager;

import peridot.GUI.component.BigButton;
import peridot.GUI.component.CheckBox;
import peridot.GUI.component.Dialog;
import peridot.GUI.component.Label;
import peridot.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetNewResultDialog extends Dialog {
    public String name;
    public boolean mandatory;
    private String defaultToolTip = "[result file name here]";
    private String invalidNameToolTip = "[invalid file name!]";

    public GetNewResultDialog(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        this.mandatory = false;
        this.name = null;
        build();
    }

    public GetNewResultDialog(java.awt.Frame parent, boolean modal, String name, boolean mandatory){
        super(parent, modal);
        this.mandatory = mandatory;
        this.name = name;
        build();
    }

    private void build(){
        Dimension dialogSize = new Dimension(250, 140);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        this.setMinimumSize(dialogSize);
        this.setResizable(false);
        this.setTitle("New Result Dialog");
        descriptionLabel = new Label("Insert the name of a script result file: ");

        nameField = new JTextField();
        if(name != null){
            nameField.setText(name);
        }else{
            nameField.setText(defaultToolTip);
        }
        nameField.setPreferredSize(new Dimension(230, 25));
        nameField.addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if(nameField.getText().equals(defaultToolTip)
                        || nameField.getText().equals(invalidNameToolTip))
                {
                    nameField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {

            }
        });

        mandatoryCheckBox = new CheckBox();
        mandatoryCheckBox.setText("Mandatory result");
        if(mandatory){
            mandatoryCheckBox.doClick();
        }

        addButton = new BigButton();
        addButton.setText("Add to results");
        addButton.addActionListener((ActionEvent evt) -> {
            mandatory = (mandatoryCheckBox.isSelected());
            name = nameField.getText();
            try{
                if(name == null){
                    throw new InvalidPathException("","");
                }
                if(name.length() <= 1 || name.equals(invalidNameToolTip) || name.equals(defaultToolTip)){
                    throw new InvalidPathException(name,name);
                }
                Path path = Paths.get(name);
                this.setVisible(false);
            }catch (Exception ex){
                name = null;
                Log.logger.warning("Not a valid file name. Try a different name for your result.");
                nameField.setText(invalidNameToolTip);
            }

        });
        //addButton.setMinimumSize(new Dimension(120, 32));
        addButton.setPreferredSize(new Dimension(120, 35));

        mandatoryCheckBox.requestFocus();

        add(descriptionLabel);
        add(nameField);
        add(mandatoryCheckBox);
        add(addButton);
    }

    public boolean validInfo(){
        return (name != null);
    }

    private JLabel descriptionLabel;
    private JTextField nameField;
    private JCheckBox mandatoryCheckBox;
    private JButton addButton;
}

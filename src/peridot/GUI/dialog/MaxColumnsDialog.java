package peridot.GUI.dialog;

import peridot.Archiver.PeridotConfig;
import peridot.Archiver.Places;
import peridot.GUI.WrapLayout;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.BiggerLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MaxColumnsDialog extends JDialog {
    public MaxColumnsDialog(java.awt.Frame parent){
        super(parent, true);
        initComponents();
    }

    private void initComponents(){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(590, 180));
        setMinimumSize(this.getPreferredSize());
        getContentPane().setLayout(new WrapLayout(java.awt.FlowLayout.CENTER, 0, 12));
        this.setResizable(false);
        setLocationRelativeTo(null);
        String question = "Your data is too big";
        this.setTitle(question);
        titleLabel = new peridot.GUI.component.BiggerLabel("This software was design for small analysis on personal computers");
        add(titleLabel);
        add(new BigLabel("<html>R-Peridot has a limit of 100 columns in expression files</html>"));
        String contactLink = PeridotConfig.get().rPeridotWebSite + "about.html?contact=True&from=gui";
        JLabel contactLabel = new BigLabel("<html>Please "
                + "<a href=\\\"" + contactLink + "\\\">contact</a>"
                + " us for requests for analyzes on large amounts of data</html>");
        contactLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(contactLink));
                } catch (URISyntaxException | IOException ex) {
                    //It looks like there's a problem
                }
            }
        });
        add(contactLabel);
        okButton = new peridot.GUI.component.BigButton();
        okButton.setPreferredSize(new Dimension(200,40));
        okButton.setText("OK");
        okButton.addActionListener((java.awt.event.ActionEvent evt) ->
                {
                    setVisible(false);
                }
        );
        add(okButton);
    }

    private BiggerLabel titleLabel;
    private JButton okButton;
}

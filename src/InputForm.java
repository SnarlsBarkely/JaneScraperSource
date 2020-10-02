import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class InputForm {

	public static void main(String[] args) {
		buildForm();


	}
	
	public static void buildForm() {
		JFileChooser f = new JFileChooser();
		    
	    //the panel that we are putting everything on
	    JPanel rootPanel = new JPanel();
	    rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
	    rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    
	    //start storeID panel
	    //for getting the store ID of the iheartjane site to scrape
	    JPanel storeIDPanel = new JPanel();
	    storeIDPanel.add(new JLabel("Store ID:"));
	    JTextField storeID = new JTextField(20);
	    storeID.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(storeID.getText());
			}
	    });
	    storeID.setBorder(new EmptyBorder(10, 0, 10, 10));
	    storeIDPanel.add(storeID);
	    storeIDPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    //end storeID panel
	    
	    
	    
	    //start path panel
	    //This is the button for the output path 
	    JPanel pathBtnPanel = new JPanel();
	    pathBtnPanel.setLayout(new BoxLayout(pathBtnPanel, BoxLayout.X_AXIS));
	    pathBtnPanel.setSize(storeIDPanel.getSize());
	    pathBtnPanel.setAlignmentX(0);
	    //label that the output path will be printed to
	    JLabel filePathLbl = new JLabel("file path");
	    filePathLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
	    //Output Path Selection Button: opens a file selector
	    JButton outputPathBtn = new JButton("Output Path");
	    outputPathBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
	    outputPathBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				f.showSaveDialog(null);
				filePathLbl.setText(f.getSelectedFile().getAbsolutePath());
			}
        });
	    pathBtnPanel.add(outputPathBtn);
	    pathBtnPanel.add(filePathLbl);
	    pathBtnPanel.setAlignmentX(pathBtnPanel.LEFT_ALIGNMENT);
	    pathBtnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    //end path panel
	    
	    
	    
	    
	    //start goBtn panel
	    JPanel goBtnPanel = new JPanel();
	    JButton goBtn = new JButton("Get scrapin'");
	    goBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String[] args = {storeID.getText(), filePathLbl.getText()};
				MainJane.main(args);
			}
        });
	    goBtnPanel.add(goBtn);
	    
	    rootPanel.add(storeIDPanel);
	    rootPanel.add(pathBtnPanel);
	    rootPanel.add(goBtnPanel);
	    
	    JFrame frame = new JFrame();
	    frame.add(rootPanel);
	    frame.pack();
	    frame.setVisible(true);
	    //need to set the actions for the button
	    //https://stackoverflow.com/questions/21879243/how-to-create-on-click-event-for-buttons-in-swing/21879526
	}

}

/**
 * TCSS 305 – Winter 2016. 
 * Assignment 4 - SnapShop
 */

package gui;

import filters.AbstractFilter;
import filters.EdgeDetectFilter;
import filters.EdgeHighlightFilter;
import filters.FlipHorizontalFilter;
import filters.FlipVerticalFilter;
import filters.GrayscaleFilter;
import filters.SharpenFilter;
import filters.SoftenFilter;

import image.PixelImage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This program displays and manipulates images. 
 * @author Hsin-Jung Wang (Cindy)
 * @version 3.0
 */
public class SnapShopGUI extends JFrame {
    /**
     * A generated serial version UID for object Serialization. 
     */
    private static final long serialVersionUID = 9041174495750680145L;

    /**
     * The number of filters.
     */
    private static final int FILTER_NUM  = 7;
    
    /**
     * The number of buttons excluding the number of filters.
     */
    private static final int OTHER_NUM  = 3;
    
    /**
     * The number of columns.
     */
    private static final int COL  = 1;
    
    /**
     * An instant variable that stores an image. 
     */
    private PixelImage myImage;
    
    /**
     * A label for holding the image. 
     */
    private final JLabel myLabel;
    
    /**
     * A file chooser for opening and closing the image
     * and for storing the current directory. 
     */
    private final JFileChooser myFileChooser;
    
    /**
     * A variable storing the current frame.
     */
    private JFrame myFrame;
    
    /**
     * The panel where the image is displayed.
     */
    private final JPanel myImagePanel;
    
    /**
     * The panel where the filter buttons are displayed. 
     */
    private final JPanel myNorthPanel;
    
    /**
     * The list of buttons that contains open, save, and close. 
     */
    private final List<JButton> mySouthButtonList;
    
    /**
     * The list of buttons that contains the filters. 
     */
    private final List<JButton> myNorthButtonList;
    
    /**
     * Initializes the JFrame. 
     * The JFrame's overloaded constructor can set the JFrame title. 
     */
    public SnapShopGUI() {
        super("TCSS 305 SnapShop");
        myNorthPanel = new JPanel(new GridLayout(FILTER_NUM, COL));
        mySouthButtonList = new ArrayList<JButton>();
        myNorthButtonList = new ArrayList<JButton>();
        myImagePanel  = new JPanel(new FlowLayout(FlowLayout.LEADING));
        myFileChooser = new JFileChooser(".");
        myLabel = new JLabel();
    }
    
    /**
     * Starts the GUI. 
     */
    public void start() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        final JPanel outerMainPanel = new JPanel(new BorderLayout());
        final JPanel mainPanel = new JPanel(new BorderLayout());
        final JPanel southPanel = new JPanel(new GridLayout(OTHER_NUM, COL));

        final AbstractFilter[] filterList = {new EdgeDetectFilter(), 
            new EdgeHighlightFilter(), new FlipHorizontalFilter(), 
            new FlipVerticalFilter(), new GrayscaleFilter(), 
            new SharpenFilter(), new SoftenFilter()};
        
        add(outerMainPanel);
        outerMainPanel.add(mainPanel, BorderLayout.WEST);
        outerMainPanel.add(myImagePanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.add(myNorthPanel, BorderLayout.NORTH);
        
        // Construct filter object for each filter. 
        for (int i = 0; i < FILTER_NUM; i++) {
            filterImage(filterList[i]);
        }
        
        mySouthButtonList.add(new JButton("Open...")); 
        mySouthButtonList.add(new JButton("Save As..."));
        mySouthButtonList.add(new JButton("Close Image"));
        
        for (int i = 0; i < OTHER_NUM; i++) {
            southPanel.add(mySouthButtonList.get(i));
            mySouthButtonList.get(i).setEnabled(false);
        }        
        openImage();
        saveImage();
        closeImage();
        pack();
        setVisible(true);
    }

    /**
     * Applies filter to the image. 
     * @param theFilt is a filter from the abstract class. 
     */
    private void filterImage(final AbstractFilter theFilt) {
        final JButton tempButton = new JButton(theFilt.getDescription());
        tempButton.setEnabled(false);
        myNorthButtonList.add(tempButton);
        myNorthPanel.add(tempButton);
        tempButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                theFilt.filter(myImage);
                myLabel.setIcon(new ImageIcon(myImage));  
            } 
        });
    }
    
    /**
     * Opens the image. 
     */
    private void openImage() {
        if (mySouthButtonList.get(0).getText().contains("Open")) {
            mySouthButtonList.get(0).setEnabled(true);
            myFrame = this;
            mySouthButtonList.get(0).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent theActionEvent) {
                        try {
                            myFileChooser.showOpenDialog(null);
                            final File file  = myFileChooser.getSelectedFile();
                            if (file != null) {
                                myImage = PixelImage.load(file);
                                myLabel.repaint();
                                enableButton(); 
                            }
                        } catch (final IOException ex) {
                            showException();
                        }
                    }
                }); 
        }
    }
    
    /**
     * Save the image of the selected image file. 
     * If there is an existing image file that has the same name in 
     * that same folder, then a warning will pop out confirming 
     * whether the user would like to override the existing image file. 
     */
    private void saveImage() {
        mySouthButtonList.get(1).addActionListener(new SaveListener());
    } 
    
    
    /**
     * Display an error message when loading image fails. 
     */
    private void showException() {
        JOptionPane.showMessageDialog(null, 
                                    " The selected file did not contain an image!", 
                                    "Error!", 
                                    JOptionPane.ERROR_MESSAGE);
        System.out.println();
    }
    
    /**
     * Activate all the previously disabled buttons. 
     */
    private void enableButton() {
        myFrame.setMinimumSize(null);
        System.out.println(myImage);
        myLabel.setIcon(new ImageIcon(myImage));
        myImagePanel.add(myLabel);
        pack();
        myFrame.setMinimumSize(getSize());
        for (int j = 0; j < FILTER_NUM; j++) {
            myNorthButtonList.get(j).setEnabled(true);
        } 
        for (int j = 1; j < OTHER_NUM; j++) {
            mySouthButtonList.get(j).setEnabled(true);
        }
    }
    
    /**
     * Close the image and reset the size of the frame 
     * to the original menu size. 
     */
    private void closeImage() {
        if (mySouthButtonList.get(2).getText().contains("Close")) {
            mySouthButtonList.get(2).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent theEvent) {
                    myLabel.setIcon(null);
                    setMinimumSize(new Dimension(0, 0));
                    pack();
                    for (int j = 0; j < FILTER_NUM; j++) {
                        myNorthButtonList.get(j).setEnabled(false);
                    } 
                    for (int j = 1; j < OTHER_NUM; j++) {
                        mySouthButtonList.get(j).setEnabled(false);
                    }
                }
            });
        }
    }
    
    /**
     * Provide the user two options 
     * (cancel or replace) to choose from.
     * @author Hsin-Jung Wang
     */
    class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent theEvent) {
            myFileChooser.showSaveDialog(null);
            try {
                final File file = myFileChooser.getSelectedFile();
                if (file != null) {
                    int n = 0;
                    if (file.exists()) {
                        final Object[] options = {"Cancel", "Replace"};
                        n = JOptionPane.showOptionDialog(null, 
                                           "\"" + file.getName()
                                          + "\" already exist. Do you want to replace it?",
                                          null, JOptionPane.OK_CANCEL_OPTION,
                                          JOptionPane.WARNING_MESSAGE, 
                                          null, options, options[1]);
                    } else {
                        n = 1;
                    }
                    if (n == 1) {
                        myImage.save(file);
                    }
                } 
            } catch (final IOException e) {
                showException();
            }  
        }
    }

}
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class CYM extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    
    // Screen
    private Toolkit toolkit =  Toolkit.getDefaultToolkit();
    private Dimension dimensionScreen_ = toolkit.getScreenSize();
    
    // Button parameters
    private final String CHOOSE_NAME = "CHOOSE";
    private final String VIEW_PREV_NAME = "VIEW PREV";
    private final String VIEW_NAME = "VIEW";
    private final String SELECTED_NAME = "SELECTED";
    private final String VIEW_NEXT_NAME = "VIEW NEXT";
    private final String UPLOAD_NAME = "UPLOAD";
    
    protected JPanel mainPanel_ = new JPanel();
    protected JButton chooseButton_ = new JButton(CHOOSE_NAME);
    protected JButton viewPrevButton_ = new JButton(VIEW_PREV_NAME);
    protected JButton viewButton_ = new JButton(" ");
    protected JButton selectedButton_ = new JButton(" ");
    protected JButton viewNextButton_ = new JButton(VIEW_NEXT_NAME);
    protected JButton uploadButton_ = new JButton(UPLOAD_NAME);
    
    protected int viewCount_ = 0;
    protected int selectedIndex = 0;
    
    // Image parameters
    private final int IMAGE_SETS = 15;  // Must be greater than 0
    private final int IMAGES_VIEW = 5;  // Must be between 2 and 8
    private final int IMAGES_MAX = IMAGE_SETS*IMAGES_VIEW;
    
    private BufferedImage images_[] = new BufferedImage[IMAGES_MAX];
    private Rectangle imageRectangle_ = new Rectangle(dimensionScreen_);
    private Robot imageRobot_;
    
    // View parameters
    // 80% of the width, 80% of the height
    private Dimension dimensionFrame_ = new Dimension(9*(dimensionScreen_.width/10), 9*(dimensionScreen_.height/10));
    // 100% of the width, 10% of the height
    private Dimension dimensionBar_ = new Dimension(dimensionScreen_.width, dimensionScreen_.height/10);
    
    private JFrame viewFrame = new JFrame();
    private JLabel photographLabel = new JLabel();
    private JToolBar buttonBar = new JToolBar();
    
    public CYM() 
    {
        mainPanel_.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        
        chooseButton_.setVerticalTextPosition(AbstractButton.CENTER);
        chooseButton_.setHorizontalTextPosition(AbstractButton.LEADING);
        //chooseButton_.setMnemonic(KeyEvent.VK_C);
        chooseButton_.setActionCommand(CHOOSE_NAME);
        chooseButton_.setToolTipText("Click this button to capture.");
        chooseButton_.addActionListener(this);
        chooseButton_.setEnabled(true);
        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel_.add(chooseButton_, constraints);
        
        viewPrevButton_.setVerticalTextPosition(AbstractButton.CENTER);
        viewPrevButton_.setHorizontalTextPosition(AbstractButton.LEADING);
        //viewPrevButton_.setMnemonic(KeyEvent.VK_P);
        viewPrevButton_.setActionCommand(VIEW_PREV_NAME);
        viewPrevButton_.setToolTipText("Click this button to view previous " + IMAGES_VIEW + " captures.");
        viewPrevButton_.addActionListener(this);
        viewPrevButton_.setEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 4;
        mainPanel_.add(viewPrevButton_, constraints);
        
        viewButton_.setVerticalTextPosition(AbstractButton.CENTER);
        viewButton_.setHorizontalTextPosition(AbstractButton.LEADING);
        //viewButton_.setMnemonic(KeyEvent.VK_V);
        viewButton_.setActionCommand(VIEW_NAME);
        viewButton_.setEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        mainPanel_.add(viewButton_, constraints);
        constraints.gridwidth = 1;
        
        selectedButton_.setVerticalTextPosition(AbstractButton.CENTER);
        selectedButton_.setHorizontalTextPosition(AbstractButton.LEADING);
        //selectedButton_.setMnemonic(KeyEvent.VK_S);
        selectedButton_.setActionCommand(SELECTED_NAME);
        selectedButton_.setEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        mainPanel_.add(selectedButton_, constraints);
        constraints.gridwidth = 1;
        
        viewNextButton_.setVerticalTextPosition(AbstractButton.CENTER);
        viewNextButton_.setHorizontalTextPosition(AbstractButton.LEADING);
        //viewNextButton_.setMnemonic(KeyEvent.VK_N);
        viewNextButton_.setActionCommand(VIEW_NEXT_NAME);
        viewNextButton_.setToolTipText("Click this button to view next " + IMAGES_VIEW + " captures.");
        viewNextButton_.addActionListener(this);
        viewNextButton_.setEnabled(false);
        constraints.gridx = 1;
        constraints.gridy = 4;
        mainPanel_.add(viewNextButton_, constraints);
        
        uploadButton_.setVerticalTextPosition(AbstractButton.CENTER);
        uploadButton_.setHorizontalTextPosition(AbstractButton.LEADING);
        //uploadButton_.setMnemonic(KeyEvent.VK_U);
        uploadButton_.setActionCommand(UPLOAD_NAME);
        uploadButton_.setToolTipText("Click this button to upload capture.");
        uploadButton_.addActionListener(this);
        uploadButton_.setEnabled(false);
        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel_.add(uploadButton_, constraints);
        
        mainPanel_.setOpaque(true); //content panes must be opaque
        
        // Initialize image robot
        try
        {
            imageRobot_ = new Robot();
        }
        catch (AWTException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.setTitle("ChooseYourMoment");
        
        // A label for displaying the pictures
        photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
        photographLabel.setHorizontalTextPosition(JLabel.CENTER);
        photographLabel.setHorizontalAlignment(JLabel.CENTER);
        photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        buttonBar.setPreferredSize(dimensionBar_);
        // We add two glue components. Later in process() we will add thumbnail buttons
        // to the toolbar inbetween thease glue compoents. This will center the
        // buttons in the toolbar.
        buttonBar.add(Box.createGlue());
        buttonBar.add(Box.createGlue());
        
        viewFrame.add(buttonBar, BorderLayout.SOUTH);
        viewFrame.add(photographLabel, BorderLayout.CENTER);
        
        viewFrame.setSize(dimensionFrame_);
        
        // this centers the frame on the screen
        viewFrame.setLocationRelativeTo(null);
        
        viewFrame.setVisible(false);
        
        //Create and set up the window.
        //JFrame frame = new JFrame("CYMApp");
        setContentPane(mainPanel_);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Display the window.
        pack();
        setAlwaysOnTop(true);
        setResizable(false);
        setVisible(true);
        setFocusable(false);
    }
    
    /**
     * SwingWorker class that loads the images a background thread and calls publish
     * when a new one is ready to be displayed.
     *
     * We use Void as the first SwingWroker param as we do not need to return
     * anything from doInBackground().
     */
    private void loadimages()
    {
        photographLabel.setIcon(null);
        while(buttonBar.getComponentCount() > 2)
        {
            buttonBar.remove(buttonBar.getComponent(1));
        }
        
        viewFrame.setVisible(true);
        
        int imagesStart = viewCount_*IMAGES_VIEW;
        int imagesEnd = imagesStart + IMAGES_VIEW;
        for (int i = imagesStart; i < imagesEnd; i++)
        {
            ImageIcon icon = new ImageIcon(images_[i]);
            ThumbnailAction thumbAction;
            ImageIcon mainIcon = new ImageIcon(scaleImage(icon.getImage(), photographLabel.getWidth(), photographLabel.getHeight()));
            if (i == imagesStart)
            {
                photographLabel.setIcon(mainIcon);
                selectedIndex = i;
            }
            ImageIcon thumbnailIcon = new ImageIcon(scaleImage(icon.getImage(), buttonBar.getWidth(), buttonBar.getHeight()));
            thumbAction = new ThumbnailAction(mainIcon, thumbnailIcon, i);
            JButton thumbButton = new JButton(thumbAction);
            buttonBar.add(thumbButton, buttonBar.getComponentCount() - 1);
        }
    };
    
    /**
     * Action class that shows the image specified in it's constructor.
     */
    private class ThumbnailAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        
        /**
         *The icon if the full image we want to display.
         */
        private Icon displayPhoto;
        
        private int photoIndex;
        
        /**
         * @param Icon - The full size photo to show in the button.
         * @param Icon - The thumbnail to show in the button.
         * @param String - The descriptioon of the icon.
         */
        public ThumbnailAction(Icon photo, Icon thumb, int index)
        {
            displayPhoto = photo;
            
            photoIndex = index;
            
            // The LARGE_ICON_KEY is the key for setting the
            // icon when an Action is applied to a button.
            putValue(LARGE_ICON_KEY, thumb);
        }
        
        /**
         * Shows the full image in the main area and sets the application title.
         */
        public void actionPerformed(ActionEvent e)
        {
            photographLabel.setIcon(displayPhoto);
            selectedIndex = photoIndex;
            updateSelectedText();
        }
    }
    
    private BufferedImage scaleImage(Image sourceImage, int newWidth, int newHeight)
    {
        // Make sure the aspect ratio is maintained, so the image is not distorted
        double newRatio = (double) newWidth / (double) newHeight;
        int sourceImageWidth = sourceImage.getWidth(null);
        int sourceImageHeight = sourceImage.getHeight(null);
        double sourceRatio = (double) sourceImageWidth / (double) sourceImageHeight;
        
        if (newRatio < sourceRatio)
        {
            newHeight = (int) (newWidth / sourceRatio);
        }
        else
        {
            newWidth = (int) (newHeight * sourceRatio);
        }
        
        // Draw the scaled image
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(sourceImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        return newImage;
    }
    
    public void actionPerformed(ActionEvent e) 
    {
        if (CHOOSE_NAME.equals(e.getActionCommand())) 
        {
            //viewFrame.toBack();
            //viewFrame.repaint();
            //viewFrame.validate();
            toBack();
            repaint();
            validate();
            
            // Capture images
            for (int i = 0; i < IMAGES_MAX; i++)
            {
                images_[i] = imageRobot_.createScreenCapture(imageRectangle_);
            }
            
            viewCount_ = 0;
            
            viewPrevButton_.setEnabled(false);
            
            if (IMAGE_SETS > 1)
                viewNextButton_.setEnabled(true);
            else
                viewNextButton_.setEnabled(false);
        }
        else if (VIEW_NEXT_NAME.equals(e.getActionCommand())) 
        {
            if (viewCount_ < IMAGE_SETS - 1)
                viewCount_++;
            
            if (viewCount_ >= IMAGE_SETS - 1)
                viewNextButton_.setEnabled(false);
            
            viewPrevButton_.setEnabled(true);
        }
        else if (VIEW_PREV_NAME.equals(e.getActionCommand())) 
        {
            if (viewCount_ > 0)
                viewCount_--;
            
            if (viewCount_ < IMAGE_SETS - 1)
                viewNextButton_.setEnabled(true);
            
            if (viewCount_ <= 0)
                viewPrevButton_.setEnabled(false);
        }
        else if (UPLOAD_NAME.equals(e.getActionCommand())) 
        {
            System.out.println("Uploading index: " + selectedIndex);
        }

        if (CHOOSE_NAME.equals(e.getActionCommand()) || 
            VIEW_NEXT_NAME.equals(e.getActionCommand()) || 
            VIEW_PREV_NAME.equals(e.getActionCommand())) 
        {
            uploadButton_.setEnabled(false);
            
            loadimages();
            updateViewText();
            updateSelectedText();
            
            //viewFrame.toFront();
            //viewFrame.repaint();
            //viewFrame.paint(getGraphics());
            //viewFrame.update(getGraphics());
            //viewFrame.validate();
            toFront();
            //repaint();
            //paint(getGraphics());
            update(getGraphics());
            validate();
            mainPanel_.revalidate();
            
            //try {Thread.sleep(100);}
            //catch(InterruptedException ex) {}
            
            uploadButton_.setEnabled(true);
        }
    }
    
    private void updateViewText()
    {
        int imagesStart = viewCount_*IMAGES_VIEW + 1;
        int imagesEnd = imagesStart + IMAGES_VIEW - 1;
        viewButton_.setText("Viewing captures " +imagesStart + " - " + 
                             imagesEnd + " of " + IMAGES_MAX + ".");
    }
    
    private void updateSelectedText()
    {
        selectedButton_.setText("Capture " + (selectedIndex + 1) + " selected.");
    }
}
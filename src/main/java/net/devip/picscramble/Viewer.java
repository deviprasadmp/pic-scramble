package net.devip.picscramble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

final class Viewer {
    
    private static final Logger log = LoggerFactory.getLogger(Viewer.class);
    
    private static final class ImagePanel extends JPanel {
        
        private final BufferedImage bufferedImage;
        
        private ImagePanel(BufferedImage bufferedImage) {
            
            this.bufferedImage = bufferedImage;
        }
        
        @Override
        public void paintComponent(Graphics graphics) {
    
            super.paintComponent(graphics);
            
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, width, height);
    
            if (width > 768) {
        
                width = 768;
            }
    
            if (height > 1024) {
        
                height = 1024;
            }
            
            graphics.drawImage(bufferedImage, 0, 0, width, height, null);

//            graphics.setColor(Color.RED);
//            graphics.drawRect(0, 0, width, height);
            
            graphics.dispose();
        }
    }
    
    public static void show(BufferedImage bufferedImage, boolean isModal) {
        
        ImagePanel imagePanel = new ImagePanel(bufferedImage);
        
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
    
        if (width > 768) {
        
            width = 768;
        }
    
        if (height > 1024) {
        
            height = 1024;
        }
        
        JDialog jDialog = new JDialog();
        jDialog.setModal(isModal);
        
        jDialog.setContentPane(imagePanel);
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jDialog.setSize(width, height);
        jDialog.setVisible(true);
    }
}

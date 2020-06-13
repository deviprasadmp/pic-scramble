package net.devip.picscramble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

final class ScrambleMain {
    
    private static final Logger log = LoggerFactory.getLogger(ScrambleMain.class);
    
    
    public void scramble() {
        
        File[] imageFiles = new File("/home/devip/temp").listFiles();
        
        for (File imageFile : imageFiles) {
            
            String imageFileName = imageFile.getName()
                                            .toLowerCase();
            if ((!imageFileName.contains("-scrambled") && !imageFileName.contains("-unscrambled"))
                && (imageFileName.endsWith(".jpg") || imageFileName.endsWith(".png"))) {
                
                new Scrambler(imageFile).scramble();
            }
        }
    }
    
    public void unscramble() {
        
        File[] imageFiles = new File("/home/devip/temp").listFiles();
        
        for (File imageFile : imageFiles) {
            
            String imageFileName = imageFile.getName()
                                            .toLowerCase();
            if (imageFileName.contains("-scrambled")) {
                
                new Scrambler(imageFile).unscramble();
            }
        }
    }
    
    public static void main(String[] args) {
    
    
        ScrambleMain scrambleMain = new ScrambleMain();
        scrambleMain.scramble();
        scrambleMain.unscramble();
    }
}

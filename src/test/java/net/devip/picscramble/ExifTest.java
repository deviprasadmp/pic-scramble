package net.devip.picscramble;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class ExifTest {
    
    private static final Logger log = LoggerFactory.getLogger(ExifTest.class);
    
    @BeforeEach
    public void setUp() {
        
    }
    
    @AfterEach
    public void tearDown() {
        
    }
    
    @Test
    public void test() {
        
        assertThat(1).isEqualTo(1);
    }
    
    
    @Test
    void readApplicationNotes() {
        
        File jpegFile = new File("/home/devip/temp/animal-eyes-big-dangerous-87403-scrambled.jpg");
        Exif exif = new Exif(jpegFile);
        exif.readApplicationNotes();
    }
}
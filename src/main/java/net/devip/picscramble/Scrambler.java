package net.devip.picscramble;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

final class Scrambler {
    
    private static final Logger log = LoggerFactory.getLogger(Scrambler.class);
    
    private final File inFile;
    
    private final Exif exif;
    
    public Scrambler(File inFile) {
        
        this.inFile = inFile;
        this.exif = new Exif();
    }
    
    private BufferedImage rotate(BufferedImage orig, double radians) {
        
        int width = orig.getWidth();
        Validate.isTrue(width % 2 == 0, "Width " + width + " is odd");
        
        int height = orig.getHeight();
        Validate.isTrue(height % 2 == 0, "Height " + height + "is odd");
        
        BufferedImage rotated = new BufferedImage(width, height, orig.getType());
        Graphics2D rotatedGraphics = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        
        int pivotX = width / 2;
        int pivotY = height / 2;
        
        at.rotate(radians, pivotX, pivotY);
        rotatedGraphics.setTransform(at);
        rotatedGraphics.drawImage(orig, 0, 0, null);
        rotatedGraphics.dispose();
        
        return rotated;
    }
    
    private BufferedImage rotateTiles(BufferedImage origImage, int tileSideLen, List<Double> rotationSeqRadians) {
        
        int width = origImage.getWidth();
        log.info("width={}", width);
        
        int height = origImage.getHeight();
        log.info("height={}", height);
        
        BufferedImage rotatedImage = new BufferedImage(width, height, origImage.getType());
        Graphics2D scrambledGraphics = rotatedImage.createGraphics();
        
        int rotationSeqIndex = 0;
        for (int x = 0; x < width; x += tileSideLen) {
    
            for (int y = 0; y < height; y += tileSideLen) {
        
                int tileWidth = tileSideLen;
                if ((x + tileWidth) > width) {
    
                    tileWidth = width - x;
                }
        
                int tileHeight = tileSideLen;
                if ((y + tileHeight) > height) {
    
                    tileHeight = height - y;
                }
        
                BufferedImage origTile = origImage.getSubimage(x, y, tileWidth, tileHeight);
        
                if (tileWidth == tileHeight) {
            
                    double rotationRadians = rotationSeqRadians.get(rotationSeqIndex);
                    BufferedImage rotatedTile = rotate(origTile, rotationRadians);
                    scrambledGraphics.drawImage(rotatedTile, x, y, null);
                    rotationSeqIndex = (rotationSeqIndex + 1) % rotationSeqRadians.size();
                } else {
            
                    scrambledGraphics.drawImage(origTile, x, y, null);
                }
            }
        }
        
        scrambledGraphics.dispose();
        
        return rotatedImage;
    }
    
    private BufferedImage scrambleInternal() throws IOException {
        
        log.info("");
        log.info("=============================================");
        log.info("Scrambling {}", inFile.getName());
        
        BufferedImage origImage = ImageIO.read(inFile);
        
        int width = origImage.getWidth();
        log.info("width={}", width);
        
        int height = origImage.getHeight();
        log.info("height={}", height);
        
        // Divide the width into at least 20 parts
        int maxDivisor = width / 32;
        
        // Divide the width into at most 100 parts
        int minDivisor = width / 100;
        
        log.info("minDivisor={}", minDivisor);
        log.info("maxDivisor={}", maxDivisor);
        
        int tileSideLen = Math.randomCommonEvenDivisor(width, height, minDivisor, maxDivisor);
        exif.setTileSideLen(tileSideLen);
        log.info("tileSideLen={}", tileSideLen);
        
        List<Integer> rotationSeqDegrees = Math.randomRotationSequence();
        exif.setRotationSeqDegrees(rotationSeqDegrees);
        log.info("rotationSeqDegrees={}", rotationSeqDegrees);
        
        List<Double> rotationSeqRadians =
                rotationSeqDegrees.stream()
                                  .map(d -> Math.radians(d))
                                  .collect(Collectors.toList());
        log.info("rotationSeqRadians={}", rotationSeqRadians);
        
        BufferedImage scrambledImage = rotateTiles(origImage, tileSideLen, rotationSeqRadians);
        return scrambledImage;
    }
    
    private BufferedImage unscrambleInternal() throws IOException {
        
        log.info("");
        log.info("=============================================");
        log.info("Unscrambling {}", inFile.getName());
        
        BufferedImage origImage = ImageIO.read(inFile);
        
        exif.readApplicationNotes(inFile);
        
        int tileSideLen = exif.getTileSideLen();
        
        List<Integer> rotationSeqDegrees = exif.getRotationSeqDegrees();
        log.info("rotationSeqDegrees={}", rotationSeqDegrees);
        
        List<Double> rotationSeqRadiansRev =
                rotationSeqDegrees.stream()
                                  .map(d -> Math.radians(-d))
                                  .collect(Collectors.toList());
        log.info("rotationSeqRadiansRev={}", rotationSeqRadiansRev);
        
        BufferedImage unscrambledImage = rotateTiles(origImage, tileSideLen, rotationSeqRadiansRev);
        return unscrambledImage;
    }
    
    private void write(BufferedImage image, String suffix) throws IOException {
        
        String outFileName = inFile.getName();
        int lastDotAt = outFileName.lastIndexOf('.');
        if (lastDotAt > -1) {
            
            outFileName = outFileName.substring(0, lastDotAt);
        }
        
        outFileName = outFileName.replace("-scrambled", "") + "-" + suffix + ".jpg";
        
        File outFile = new File(inFile.getParentFile(), outFileName);
        ImageIO.write(image, "jpg", outFile);
        exif.write(outFile);
    }
    
    public void scramble() {
        
        try {
    
            BufferedImage scrambled = scrambleInternal();
            write(scrambled, "scrambled");
        } catch (Exception ex) {
            
            throw new RuntimeException(ex);
        }
    }
    
    public void unscramble() {
        
        try {
            
            BufferedImage unscrambled = unscrambleInternal();
            write(unscrambled, "unscrambled");
        } catch (Exception ex) {
            
            throw new RuntimeException(ex);
        }
    }
}

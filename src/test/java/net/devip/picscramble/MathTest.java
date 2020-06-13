package net.devip.picscramble;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

final class MathTest {
    
    private static final Logger log = LoggerFactory.getLogger(MathTest.class);
    
    @Test
    void evenDivisors() {
    
        List<Integer> widthDivisors = Math.evenDivisors(3000, 32, 64);
        log.info("w={}", widthDivisors);
    
        List<Integer> heightDivisors = Math.evenDivisors(4000, 32, 64);
        log.info("h={}", heightDivisors);
    }
    
    @Test
    void commonEvenDivisors() {
    
        List<Integer> commonDivisors = Math.commonEvenDivisors(3000, 4000, 32, 64);
        log.info("commonEvenDivisors={}", commonDivisors);
    
        commonDivisors = Math.commonEvenDivisors(1024, 768, 32, 64);
        log.info("commonEvenDivisors={}", commonDivisors);
    }
    
    @Test
    void randomCommonEvenDivisor() {
    
        int randomCommonDivisor = Math.randomCommonEvenDivisor(3000, 4000, 32, 64);
        log.info("randomCommonEvenDivisor={}", randomCommonDivisor);
    
        randomCommonDivisor = Math.randomCommonEvenDivisor(1024, 768, 32, 64);
        log.info("randomCommonEvenDivisor={}", randomCommonDivisor);
    }
    
    @Test
    void randomRotationSequence() {
    
        List<Integer> randomRotationSequence = Math.randomRotationSequence();
        log.info("randomRotationSequence={}", randomRotationSequence);
    }
    
    @Test
    void randomPoint() {
    
        boolean[][] grid = new boolean[7][13];
    
        // Use coordinate system - x, y.  *Not* matrix system - rows, cols
        for (int x = 0; x < 7; x++) {
    
            for (int y = 0; y < 13; y++) {
        
                Point point = Math.randomFreeTile(grid);
                log.info("point={}", point);
            }
        }
    }
}

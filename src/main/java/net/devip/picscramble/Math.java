package net.devip.picscramble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;

final class Math {
    
    private static final Logger log = LoggerFactory.getLogger(java.lang.Math.class);
    
    private static final Random RND = new Random();
    
    private Math() {
    
    }
    
    public static List<Integer> evenDivisors(int divident, int minDivisor, int maxDivisor) {
        
        List<Integer> divisors = new ArrayList<>();
        
        int minDivisor1 = (minDivisor % 2 == 0 ? minDivisor : minDivisor + 1);
        for (int divisor = minDivisor1; divisor < divident && divisor < maxDivisor; divisor += 2) {
            
            int reminder = divident % divisor;
            if (reminder == 0) {
                
                divisors.add(divisor);
            }
        }
        
        return Collections.unmodifiableList(divisors);
    }
    
    public static int randomEvenDivisor(int divident, int minDivisor, int maxDivisor) {
        
        List<Integer> divisors = evenDivisors(divident, minDivisor, maxDivisor);
        int rndIndex = RND.nextInt(divisors.size());
        int randomDivisor = divisors.get(rndIndex);
        return randomDivisor;
    }
    
    public static List<Integer> commonEvenDivisors(int number1, int number2, int minDivisor, int maxDivisor) {
        
        Set<Integer> divisors1 = new TreeSet<>(evenDivisors(number1, minDivisor, maxDivisor));
        log.info("divisors1={}", divisors1);
        
        Set<Integer> divisors2 = new TreeSet<>(evenDivisors(number2, minDivisor, maxDivisor));
        log.info("divisors2={}", divisors2);
        
        List<Integer> commonDivisors = new ArrayList<>();
        for (Integer divisor : divisors1) {
    
            if (divisor >= minDivisor && divisors2.contains(divisor)) {
                
                commonDivisors.add(divisor);
            }
        }
        
        return Collections.unmodifiableList(commonDivisors);
    }
    
    public static int randomCommonEvenDivisor(int number1, int number2, int minDivisor, int maxDivisor) {
        
        List<Integer> commonDivisors = commonEvenDivisors(number1, number2, minDivisor, maxDivisor);
        log.info("commonEvenDivisors={}", commonDivisors);
        
        int randomCommonDivisor = (minDivisor + maxDivisor) / 2;
        randomCommonDivisor = (randomCommonDivisor % 2 == 0 ? randomCommonDivisor : randomCommonDivisor + 1);
        if (commonDivisors.isEmpty()) {
            
            log.warn("No common evenDivisors. Assuming {}", randomCommonDivisor);
        } else {
            
            int rndIndex = RND.nextInt(commonDivisors.size());
            randomCommonDivisor = commonDivisors.get(rndIndex);
        }
        
        return randomCommonDivisor;
    }
    
    public static double radians(int degrees) {
        
        double radians = java.lang.Math.toRadians(degrees);
        return radians;
    }
    
    public static List<Integer> randomRotationSequence() {
        
        List<Integer> randomRotationSeq = new ArrayList<>();
    
        int times = RND.nextInt(23) + 7;
        for (int i = 0; i < times; i++) {
            
            randomRotationSeq.add(90);
            randomRotationSeq.add(180);
            randomRotationSeq.add(270);
        }
        
        Collections.shuffle(randomRotationSeq);
        
        return Collections.unmodifiableList(randomRotationSeq);
    }
    
    public static Point randomFreeTile(boolean[][] grid) {
        
        int numRandomTries = 2 * grid[0].length * grid.length;
        
        int x;
        int y;
        
        // Try a few times
        for (int i = 0; i < numRandomTries; i++) {
            
            // Use coordinate system - x, y.  *Not* matrix system - rows, cols
            x = RND.nextInt(grid[0].length);
            y = RND.nextInt(grid.length);
            
            // Reverse array indices because we are using coordinate system
            if (!grid[y][x]) {
                
                grid[y][x] = true;
                return new Point(x, y);
            }
        }
        
        // Cannot find a random vacant tile.  Occupy the first one that is free
        // Use coordinate system - x, y.  *Not* matrix system - rows, cols
        for (x = 0; x < grid[0].length; x++) {
            
            for (y = 0; y < grid.length; y++) {
                
                // Reverse array indices because we are using coordinate system
                if (!grid[y][x]) {
                    
                    grid[y][x] = true;
                    return new Point(x, y);
                }
            }
        }
        
        throw new RuntimeException("No free tile");
    }
}

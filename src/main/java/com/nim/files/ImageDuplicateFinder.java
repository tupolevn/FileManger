package com.nim.files;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ImageDuplicateFinder {

    public static void main(String[] args) {
        String directoryPath = "path/to/your/images";  // Replace with the path to your image directory

        Map<String, String> imageHashes = new HashMap<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isImage(file)) {
                    String hash = generateAverageHash(file);
                    if (hash != null) {
                        if (imageHashes.containsKey(hash)) {
                            System.out.println("Duplicate images found: " + file.getName() + " and " + imageHashes.get(hash));
                        } else {
                            imageHashes.put(hash, file.getName());
                        }
                    }
                }
            }
        }
    }

    private static boolean isImage(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    public static boolean isEqualImage (File image1, File image2) {
    	String hash1 = generateAverageHash(image1);
    	String hash2 = generateAverageHash(image2);
    	
    	return hash1.equals(hash2);
    }
    
    private static String generateAverageHash(File file) {
        try {
            BufferedImage image = ImageIO.read(file);

            int width = 8;
            int height = 8;

            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resizedImage.getGraphics().drawImage(image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), 0, 0, null);

            int[][] pixels = new int[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    pixels[i][j] = resizedImage.getRGB(i, j);
                }
            }

            int averageColor = getAverageColor(pixels);

            StringBuilder hashBuilder = new StringBuilder();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    hashBuilder.append((pixels[i][j] < averageColor) ? "0" : "1");
                }
            }

            return hashBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int getAverageColor(int[][] pixels) {
        int sum = 0;

        for (int[] row : pixels) {
            for (int pixel : row) {
                sum += pixel;
            }
        }

        return sum / (pixels.length * pixels[0].length);
    }
}


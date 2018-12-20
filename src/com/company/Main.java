package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    static int [][] imagMatrix;
    static int vecRow,vecCol,vecNo;
    static Scanner scanner = new Scanner(System.in);
    static Vector<Block> codeBook, original, splitedVec;
    static Vector<Vector<Block>> nearest;
    static int [][] compressed, reconstructed;
    static int width, height;
    static File imageFile = new File("E:\\Downloads\\test1.png");
    static File output = new File("E:\\Downloads\\test_new.png");
    private static BufferedImage image = null, newImage = null;

    public static void main(String[] args) {

//        for ( int i=0;i<6;i++){
//            for (int j=0;j<6;j++){
//                image[i][j] = scanner.nextInt();
//            }
//        }

        readImage();
        convertToPixels();

        System.out.print("Vector size( Row col ): ");
        vecRow = scanner.nextInt();
        vecCol = scanner.nextInt();
        // setting row & col
        Block block = new Block(vecRow, vecCol);
        codeBook = new Vector<>();
        original = new Vector<>();

        System.out.print("No. of Vectors: ");
        vecNo = scanner.nextInt();

        buildBlocks();

        Block tempAverage = getAverage(original);

        codeBook.add(tempAverage);

        buildCodeBook();

//        printBlocks(codeBook);

        imageEncoding();

        imageDecoding(compressed);

        newImage = getImageFromArray();

        writeImage();

    }

    private static void imageDecoding( int [][] compressed ) {
        reconstructed = new int[height][width];

        for (int i = 0; i < height/vecRow; i++) {
            for (int j = 0; j < width/vecCol; j++) {
                for (int k = 0; k < vecRow; k++) {
                    for (int l = 0; l < vecCol; l++) {
                        reconstructed[(i * vecRow) + k][(j * vecCol) + l]
                                = (int) Math.round(codeBook.get(compressed[i][j]).getElement(k, l));
                    }
                }
            }
        }
    }

    private static void imageEncoding() {
        compressed = new int[height/vecRow][width/vecCol];
        for (int i = 0; i < original.size(); i++) {
            for (int j = 0; j < codeBook.size(); j++) {
                for (int k = 0; k < nearest.get(j).size(); k++) {
                    if ( original.get(i).equals(nearest.get(j).get(k)) ){
                        compressed[i/(height/vecRow)][i%(width/vecCol)]=j;
                    }
                }
            }
        }
    }

    private static void buildCodeBook() {
        while ( codeBook.size() != vecNo ){
            splitedVec = new Vector<>();
            for ( int i=0;i<codeBook.size();i++){
                split(codeBook.get(i));
            }
            nearest = new Vector<>();
            nearest.setSize(splitedVec.size());
            for (int i = 0; i < splitedVec.size(); i++) {
                nearest.set(i, new Vector<>());
            }
            for (int i = 0; i < original.size(); i++) {
                double min = 999999999;
                int ind = 0;
                for (int j = 0; j < splitedVec.size(); j++) {
                    double diff = getDifference(original.get(i),splitedVec.get(j));
                    if ( diff < min ){
                        min = diff;
                        ind = j;
                    }
                }
                nearest.get(ind).add(original.get(i));
            }
            codeBook.clear();
            for (int i = 0; i < nearest.size(); i++) {
                codeBook.add(getAverage(nearest.get(i)));
            }
        }
        splitedVec = codeBook;
        while ( true ){
            for (int i = 0; i < splitedVec.size(); i++) {
                nearest.get(i).clear();
            }
            for (int i = 0; i < original.size(); i++) {
                double min = 999999999;
                int ind = 0;
                for (int j = 0; j < splitedVec.size(); j++) {
                    double diff = getDifference(original.get(i),splitedVec.get(j));
                    if ( diff < min ){
                        min = diff;
                        ind = j;
                    }
                }
                nearest.get(ind).add(original.get(i));
            }

            codeBook.clear();
            for (int i = 0; i < nearest.size(); i++) {
                codeBook.add(getAverage(nearest.get(i)));
            }
            if ( codeBook.equals(splitedVec) ){
                break;
            }
            splitedVec = codeBook;
        }
    }

    private static double getDifference(Block block, Block block1) {
        double diff = 0;
        for (int i = 0; i < vecRow; i++) {
            for (int j = 0; j < vecCol; j++) {
                diff+= Math.abs(block.getElement(i,j)-block1.getElement(i,j));
            }
        }
        return diff;
    }

    private static void split(Block block) {
        Block mFloor = new Block();
        Block mCeil= new Block();
        for ( int i=0;i<vecRow;i++){
            for (int j = 0; j < vecCol; j++) {
                mFloor.setElement(i,j,Math.ceil(block.getElement(i,j)-1));
            }
        }
        splitedVec.add(mFloor);
        for ( int i=0;i<vecRow;i++){
            for (int j = 0; j < vecCol; j++) {
                mCeil.setElement(i,j,Math.floor(block.getElement(i,j)+1));
            }
        }
        splitedVec.add(mCeil);
    }

    private static Block getAverage(Vector<Block> original) {
        Block average = new Block();
        for (int i = 0; i < original.size(); i++) {
            for (int j = 0; j < vecRow; j++) {
                for (int k = 0; k < vecCol; k++) {
                    double value = (original.get(i).getElement(j,k)/original.size())+average.getElement(j,k);
                    average.setElement(j,k,value);
                }
            }
        }
        return average;
    }

    private static void printBlocks(Vector<Block> original) {
        for ( int i=0;i<original.size();i++){
            for ( int j=0;j<Block.row;j++){
                for ( int k=0;k<Block.col;k++){
                    System.out.print(Math.round(original.get(i).getElement(j,k))+" ");
                }
                System.out.println();
            }
            System.out.println("=====================");
        }
    }

    private static void buildBlocks() {
        for (int i = 0; i < height; i+=vecRow) {
            for (int j = 0; j < width; j+=vecCol) {
                Block obj = new Block();
                for (int k = 0; k < vecRow; k++) {
                    for (int l = 0; l < vecCol; l++) {
                        if ( i+k >= height || j+l >= width )
                            continue;
                        obj.setElement(k,l, imagMatrix[i+k][j+l] );
                    }
                }
                original.add(obj);
            }
        }
    }

    public static void readImage() {
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public static void convertToPixels() {
        width = image.getWidth();
        height = image.getHeight();
        imagMatrix = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);
                int alpha = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb >> 0) & 0xff;
                imagMatrix[i][j] = r;
            }
        }
    }

    public static BufferedImage getImageFromArray() {
        BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image2.setRGB(x, y, (reconstructed[y][x] << 16) | (reconstructed[y][x] << 8) | (reconstructed[y][x]));
            }
        }
        return image2;
    }

    public static void writeImage() {
        try {
            ImageIO.write(newImage, "jpg", output);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}


/*
1 2 7 9 4 11
3 4
6 6
12 12
4 9 15 14 9 9
10 10
20 18
8 8
4 3 17 16 1 4
4 5
18 18
5 6
 */


//            for (int i = 0; i < splitedVec.size(); i++) {
//                for (int j = 0; j < original.size(); j++) {
//
//                    int co=0;
//                    for (int k = 0; k < vecRow; k++) {
//                        for (int l = 0; l < vecCol; l++) {
//                            if ( original.get(j).getElement(k,l) <= splitedVec.get(i).getElement(k,l) ){
//                                co++;
//                            }
//                        }
//                    }
//                    if ( co >= vecRow*vecCol){
//
//                    }
//                }
//            }
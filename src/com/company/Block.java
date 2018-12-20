package com.company;

public class Block {

    static int row,col;
    double [][] arr;

    public Block(int row, int col){
        this.row = row;
        this.col = col;
        arr = new double[row][col];
        intialize();
    }

    public Block(){
        arr = new double[row][col];
        intialize();
    }

    private void intialize(){
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arr[i][j]=0;
            }
        }
    }

    public void setElement( int i, int j, double value){
        arr[i][j]=value;
    }

    public double getElement( int i, int j ){
        return arr[i][j];
    }



}

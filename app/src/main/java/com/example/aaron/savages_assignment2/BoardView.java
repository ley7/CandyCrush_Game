package com.example.aaron.savages_assignment2;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.SurfaceView;
        import android.view.SurfaceHolder;
        import android.graphics.Rect;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;
        import	java.util.Random;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {  

    private Bitmap icons[];
    private int prevX;
    private int prevY;
    private List<Integer> indices;
    private int startRowNum;
    private int startColNum;
    int score = 0;


    public BoardView(Context context) {  //initialize the board
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        System.out.println("In Constructor");
        icons = new Bitmap[9];
        prevX = 0;
        prevY = 0;
        indices = new ArrayList<Integer>();
        startRowNum = 0;
        startColNum = 0;
        for(int i = 0; i < 9; i++) {  
            indices.add(i);
            indices.add(i);
            indices.add(i);
            indices.add(i);
            indices.add(i);
            indices.add(i);
            indices.add(i);
            indices.add(i);
            indices.add(i);
        }

        Collections.shuffle(indices);  //make candies random

        boolean flag = true;
        while(flag) {    //make true the initialization board will not have combination of three or more candies.
            flag = false;
            for (int i = 0; i < 9; ++i) {
                for (int j = 0; j < 9; ++j) {
                    if(isLine(i, j)) {
                        flag = true;
                    }
                }
            }
        }
        score = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {  //detect the touch and slide
        System.out.println("Touch event occured");

        int currX;
        int currY;
        int endRowNum = 0;
        int endColNum = 0;
        int width = getWidth();
        int height = getWidth();

        int rowHeight = height / 9;
        int columnWidth = width / 9;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            prevX = (int) event.getX();
            prevY = (int) event.getY();
            if(prevY>width) return false;
            startRowNum = prevY / rowHeight;
            startColNum = prevX / columnWidth;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            currX = (int) event.getX();
            currY = (int) event.getY();
            if(currY>width) return false;
            endRowNum = currY / rowHeight;
            endColNum = currX / columnWidth;

            System.out.println("StartRowNum : " + startRowNum + " StartColNum: " + startColNum);
            System.out.println("EndRowNum: " + endRowNum + " EndColNum: " + endColNum);

            if (startRowNum == endRowNum) {
                if (startColNum > endColNum) {
                    System.out.println("R to L");
                    swapImages(startRowNum, startColNum, endRowNum, endColNum);
                    if(!haschange()){
                        swapImages(startRowNum, startColNum, endRowNum, endColNum);
                        return false;
                    }
                } else if (startColNum < endColNum) {
                    System.out.println("L to R");
                    swapImages(startRowNum, startColNum, endRowNum, endColNum);
                    if(!haschange()){
                        swapImages(startRowNum, startColNum, endRowNum, endColNum);
                        return false;
                    }
                } else {
                    System.out.println("Unrecognized action");
                }
            } else if (startColNum == endColNum) {
                if (startRowNum < endRowNum) {
                    System.out.println("Top to bottom");
                    swapImages(startRowNum, startColNum, endRowNum, endColNum);
                    if(!haschange()){
                        swapImages(startRowNum, startColNum, endRowNum, endColNum);
                        return false;
                    }
                } else if (startRowNum > endRowNum) {
                    System.out.println("Bottom to top");
                    swapImages(startRowNum, startColNum, endRowNum, endColNum);
                    if(!haschange()){
                        swapImages(startRowNum, startColNum, endRowNum, endColNum);
                        return false;
                    }
                }
            } else {
                System.out.println("Invalid move");
            }
            invalidate();
        }
        return true;
    }
    boolean haschange() {       //to detect whether this move will cause candy eliminations
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (detectLine(i, j)) return true;
            }
        }
        return false;
    }

    void swapImages(int startRow, int startCol, int endRow, int endCol) {  //swap two candies
        int srcIndex = startRow * 9 + startCol;
        int destIndex = endRow * 9 + endCol;

        int srcVal = indices.get(srcIndex);
        int destVal = indices.get(destIndex);

        indices.set(srcIndex, destVal);
        indices.set(destIndex, srcVal);
    }

    @Override
    protected void onDraw(Canvas canvas) {  //draw the board
        System.out.println("OnDraw called");
        canvas.drawColor(Color.WHITE);
        Rect rect = new Rect();

        int width = getWidth();
        int height = getWidth();
        int rowHeight = height / 9;
        int columnWidth = width / 9;
        boolean flag = true;
        while(flag) {
            flag = false;
            for (int i = 0; i < 9; ++i) {
                for (int j = 0; j < 9; ++j) {
                    if(isLine(i, j)) {
                        flag = true;
                    }
                }
            }
        }

        Paint p = new Paint(Color.GREEN);
        p.setTextSize(60);  // Set text size
        String s = "Score:";  // The text content
        s+=String.valueOf(score);
        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(s, width/2, getHeight()*4/5 ,p);  //draw the score

        for (int i = 0; i < 9; i++) {   //draw the board
            for (int j = 0; j < 9; j++) {
                rect.set(j * columnWidth, i * rowHeight, (j + 1) * columnWidth, (i + 1) * rowHeight);
                canvas.drawBitmap(icons[(indices.get(i * 9 + j)) % 5], null, rect, null);
            }
        }
        boolean flag_die = true;
       for(int i = 0;i < 9; i++) {   //judge whether there will be possible moves on the board. If there is a possible move, isDie is false, flaf_die will be
           for (int j = 0; j < 9; j++) { //set false, the game will continue. If there is no more possible move, flag_die will be true, then the game will be terminated
               if (!isDie(i, j)) {
                   flag_die = false;
               }
           }
       }

        if(flag_die) { ////if there is no more possible move, then a dialog will be shown to tell you you lose, click the button OK and you will terminate the game
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Sorry")
                    .setMessage("You Lose:(")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if(score>8000) { //if the score is more than 8000, then a dialog will be shown to congratulate you, click the button OK and you will terminate the game
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
           builder.setTitle("Congratulations")
                   .setMessage("You Win!")
                   .setNeutralButton("OK", new DialogInterface.OnClickListener(){
                       public void onClick(DialogInterface dialog, int id) {
                           System.exit(0);
                       }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface created");
        setWillNotDraw(false);
        icons[0] = BitmapFactory.decodeResource(getResources(), R.drawable.icon_1);
        icons[1] = BitmapFactory.decodeResource(getResources(), R.drawable.icon_2);
        icons[2] = BitmapFactory.decodeResource(getResources(), R.drawable.icon_3);
        icons[3] = BitmapFactory.decodeResource(getResources(), R.drawable.icon_4);
        icons[4] = BitmapFactory.decodeResource(getResources(), R.drawable.icon_5);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    boolean isLine(int y, int x) {  //Detect whteher there are candy eliminations,if has candy eliminations, eliminate these candies
        boolean lx1 = x - 1 > -1; //There are several suitations because in some cases it will have reaach the boundary of the board, the left boundary, right boundary,up and down.
        boolean lx2 = x - 2 > -1;
        boolean bx1 = x + 1 < 9;
        boolean bx2 = x + 2 < 9;
        boolean ly1 = y - 1 > -1;
        boolean ly2 = y - 2 > -1;
        boolean by1 = y + 1 < 9;
        boolean by2 = y + 2 < 9;
        if (ly1 && by1) {
            if (isSameColor(x, y - 1, x, y, x, y + 1)) {
                hastworight_and_elimanite(x,y-1);  //detect whether there are possible eliminations in the same row near this cell
                hastworight_and_elimanite(x,y); //detect whether there are possible eliminations in the same row near this cell
                hastworight_and_elimanite(x,y+1);//detect whether there are possible eliminations in the same row near this cell
                hastwoleft_and_elimanite(x,y);//detect whether there are possible eliminations in the same row near this cell
                hastwoleft_and_elimanite(x,y+1);//detect whether there are possible eliminations in the same row near this cell
               if (hasdown(x,y+1)) {  //detect whther there are possible eliminations more than 3, ie. 4 or 5
                    if (hasdown(x,y+2)) {
                        for (int q = y - 2; q >= 0; q--) {  //there will be 5 candies eliminations
                            indices.set(q * 9 + x + 27+18, indices.get(q * 9 + x));
                        }
                        Random rand = new Random();
                        indices.set(x, rand.nextInt(5));
                        indices.set(9 + x, rand.nextInt(5));
                        indices.set(18 + x, rand.nextInt(5));
                        indices.set(27 + x, rand.nextInt(5));
                        indices.set(36 + x, rand.nextInt(5));
                        score+=100;
                        return true;
                    } else { //there will be 4 candies eliminations
                        for (int q = y - 2; q >= 0; q--) {
                            indices.set(q * 9 + x + 27+9, indices.get(q * 9 + x));
                        }
                        Random rand = new Random();
                        indices.set(x, rand.nextInt(5));
                        indices.set(9 + x, rand.nextInt(5));
                        indices.set(18 + x, rand.nextInt(5));
                        indices.set(27 + x, rand.nextInt(5));
                        score+=80;
                        return true;
                    }
                } else { //there will be 3 candies eliminations
                    for (int q = y - 2; q >= 0; q--) {
                        indices.set(q * 9 + x + 27, indices.get(q * 9 + x));
                    }
                    Random rand = new Random();
                    indices.set(x, rand.nextInt(5));
                    indices.set(9 + x, rand.nextInt(5));
                    indices.set(18 + x, rand.nextInt(5));
                    score+=60;
                    return true;
                }
            }
        }
        if (lx1 && bx1) {
            if (isSameColor(x - 1,y,x,y,x + 1,y)) {
                hastwodown_and_eliminate(x+1,y);
                hastwodown_and_eliminate(x,y);
                hastwodown_and_eliminate(x-1,y);
                if (hasright(x + 1, y)) {
                        if (hasright(x + 2, y)) {
                            for (int q = y - 1; q >= 0; q--) {
                                indices.set(q * 9 + x - 1 + 9, indices.get(q * 9 + x - 1));
                                indices.set(q * 9 + x + 9, indices.get(q * 9 + x));
                                indices.set(q * 9 + x + 1 + 9, indices.get(q * 9 + x + 1));
                                indices.set(q * 9 + x + 2 + 9, indices.get(q * 9 + x + 2));
                                indices.set(q * 9 + x + 3 + 9, indices.get(q * 9 + x + 3));
                            }

                            Random rand = new Random();
                            indices.set(x - 1, rand.nextInt(5));
                            indices.set(x, rand.nextInt(5));
                            indices.set(x + 1, rand.nextInt(5));
                            indices.set(x + 2, rand.nextInt(5));
                            indices.set(x + 3, rand.nextInt(5));
                            score+=100;
                            return true;
                        }
                        else{
                            for (int q = y - 1; q >= 0; q--) {
                                indices.set(q * 9 + x - 1 + 9, indices.get(q * 9 + x - 1));
                                indices.set(q * 9 + x + 9, indices.get(q * 9 + x));
                                indices.set(q * 9 + x + 1 + 9, indices.get(q * 9 + x + 1));
                                indices.set(q * 9 + x + 2 + 9, indices.get(q * 9 + x + 2));
                            }

                            Random rand = new Random();
                            indices.set(x - 1, rand.nextInt(5));
                            indices.set(x, rand.nextInt(5));
                            indices.set(x + 1, rand.nextInt(5));
                            indices.set(x + 2, rand.nextInt(5));
                            score+=80;
                            return true;
                        }
                    }

                else {
                        for (int q = y - 1; q >= 0; q--) {
                            indices.set(q * 9 + x - 1 + 9, indices.get(q * 9 + x - 1));
                            indices.set(q * 9 + x + 9, indices.get(q * 9 + x));
                            indices.set(q * 9 + x + 1 + 9, indices.get(q * 9 + x + 1));
                        }

                        Random rand = new Random();
                        indices.set(x - 1, rand.nextInt(5));
                        indices.set(x, rand.nextInt(5));
                        indices.set(x + 1, rand.nextInt(5));
                        score+=60;
                        return true;
                    }
            }
        }
        if (ly2) {
            if (isSameColor(x,y,x,y - 1,x,y - 2)) {
                hastworight_and_elimanite(x,y-2);
                hastworight_and_elimanite(x,y-1);
                hastworight_and_elimanite(x,y);
                hastwoleft_and_elimanite(x,y);
                hastwoleft_and_elimanite(x,y-1);
                if (hasdown(x, y + 1)) {
                    if (hasdown(x, y + 2)) {
                        for (int q = y - 3; q >= 0; q--) {
                            indices.set(q * 9 + x + 27 + 18, indices.get(q * 9 + x));
                        }
                        Random rand = new Random();
                        indices.set(x, rand.nextInt(5));
                        indices.set(9 + x, rand.nextInt(5));
                        indices.set(18 + x, rand.nextInt(5));
                        indices.set(27 + x, rand.nextInt(5));
                        indices.set(36 + x, rand.nextInt(5));
                        score+=100;
                        return true;
                    } else {
                        for (int q = y - 3; q >= 0; q--) {
                            indices.set(q * 9 + x + 27 + 9, indices.get(q * 9 + x));
                        }
                        Random rand = new Random();
                        indices.set(x, rand.nextInt(5));
                        indices.set(9 + x, rand.nextInt(5));
                        indices.set(18 + x, rand.nextInt(5));
                        indices.set(27 + x, rand.nextInt(5));
                        score+=80;
                        return true;
                    }
                } else {
                    for (int q = y - 3; q >= 0; q--) {
                        indices.set(q * 9 + x + 27, indices.get(q * 9 + x));
                    }
                    Random rand = new Random();
                    indices.set(x, rand.nextInt(5));
                    indices.set(9 + x, rand.nextInt(5));
                    indices.set(18 + x, rand.nextInt(5));
                    score+=60;
                    return true;
                }
            }
        }
        if (by2) {
            if (isSameColor(x, y, x, y + 1, x, y + 2)) {
                hastworight_and_elimanite(x,y+1);
                hastworight_and_elimanite(x,y+2);

                hastwoleft_and_elimanite(x,y+1);
                hastwoleft_and_elimanite(x,y+2);

                if (hasdown(x, y + 2)) {
                    if (hasdown(x, y + 3)) {
                        for (int q = y - 1; q >= 0; q--) {
                            indices.set(q * 9 + x + 27 + 18, indices.get(q * 9 + x));
                        }
                        Random rand = new Random();
                        indices.set(x, rand.nextInt(5));
                        indices.set(9 + x, rand.nextInt(5));
                        indices.set(18 + x, rand.nextInt(5));
                        indices.set(27 + x, rand.nextInt(5));
                        indices.set(36 + x, rand.nextInt(5));
                        score+=100;
                        return true;
                    } else {
                        for (int q = y - 1; q >= 0; q--) {
                            indices.set(q * 9 + x + 27 + 9, indices.get(q * 9 + x));
                        }
                        Random rand = new Random();
                        indices.set(x, rand.nextInt(5));
                        indices.set(9 + x, rand.nextInt(5));
                        indices.set(18 + x, rand.nextInt(5));
                        indices.set(27 + x, rand.nextInt(5));
                        score+=80;
                        return true;
                    }
                } else {
                    for (int q = y - 1; q >= 0; q--) {
                        indices.set(q * 9 + x + 27, indices.get(q * 9 + x));
                    }
                    Random rand = new Random();
                    indices.set(x, rand.nextInt(5));
                    indices.set(9 + x, rand.nextInt(5));
                    indices.set(18 + x, rand.nextInt(5));
                    score+=60;
                    return true;
                }

            }

        }

        if (lx2) {
            if (isSameColor(x,y,x - 1,y,x - 2,y)) {
                hastwodown_and_eliminate(x,y);
                hastwodown_and_eliminate(x-1,y);
                hastwodown_and_eliminate(x-2,y);
               if (hasright(x, y)) {
                    if (hasright(x + 1, y)) {
                        for (int q = y - 1; q >= 0; q--) {
                            indices.set(q * 9 + x - 2 + 9, indices.get(q * 9 + x - 2));
                            indices.set(q * 9 + x - 1 + 9, indices.get(q * 9 + x - 1));
                            indices.set(q * 9 + x + 9, indices.get(q * 9 + x));
                            indices.set(q * 9 + x + 1 + 9, indices.get(q * 9 + x + 1));
                            indices.set(q * 9 + x + 2 + 9, indices.get(q * 9 + x + 2));
                        }

                        Random rand = new Random();
                        indices.set(x - 2, rand.nextInt(5));
                        indices.set(x - 1, rand.nextInt(5));
                        indices.set(x, rand.nextInt(5));
                        indices.set(x + 1, rand.nextInt(5));
                        indices.set(x + 2, rand.nextInt(5));
                        score+=100;
                        return true;
                    }
                    else{
                        for (int q = y - 1; q >= 0; q--) {
                            indices.set(q * 9 + x - 2 + 9, indices.get(q * 9 + x - 2));
                            indices.set(q * 9 + x - 1+ 9, indices.get(q * 9 + x - 1));
                            indices.set(q * 9 + x + 9, indices.get(q * 9 + x));
                            indices.set(q * 9 + x + 1 + 9, indices.get(q * 9 + x + 1));
                        }

                        Random rand = new Random();
                        indices.set(x - 2, rand.nextInt(5));
                        indices.set(x - 1, rand.nextInt(5));
                        indices.set(x, rand.nextInt(5));
                        indices.set(x + 1, rand.nextInt(5));
                        score+=80;
                        return true;
                    }
                }

                else {
                    for (int q = y - 1; q >= 0; q--) {
                        indices.set(q*9+x+9,indices.get(q*9+x));
                        indices.set(q*9+x-1+9,indices.get(q*9+x-1));
                        indices.set(q*9+x-2+9,indices.get(q*9+x-2));
                    }

                    Random rand = new Random();
                    indices.set(x,rand.nextInt(5));
                    indices.set(x-1,rand.nextInt(5));
                    indices.set(x-2,rand.nextInt(5));
                    score+=60;
                    return true;
                }
            }
        }

        if (bx2) {
            if (isSameColor(x, y, x + 1, y, x + 2, y)) {
                hastwodown_and_eliminate(x+2,y);
                hastwodown_and_eliminate(x+1,y);
                hastwodown_and_eliminate(x,y);
                if (hasright(x + 2, y)) {
                    if (hasright(x + 3, y)) {
                        for (int q = y - 1; q >= 0; q--) {
                            indices.set(q * 9 + x + 4 + 9, indices.get(q * 9 + x + 4));
                            indices.set(q * 9 + x + 3 + 9, indices.get(q * 9 + x + 3));
                            indices.set(q * 9 + x + 2 + 9, indices.get(q * 9 + x + 2));
                            indices.set(q * 9 + x + 1 + 9, indices.get(q * 9 + x + 1));
                            indices.set(q * 9 + x + 9, indices.get(q * 9 + x));
                        }

                        Random rand = new Random();
                        indices.set(x + 4, rand.nextInt(5));
                        indices.set(x + 3, rand.nextInt(5));
                        indices.set(x + 2, rand.nextInt(5));
                        indices.set(x + 1, rand.nextInt(5));
                        indices.set(x, rand.nextInt(5));
                        score+=100;
                        return true;
                    }
                    else{
                        for (int q = y - 1; q >= 0; q--) {
                            indices.set(q * 9 + x + 3 + 9, indices.get(q * 9 + x + 3));
                            indices.set(q * 9 + x + 2 + 9, indices.get(q * 9 + x + 2));
                            indices.set(q * 9 + x + 1 + 9, indices.get(q * 9 + x + 1));
                            indices.set(q * 9 + x + 9, indices.get(q * 9 + x));
                        }

                        Random rand = new Random();
                        indices.set(x + 3, rand.nextInt(5));
                        indices.set(x + 2, rand.nextInt(5));
                        indices.set(x + 1, rand.nextInt(5));
                        indices.set(x, rand.nextInt(5));
                        score+=80;
                        return true;
                    }
                }

                else {
                    for(int q=y-1;q>=0;q--){
                        indices.set(q*9+x+9,indices.get(q*9+x));
                        indices.set(q*9+x+1+9,indices.get(q*9+x+1));
                        indices.set(q*9+x+2+9,indices.get(q*9+x+2));
                    }
                    Random rand = new Random();
                    indices.set(x,rand.nextInt(5));
                    indices.set(x+1,rand.nextInt(5));
                    indices.set(x+2,rand.nextInt(5));
                    score+=60;
                    return true;
                }
            }
        }
        return false;
    }

    boolean detectLine(int y, int x) {  // detect whether there will be candies elimaintion, but this function will not do the elimination
        boolean lx1 = x - 1 > -1;
        boolean lx2 = x - 2 > -1;
        boolean bx1 = x + 1 < 9;
        boolean bx2 = x + 2 < 9;
        boolean ly1 = y - 1 > -1;
        boolean ly2 = y - 2 > -1;
        boolean by1 = y + 1 < 9;
        boolean by2 = y + 2 < 9;
        if (ly1 && by1) {
            if (isSameColor(x, y - 1, x, y, x, y+1)) {
                return true;
            }
        }
        if (lx1 && bx1) {
            if (isSameColor(x - 1,y,x,y,x + 1,y)) {
                return true;
            }
        }
        if (ly2) {
            if (isSameColor(x,y,x,y - 1,x,y - 2)) {
                return true;
            }
        }
        if (by2) {
            if (isSameColor(x,y,x,y + 1,x,y+2)) {
                return true;
            }
        }

        if (lx2) {
            if (isSameColor(x,y,x - 1,y,x - 2,y)) {
                return true;
            }
        }

        if (bx2) {
            if (isSameColor(x, y, x + 1, y, x + 2, y)) {
                return true;
            }
        }
        return false;
    }

    boolean isSameColor(int x1, int y1, int x2, int y2, int x3, int y3) {  //detect whether these three candies is the same kind candy
        return (indices.get(y1 * 9 + x1)) % 5 == (indices.get(y2 * 9 + x2)) % 5 && (indices.get(y2 * 9 + x2)) % 5 == (indices.get(y3 * 9 + x3)) % 5;
    }

    boolean hasdown(int x,int y){
        if(y+1<9) {
            if((indices.get(y*9+x))%5 == (indices.get(y*9+9+x))%5) {
                return true;
            }
        }
        return false;
    }

    boolean hasright(int x,int y){
        if(x+1<9) {
            if((indices.get(y*9+x))%5 == (indices.get(y*9+1+x))%5) {
                return true;
            }
        }
        return false;
    }

    void hastwoleft_and_elimanite(int x,int y){
        if(x-2>=0) {
            if((indices.get(y*9+x))%5 == (indices.get(y*9+x-1))%5 && (indices.get(y*9+x))%5 == (indices.get(y*9+x-2))%5) {
                for(int q=y-1;q>=0;q--){
                    indices.set(q*9+x-1+9,indices.get(q*9+x-1));
                    indices.set(q*9+x-2+9,indices.get(q*9+x-2));
                }
                Random rand = new Random();
                indices.set(x-1,rand.nextInt(5));
                indices.set(x-2,rand.nextInt(5));
                score+=40;
            }
        }
    }

    void hastworight_and_elimanite(int x,int y){
        if(x+2<9) {
            if((indices.get(y*9+x))%5 == (indices.get(y*9+x+1))%5 && (indices.get(y*9+x))%5 == (indices.get(y*9+x+2))%5) {
                for(int q=y-1;q>=0;q--){
                    indices.set(q*9+x+1+9,indices.get(q*9+x+1));
                    indices.set(q*9+x+2+9,indices.get(q*9+x+2));
                }
                Random rand = new Random();
                indices.set(x+1,rand.nextInt(5));
                indices.set(x+2,rand.nextInt(5));
                score+=40;
            }
        }
    }

    void hastwodown_and_eliminate(int x,int y){
        if(y+2<9) {
            if((indices.get(y*9+x))%5 == (indices.get(y*9+9+x))%5 && (indices.get(y*9+x))%5 == (indices.get(y*9+18+x))%5) {
                indices.set((y+1) * 9 + x, indices.get((y-2) * 9 + x));
                indices.set((y+2) * 9 + x, indices.get((y-1) * 9 + x));
                for (int q = y - 3; q >= 0; q--) {
                    indices.set(q * 9 + x + 18, indices.get(q * 9 + x));
                }
                Random rand = new Random();
                indices.set(x, rand.nextInt(5));
                indices.set(9 + x, rand.nextInt(5));
                score+=40;
            }
        }
    }

    boolean isDie(int y, int x) {  //detect whether there will be possible moves on the board, if no, then the player lose
        boolean lx1 = x - 1 > -1;
        boolean lx2 = x - 2 > -1;
        boolean lx3 = x - 3 > -1;
        boolean bx1 = x + 1 < 9;
        boolean bx2 = x + 2 < 9;
        boolean bx3 = x + 3 < 9;
        boolean ly1 = y - 1 > -1;
        boolean ly2 = y - 2 > -1;
        boolean ly3 = y - 3 > -1;
        boolean by1 = y + 1 < 9;
        boolean by2 = y + 2 < 9;
        boolean by3 = y + 3 < 9;
        int color = (indices.get(y*9+x))%5;
        if (bx1) {
            if ((indices.get(y*9+x+1))%5 == color) {
                if (bx3) {
                    if ((indices.get(y*9+x+3))%5 == color) {
                        return false;
                    }
                }
                if (bx2 && by1) {
                    if ((indices.get((y+1)*9+x+2))%5== color) {
                        return false;
                    }
                }
                if (bx2 && ly1) {
                    if ((indices.get((y-1)*9+x+2))%5 == color) {
                        return false;
                    }
                }
                if (lx2) {
                    if ((indices.get(y*9+x-2))%5 == color) {
                        return false;
                    }
                }
                if (lx1 && ly1) {
                    if ((indices.get((y-1)*9+x-1))%5 == color) {
                        return false;
                    }
                }
                if (lx1 && by1) {
                    if ((indices.get((y+1)*9+x-1))%5== color) {
                        return false;
                    }
                }
            }
            if (ly1 && by1) {
                if ((indices.get((y-1)*9+x+1))%5 == color && (indices.get((y+1)*9+x+1))%5== color) {
                    return false;
                }
            }
        }
        if (lx1) {
            if ((indices.get(y*9+x-1))%5 == color) {
                if (lx3) {
                    if ((indices.get(y*9+x-3))%5 == color) {
                        return false;
                    }
                }
                if (lx2 && by1) {
                    if ((indices.get((y+1)*9+x-2))%5== color) {
                        return false;
                    }
                }
                if (lx2 && ly1) {
                    if ((indices.get((y-1)*9+x-2))%5 == color) {
                        return false;
                    }
                }
                if (bx2) {
                    if ((indices.get(y*9+x+2))%5 == color) {
                        return false;
                    }
                }
                if (bx1 && ly1) {
                    if ((indices.get((y-1)*9+x+1))%5 == color) {
                        return false;
                    }
                }
                if (bx1 && by1) {
                    if ((indices.get((y+1)*9+x+1))%5== color) {
                        return false;
                    }
                }
            }
            if (ly1 && by1) {
                if ((indices.get((y-1)*9+x-1))%5 == color && (indices.get((y+1)*9+x-1))%5 == color) {
                    return false;
                }
            }
        }
        if (by1) {
            if ((indices.get((y+1)*9+x))%5 == color) {
                if (by3) {
                    if ((indices.get((y+3)*9+x))%5 == color) {
                        return false;
                    }
                }
                if (lx1 && by2) {
                    if ((indices.get((y+2)*9+x-1))%5 == color) {
                        return false;
                    }
                }
                if (bx1 && by2) {
                    if ((indices.get((y+2)*9+x+1))%5 == color) {
                        return false;
                    }
                }
                if (ly2) {
                    if ((indices.get((y-2)*9+x))%5 == color) {
                        return false;
                    }
                }
                if (bx1 && ly1) {
                    if ((indices.get((y-1)*9+x+1))%5 == color) {
                        return false;
                    }
                }
                if (lx1 && ly1) {
                    if ((indices.get((y-1)*9+x-1))%5 == color) {
                        return false;
                    }
                }
            }
            if (lx1 && bx1) {
                if ((indices.get((y+1)*9+x-1))%5 == color && (indices.get((y+1)*9+x+1))%5 == color) {
                    return false;
                }
            }
        }
        if (ly1) {
            if ((indices.get((y-1)*9+x))%5 == color) {
                if (ly3) {
                    if ((indices.get((y-3)*9+x))%5 == color) {
                        return false;
                    }
                }
                if (lx1 && ly2) {
                    if ((indices.get((y-2)*9+x-1))%5== color) {
                        return false;
                    }
                }
                if (bx1 && ly2) {
                    if ((indices.get((y-2)*9+x+1))%5 == color) {
                        return false;
                    }
                }
                if (by2) {
                    if ((indices.get((y+2)*9+x))%5 == color) {
                        return false;
                    }
                }
                if (bx1 && by1) {
                    if ((indices.get((y+1)*9+x+1))%5 == color) {
                        return false;
                    }
                }
                if (lx1 && by1) {
                    if ((indices.get((y+1)*9+x-1))%5 == color) {
                        return false;
                    }
                }
            }
            if (lx1 && bx1) {
                if ((indices.get((y-1)*9+x-1))%5 == color && (indices.get((y-1)*9+x+1))%5 == color) {
                    return false;
                }
            }
        }
        return true;
    }

}


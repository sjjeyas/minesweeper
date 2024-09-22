package com.example.project1_310;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.gridlayout.widget.GridLayout;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.Random;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int clock = 0;
    private boolean running = true;
    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;
    private int[][] mineLocation;
    private int[][] revealed;
    private int mine1 = 0;
    private int mine2 = 0;
    private int mine3 = 0;
    private int mine4 = 0;
    private Button button;
    private boolean mining = true;
    private int numFlags = 0;
    private int numBombs = 4;


    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void randomizeMines(){
        mineLocation = new int[ROW_COUNT][COLUMN_COUNT];
        revealed = new int[ROW_COUNT][COLUMN_COUNT];
        for (int i = 0; i < ROW_COUNT; i++){
            for (int j =0 ; j < COLUMN_COUNT; j++){
                mineLocation[i][j] = 0;
                revealed[i][j] = 0;
            }
        }

        Random rand = new Random();
        do {
            mine1 =  rand.nextInt(120);
            mine2 =  rand.nextInt(120);
            mine3 = rand.nextInt(120);
            mine4 =  rand.nextInt(120);

        } while (checkDuplicates(mine1, mine2, mine3, mine4));

        mineLocation[mine1 / COLUMN_COUNT][mine1 % COLUMN_COUNT] = -1;
        mineLocation[mine2 / COLUMN_COUNT][mine2 % COLUMN_COUNT] = -1;
        mineLocation[mine3 / COLUMN_COUNT][mine3 % COLUMN_COUNT] = -1;
        mineLocation[mine4 / COLUMN_COUNT][mine4 % COLUMN_COUNT] = -1;
        System.out.println(mine1);
        System.out.println(mine2);
        System.out.println(mine3);
        System.out.println(mine4);
        final TextView mineView = (TextView) findViewById(R.id.mineView);
        String mines = String.format("%d, %d, %d, %d", mine1, mine2, mine3, mine4);
        mineView.setText(mines);
        calculateNearbyMines(mine1);
        calculateNearbyMines(mine2);
        calculateNearbyMines(mine3);
        calculateNearbyMines(mine4);
    }



    void calculateNearbyMines(int mine){
        boolean top = false;
        boolean bottom = false;
        boolean right = false;
        boolean left = false;

        if (mine / COLUMN_COUNT != 0){ // top mine
            top = true;
            if (mineLocation[mine / COLUMN_COUNT -1][mine % COLUMN_COUNT] != -1){
                mineLocation[mine / COLUMN_COUNT -1][mine % COLUMN_COUNT] += 1;
            }
        }
        if (mine / COLUMN_COUNT != 11){ // bottom mine
            bottom = true;
            if (mineLocation[mine / COLUMN_COUNT + 1][mine % COLUMN_COUNT] != -1){
                mineLocation[mine / COLUMN_COUNT + 1][mine % COLUMN_COUNT] += 1;
            }
        }
        if (mine % COLUMN_COUNT != 9){ // right mine
            right = true;
            if (mineLocation [mine / COLUMN_COUNT ][mine % COLUMN_COUNT + 1] != -1){
                mineLocation [mine / COLUMN_COUNT ][mine % COLUMN_COUNT + 1] += 1;
            }
        }
        if (mine % COLUMN_COUNT != 0){ // left mine
            left = true;
            if ( mineLocation [mine / COLUMN_COUNT ][mine % COLUMN_COUNT - 1] != -1){
                mineLocation [mine / COLUMN_COUNT ][mine % COLUMN_COUNT - 1] += 1;
            }
        }
        if (left && top){ // top left mine
            if ( mineLocation [mine / COLUMN_COUNT -1][mine % COLUMN_COUNT - 1] != -1){
                mineLocation [mine / COLUMN_COUNT -1 ][mine % COLUMN_COUNT - 1] += 1;
            }
        }
        if (right && top){ // top right mine
            if ( mineLocation [mine / COLUMN_COUNT - 1][mine % COLUMN_COUNT + 1] != -1){
                mineLocation [mine / COLUMN_COUNT - 1][mine % COLUMN_COUNT + 1] += 1;
            }
        }
        if (left && bottom){ // bottom left mine
            if (  mineLocation [mine / COLUMN_COUNT + 1][mine % COLUMN_COUNT - 1] != -1){
                mineLocation [mine / COLUMN_COUNT + 1][mine % COLUMN_COUNT - 1]+= 1;
            }
        }
        if (right && bottom){ // bottom right mine
            if (  mineLocation [mine / COLUMN_COUNT + 1][mine % COLUMN_COUNT + 1] != -1){
                mineLocation [mine / COLUMN_COUNT + 1][mine % COLUMN_COUNT + 1] += 1;
            }
        }

    }

    private boolean checkDuplicates(int mine1, int mine2, int mine3, int mine4){
        if (mine1 == mine2 || mine1 == mine3 || mine1 == mine4){
            return true;
        }
        if (mine2 == mine3 || mine2 == mine4){
            return true;
        }
        return (mine3 == mine4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.minebutton);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mining){
                    button.setText("🚩");
                    mining = false;// Code here executes on main thread after user presses button
                }else{
                    mining = true;
                    button.setText("⛏");
                }
            }
        });

        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }
        runTimer();
        randomizeMines();

        cell_tvs = new ArrayList<TextView>();
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout00);
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                TextView tv = new TextView(this );
                tv.setHeight(dpToPixel(25));
                tv.setWidth(dpToPixel(25));
                tv.setTextSize(16);//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }

    public void onClickStart(View view) {
        running = true;
    }

    public void onClickStop(View view) {
        running = false;
    }
    public void onClickClear(View view) {
        running = false;
        clock = 0;
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.timeView);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                String time = String.format("%3d", clock);
                timeView.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        final TextView flagView = (TextView) findViewById(R.id.flagView);
        final TextView bombView = (TextView) findViewById(R.id.bombView);
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        revealed[i][j] = 1;
        String format = Integer.toString(mineLocation[i][j]) ;
        tv.setText(format);
        if (!mining && mineLocation[i][j] == -1){
            tv.setTextColor(Color.BLACK);
            tv.setText("🚩");
            tv.setBackgroundColor(Color.WHITE);
            numFlags++;
            numBombs--;
            flagView.setText(Integer.toString(numFlags));
            bombView.setText(Integer.toString(numBombs));
        }
        else if (!mining){
            tv.setTextColor(Color.BLACK);
            tv.setText("🚩");
            tv.setBackgroundColor(Color.WHITE);
            numFlags++;
            flagView.setText(Integer.toString(numFlags));
            bombView.setText(Integer.toString(numBombs));
        }
        else if (mineLocation[i][j] == -1 && mining) {
            tv.setTextColor(Color.BLACK);
            tv.setText("💣");
            tv.setBackgroundColor(Color.RED);
        }
        else if (mineLocation[i][j] == 0 && mining) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.parseColor("lime"));
            revealed[0][0] = 1;

        }
        else {
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.YELLOW);
        }
    }

    boolean checkBounds(int i , int j){
        return (i != -1 && i != 13 && j != -1 && j != 10);
    }



}

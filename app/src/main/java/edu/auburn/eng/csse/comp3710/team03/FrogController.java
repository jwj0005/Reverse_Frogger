package edu.auburn.eng.csse.comp3710.team03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by JDSS on 16/4/15.
 */
public class FrogController implements Updateable {

    private static final int MAX_DIFFICULTY = 2;
    private static final int MIN_DIFFICULTY = 0;

    private Context context;

    private ArrayList<Frog> frogs;
    private ArrayList<Integer> freeColumns;

    //endColumn and endLane are the number of columns and lanes
    //numbered from top left to bottom right in portrait mode
    private int endColumn;
    private int endLane;
    private int difficulty;
    int[][] carLocations;

    private Bitmap frogSitBitmap;
    private Bitmap frogJumpBitmap;

    private Random RNGesus;

    private int frogsEscaped;
    private int frogsHit;


    public FrogController(Context newContext) {
        context = newContext;
        endColumn = 8;
        endLane = 4;
        difficulty = 0;
        frogs = new ArrayList<Frog>();
        RNGesus = new Random();
        freeColumns  = new ArrayList<Integer>();
        for (int i = 0; i < endColumn; i++) {
            freeColumns.add(i);
        }
        frogsEscaped = 0;
        frogsHit = 0;
        frogSitBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_logo);
    }

    public FrogController(Context newContext, int newEndColumn, int newEndLane, int newDifficulty) {
        context = newContext;
        if (setDifficulty(newDifficulty))
            difficulty = 0;
        setEndColumn(newEndColumn);
        setEndLane(newEndLane);
        frogs = new ArrayList<Frog>();
        RNGesus = new Random();
        freeColumns = new ArrayList<Integer>();
        for (int i = 0; i < endColumn; i++) {
            freeColumns.add(i);
        }
        frogsEscaped = 0;
        frogsHit = 0;
        frogSitBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_logo);
    }

    public int getEndColumn() {
        return endColumn;
    }

    private void setEndColumn(int newEndColumn) {
        endColumn = newEndColumn;
    }

    public int getEndLane() {
        return endLane;
    }

    private void setEndLane(int newEndLane) {
        endLane = newEndLane;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Boolean setDifficulty(int newDifficulty) {
        if (newDifficulty > MAX_DIFFICULTY || newDifficulty < MIN_DIFFICULTY)
            return false;
        else {
            difficulty = newDifficulty;
            return true;
        }
    }

    public Boolean spawnFrog() {
        //if there are no empty columns, no frog spawned
        if (freeColumns.size() == 0) {
            return false;
        }
        else {
            //get a random number between [0, numFreeColumns)
            int randNum = RNGesus.nextInt(freeColumns.size());
            //add a frog to the column number stored in the randNum index of freeColumns
            frogs.add(
                    //add new frog into lane 0
                    new Frog(freeColumns.get(randNum), 0)
            );
            //that column is not longer empty
            freeColumns.remove(randNum);
            return true;
        }
    }

    public int[][] getFrogLocations() {
        //list of column,lane coord's for each frog
        int[][] locations = new int[frogs.size()][2];
        for (int i= 0; i < frogs.size(); i++) {
            locations[i][0] = frogs.get(i).getColumn();
            locations[i][1] = frogs.get(i).getLane();
        }
        return locations;
    }

    public void setCarLocations(int[][] newCarLocations) {
        carLocations = newCarLocations;
    }

    private void Jump(Frog frog, int[][] cars) {

        //percentage probabilities of the frog's movement, out of 100
        int forward;
        int stay;
        int rearward;
        int movement;

        //if the frog is not in the starting lane
        if (frog.getLane() != 0) {
            forward = 40;
            stay = 30;
            rearward = 30;
            //search through carLocations for nearby carLocations
            for (int i = 0; i < cars.length; i++) {
                //if a car 1 column away
                if (cars[i][0] == frog.getColumn() + 1) {
                    //if the car is in the same lane
                    if (cars[i][1] == frog.getLane()) {
                        stay -= 15;
                        forward += 10;
                        rearward += 5;
                    }
                    //if the car is in the forward lane
                    else if (cars[i][1] == frog.getLane() + 1) {
                        forward -= 15;
                        stay += 10;
                        rearward += 5;
                    }
                    //if the car is in the rearward lane
                    else if (cars[i][1] == frog.getLane() - 1) {
                        rearward -= 15;
                        forward += 10;
                        stay += 5;
                    }
                    //else do nothing to the probabilities
                }
                //if a car is 2 columns away
                else if (cars[i][0] == frog.getColumn() + 2) {
                    //if the car is in the same lane
                    if (cars[i][1] == frog.getLane()) {
                        stay -= 10;
                        forward += 7;
                        rearward += 3;
                    }
                    //if the car is in the forward lane
                    else if (cars[i][1] == frog.getLane() + 1) {
                        forward -= 10;
                        stay += 7;
                        rearward += 3;
                    }
                    //if the car is in the rearward lane
                    else if (cars[i][1] == frog.getLane() - 1) {
                        rearward -= 10;
                        forward += 7;
                        stay += 3;
                    }
                    //else do nothing to the probabilities
                }
            }
            //PRAISE RNGesus
            movement = RNGesus.nextInt(100);
            //move frog
            if (movement < forward) { //move forward
                frog.setLane(frog.getLane() + 1);
            }
            else if (movement < forward + rearward) { //move rearward
                frog.setLane(frog.getLane() - 1);
            }
            //otherwise, stay still
        }

        //else the frog is in the first lane
        else {
            //percentage probabilities of the frog's movement, out of 100
            forward = 60;
            stay = 40;
            //search through carLocations for nearby carLocations
            for (int i = 0; i < cars.length; i++) {

                //if a car 1 column away
                if (cars[i][0] == frog.getColumn() + 1) {
                    //if the car is in the same lane
                    if (cars[i][1] == frog.getLane()) {
                        stay -= 20;
                        forward += 20;
                    }
                    //if the car is in the forward lane
                    else if (cars[i][1] == frog.getLane() + 1) {
                        forward -= 20;
                        stay += 20;
                    }
                    //else do nothing to the probabilities
                }
                //if a car is 2 columns away
                else if (cars[i][0] == frog.getColumn() + 2) {
                    //if the car is in the same lane
                    if (cars[i][1] == frog.getLane()) {
                        stay -= 12;
                        forward += 12;
                    }
                    //if the car is in the forward lane
                    else if (cars[i][1] == frog.getLane() + 1) {
                        forward -= 12;
                        stay += 12;
                    }
                    //else do nothing to the probabilities
                }

            }

            //PRAISE RNGesus
            movement = RNGesus.nextInt(100);
            //move frog
            if (movement < forward) { //move forward
                frog.setLane(frog.getLane() + 1);
            }
        }
    }

    private Boolean isHit(Frog frog, int[][] cars) {
        for (int i = 0; i < carLocations.length; i++) {
            //if frog is in same location as car, frog was hit
            if (frog.getLane() == carLocations[i][1] && frog.getColumn() == carLocations[i][0]) {
                i = carLocations.length;
                return true;
            }
        }
        return false;
    }

    @Override
    public void Draw(Canvas canvas) {
        Paint paint = new Paint();

/*      //use for testing
        ArrayList<Frog> tempFrogs = new ArrayList<>();
        tempFrogs.add(new Frog(4, 0));
        tempFrogs.add(new Frog(5, 1));
        tempFrogs.add(new Frog(6, 2));
        tempFrogs.add(new Frog(7, 3));

        for (Frog frog : tempFrogs) {
*/
        for (Frog frog : frogs) {
            canvas.drawBitmap(
                    frogSitBitmap,
                    null,
                    new Rect(
                            (int) (((float) frog.getLane()/endLane) * canvas.getWidth()),
                            (int) (((float) frog.getColumn()/endColumn) * canvas.getHeight()),
                            (int) ((((float) frog.getLane() + 1)/endLane) * canvas.getWidth()),
                            (int) ((((float) frog.getColumn() + 1)/endColumn) * canvas.getHeight())
                    ),
                    paint
            );
        }
    }

    @Override
    public void Update() {
        //look through frogs
        for (Iterator<Frog> iterator = frogs.iterator(); iterator.hasNext();) {
            Frog frog = iterator.next();

            //if frog is in endLane, frog has escaped
            if (frog.getLane() >= endLane) {
                iterator.remove();
                frogsEscaped++;
            }
            //else if frog is hit by car
            else if (isHit(frog, carLocations)) {
                iterator.remove();
                frogsHit++;
            }
            //else frog can jump
            else {
                Jump(frog, carLocations);
            }
        }
    }
}

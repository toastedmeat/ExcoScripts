/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toastyflaxer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.excobot.Application;
import org.excobot.bot.event.listeners.PaintListener;
import org.excobot.bot.script.GameScript;
import org.excobot.bot.script.Manifest;
import org.excobot.game.api.methods.cache.media.Widgets;
import org.excobot.game.api.methods.input.Mouse;
import org.excobot.game.api.methods.media.Bank;
import org.excobot.game.api.methods.media.animable.GameObjects;
import org.excobot.game.api.methods.media.animable.actor.Players;
import org.excobot.game.api.methods.scene.Camera;
import org.excobot.game.api.methods.scene.Movement;
import org.excobot.game.api.methods.tab.Inventory;
import org.excobot.game.api.methods.tab.Tabs;
import org.excobot.game.api.wrappers.media.animable.object.GameObject;
import org.excobot.game.api.util.Random;
import static org.excobot.game.api.util.Time.sleep;
import org.excobot.game.api.wrappers.media.animable.actor.Player;
import org.excobot.game.api.wrappers.scene.Tile;

@Manifest(singleInstance = false, authors = {"Toastedmeat"}, version = 1.2, description = "Flax", name = "ToastyFlaxerExco")
public class ToastyFlaxerExco extends GameScript implements PaintListener {

    final int myVersion = 2;

    //Area's
    private final Area gflaxSpot1 = new Area(2439, 3393, 2454, 3411);
    private final Area gflaxSpot2 = new Area(2465, 3402, 2472, 3408);
    private final Area gflaxSpot3 = new Area(2479, 3396, 2484, 3399);

    private final Area sFlaxSpot = new Area(2735, 3436, 2751, 3451);

    private final Area gBankArea = new Area(2444, 3415, 2447, 3431);
    private final Area gBottomArea = new Area(2442, 3411, 2448, 3418);

    private final Area sBankArea = new Area(2721, 3490, 2727, 3493);

    // Paths
    private final int[][] toGSpot1 = new int[][]{{2446, 3415}, {2448, 3404}};
    private final int[][] toGSpot2 = new int[][]{{2446, 3415}, {2459, 3409}, {2469, 3404}};
    private final int[][] toGSpot3 = new int[][]{{2446, 3415}, {2459, 3409}, {2469, 3399}, {2481, 3397}};

    private final int[][] toSSpot = new int[][]{{2725, 3491}, {2728, 3480}, {2728, 3469}, {2727, 3461}, {2729, 3452}, {2739, 3447}};
    // ID's
    private final int gBankID = 2196;
    private final int sBankID = 25808;

    //Objects
    private GameObject bankBooth, stairsUp, stairsDown, flax, failStairs;

    //Rectangles
    private final Rectangle shading = new Rectangle(0, 270, 515, 68);

    //Ints
    private int spotChooser;

    //Run
    private boolean run;
    private int runInt;

    //Booleans
    private boolean walkedToFlax;

    private boolean gnome, seers;

    //Timers
    private long startTime = 0L, millis = 0L, hours = 0L;
    private long minutes = 0L, seconds = 0L, last = 0L;

    private long playerLastMoved;

    //Profits
    private double priceFlax, profit = 0, flaxTemp;
    private double flaxCounter;

    private int profitPH = 0, flaxPH;

    private enum State {

        IDLE, FAILSAFE, STAIRSDOWN, STAIRSUP, WALKTOFLAX, WALKTOBANK, PICKINGFLAX, BANK;
    }

    private State state = State.IDLE;

    @Override
    public boolean start() {

        //Init Loot Variables
        priceFlax = 0;
        flaxCounter = 0;
        flaxPH = 0;
        flaxTemp = Inventory.getCount("Flax", false);
        profit = 0;
        profitPH = 0;

        //Run
        run = false;
        runInt = Random.nextInt(15, 27);

        //Booleans
        walkedToFlax = false;

        //start timer
        startTime = System.currentTimeMillis();

        rsItem f = new rsItem("flax");
        priceFlax = f.getAveragePrice();

        FlaxerGui.buildGUI();
        FlaxerGui.frame.setSize(140, 250);
        FlaxerGui.frame.setLocation(300, 300);
        FlaxerGui.frame.setVisible(true);

        gnome = FlaxerGui.rdbtnGnome.isSelected();
        seers = FlaxerGui.rdbtnSeers.isSelected();
        int tempSpot = Integer.parseInt(FlaxerGui.textSpot.getText());
        spotChooser = tempSpot;

        return true;
    }

    @Override
    public int execute() throws InterruptedException {
        if (FlaxerGui.frame.isVisible()) {
            gnome = FlaxerGui.rdbtnGnome.isSelected();
            seers = FlaxerGui.rdbtnSeers.isSelected();
            int tempSpot = Integer.parseInt(FlaxerGui.textSpot.getText());
            spotChooser = tempSpot;
            Mouse.setSpeed(Random.nextInt(5, 8));
            if (gnome) {
                //Application.A("Picking in the Gnome Stronghold");
            } else if (seers) {
                //Application.A("Picking in the flax field in Seers village");
            }
            //Application.A("Price of flax: " + priceFlax);
            //Application.A("Starting ToastyFlaxerExco v" + myVersion);
            return 500;
        }
        if (Players.getLocal() != null && Players.getLocal().isMoving()) {
            playerLastMoved = System.currentTimeMillis();
        }
        state = checkState();

        switch (state) {
            case STAIRSDOWN:
                //Application.A("Going down the stairs.");
                stairsDown = GameObjects.getNearest("Staircase");
                if (stairsDown.isOnGameScreen()) {
                    stairsDown.interact("Climb-down");
                    sleep(Random.nextInt(600, 700));
                } else {
                    Movement.walkTileMM(new Tile(2445, 3416, 1));
                    sleep(Random.nextInt(1000, 1200));
                }
                break;
            case STAIRSUP:
                //Application.A("Going up the stairs.");
                stairsUp = GameObjects.getNearest("Staircase");
                stairsUp.interact("Climb-up");
                sleep(Random.nextInt(800, 900));
                if (Players.getLocal().getLocation().getPlane() == 1) {
                    Movement.walkTileMM(new Tile(2445, 3424, 1));
                    sleep(Random.nextInt(1000, 1200));
                }
                break;
            case WALKTOFLAX:
                walkToFlax(spotChooser);
                break;
            case WALKTOBANK:
                walkToBank(spotChooser);
                break;
            case PICKINGFLAX:
                pickFlax(spotChooser);
                break;
            case BANK:
                bank();
                break;
        }
        runningCheck();
        return 300;
    }

    @Override
    public void repaint(Graphics g) {
        if (Inventory.getCount("Flax", false) != flaxTemp && state == State.PICKINGFLAX) {
            ++flaxCounter;
            flaxTemp = Inventory.getCount("Flax", false);
        }

        // Timer Variables
        millis = (System.currentTimeMillis() - startTime);
        hours = (millis / 3600000L);
        millis -= hours * 3600000L;
        minutes = (millis / 60000L);
        millis -= minutes * 60000L;
        seconds = (millis / 1000L);

        flaxPH = ((int) (flaxCounter * 3600000.0D / (System
                .currentTimeMillis() - startTime)));

        profit = ((priceFlax * flaxCounter) / 1000);
        profitPH = ((int) (profit * 3600000.0D / (System.currentTimeMillis() - startTime)));

        Graphics2D g2 = (Graphics2D) g;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.40f));
        g2.setColor(Color.BLACK);
        g2.fill(shading);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                1.00f));

        g2.setColor(Color.RED);
        g2.drawString("Flax picked: " + flaxCounter, 125, 300);
        g2.drawString("Flax p/h: " + flaxPH, 125, 320);
        g2.drawString("Profit: " + profit + "k", 325, 300);
        g2.drawString("Profit p/h: " + profitPH + "k", 325, 320);

        g2.setColor(Color.BLACK);
        g2.drawString("State: " + state, 360, 458);
        g2.setColor(Color.CYAN);
        //g2.drawString("World: " + , 10, 14);
        g2.drawString("Time Elapsed: " + hours + " hours " + minutes
                + " minutes " + seconds + " seconds", 100, 14);
        g2.drawString("Idle Time: " + (getLastMovementTime() / 1000) + " Seconds", 375, 14);
        g2.drawString("Toasty's Flaxer", 0, 270);
        g2.drawString("Made by Toastedmeat V" + myVersion, 365, 337);

    }

    @Override
    public void onFinish() {
        FlaxerGui.CloseGUI();
        //Application.A("Picked: " + flaxCounter + " Flax");
        //Application.A("Profit gained: " + (flaxCounter * priceFlax) / 1000 + "k");
    }

    public State checkState() throws InterruptedException {
        flax = GameObjects.getNearest("Flax");
        if (gnome) {
            if (Players.getLocal().getX() == 2440 && Players.getLocal().getY() == 3404 && Players.getLocal().getLocation().getPlane() == 1) {
                failStairs = GameObjects.getNearest("Staircase");
                failStairs.interact("Climb-down");
                sleep(Random.nextInt(600, 700));
                Movement.walkTileMM(new Tile(2449, 3404, 0));
                sleep(Random.nextInt(1200, 1500));
                return State.FAILSAFE;
            } else if (Inventory.isFull() && (gBankArea.contains(Players.getLocal()) || gBottomArea.contains(Players.getLocal())) && Players.getLocal().getLocation().getPlane() == 0) {
                return State.STAIRSUP;
            } else if (Inventory.isFull() && !gBankArea.contains(Players.getLocal()) && !gBottomArea.contains(Players.getLocal())) {
                return State.WALKTOBANK;
            } else if (Inventory.isFull() && gBankArea.contains(Players.getLocal())) {
                return State.BANK;
            } else if (!Inventory.isFull() && gBankArea.contains(Players.getLocal()) && Players.getLocal().getLocation().getPlane() == 1) {
                return State.STAIRSDOWN;
            } else if (!Inventory.isFull() && (gBottomArea.contains(Players.getLocal()) || !walkedToFlax)) {
                return State.WALKTOFLAX;
            } else if (flax != null && (gflaxSpot1.contains(Players.getLocal()) || gflaxSpot2.contains(Players.getLocal())
                    || gflaxSpot3.contains(Players.getLocal()))) {
                return State.PICKINGFLAX;
            }
        } else if (seers) {
            if (new Area(2725, 3490, 2729, 3494).contains(Players.getLocal()) && Players.getLocal().getLocation().getPlane() == 1) {
                failStairs = GameObjects.getNearest("Ladder");
                failStairs.interact("Climb-down");
                sleep(Random.nextInt(600, 700));
                Movement.walkTileMM(new Tile(2724, 3493, 0));
                sleep(Random.nextInt(1200, 1500));
                return State.FAILSAFE;
            } else if (Inventory.isFull() && !sBankArea.contains(Players.getLocal())) {
                return State.WALKTOBANK;
            } else if (Inventory.isFull() && sBankArea.contains(Players.getLocal())) {
                return State.BANK;
            } else if (!Inventory.isFull() && !walkedToFlax) {
                return State.WALKTOFLAX;
            } else if (flax != null && sFlaxSpot.contains(Players.getLocal())) {
                return State.PICKINGFLAX;
            }
        }
        return State.IDLE;
    }

    public void walkToFlax(int spot) throws InterruptedException {
        if (gnome) {
            switch (spot) {
                case 1:
                    WalkAlongPath(toGSpot1, true);
                    if (gflaxSpot1.contains(Players.getLocal())) {
                        walkedToFlax = true;
                    }
                    break;
                case 2:
                    WalkAlongPath(toGSpot2, true);
                    if (gflaxSpot2.contains(Players.getLocal())) {
                        walkedToFlax = true;
                    }
                    break;
                case 3:
                    WalkAlongPath(toGSpot3, true);
                    if (gflaxSpot3.contains(Players.getLocal())) {
                        walkedToFlax = true;
                    }
                    break;
            }
        } else if (seers) {
            switch (spot) {
                case 1:
                    WalkAlongPath(toSSpot, true);
                    if (sFlaxSpot.contains(Players.getLocal()) && !Players.getLocal().isMoving()) {
                        walkedToFlax = true;
                    }
                    break;
            }
        }
    }

    public void walkToBank(int spot) throws InterruptedException {
        if (gnome) {
            switch (spot) {
                case 1:
                    WalkAlongPath(toGSpot1, false);
                    break;
                case 2:
                    WalkAlongPath(toGSpot2, false);
                    break;
                case 3:
                    WalkAlongPath(toGSpot3, false);
                    break;
            }
        } else if (seers) {
            switch (spot) {
                case 1:
                    WalkAlongPath(toSSpot, false);
                    break;
            }
        }
    }

    public void pickFlax(int spot) throws InterruptedException {
        if (Players.getLocal().getAnimation() != -1) {
            playerLastMoved = System.currentTimeMillis();
        }
        if (Camera.getPitch() < 80) {
            Camera.setPitch(Random.nextInt(82, 100));
            sleep(Random.nextInt(400, 600));
        }
        if (gnome) {
            switch (spot) {
                case 1:
                    if (gflaxSpot1.contains(Players.getLocal())) {
                    flax = GameObjects.getNearest("Flax");
                    if (flax != null && !Players.getLocal().isMoving()) {
                        flax.interact("Pick");
                        sleep(Random.nextInt(400, 600));
                    }

                } else {
                    Movement.walkTileMM(gflaxSpot1.getCentralTile());
                }
                    break;
                case 2:
                    if (gflaxSpot2.contains(Players.getLocal())) {
                    if (Players.getLocal().getAnimation() == -1) {
                        flax = GameObjects.getNearest("Flax");
                        if (flax != null && !Players.getLocal().isMoving()) {
                            flax.interact("Pick");
                            sleep(Random.nextInt(300, 600));
                        }
                    }
                } else {
                    Movement.walkTileMM(gflaxSpot2.getCentralTile());
                }
                    break;
                case 3:
                    if (gflaxSpot3.contains(Players.getLocal())) {
                    if (Players.getLocal().getAnimation() == -1) {
                        flax = GameObjects.getNearest("Flax");
                        if (flax != null && !Players.getLocal().isMoving()) {
                            flax.interact("Pick");
                            sleep(Random.nextInt(300, 600));
                        }
                    }
                } else {
                    Movement.walkTileMM(gflaxSpot2.getCentralTile());
                }
                    break;
            }
        } else if (seers) {
            switch (spot) {
                case 1:
                    if (sFlaxSpot.contains(Players.getLocal())) {
                    flax = GameObjects.getNearest("Flax");
                    if (flax != null && !Players.getLocal().isMoving()) {
                        if (Players.getLocal().getAnimation() == -1) {
                            flax.interact("Pick");
                            sleep(Random.nextInt(300, 600));
                        }
                    }

                } else {
                    Movement.walkTileMM(sFlaxSpot.getCentralTile());
                }
                    break;
            }
        }

    }

    public void bank() throws InterruptedException {
        if (gnome) {
            bankBooth = GameObjects.getNearest(gBankID);
        } else if (seers) {
            bankBooth = GameObjects.getNearest(sBankID);
        }
        walkedToFlax = false;
        if (Bank.isOpen()) {
            if (Inventory.isFull()) {
                Bank.depositAll();
                sleep(Random.nextInt(800, 900));
                Bank.close();
                sleep(Random.nextInt(800, 900));
                flaxTemp = Inventory.getCount("Flax", false);
            }
        } else {
            if (bankBooth != null) {
                if (bankBooth.getLocation().distance(Players.getLocal()) < 7) {
                    if (bankBooth.isOnGameScreen()) {
                        if (!Players.getLocal().isMoving()) {
                            bankBooth.interact("Bank");
                            sleep(Random.nextInt(700, 900));
                            bank();
                        }
                    } else {
                        Camera.setPitch(Random.nextInt(0, 15));
                        Camera.setAngle(Random.nextInt(0, 25));
                    }
                } else if (bankBooth.getLocation().distance(Players.getLocal()) > 5 && gnome) {
                    //Application.A("Walked");
                    Movement.walkTileMM(new Tile(2445, 3424, 1));
                } else if (bankBooth.getLocation().distance(Players.getLocal()) > 5 && seers) {
                    //Application.A("Walked");
                    Movement.walkTileMM(new Tile(2724, 3493, 0));
                }
            }
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private long getLastMovementTime() {
        return System.currentTimeMillis() - playerLastMoved;
    }

    public class Area {

        private int x1, y1, x2, y2;

        public Area(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public boolean contains(Player player) {
            int x = player.getX(), y = player.getY();
            return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
        }

        public Tile getCentralTile() {
            return new Tile((x1 + x2) / 2, (y1 + y2) / 2);
        }
    }

    public void runningCheck() throws InterruptedException {
        run = Movement.isRunEnabled();
        //Application.A("isRunning: " + Boolean.toString(run));
        if ((run == false) && (Movement.getEnergy() >= runInt)) {
            Tabs.SETTINGS.open();
            sleep(Random.nextInt(900, 1279));
            Movement.setRun(true);
            sleep(Random.nextInt(700, 900));
            Tabs.INVENTORY.open();
        }
        if (Movement.getEnergy() < 10) {
            runInt = Random.nextInt(15, 47);
        }
    }

    public boolean WalkAlongPath(int[][] path, boolean forward,
            int disFromEnd) throws InterruptedException {
        if (disToPt(forward ? path[path.length - 1][0]
                : path[0][0], forward ? path[path.length - 1][1]
                : path[0][1]) <= disFromEnd) {
            return true;
        } else {
            WalkAlongPath(path, forward);
            return false;
        }
    }

    public void WalkAlongPath(int[][] path, boolean forward) throws InterruptedException {
        int des = 0;
        for (int i = 0; i < path.length; i++) {
            if (disToPt(path[i][0], path[i][1]) < disToPt(
                    path[des][0], path[des][1])) {
                des = i;
            }
        }
        if (Players.getLocal().isMoving()
                && disToPt(path[des][0], path[des][1]) > (Movement.isRunEnabled() ? 3
                : 2)) {
            return;
        }
        if (forward && des != path.length - 1
                || !forward && des != 0) {
            des += (forward ? 1 : -1);
        }
        //Application.A("Walking to point:" + destination);
        Movement.walkTileMM(new Tile(path[des][0], path[des][1]));

    }

    private int disToPt(int ptOne, int ptTwo) {
        return (int) Math.sqrt(Math
                .pow(Players.getLocal().getX() - ptOne, 2)
                + Math.pow(Players.getLocal().getY() - ptTwo, 2));
    }

}

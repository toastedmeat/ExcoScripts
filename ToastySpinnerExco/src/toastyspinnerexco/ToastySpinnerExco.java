/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toastyspinnerexco;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.excobot.Application;
import org.excobot.bot.event.listeners.PaintListener;
import org.excobot.bot.script.GameScript;
import org.excobot.bot.script.Manifest;
import org.excobot.game.api.methods.cache.Game;
import org.excobot.game.api.methods.cache.media.Widgets;
import org.excobot.game.api.methods.input.Keyboard;
import org.excobot.game.api.methods.input.Mouse;
import org.excobot.game.api.methods.media.Bank;
import org.excobot.game.api.methods.media.animable.GameObjects;
import org.excobot.game.api.methods.media.animable.actor.Players;
import org.excobot.game.api.methods.scene.Movement;
import org.excobot.game.api.methods.tab.Inventory;
import org.excobot.game.api.methods.tab.Skills;
import org.excobot.game.api.methods.tab.Tabs;
import org.excobot.game.api.util.Random;
import static org.excobot.game.api.util.Time.sleep;
import org.excobot.game.api.util.impl.Filter;
import org.excobot.game.api.wrappers.media.animable.actor.Player;
import org.excobot.game.api.wrappers.media.animable.object.GameObject;
import org.excobot.game.api.wrappers.scene.Path;
import org.excobot.game.api.wrappers.scene.Tile;

/**
 *
 * @author Eric
 */
@Manifest(authors = "toastedmeat", name = "Toasty's Spinner",
        description = "Spins flax into bow string in lumbridge.", version = 1.3)
public class ToastySpinnerExco extends GameScript implements PaintListener {

    final int myVersion = 1;

    private final Font regFont = new Font("Serif", Font.BOLD, 13);

    //private final Area lumby = new Area(3203, 3231, 3216, 3207);
    private final Area lumby = new Area(3202, 3206, 3217, 3230);
    //private final Area dead = new Area(3216, 3230, 3224, 3208);
    private final Area dead = new Area(3218, 3210, 3224, 3230);

    //Rectangles
    private final Rectangle shading = new Rectangle(0, 270, 515, 68);

    //Timers
    private long startTime = 0L, millis = 0L, hours = 0L;
    private long minutes = 0L, seconds = 0L, last = 0L;

    private long playerLastMoved;

    //ExP
    private double startCrafting, craftingExp, craftingExpPH;

    //running checks
    private boolean run;
    private int runInt;

    //profit
    private double priceFlax, priceBS, profit = 0, flaxTemp;
    private int bsMade, bsPH;
    private int profitPH = 0;

    boolean firstClick;

    private enum State {

        idle, failSafe, died, walkBackToBank, bank, walkDown, walkUp, spin, close;
    }
    private State state = State.idle;

    @Override
    public boolean start() {

        //Timers
        playerLastMoved = System.currentTimeMillis();
        startTime = System.currentTimeMillis();

        //Counters
        bsMade = 0;

        //Items Prices
        rsItem bs = new rsItem("Bow+string");
        priceBS = bs.getAveragePrice();
        rsItem f = new rsItem("flax");
        priceFlax = f.getAveragePrice();

        //Run
        run = false;
        runInt = Random.nextInt(15, 27);

        //Exp
        startCrafting = Skills.CRAFTING.getExperience();

        //Mouse
        Mouse.setSpeed(Random.nextInt(5, 8));

        flaxTemp = Inventory.getCount("Flax", false);

        firstClick = false;

        Application.log("Price of Bow Strings: " + priceBS, Color.RED);
        Application.log("Price of Bow Strings: " + priceFlax, Color.RED);

        return true;
    }

    @Override
    public int execute() throws InterruptedException {

        state = checkStates();

        switch (state) {
            case close:
                Widgets.getComponent(553, 10).hover();
                sleep(Random.nextInt(700, 800));
                Mouse.click(true);
                break;
            case failSafe:
                Tabs.MAGIC.open();
                sleep(Random.nextInt(1000, 1200));
                Widgets.getComponent(192, 0).interact("Cast");
                sleep(Random.nextInt(10000, 12000));
                break;
            case died:
                Path toStairs = new Path(new Tile[]{new Tile(3223, 3219, 0), new Tile(3214, 3210, 0), new Tile(3205, 3209, 0)});
                toStairs.traverse();
                break;
            case walkBackToBank:
                GameObjects.getNearest("Staircase").interact("Climb-up");
                sleep(Random.nextInt(800, 1200));
                break;
            case bank:
                bank();
                break;
            case walkDown:
                walkDown();
                break;
            case walkUp:
                walkUp();
                break;
            case spin:
                spin();
                break;
        }
        runningCheck();
        return 300;
    }

    @Override
    public void repaint(Graphics g) {

        if (Inventory.getCount("Flax", false) != flaxTemp && state == State.spin) {
            ++bsMade;
            flaxTemp = Inventory.getCount("Flax", false);
        }

        if ((Players.getLocal() != null && Players.getLocal().isMoving()) || Players.getLocal().getAnimation() != -1) {
            playerLastMoved = System.currentTimeMillis();
        }

        // Timer Variables
        millis = (System.currentTimeMillis() - startTime);
        hours = (millis / 3600000L);
        millis -= hours * 3600000L;
        minutes = (millis / 60000L);
        millis -= minutes * 60000L;
        seconds = (millis / 1000L);

        bsPH = ((int) (bsMade * 3600000.0D / (System
                .currentTimeMillis() - startTime)));

        profit = (((priceBS * bsMade) - (priceFlax * bsMade)) / 1000);
        profitPH = ((int) (profit * 3600000.0D / (System.currentTimeMillis() - startTime)));

        craftingExp = (Skills.CRAFTING.getExperience() - startCrafting);
        craftingExpPH = ((int) (craftingExp * 3600000.0D / (System.currentTimeMillis() - startTime)));

        Graphics2D g2 = (Graphics2D) g;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.40f));
        g2.setColor(Color.BLACK);
        g2.fill(shading);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                1.00f));

        g2.setFont(regFont);
        g2.setColor(Color.cyan);

        g2.drawRect(Mouse.getX() - 6, Mouse.getY() - 6, 12, 12);
        g2.drawString("☼", Mouse.getX() - 5, Mouse.getY() + 5);

        g2.drawString("Crafting Exp: " + craftingExp, 125, 280);
        g2.drawString("Strings Made: " + bsMade, 125, 300);
        g2.drawString("Profit: " + profit + "k", 125, 320);

        g2.drawString("Crafting Exp p/h: " + craftingExpPH, 325, 280);
        g2.drawString("Strings p/h: " + bsPH, 325, 300);
        g2.drawString("Profit p/h: " + profitPH + "k", 325, 320);

        g2.drawString("Time Elapsed: " + hours + " hours " + minutes
                + " minutes " + seconds + " seconds", 60, 14);
        g2.drawString("Idle Time: " + getLastMovementTime() + " Seconds", 375, 14);
        g2.drawString("State:" + state.toString(), 10, 300);
        g2.drawString("Toasty's Spinner", 0, 270);
        g2.drawString("Made by Toastedmeat V" + myVersion, 365, 337);
    }

    @Override
    public void onFinish() {
    }

    //~~~~~~~~~~~~~~~~~~~~~~ Calls ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public State checkStates() {
        if (Widgets.get(553) != null && Widgets.getComponent(553, 10).isVisible()) {
            return State.close;
        } else if (!lumby.contains(Players.getLocal()) && !dead.contains(Players.getLocal())) {
            return State.failSafe;
        } else if (dead.contains(Players.getLocal())) {
            return State.died;
        } else if (lumby.contains(Players.getLocal()) && Players.getLocal().getPlane() == 0) {
            return State.walkBackToBank;
        } else if (!Inventory.contains("Flax") && Players.getLocal().getPlane() == 1) {
            Movement.walkTileMM(new Tile(3205, 3209, 0));
            sleep(Random.nextInt(1200, 1300));
            return State.walkUp;
        } else if (!Inventory.contains("Flax") && Players.getLocal().getPlane() == 2) {
            return State.bank;
        } else if (Inventory.contains("Flax") && Players.getLocal().getPlane() == 2) {
            return State.walkDown;
        } else if (Inventory.contains("Flax") && Players.getLocal().getPlane() == 1) {
            return State.spin;
        }
        return State.idle;
    }

    public void bank() {
        firstClick = false;
        GameObject bankBooth = GameObjects.getNearest("Bank booth");//
        if (bankBooth != null && bankBooth.isOnGameScreen()) {
            if (Bank.isOpen()) {
                if (Inventory.contains("Flax")) {
                } else {
                    Bank.depositAll();
                    sleep(Random.nextInt(1000, 1500));
                    if (Bank.contains("Flax")) {
                        Bank.withdraw(Bank.getItem("Flax").getId(), Bank.Amount.ALL);
                        sleep(Random.nextInt(1000, 1500));
                        flaxTemp = Inventory.getCount("Flax", false);
                    } else {
                        Application.log("Ran out of flax.", Color.RED);
                        stop();
                    }
                }
            } else {
                bankBooth.interact("Bank");
            }
        } else {
            Movement.walkTileMM(new Tile(3208, 3218, 2));
        }
    }

    public void spin() {
        GameObject spinner = GameObjects.getNearest("Spinning wheel");//
        GameObject door2 = GameObjects.getNearestTo(new Tile(3208, 3214), new Filter<GameObject>() {
            public boolean accept(final GameObject gameObject) {
                return gameObject.getName().equalsIgnoreCase("Door");
            }
        });
        if (Players.getLocal().getLocation().getX() >= 3208) {
            if (Game.getColorAt(259, 428) == -16777088) {
                Application.log("Spinning", Color.GREEN);
                Keyboard.sendKeys("28", true);
                sleep(Random.nextInt(1500, 1900));
            }
            if ((Widgets.get(459) == null && getLastMovementTime() > 1.2) || !firstClick) {
                Application.log("Open Menu", Color.CYAN); //
                if (spinner.interact("Spin")) {
                    firstClick = true;
                    sleep(Random.nextInt(500, 900));
                    playerLastMoved = System.currentTimeMillis();
                }
            } else if (Widgets.get(459) != null && Widgets.getComponent(459, 92).isVisible()
                    && Players.getLocal().getAnimation() == -1) {
                Application.log("Make X", Color.YELLOW);
                Widgets.getComponent(459, 92).interact("Make X");
                sleep(Random.nextInt(500, 900));
                playerLastMoved = System.currentTimeMillis();
            }
        } else {
            if (door2 != null) {
                if(door2.interact("Open")){
                } else {
                    Movement.walkTileMM(new Tile(3209, 3213, 1));
                }
            }
        }
    }

    public void walkUp() {
        GameObject door = GameObjects.getNearestTo(new Tile(3208, 3214), new Filter<GameObject>() {
            public boolean accept(final GameObject gameObject) {
                return gameObject.getName().equalsIgnoreCase("Door");
            }
        });//
        GameObject stair = GameObjects.getNearestTo(new Tile(3204, 3208), new Filter<GameObject>() {
            public boolean accept(final GameObject gameObject) {
                return gameObject.getName().equalsIgnoreCase("Staircase");
            }
        });
        if (Players.getLocal().getLocation().getX() <= 3207) {
            if (stair.isOnGameScreen()) {
                if (stair.getLocation().getY() < 3210) {
                    Application.log("Up the stairs", Color.RED);
                    while (!stair.interact("Climb-up")) {
                    }
                    sleep(Random.nextInt(800, 1200));
                }
            } else if (stair.getLocation().getY() < 3210 && !stair.isOnGameScreen()) {
                Application.log("Can't see the stairs", Color.ORANGE);
                Movement.walkTileMM(new Tile(3204, 3208, 1));
            }
        } else if (door != null
                && door.getLocation().distance(Players.getLocal().getLocation()) < 4
                && Players.getLocal().getLocation().getX() >= 3208) {
            String[] act = door.getActions();
            if (door != null && act[0] != null && act[0].equalsIgnoreCase("Open")) {
                Application.log("Doors closed", Color.RED);
                door.interact("Open");
            } else {
                Application.log("To the stairs.", Color.RED);
                Movement.walkTileMM(new Tile(3204, 3208, 1));
            }
        }
    }

    public void walkDown() {
        if (GameObjects.getNearest("Staircase").isOnGameScreen()) {//
            if (GameObjects.getNearest("Staircase").interact("Climb-down")) {
                sleep(Random.nextInt(800, 1200));
            }
        } else {
            Movement.walkTileMM(new Tile(3205, 3208, 2));
            sleep(Random.nextInt(4500, 4800));
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~ Others ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private long getLastMovementTime() {
        return (System.currentTimeMillis() - playerLastMoved) / 1000;
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

        public boolean contains(GameObject obj) {
            int x = obj.getX(), y = obj.getY();
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

}

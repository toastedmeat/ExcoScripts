/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toastytut;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.excobot.Application;
import static org.excobot.Application.log;
import org.excobot.bot.event.listeners.PaintListener;
import org.excobot.bot.script.GameScript;
import org.excobot.bot.script.Manifest;
import org.excobot.game.api.methods.Calculations;
import org.excobot.game.api.methods.cache.Game;
import org.excobot.game.api.methods.cache.media.Widgets;
import org.excobot.game.api.methods.input.Mouse;
import org.excobot.game.api.methods.media.Bank;
import org.excobot.game.api.methods.media.animable.GameObjects;
import org.excobot.game.api.methods.media.animable.actor.NPCs;
import org.excobot.game.api.methods.media.animable.actor.Players;
import org.excobot.game.api.methods.scene.Camera;
import org.excobot.game.api.methods.scene.Movement;
import org.excobot.game.api.methods.tab.Equipment;
import org.excobot.game.api.methods.tab.Inventory;
import org.excobot.game.api.methods.tab.Tabs;
import org.excobot.game.api.util.Random;
import static org.excobot.game.api.util.Time.sleep;
import org.excobot.game.api.wrappers.cache.media.Component;
import org.excobot.game.api.wrappers.cache.media.Widget;
import org.excobot.game.api.wrappers.media.animable.actor.NPC;
import org.excobot.game.api.wrappers.media.animable.actor.Player;
import org.excobot.game.api.wrappers.media.animable.object.GameObject;
import org.excobot.game.api.wrappers.scene.Tile;

@Manifest(authors = {"Toastedmeat"}, description = "Tutorial Island", name = "Toasty's Tutorial Island", version = 1.1)
public class ToastyTutExco extends GameScript implements PaintListener {

    private final int myVersion = 2;

    private final Area firstDoor = new Area(3097, 3107, 3097, 3017);
    private final Area cookingArea = new Area(3073, 3082, 3078, 3091);
    //Paths
    private int[][] toFisher = new int[][]{{3098, 3107}, {3103, 3104}, {3103, 3100}, {3102, 3095}};
    private int[][] toGate1 = new int[][]{{3101, 3096}, {3095, 3091}, {3090, 3092}};
    private int[][] toCookingArea = new int[][]{{3089, 3092}, {3086, 3083}, {3079, 3084}};
    private int[][] toQuester = new int[][]{{3071, 3090}, {3070, 3100},
    {3070, 3110}, {3073, 3120}, {3079, 3128}, {3086, 3127}
    };
    private int[][] toMiningSmith = new int[][]{{3088, 9520}, {3079, 9516}, {3081, 9508}, {3081, 9505}};
    private int[][] toCombat = new int[][]{{3082, 9499}, {3086, 9504}, {3094, 9503}};
    private int[][] toChurch = new int[][]{{3130, 3124}, {3134, 3116}, {3130, 3109}, {3129, 3107}};
    private int[][] toWizard = new int[][]{{3122, 3102}, {3126, 3095}, {3130, 3089}, {3140, 3087}};

    //Boolean's
    private boolean passedGate1, chefDoor, passedQuest, upToDate;
    //for running
    private boolean run;
    private int runInt;

    //Timer
    private long startTime = 0L, millis = 0L, hours = 0L;
    private long minutes = 0L, seconds = 0L;

    private enum State {

        IDLE, FIRST, SURVIVAL, COOKING, QUEST, MINESMITH, COMB, BANK,
        FINANCE, PRAYER, MAGIC, DONE, CONTINUECONVO;
    }
    private State state = State.IDLE;

    //ints
    @Override
    public boolean start() {
        startTime = System.currentTimeMillis();
        passedGate1 = false;
        chefDoor = false;
        passedQuest = false;
        upToDate = false;
        checkVersion();
        return true;

    }

    @Override
    public int execute() throws InterruptedException {
        if (!upToDate) {
            log("[Toasty Tutorial island] is not up to date!", Color.RED);
            stop();
        }
        state = checkStates();
        switch (state) {
            case FIRST:
                init();
                break;
            case SURVIVAL:
                survival();
                break;
            case COOKING:
                cook();
                break;
            case QUEST:
                questing();
                break;
            case MINESMITH:
                mineAndSmith();
                break;
            case COMB:
                combat();
                break;
            case BANK:
                banking();
                break;
            case FINANCE:
                finance();
                break;
            case PRAYER:
                prayer();
                break;
            case MAGIC:
                magic();
                break;
            case DONE:
                log("All done.");
                stop();
                break;
            case CONTINUECONVO:
                while (continueConvo()) {
            }
                break;
        }
        if (state != State.FIRST) {
            runningCheck();
        }

        return 600;
    }

    @Override
    public void repaint(Graphics g) {

        millis = (System.currentTimeMillis() - startTime);
        hours = (millis / 3600000L);
        millis -= hours * 3600000L;
        minutes = (millis / 60000L);
        millis -= minutes * 60000L;
        seconds = (millis / 1000L);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.CYAN);

        g2.drawString("Time Elapsed: " + hours + " hours " + minutes
                + " minutes " + seconds + " seconds", 150, 293);

        g2.setFont(new Font("Serif", Font.BOLD, 12));
        g2.drawString("State: " + state, 325, 336);

        g2.drawRect(Mouse.getX() - 6, Mouse.getY() - 6, 12, 12);
        g2.drawString("☼", Mouse.getX() - 5, Mouse.getY() + 5);
    }

    @Override
    public void onFinish() {
        if (Thread.currentThread().isInterrupted()) {
            Thread.currentThread().getUncaughtExceptionHandler();
        }
    }

    //~~~~~~~~~~~~~~~~~~~~ EXTRAS ~~~~~~~~~~~~~~~~~~~
    public State checkStates() throws InterruptedException {
        if (continueConvo()) {
            return State.CONTINUECONVO;
        } else if (new Area(3228, 3226, 3235, 3235).contains(Players.getLocal())) {
            return State.DONE;
        } else if ((canSeeText("Your final instructor!") && Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Your final instructor!"))
                || canSeeText("Open up your final menu.")
                || canSeeText("Cast Wind Strike at a chicken.")
                || canSeeText("You have almost completed the tutorial!")
                || (canSeeText("Yes") && Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("You have almost completed the tutorial!"))) {
            return State.MAGIC;
        } else if (canSeeText("Prayer.")
                || canSeeText("Your Prayer menu.")
                || canSeeText("Talk with Brother Brace and he'll tell you about prayers.")
                || canSeeText("Friends list.")
                || canSeeText("This is your friends list.")
                || canSeeText("This is your ignore list.")
                || canSeeText("Your final instructor!")) {
            return State.PRAYER;
        } else if (canSeeText("Financial advice.")
                || canSeeText("Continue through the next door.")) {
            return State.FINANCE;
        } else if (canSeeText("Banking.")
                || canSeeText("This is your bank box.")
                || (canSeeText("Yes") && Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Banking."))) {
            return State.BANK;
        } else if (canSeeText("You've finished in this area.")
                || canSeeText("Combat.")
                || canSeeText("Wielding weapons.")
                || canSeeText("This is your worn inventory.")
                || canSeeText("Worn interface")
                || canSeeText("You're now holding your dagger.")
                || canSeeText("Unequipping items.")
                || canSeeText("Combat interface.")
                || canSeeText("This is your combat interface.")
                || canSeeText("Attacking.")
                || canSeeText("Sit back and watch.")
                || canSeeText("Well done, you've made your first kill!")
                || canSeeText("Rat ranging.")
                || (canSeeText("Moving on.") && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Moving on."))) {
            return State.COMB;
        } else if (canSeeText("Mining and Smithing.")
                || canSeeText("Prospecting.")
                || canSeeText("It's tin.")
                || canSeeText("It's copper.")
                || canSeeText("Mining.")
                || canSeeText("Smelting.")
                || canSeeText("You've made a bronze bar!")
                || canSeeText("Smithing a dagger.")) {
            return State.MINESMITH;
        } else if (canSeeText("Run to the next guide.")
                || canSeeText("Talk with the Quest Guide.")
                || canSeeText("Open the Quest Journal.")
                || canSeeText("Your Quest Journal.")
                || canSeeText("Moving on.")) {
            return State.QUEST;
        } else if (canSeeText("Find your next instructor.")
                || canSeeText("Making dough.")
                || canSeeText("Cooking dough.")
                || canSeeText("Cooking dough")
                || canSeeText("The music player.")
                || canSeeText("Emotes.")
                || canSeeText("Emotes.")
                || canSeeText("Running.")) {
            return State.COOKING;
        } else if (canSeeText("Moving around")
                || canSeeText("Viewing the items that you were given.")
                || canSeeText("Cut down a tree")
                || canSeeText("Making a fire")
                || canSeeText("to see your skill stats.")
                || canSeeText("Your skill stats.")
                || canSeeText("Catch some Shrimp.")
                || canSeeText("Cooking your shrimp.")
                || canSeeText("Burning your shrimp.")
                || canSeeText("Well done, you've just cooked your first RuneScape meal.")) {
            return State.SURVIVAL;
        } else if (canSeeText("Moving around")
                || canSeeText("Getting started")
                || canSeeText("Please click on the flashing spanner icon found at the bottom")
                || canSeeText("Player controls")
                || canSeeText("Interacting with scenery")) {
            return State.FIRST;
        }
        return State.IDLE;
    }

    public void init() throws InterruptedException {
        NPC first = NPCs.getNearest("RuneScape Guide");
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Getting started")
                && Widgets.getComponent(372, 0).isVisible()) {
            log("Talking to RuneScape Guide");
            first.interact("Talk-to");
            sleep(Random.nextInt(800, 900));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 2).getText().equalsIgnoreCase("Please click on the flashing spanner icon found at the bottom")) {
            log("Opening settings");
            Tabs.SETTINGS.open();
            sleep(Random.nextInt(600, 700));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Player controls")
                && Widgets.getComponent(421, 1).isVisible()) {
            log("Talking to RuneScape Guide");
            first.interact("Talk-to");
            sleep(Random.nextInt(600, 700));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Interacting with scenery")) {
            log("Opening door and leaving.");
            Movement.walkTileMM(firstDoor.getCentralTile());
            sleep(Random.nextInt(800, 900));
            GameObject door1 = GameObjects.getNearest(2124);//"Door"
            door1.interact("Open");
            sleep(Random.nextInt(1800, 1900));
        }
    }

    public void survival() throws InterruptedException {

        if (Widgets.get(372) != null
                && (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Moving around"))
                && Widgets.getComponent(372, 0).isVisible()) {
            log("Walking to survival expert");
            WalkAlongPath(toFisher, true);
            NPC survive = NPCs.getNearest("Survival Expert");
            survive.interact("Talk-to");
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Viewing the items that you were given.")) {
            log("Opening inventory.");
            Tabs.INVENTORY.open();
            sleep(Random.nextInt(600, 700));
        }

        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Cut down a tree")) {
            log("Chopping down a tree.");
            GameObject tree = GameObjects.getNearest("Tree");
            if (tree != null && Players.getLocal().getAnimation() == -1) {
                tree.interact("Chop down");
                sleep(Random.nextInt(1500, 2100));
            }
        }

        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Making a fire")) {
            log("Making fire.");
            Inventory.getItem("Tinderbox").interact("Use");
            sleep(Random.nextInt(300, 600));
            Inventory.getItem("Logs").interact("Use");
            sleep(Random.nextInt(1200, 1600));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 3).getText().equalsIgnoreCase("to see your skill stats.")) {
            log("Opening skills");
            Tabs.SKILLS.open();
            sleep(Random.nextInt(700, 900));
            Tabs.INVENTORY.open();
            sleep(Random.nextInt(700, 900));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Your skill stats.")) {
            log("Talking to Survival Expert");
            NPC survive = NPCs.getNearest("Survival Expert");
            survive.interact("Talk-to");
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Catch some Shrimp.")) {
            log("Fishing for shrimp");
            NPC spot = NPCs.getNearest("Fishing spot");
            if (Players.getLocal().getAnimation() == -1) {
                spot.interact("Net");
                sleep(Random.nextInt(6500, 6800));
                spot.interact("Net");
                sleep(Random.nextInt(6500, 6800));
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Cooking your shrimp.")) {
            log("Cooking shrimp");
            GameObject fire = GameObjects.getNearest("Fire");
            if (fire != null) {
                Inventory.getItem("Raw shrimps").interact("Use");
                sleep(Random.nextInt(600, 800));
                fire.interact("Use", "Raw shrimps -> Fire");
                sleep(Random.nextInt(600, 800));
            } else {
                log("No fire so, Making fire.");
                if (Inventory.contains("Logs")) {
                    Inventory.getItem("Tinderbox").interact("Use");
                    sleep(Random.nextInt(300, 600));
                    Inventory.getItem("Logs").interact("Use");
                    sleep(Random.nextInt(1200, 1600));
                } else {
                    log("No logs so, Chopping down a tree.");
                    GameObject tree = GameObjects.getNearest("Tree");
                    tree.interact("Chop down");
                    sleep(Random.nextInt(1500, 2100));
                }
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Burning your shrimp.")) {
            log("Burnt a shrimp");
            GameObject fire = GameObjects.getNearest("Fire");
            if (Inventory.contains("Raw shrimps")) {
                if (fire != null) {
                    Inventory.getItem("Raw shrimps").interact("Use");
                    sleep(Random.nextInt(600, 800));
                    fire.interact("Use", "Raw shrimps -> Fire");
                    sleep(Random.nextInt(600, 800));
                } else {
                    log("No fire, Making fire.");
                    if (Inventory.contains("Logs")) {
                        Inventory.getItem("Tinderbox").interact("Use");
                        sleep(Random.nextInt(300, 600));
                        Inventory.getItem("Logs").interact("Use");
                        sleep(Random.nextInt(1200, 1600));
                    } else {
                        log("No logs, Chopping down a tree.");
                        GameObject tree = GameObjects.getNearest("Tree");
                        tree.interact("Chop down");
                        sleep(Random.nextInt(1500, 2100));
                    }
                }
            } else {
                log("No shrimp so, Catching Shrimp");
                NPC spot = NPCs.getNearest("Fishing spot");
                if (Players.getLocal().getAnimation() == -1) {
                    spot.interact("Net");
                    sleep(Random.nextInt(6500, 6800));
                }
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Well done, you've just cooked your first RuneScape meal.")) {
            WalkAlongPath(toGate1, true);
            GameObject gate = GameObjects.getNearest(2126);//"Gate"
            if (gate != null && gate.getLocation().distance(Players.getLocal().getLocation()) < 5) {
                gate.interact("Open");
                sleep(Random.nextInt(1300, 1500));
            }

        }
    }

    public void cook() throws InterruptedException {
        NPC chef = NPCs.getNearest("Master Chef");
        if (Players.getLocal().getX() == 3078 && Players.getLocal().getY() == 3084 && Players.getLocal().getPlane() == 0) {
            chefDoor = true;
        }
        if ((!cookingArea.contains(Players.getLocal()) || (chef != null && !chefDoor && !Calculations.canReach(chef.getLocation())))
                && Widgets.get(372) != null /*chef.getLocation().distance(Players.getLocal().getLocation()) > 3 && */
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Find your next instructor.")) {
            Application.log("Walking to cook");
            WalkAlongPath(toCookingArea, true);
            sleep(Random.nextInt(1400, 1600));
            GameObject door2 = GameObjects.getNearest(2127);//"Door"
            door2.interact("Open");
            sleep(Random.nextInt(2000, 2800));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Find your next instructor.")
                && chef != null && Calculations.canReach(chef.getLocation())) {
            Application.log("Talking to cook");
            if (chef != null && chef.getLocation().distance(Players.getLocal().getLocation()) < 10) {
                chef.interact("Talk-to");
                sleep(Random.nextInt(1600, 1800));
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Making dough.")) {
            Application.log("Combining items");
            Inventory.getItem("Pot of flour").interact("Use");
            sleep(Random.nextInt(300, 400));
            Inventory.getItem("Bucket of water").interact("Use");
            sleep(Random.nextInt(400, 600));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Cooking dough.")) {
            Application.log("Cooking dough");
            GameObject range = GameObjects.getNearest(2457);//"Range"
            Inventory.getItem("Bread dough").interact("Use");
            sleep(Random.nextInt(400, 500));
            range.interact("Use", "Bread dough -> Range");
            sleep(Random.nextInt(600, 800));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Cooking dough")) {
            Tabs.MUSIC.open();
            sleep(Random.nextInt(600, 800));
            Tabs.INVENTORY.open();
            sleep(Random.nextInt(400, 500));
            Movement.walkTileMM(new Tile(3073, 3090, 0));
            sleep(Random.nextInt(600, 800));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("The music player.")) {

            if (Players.getLocal().getX() == 3073 && Players.getLocal().getY() == 3090 && Players.getLocal().getPlane() == 0) {
                GameObject door = GameObjects.getNearest(2128);//"Door"
                door.interact("Open");
                sleep(Random.nextInt(600, 800));
            } else {
                Movement.walkTileMM(new Tile(3073, 3090, 0));
                sleep(Random.nextInt(600, 800));
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Emotes.")) {
            Tabs.EMOTES.open();
            sleep(Random.nextInt(600, 800));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Emotes.")) {
            Widgets.getComponent(464, 47).interact("Laugh");
            sleep(Random.nextInt(600, 800));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Running.")) {
            Tabs.SETTINGS.open();
            Movement.setRun(true);
            sleep(Random.nextInt(400, 600));
        }
    }

    public void questing() throws InterruptedException {
        NPC questGuide = NPCs.getNearest("Quest Guide");
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Run to the next guide.")) {
            WalkAlongPath(toQuester, true);
            sleep(Random.nextInt(400, 500));

            if (Players.getLocal().getLocation().distance(new Tile(3086, 3127, 0).getLocation()) < 3) {
                GameObject door = GameObjects.getNearest(2129);//"Door"
                door.interact("Open");
                sleep(Random.nextInt(700, 1000));
            }
        }

        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 1).getText().equalsIgnoreCase("Talk with the Quest Guide.")
                && questGuide != null && Calculations.canReach(questGuide.getLocation())) {

            questGuide.interact("Talk-to");
            sleep(Random.nextInt(600, 800));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 1).getText().equalsIgnoreCase("Open the Quest Journal.")) {
            Tabs.QUEST.open();
            sleep(Random.nextInt(600, 800));
        }
        if (Widgets.get(372) != null && questGuide != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Your Quest Journal.")) {
            questGuide.interact("Talk-to");
            sleep(Random.nextInt(600, 800));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 1).getText().equalsIgnoreCase("Moving on.")) {
            GameObject ladder = GameObjects.getNearest(2139);//"Ladder"
            if (ladder.isOnGameScreen()) {
                ladder.interact("Climb-down");
                sleep(Random.nextInt(600, 800));
            } else {
                Camera.turnTo(ladder.getLocation());
                sleep(Random.nextInt(600, 800));
            }
        }
    }

    public void mineAndSmith() throws InterruptedException {
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Mining and Smithing.")) {
            passedQuest = true;
            WalkAlongPath(toMiningSmith, true);
            sleep(Random.nextInt(700, 1000));
            NPC mining = NPCs.getNearest("Mining Instructor");
            if (mining != null) {
                if (mining.isOnGameScreen()) {
                    mining.interact("Talk-to");
                } else {
                    Movement.walkTileMM(mining.getLocation());
                }
            }
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Prospecting.")) {
            Movement.walkTileMM(new Tile(3078, 9504, 0));
            sleep(Random.nextInt(3000, 3200));
            GameObject tinRock = GameObjects.getNearest("Rocks");//2461
            tinRock.interact("Prospect");
            sleep(Random.nextInt(1800, 2000));
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("It's tin.")) {
            Movement.walkTileMM(new Tile(3082, 9501, 0));
            sleep(Random.nextInt(3000, 3200));
            GameObject copperRock = GameObjects.getNearest("Rocks");//2460
            copperRock.interact("Prospect");
            sleep(Random.nextInt(1800, 2000));
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("It's copper.")) {
            NPC mining = NPCs.getNearest("Mining Instructor");
            mining.interact("Talk-to");
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Mining.")
                && !Widgets.getComponent(372, 1)
                .getText().equalsIgnoreCase("Now you have some tin ore you just need some copper ore,")) { // need to fix
            Movement.walkTileMM(new Tile(3078, 9504, 0));
            sleep(Random.nextInt(3000, 3200));
            GameObject tinRock = GameObjects.getNearest("Rocks");//2461
            tinRock.interact("Mine");
            sleep(Random.nextInt(3800, 4000));
        }
        if (Widgets.getComponent(372, 1).getText().equalsIgnoreCase("Now you have some tin ore you just need some copper ore,")) {
            Movement.walkTileMM(new Tile(3082, 9501, 0));
            sleep(Random.nextInt(3000, 3200));
            GameObject copperRock = GameObjects.getNearest("Rocks");//2460
            copperRock.interact("Mine");
            sleep(Random.nextInt(3800, 4000));
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Smelting.")) {
            Movement.walkTileMM(new Tile(3079, 9498, 0));
            GameObject furn = GameObjects.getNearest(2463);//"Furnace"
            Tabs.INVENTORY.open();
            sleep(Random.nextInt(1000, 1200));
            Inventory.getItem("Copper ore").interact("Use");
            sleep(Random.nextInt(1000, 1200));
            furn.interact("Use", "Copper ore -> Furnace");
            sleep(Random.nextInt(1000, 1200));
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("You've made a bronze bar!")) {
            NPC mining = NPCs.getNearest("Mining Instructor");
            Movement.walkTileMM(mining.getLocation());
            sleep(Random.nextInt(700, 1000));
            if (mining != null) {
                if (mining.isOnGameScreen()) {
                    mining.interact("Talk-to");
                } else {
                    Movement.walkTileMM(mining.getLocation());
                }
            }
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Smithing a dagger.")) {
            if (Widgets.get(312) != null) {
                Mouse.move(new Rectangle(20, 50, 20, 20));
                sleep(Random.nextInt(700, 1000));
                Mouse.click(true);
                sleep(Random.nextInt(4000, 5500));
            } else {
                Movement.walkTileMM(new Tile(3082, 9499, 0));
                sleep(Random.nextInt(1000, 1200));
                GameObject anvil = GameObjects.getNearest("Anvil");
                Inventory.getItem("Bronze bar").interact("Use");
                sleep(Random.nextInt(400, 600));
                anvil.interact("Use", "Bronze bar -> Anvil");
                sleep(Random.nextInt(1000, 1200));                
            }
        }
    }

    public void combat() throws InterruptedException {
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("You've finished in this area.")) {
            WalkAlongPath(toCombat, true);
            sleep(Random.nextInt(400, 500));
            GameObject gate = GameObjects.getNearest(2131);//"Gate"
            if (!Players.getLocal().isMoving() && gate != null && Players.getLocal().getX() < 3095) {
                gate.interact("Open");
                sleep(Random.nextInt(600, 800));
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Combat.")) {
            Movement.walkTileMM(new Tile(3106, 9509, 0));
            NPC person = NPCs.getNearest("Combat Instructor");
            person.interact("Talk-to");
            sleep(Random.nextInt(1000, 1200));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Wielding weapons.")) {
            Tabs.EQUIPMENT.open();
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("This is your worn inventory.")) {
            Widgets.getComponent(387, 51).interact("Show Equipment Stats");
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Worn interface")) {
            Inventory.getItem("Bronze dagger").interact("Wear");
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("You're now holding your dagger.")) {
            if (Widgets.get(465) != null) {
                Widgets.getComponent(465, 77).interact("Close");
                sleep(Random.nextInt(700, 1000));
                Tabs.INVENTORY.open();
                sleep(Random.nextInt(600, 800));
            }
            NPC guy = NPCs.getNearest("Combat Instructor");
            guy.interact("Talk-to");
            sleep(Random.nextInt(1000, 1200));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Unequipping items.")) {
            Tabs.INVENTORY.open();
            sleep(Random.nextInt(600, 800));
            Inventory.getItem("Bronze sword").interact("Wield");
            sleep(Random.nextInt(400, 600));
            Inventory.getItem("Wooden shield").interact("Wield");
            sleep(Random.nextInt(400, 600));
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Combat interface.")) {
            Tabs.COMBAT.open();
            sleep(Random.nextInt(400, 600));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("This is your combat interface.")) {
            Movement.walkTileMM(new Tile(3111, 9518, 0));
            sleep(Random.nextInt(1000, 1200));
            GameObject gate = GameObjects.getNearest(2132);//"Gate"
            gate.interact("Open");
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Attacking.")) {
            Widgets.getComponent(89, 28).interact("Lunge");
            sleep(Random.nextInt(600, 800));
            if (Players.getLocal().getAnimation() == -1) {
                if (!Players.getLocal().isMoving()) {
                    NPC rat = NPCs.getNearest("Giant rat");
                    if (Players.getLocal().getInteractingIndex() == -1) {
                        rat.interact("Attack");
                        sleep(Random.nextInt(700, 1000));
                    }
                }
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Sit back and watch.")) {
            Widgets.getComponent(89, 27).interact("Stab");
            sleep(Random.nextInt(600, 800));
            if (Players.getLocal().getAnimation() == -1) {
                if (!Players.getLocal().isMoving()) {
                    NPC rat = NPCs.getNearest("Giant rat");
                    if (Players.getLocal().getInteractingIndex() == -1) {
                        rat.interact("Attack");
                        sleep(Random.nextInt(700, 1000));
                    }
                }
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Well done, you've made your first kill!")) {
            NPC guy = NPCs.getNearest("Combat Instructor");
            if (Calculations.canReach(guy.getLocation())) {
                Movement.walkTileMM(new Tile(3107, 9508, 0));
                sleep(Random.nextInt(1100, 1300));
                guy.interact("Talk-to");
                sleep(Random.nextInt(1000, 1200));
            } else {
                GameObject gate = GameObjects.getNearest(2132);//"Gate"
                gate.interact("Open");
                sleep(Random.nextInt(700, 1000));
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Rat ranging.")) {
            if (!Equipment.getItem(Equipment.Slot.WEAPON).getName().equalsIgnoreCase("Shortbow")) {
                Tabs.INVENTORY.open();
                sleep(Random.nextInt(700, 1000));
                Inventory.getItem("Shortbow").interact("Wield");
                sleep(Random.nextInt(700, 1000));
                Inventory.getItem("Bronze arrow").interact("Wield");
                sleep(Random.nextInt(700, 1000));
            } else {
                NPC rat = NPCs.getNearest("Giant rat");
                rat.interact("Attack");
                sleep(Random.nextInt(700, 1000));
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Moving on.")) {
            Movement.walkTileMM(new Tile(3112, 9525, 0));
            sleep(Random.nextInt(1500, 2500));
            GameObject ladder = GameObjects.getNearest(2140);//"Ladder"
            ladder.interact("Climb-up");
            sleep(Random.nextInt(1300, 1500));
        }
    }

    public void banking() throws InterruptedException {
        if (Widgets.get(372) != null && Widgets.get(228) == null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Banking.") && Widgets.getComponent(372, 0).isVisible()) {
            Movement.walkTileMM(new Tile(3120, 3123, 0));
            sleep(Random.nextInt(4700, 5000));
            GameObject booth = GameObjects.getNearest(2464);//"Bank booth"
            booth.interact("Use");
            sleep(Random.nextInt(3000, 3500));
        }
        if (Widgets.get(228) != null && Widgets.getComponent(228, 1).isVisible()) {
            sleep(Random.nextInt(1700, 1900));
            Widgets.getComponent(228, 1).interact("Continue");
            sleep(Random.nextInt(700, 1000));
        }
        if (Bank.isOpen()) {
            Bank.close();
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("This is your bank box.")) {
            GameObject door = GameObjects.getNearest(2134);//"Door"
            door.interact("Open");
            sleep(Random.nextInt(700, 1000));
        }
    }

    public void finance() throws InterruptedException {
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Financial advice.")) {
            NPC advisor = NPCs.getNearest("Financial Advisor");
            advisor.interact("Talk-to");
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.getComponent(372, 2).getText().equalsIgnoreCase("Continue through the next door.")) {
            GameObject door = GameObjects.getNearest(2135);//"Door"
            door.interact("Open");
            sleep(Random.nextInt(700, 1000));
        }
    }

    public void prayer() throws InterruptedException {
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Prayer.") && Widgets.getComponent(372, 0).isVisible()) {
            WalkAlongPath(toChurch, true);
            sleep(Random.nextInt(700, 1000));
            GameObject door = GameObjects.getNearest(6836, 6837);//"Large door"
            if (door != null) {
                NPC monk = NPCs.getNearest("Brother Brace");
                if (!Calculations.canReach(monk)) {
                    door.interact("Open");
                    sleep(Random.nextInt(700, 1000));
                    Movement.walkTileMM(new Tile(3126, 3106, 0));
                    sleep(Random.nextInt(1400, 1500));
                } else {
                    while(!monk.interact("Talk-to")){
                    }
                    sleep(Random.nextInt(700, 1000));
                }
            }
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Your Prayer menu.")) {
            Tabs.PRAYER.open();
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.getComponent(372, 3).getText().equalsIgnoreCase("Talk with Brother Brace and he'll tell you about prayers.")) {
            NPC monk = NPCs.getNearest("Brother Brace");
            monk.interact("Talk-to");
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.getComponent(372, 1).getText().equalsIgnoreCase("Friends list.")) {
            Tabs.FRIEND_LIST.open();
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("This is your friends list.")) {
            Tabs.IGNORE_LIST.open();
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.getComponent(372, 0).getText().equalsIgnoreCase("This is your ignore list.")) {
            NPC monk = NPCs.getNearest("Brother Brace");
            monk.interact("Talk-to");
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.getComponent(372, 1).getText().equalsIgnoreCase("Your final instructor!")) {
            Movement.walkTileMM(new Tile(3122, 3103, 0));
            sleep(Random.nextInt(1000, 1200));
            GameObject door = GameObjects.getNearest(2136);//"Door"
            door.interact("Open");
            sleep(Random.nextInt(700, 1000));
        }
    }

    public void magic() throws InterruptedException {
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Your final instructor!")) {
            WalkAlongPath(toWizard, true);
            sleep(Random.nextInt(400, 500));
            NPC wiz = NPCs.getNearest("Magic Instructor");
            if (wiz != null && wiz.getLocation().distance(Players.getLocal().getLocation()) < 10) {
                wiz.interact("Talk-to");
                sleep(Random.nextInt(400, 500));
            } else if (wiz != null && wiz.getLocation().distance(Players.getLocal().getLocation()) >= 10) {
                Movement.walkTileMM(wiz.getLocation());
            }
        }
        if (Widgets.get(372) != null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("Open up your final menu.")) {
            Tabs.MAGIC.open();
            sleep(Random.nextInt(700, 1000));
        }
        if (Widgets.get(421) != null
                && Widgets.getComponent(421, 1).getText().equalsIgnoreCase("Cast Wind Strike at a chicken.")) {
            Movement.walkTileMM(new Tile(3140, 3091, 0));
            sleep(Random.nextInt(700, 1000));
            Widgets.getComponent(192, 1).interact("Cast");
            sleep(Random.nextInt(700, 1000));
            NPC chick = NPCs.getNearest("Chicken");
            chick.interact("Cast", "Wind Strike -> Chicken  (level-3)");
        }
        if (Widgets.get(372) != null && Widgets.get(228) == null
                && Widgets.getComponent(372, 0).getText().equalsIgnoreCase("You have almost completed the tutorial!")
                && Widgets.getComponent(372, 0).isVisible()) {
            NPC wiz = NPCs.getNearest("Magic Instructor");
            Tabs.INVENTORY.open();
            wiz.interact("Talk-to");
            sleep(Random.nextInt(1400, 1500));

        }
        if (Widgets.get(228) != null) {
            Widgets.getComponent(228, 1).interact("Continue");
            sleep(Random.nextInt(700, 1000));
        }

    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean continueConvo() {
        int[] parent = {64, 65, 131, 163, 177, 210, 214, 241, 242, 243, 244, 519, 548};
        int[] child = {1, 2, 3, 4, 5, 6, 95};
        for (int p : parent) {
            for (int c : child) {
                if (Widgets.get(p) != null) {
                    if (Widgets.getComponent(p, c) != null) {
                        if (Widgets.getComponent(p, c).getText().equalsIgnoreCase("Click here to continue")) {
                            if (Widgets.getComponent(p, c).interact("Continue")) {
                                //log("Continue Dialog");
                                sleep(Random.nextInt(1000, 1600));
                                return true;
                            }
                        } else if (canSeeText("Click to continue") && Game.getColorAt(207, 429) == -16777088) {
                            Widgets.clickContinue();
                            /*
                             Widgets.getComponent(548, 95).getText().equalsIgnoreCase("Click to continue")
                             && Widgets.getComponent(548, 95).
                             log("Continue");
                             client.getInterface(548).getChild(95).hover();
                             sleep(Random.nextInt(700));
                             client.clickMouse(false);
                             sleep(Random.nextInt(700));
                             */
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    private boolean canSeeText(String text) {
        // -16776961 - blue 0
        // -2899295 - red 0
        // -16777088 - blue contin
        for (Widget loaded : Widgets.getLoaded()) {
            for (Component child : loaded.getChildren()) {
                if (child.getText() != null) {
                    if (child.getText().equalsIgnoreCase(text) && !child.getText().isEmpty() && child.isVisible()) {
                        return true;
                    }
                }

            }
        }
        return false;
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

    public boolean WalkAlongPath(int[][] path, boolean AscendThroughPath,
                                 int distanceFromEnd) throws InterruptedException {
        if (distanceToPoint(AscendThroughPath ? path[path.length - 1][0]
                : path[0][0], AscendThroughPath ? path[path.length - 1][1]
                : path[0][1]) <= distanceFromEnd) {
            return true;
        } else {
            WalkAlongPath(path, AscendThroughPath);
            return false;
        }
    }

    public void WalkAlongPath(int[][] path, boolean AscendThroughPath) throws InterruptedException {
        int destination = 0;
        for (int i = 0; i < path.length; i++) {
            if (distanceToPoint(path[i][0], path[i][1]) < distanceToPoint(
                    path[destination][0], path[destination][1])) {
                destination = i;
            }
        }
        if (Players.getLocal().isMoving()
                && distanceToPoint(path[destination][0], path[destination][1]) > (Movement.isRunEnabled() ? 3
                : 2)) {
            return;
        }
        if (AscendThroughPath && destination != path.length - 1
                || !AscendThroughPath && destination != 0) {
            destination += (AscendThroughPath ? 1 : -1);
        }
        Application.log("Walking to point:" + destination);
        if (new Tile(path[destination][0], path[destination][1]).isWalkable()) {
            Movement.walkTileMM(new Tile(path[destination][0], path[destination][1]));
        }
    }

    private int distanceToPoint(int pointX, int pointY) {
        return (int) Math.sqrt(Math
                .pow(Players.getLocal().getX() - pointX, 2)
                + Math.pow(Players.getLocal().getY() - pointY, 2));
    }

    public void checkVersion() {
        try {
            URL url = new URL("http://pastebin.com/raw.php?i=e9fL6dbt");
            Scanner s = new Scanner(url.openStream());
            String latestVersion = s.next();
            int latest = Integer.parseInt(latestVersion);
            if (myVersion >= latest) {
                Application.log("Up to date", Color.CYAN);
                upToDate = true;
                s.close();
            } else {
                upToDate = false;
                s.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ToastyTutExco.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

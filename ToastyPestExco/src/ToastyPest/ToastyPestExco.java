/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToastyPest;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.excobot.Application;
import org.excobot.bot.event.listeners.PaintListener;
import org.excobot.bot.script.GameScript;
import org.excobot.bot.script.Manifest;
import org.excobot.game.api.event.events.MessageEvent;
import org.excobot.game.api.event.listeners.MessageListener;
import org.excobot.game.api.methods.cache.media.Widgets;
import org.excobot.game.api.methods.input.Mouse;
import org.excobot.game.api.methods.media.Bank;
import org.excobot.game.api.methods.media.animable.GameObjects;
import org.excobot.game.api.methods.media.animable.actor.NPCs;
import org.excobot.game.api.methods.media.animable.actor.Players;
import org.excobot.game.api.methods.scene.Camera;
import org.excobot.game.api.methods.scene.Movement;
import org.excobot.game.api.methods.tab.Combat;
import org.excobot.game.api.methods.tab.Inventory;
import org.excobot.game.api.methods.tab.Tabs;
import org.excobot.game.api.util.Random;
import static org.excobot.game.api.util.Time.sleep;
import org.excobot.game.api.wrappers.media.animable.actor.NPC;
import org.excobot.game.api.wrappers.media.animable.actor.Player;
import org.excobot.game.api.wrappers.media.animable.object.GameObject;
import org.excobot.game.api.wrappers.scene.Tile;

@Manifest(authors = "toastedmeat", name = "ToastyPest",
        description = "Plays Pest Control New features everyday! "
        + "check the forum to request whatever you want!!", version = 1.7)
public class ToastyPestExco extends GameScript
        implements PaintListener, MessageListener, MouseListener {

    private final double Version = 1.7;

    //fonts
    private final Font regFont = new Font("Serif", 0, 12);
    private final Font statsFont = new Font("Serif", 0, 10);

    //Colors
    private final Color gold = new Color(255, 255, 0);

    //Areas
    final Area outsideBoat = new Area(2657, 2638, 2657, 2644);
    final Area insideBoat = new Area(2660, 2638, 2663, 2643);

    final Area outsideBoatMed = new Area(2644, 2642, 2644, 2651);
    final Area insideBoatMed = new Area(2638, 2642, 2641, 2647);

    final Area outsideBoatHigh = new Area(2638, 2652, 2638, 2655);
    final Area insideBoatHigh = new Area(2632, 2649, 2635, 2654);

    final Area buyingSpot = new Area(2658, 2646, 2664, 2655);

    final Area pestControlIsland = new Area(2638, 2638, 2670, 2663);

    final Area bankArea = new Area(2665, 2653, 2669, 2655);

    //Normal
    private Area startArea, middleArea, eGate, wGate, sGate;

    //pures
    private Area eastBar1, eastBar2, eastBar3, eastBar4, eastBar5, eastBar6,
            area1, area2, area3, area4, area5;

    private Tile pos1, pos2, pos3, pos4, pos5;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~Timers~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private long startTime = 0L, millis = 0L, hours = 0L;
    private long minutes = 0L, seconds = 0L;

    //move timers
    private long playerLastMoved;

    //Paths
    private int[][] toEastG, toEastP, toWestG, toWestP, toSouth, toSW, toSE, switchSWToW, switchSEToSW, switchEToSE;

    private final int[][] toBankNov = new int[][]{{2657, 2639}, {2657, 2643}, {2666, 2653}};
    private final int[][] toBankMed = new int[][]{{2644, 2644}, {2654, 2646}, {2666, 2653}};
    private final int[][] toBankHard = new int[][]{{2638, 2653}, {2644, 2647}, {2658, 2649}, {2666, 2653}};
    //For pures
    private int[][] toEastBar2, toEastBar3, toEastBar4, toEastBar5, toEastBar6;

    //portal
    private int westPortal = 685, eastPortal = 686,
            sEastPortal = 687, sWestPortal = 688;

    private int begPortal = 1;

    //players positon
    private int x, y, voidKx, voidKy, strPrayer = 5, attPrayer = 5;
    //Booleans
    private boolean playingPC, isFighting, fightingMisc, portalDead, setPaths,
            wonGame, prayerActivated, usingPrayer, usingSpec, atkPort;

    private boolean purpleWest, blueEast, yellowSouthE, redSouthW;

    private boolean firstMessage, init;
    // Booleans for pures
    private boolean firstBoatMove, initialWalk, doneCutting, bar1Done, bar2Done, bar3Done,
            bar4Done, bar5Done, bar6Done, walkedPassedGate, area1Done, area2Done, area3Done,
            area4Done, area5Done, doneBanking;

    //names for misc mobs
    private final String[] mobs = {"Torcher", "Defiler", "Brawler", "Ravager"};
    private NPC spinner, brawler, Portal, miscMobs, voidK;
    private GameObject tree, fence, plank;

    private final int[] Barricades = new int[]{14227, 14228, 14229, 14230, 14231, 14232};
    int prio;
    //doors
    private final int[] gates = new int[]{14233, 14237, 14241, 14234, 14238, 14242, 14245, 14246};
    final int[] gatesO = new int[]{14234, 14238, 14242, 14245, 14246};

    private final int doorO1 = 14234, doorO2 = 14238, doorO3 = 14242, doorO4 = 14245, doorO5 = 14246;
    private GameObject gate;

    // Rectangles for buying
    final Rectangle exchangeBottom = new Rectangle(475, 220, 10, 25);
    final Rectangle confirm = new Rectangle(225, 285, 45, 10);
    private int itemToBuy;
    private String buyingItem = "", boat;

    //weapons speed/spec
    private double weaponSpeed;
    private int specPerc, specTemp;
    final Rectangle specButton = new Rectangle(575, 415, 130, 13);

    // for PAINT
    private int winCounter, currentPoints;
    private double winPH, pointsPercent;
    private String timeTM;
    private Image image, imageTwo;
    private final Rectangle hidePaint = new Rectangle(Random.nextInt(10, 500), 462, 60, 12);
    
    boolean change;

    //Mouse Listener
    private Point pointer;

    //running checks
    private boolean run;
    private int runInt;

    // Death Anim
    final int deathAnim = 836;

    //Rectangles
    
    final Rectangle rectAttColor = new Rectangle(109, 375, 0, 14);
    final Rectangle mainPanels = new Rectangle(493, 302, 15, 15);
    final Rectangle infoPanels = new Rectangle(493, 302, 15, 15);
    private State state = State.IDLE;

    //~~~~~~~~~~~~~~~~~~~~~~~~ Inherited overrides ~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public boolean start() {
        try {
            //get the awesome paint for loot
            image = ImageIO.read(new URL("http://i1279.photobucket.com/albums/y535/eloo12/OP03Gea_zpsa507804b.png"));
            imageTwo = ImageIO.read(new URL("http://i1279.photobucket.com/albums/y535/eloo12/qTgCWYe_zps6d698735.png"));
        } catch (IOException e) {
            Application.log("Couldn't get the picture :(");
            e.printStackTrace();
        }

        //for running check
        run = false;
        runInt = Random.nextInt(15, 27);

        //playing
        playingPC = false;
        //fighting
        isFighting = false;
        fightingMisc = false;
        //dead portals
        portalDead = false;
        change = false;

        //start timer
        startTime = System.currentTimeMillis();
        playerLastMoved = System.currentTimeMillis();
        // others
        winCounter = 0;
        currentPoints = 0;
        specTemp = specPerc;
        setPaths = false;
        wonGame = false;
        doneBanking = false;
        firstMessage = false;
        init = false;

        //Tree's 
        initialWalk = false;
        doneCutting = false;
        walkedPassedGate = false;

        //Barricades
        bar1Done = false;
        bar2Done = false;
        bar3Done = false;
        bar4Done = false;

        prio = 0;

        ToastyPestGui.buildGUI();
        ToastyPestGui.frame.setVisible(true);
        ToastyPestGui.frame.setSize(235, 470);
        ToastyPestGui.frame.setLocation(300, 300);
        ToastyPestGui.frame.setTitle(ToastyPestGui.frame.getTitle() + " V" + Version);

        ToastyPestGui.btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                weaponSpeed = (Double.parseDouble(ToastyPestGui.textWeaponSpeed.getText()) * 1000);
                atkPort = ToastyPestGui.chkboxAtkPortal.isSelected();

                if (!ToastyPestGui.chkboxDontBuy.isSelected()) {
                    if (ToastyPestGui.rdbtnBody.isSelected()) {
                        itemToBuy = 94;
                        buyingItem = "Void Knight body";
                    } else if (ToastyPestGui.rdbtnLegs.isSelected()) {
                        itemToBuy = 95;
                        buyingItem = "Void Knight legs";
                    } else if (ToastyPestGui.rdbtnGloves.isSelected()) {
                        itemToBuy = 96;
                        buyingItem = "Void Knight gloves";
                    } else if (ToastyPestGui.rdbtnRHelm.isSelected()) {
                        itemToBuy = 120;
                        buyingItem = "Void Ranging Helm";
                    } else if (ToastyPestGui.rdbtnMeleeHelm.isSelected()) {
                        itemToBuy = 121;
                        buyingItem = "Void Melee Helm";
                    } else if (ToastyPestGui.rdbtnMageHelm.isSelected()) {
                        itemToBuy = 119;
                        buyingItem = "Void Mage Helm";
                    } else if (ToastyPestGui.rdbtnAtt.isSelected()) {
                        itemToBuy = 108;
                        buyingItem = "Void Att Exp";
                    } else if (ToastyPestGui.rdbtnStr.isSelected()) {
                        itemToBuy = 109;
                        buyingItem = "Void Str Exp";
                    } else if (ToastyPestGui.rdbtnDef.isSelected()) {
                        itemToBuy = 110;
                        buyingItem = "Void Def Exp";
                    } else if (ToastyPestGui.rdbtnRng.isSelected()) {
                        itemToBuy = 111;
                        buyingItem = "Void Rng Exp";
                    } else if (ToastyPestGui.rdbtnMage.isSelected()) {
                        itemToBuy = 112;
                        buyingItem = "Void Mage Exp";
                    }
                } else {
                    itemToBuy = 0;
                    buyingItem = "XP Shutting down at 250 points!";
                }
                if (ToastyPestGui.rdbtnNovice.isSelected()) {
                    Application.log("Novice boat selected");
                } else if (ToastyPestGui.rdbtnMedium.isSelected()) {
                    Application.log("Medium boat selected");
                } else if (ToastyPestGui.rdbtnHard.isSelected()) {
                    Application.log("Hard boad selected");
                }

                Application.log("Buying: " + buyingItem);
                Application.log("Weapon Speed: " + ToastyPestGui.textWeaponSpeed.getText());
                Application.log("Script Updated");
            }
        });

        ToastyPestGui.btnUpdatePrayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                usingPrayer = ToastyPestGui.chkboxPrayer.isSelected();
                usingSpec = ToastyPestGui.chkboxSpec.isSelected();
                specPerc = Integer.parseInt(ToastyPestGui.textSpecPercent.getText());
                specTemp = specPerc;
                if (ToastyPestGui.rdbtnAtt1.isSelected()) {
                    attPrayer = 9;
                } else if (ToastyPestGui.rdbtnAtt2.isSelected()) {
                    attPrayer = 19;
                } else if (ToastyPestGui.rdbtnAtt3.isSelected()) {
                    attPrayer = 35;
                }
                if (ToastyPestGui.rdbtnStr1.isSelected()) {
                    strPrayer = 7;
                } else if (ToastyPestGui.rdbtnStr2.isSelected()) {
                    strPrayer = 17;
                } else if (ToastyPestGui.rdbtnStr3.isSelected()) {
                    strPrayer = 33;
                }
                if (ToastyPestGui.rdbtnChiv.isSelected()) {
                    attPrayer = 53;
                    strPrayer = 0;
                } else if (ToastyPestGui.rdbtnPiety.isSelected()) {
                    attPrayer = 55;
                    strPrayer = 0;
                }

                if (ToastyPestGui.rdbtnRng1.isSelected()) {
                    attPrayer = 11;
                    strPrayer = 0;
                } else if (ToastyPestGui.rdbtnRng2.isSelected()) {
                    attPrayer = 27;
                    strPrayer = 0;
                } else if (ToastyPestGui.rdbtnRng3.isSelected()) {
                    attPrayer = 43;
                    strPrayer = 0;
                }

                if (ToastyPestGui.chkboxPrayer.isSelected()) {
                    Application.log("AttPrayer: " + attPrayer);
                    Application.log("StrPrayer: " + strPrayer);
                }
                if (ToastyPestGui.chkboxSpec.isSelected()) {
                    Application.log("Specing at: " + specPerc);
                }
                Application.log("Script Updated");
            }
        });

        ToastyPestGui.btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //start timer
                startTime = System.currentTimeMillis();

                //Idle Timer
                playerLastMoved = System.currentTimeMillis();
                winCounter = 0;
                Application.log("Paint was reset!");
            }
        });

        return true;
    }

    @Override
    public int execute() throws InterruptedException {
        if (ToastyPestGui.frame.isVisible() && !init) {
            //Initialization 
            weaponSpeed = (Double.parseDouble(ToastyPestGui.textWeaponSpeed.getText()) * 1000);
            specPerc = Integer.parseInt(ToastyPestGui.textSpecPercent.getText());
            usingPrayer = ToastyPestGui.chkboxPrayer.isSelected();
            usingSpec = ToastyPestGui.chkboxSpec.isSelected();
            atkPort = ToastyPestGui.chkboxAtkPortal.isSelected();

            if (!ToastyPestGui.chkboxDontBuy.isSelected()) {
                if (ToastyPestGui.rdbtnBody.isSelected()) {
                    itemToBuy = 94;
                    buyingItem = "Void Knight body";
                } else if (ToastyPestGui.rdbtnLegs.isSelected()) {
                    itemToBuy = 95;
                    buyingItem = "Void Knight legs";
                } else if (ToastyPestGui.rdbtnGloves.isSelected()) {
                    itemToBuy = 96;
                    buyingItem = "Void Knight gloves";
                } else if (ToastyPestGui.rdbtnRHelm.isSelected()) {
                    itemToBuy = 120;
                    buyingItem = "Void Ranging Helm";
                } else if (ToastyPestGui.rdbtnMeleeHelm.isSelected()) {
                    itemToBuy = 121;
                    buyingItem = "Void Melee Helm";
                } else if (ToastyPestGui.rdbtnMageHelm.isSelected()) {
                    itemToBuy = 119;
                    buyingItem = "Void Mage Helm";
                } else if (ToastyPestGui.rdbtnAtt.isSelected()) {
                    itemToBuy = 108;
                    buyingItem = "Void Att Exp";
                } else if (ToastyPestGui.rdbtnStr.isSelected()) {
                    itemToBuy = 109;
                    buyingItem = "Void Str Exp";
                } else if (ToastyPestGui.rdbtnDef.isSelected()) {
                    itemToBuy = 110;
                    buyingItem = "Void Def Exp";
                } else if (ToastyPestGui.rdbtnRng.isSelected()) {
                    itemToBuy = 111;
                    buyingItem = "Void Rng Exp";
                } else if (ToastyPestGui.rdbtnMage.isSelected()) {
                    itemToBuy = 112;
                    buyingItem = "Void Mage Exp";
                } else if (ToastyPestGui.rdbtnHP.isSelected()) {
                    itemToBuy = 113;
                    buyingItem = "Void HP Exp";
                }
            } else {
                itemToBuy = 0;
                buyingItem = "Shutting down at 250 points!";
            }

            if (ToastyPestGui.rdbtnAtt1.isSelected()) {
                attPrayer = 9;
            } else if (ToastyPestGui.rdbtnAtt2.isSelected()) {
                attPrayer = 19;
            } else if (ToastyPestGui.rdbtnAtt3.isSelected()) {
                attPrayer = 35;
            }
            if (ToastyPestGui.rdbtnStr1.isSelected()) {
                strPrayer = 7;
            } else if (ToastyPestGui.rdbtnStr2.isSelected()) {
                strPrayer = 17;
            } else if (ToastyPestGui.rdbtnStr3.isSelected()) {
                strPrayer = 33;
            }
            if (ToastyPestGui.rdbtnChiv.isSelected()) {
                attPrayer = 53;
                strPrayer = 0;
            } else if (ToastyPestGui.rdbtnPiety.isSelected()) {
                attPrayer = 55;
                strPrayer = 0;
            }

            if (ToastyPestGui.rdbtnRng1.isSelected()) {
                attPrayer = 11;
                strPrayer = 0;
            } else if (ToastyPestGui.rdbtnRng2.isSelected()) {
                attPrayer = 27;
                strPrayer = 0;
            } else if (ToastyPestGui.rdbtnRng3.isSelected()) {
                attPrayer = 43;
                strPrayer = 0;
            }
            boat = "";
            if (ToastyPestGui.rdbtnNovice.isSelected()) {
                boat = ToastyPestGui.rdbtnNovice.getText();
            } else if (ToastyPestGui.rdbtnMedium.isSelected()) {
                boat = ToastyPestGui.rdbtnMedium.getText();
            } else if (ToastyPestGui.rdbtnHard.isSelected()) {
                boat = ToastyPestGui.rdbtnHard.getText();
            }

            specTemp = specPerc;

            return 1000;
        }
        if (!firstMessage && !ToastyPestGui.frame.isVisible()) {
            Application.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            Application.log("Toasty Pest Control ExcoBot Release");
            Application.log("On the " + boat + " boat");
            Application.log("Buying: " + buyingItem);
            if (usingPrayer) {
                Application.log("Using Prayer: " + usingPrayer);
            }
            if (usingSpec) {
                Application.log("Using Specs: " + usingSpec + " @ " + specPerc);
            }
            Application.log("Pure Mode is released!!");
            Application.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            init = true;
            firstMessage = true;
        }

        state = checkStates();
        if (!playingPC) {
            x = Players.getLocal().getX();
            y = Players.getLocal().getY();
            // random portal
            begPortal = Random.nextInt(1, 4);
        }
        switch (state) {
            case CLOSE:
                Widgets.getComponent(553, 10).hover();
                sleep(Random.nextInt(700, 800));
                Mouse.click(true);
                break;
            case OUTSIDEBOAT:
                outsideBoat();
                break;
            case INSIDEBOAT:
                insideBoat();
                break;
            case GAMESTARTED:
                wonGame = true;
                gameStarted2(begPortal);
                break;
            case FIGHTING:
                fight();
                break;
            case NEXTPORTAL:
                nextPortalTest();
                break;
            case BUYITEMS:
                buyItems();
                break;
            case BANK:
                bank();
                break;
            case GAMESTARTEDPUREMODE:
                wonGame = true;
                gameStartedPureRetry();
                break;
            case CUTTING:
                cutting();
                break;
            case TOGATEPURE:
                walkToGatePure();
                break;
            case REPAIR:
                wonGame = true;
                repairPure();
                break;
            default:
                Application.log("Idle");
                break;
        }

        // checks to make sure we're running
        runningCheck();
        //Application.log("runn done/ loop done");
        return Random.nextInt(300, 500);
    }

    @Override
    public void repaint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        gate = GameObjects.getNearest(gates);

        millis = (System.currentTimeMillis() - startTime);
        hours = (millis / 3600000L);
        millis -= hours * 3600000L;
        minutes = (millis / 60000L);
        millis -= minutes * 60000L;
        seconds = (millis / 1000L);

        if (Players.getLocal() != null && Players.getLocal().isMoving()) {
            playerLastMoved = System.currentTimeMillis();
        }

        winPH = ((int) (winCounter * 3600000.0D / (System.currentTimeMillis() - startTime)));
        timeTM = formatTime(timeTnl((250 - currentPoints), (2 * (winCounter * 3600000.0D / (System.currentTimeMillis() - startTime)))));
        pointsPercent = (100 * ((double) currentPoints / 250));
        int pointsPercentI = (int) (pointsPercent * 2.6);
        rectAttColor.setSize(pointsPercentI, 13);

        g2.drawRect(Mouse.getX() - 6, Mouse.getY() - 6, 12, 12);
        g2.drawString("☼", Mouse.getX() - 5, Mouse.getY() + 5);

        if (ToastyPestGui.frame != null) {
            if (ToastyPestGui.rdbtnHidePaint.isSelected()) {
            } else if (ToastyPestGui.rdbtnShowPaint.isSelected()) {
                if (ToastyPestGui.rdbtnHidePaintInfo.isSelected()) {
                    // awesome paint ^_^
                    g2.drawImage(image, 4, 268, null);
                    
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.00f));
                    g2.fill(infoPanels);

                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1.00f));
                    
                    g2.drawString("" + winCounter, 78, 357);
                    g2.drawString("" + state, 395, 357);
                    g2.drawString(hours + " hrs " + minutes
                            + " mins " + seconds + " secs", 205, 357);
                    
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            0.60f));
                    g2.setColor(Color.BLUE);
                    g2.fill(rectAttColor);
                    int one = 384;
                    int two = 385;
                    g2.setFont(statsFont);

                    g2.setColor(Color.WHITE);
                    g2.drawString("Pts:", 131, two);
                    g2.setColor(gold);
                    g2.drawString(currentPoints + "/250", 149, two);
                    g2.setColor(Color.RED);

                    g2.drawString("|", 184, one);

                    g2.setColor(gold);
                    String newPointsPercent = Double.toString(pointsPercent);
                    g2.drawString(newPointsPercent.substring(0, 3), 194, two);
                    g2.setColor(Color.WHITE);
                    g2.drawString("%", 212, two);
                    g2.setColor(Color.RED);

                    g2.drawString("|", 220, one);

                    g2.setColor(gold);
                    g2.drawString(winPH * 2 + "", 237, two);
                    g2.setColor(Color.WHITE);
                    g2.drawString("Pts/h", 270, two);
                    g2.setColor(Color.RED);

                    g2.drawString("|", 296, one);

                    g2.setColor(Color.WHITE);
                    g2.drawString("TTM:", 299, two);
                    g2.setColor(gold);
                    g2.drawString(timeTM, 333, two);
                } else {
                    g2.drawImage(imageTwo, 4, 268, null);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.00f));
                    g2.fill(mainPanels);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1.00f));
                    
                    if (ToastyPestGui.chkboxPureMode.isSelected()) {
                        g2.drawString("Pure mode!", 55, 357);
                        if(ToastyPestGui.chkboxBanking.isSelected()){
                            g2.drawString("Banking!", 58, 384);
                        }
                    }
                    if (ToastyPestGui.chkboxSpec.isSelected()) {
                        g2.drawString("Spec: " + Combat.getSpecialAttackEnergy(), 62, 357);
                        g2.drawString("Specing @: " + specPerc, 40, 384);
                    }
                    g2.drawString("" + getLastMovementTime() / 1000, 430, 358);
                    g2.drawString("" + isFighting, 240, 358);
                }
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1.00f));
            g2.fill(hidePaint);
            
            g2.setColor(Color.RED);
            g2.drawString("GUI", (int) hidePaint.getX() + 20, (int) hidePaint.getY() + 11);

        }
    }

    @Override
    public void messageReceived(MessageEvent message) {
        if (message.getMessage().contains("You have been given priority level")) {
            playerLastMoved = System.currentTimeMillis();
        }
        if (message.getMessage().contains("Your inventory is too full")) {
            doneCutting = true;
        }
        if (message.getMessage().contains("It's too damaged to be moved")) {
            gate.interact("Repair");
            sleep(Random.nextInt(700, 800));
        }
        if (message.getMessage().equalsIgnoreCase("The purple, western portal shield has dropped!")) {
            purpleWest = true;
        }
        if (message.getMessage().equalsIgnoreCase("The blue, eastern portal shield has dropped!")) {
            blueEast = true;
        }
        if (message.getMessage().equalsIgnoreCase("The yellow, south-eastern portal shield has dropped!")) {
            yellowSouthE = true;
        }
        if (message.getMessage().equalsIgnoreCase("The red, south-western portal shield has dropped!")) {
            redSouthW = true;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        pointer = e.getPoint();

        if (hidePaint.contains(pointer)) {
            ToastyPestGui.frame.setVisible(true);
        }
        if(infoPanels.contains(pointer) && !change){
            ToastyPestGui.rdbtnShowPaintInfo.setSelected(true);
            change = true;
        }else if(mainPanels.contains(pointer) && change){
           ToastyPestGui.rdbtnHidePaintInfo.setSelected(true);
           change = false;
        }

    }

    @Override
    public void onFinish() {
        ToastyPestGui.CloseGUI();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~ Main Methods ~~~~~~~~~~~~~~~~~
    public State checkStates() throws InterruptedException {
        spinner = NPCs.getNearest("Spinner");
        Portal = NPCs.getNearest("Portal");
        miscMobs = NPCs.getNearest(mobs);
        //Application.log("Boo");
        if (getLastMovementTime() > 360000 + Random.nextInt(-30000, 30000)) {
            Application.log("Idle for too long logging out");
            stop();
        }
        if (Portal == null) {
            portalDead = true;
        } else if (Portal != null && Portal.getLocation().distance(Players.getLocal().getLocation()) > 8) {
            if (!Players.getLocal().isMoving()) {
                Movement.walkTileMM(new Tile(Portal.getX() + Random.nextInt(-3, 3), Portal.getY() + 3));
            }
            //portalDead = true;
        } else if (Portal != null && Portal.getLocation().distance(Players.getLocal().getLocation()) < 8) {
            portalDead = false;
        }
        if (Widgets.get(553) != null && Widgets.getComponent(553, 10).isVisible()) {
            return State.CLOSE;
        } else if (ToastyPestGui.chkboxPureMode.isSelected() && ToastyPestGui.chkboxBanking.isSelected()
                && ((pestControlIsland != null && pestControlIsland.contains(Players.getLocal())
                && Inventory.getCount(Inventory.getItem("Willow Logs").getId(), false) < 10) || !doneBanking)) {
            Application.log("Getting logs");
            return State.BANK;
        } else if (outsideBoat.contains(Players.getLocal()) || outsideBoatMed.contains(Players.getLocal())
                || outsideBoatHigh.contains(Players.getLocal())) {
            if (Widgets.getComponent(213, 4) != null) {
                Application.log("Buying?");
                return State.BUYITEMS;
            } else {
                Application.log("Outside the boat");
                return State.OUTSIDEBOAT;
            }
        } else if (insideBoat.contains(Players.getLocal()) || insideBoatMed.contains(Players.getLocal())
                || insideBoatHigh.contains(Players.getLocal())) {
            if ((Widgets.getComponent(407, 14).getText().equalsIgnoreCase("Pest Points: 150")
                    && ToastyPestGui.rdbtnGloves.isSelected())
                    || (Widgets.getComponent(407, 14).getText().equalsIgnoreCase("Pest Points: 200")
                    && (ToastyPestGui.rdbtnRHelm.isSelected()
                    || ToastyPestGui.rdbtnMeleeHelm.isSelected()
                    || ToastyPestGui.rdbtnMageHelm.isSelected()))
                    || (Widgets.getComponent(407, 14).getText().equalsIgnoreCase("Pest Points: 100")
                    && (ToastyPestGui.rdbtnAtt.isSelected() || ToastyPestGui.rdbtnDef.isSelected()
                    || ToastyPestGui.rdbtnStr.isSelected() || ToastyPestGui.rdbtnRng.isSelected()
                    || ToastyPestGui.rdbtnMage.isSelected()))) {
                Application.log("Buying items");
                return State.BUYITEMS;
            } else {
                //Application.log("Inside the boat, Waiting");
                return State.INSIDEBOAT;
            }
        } else if (playingPC && ToastyPestGui.chkboxPureMode.isSelected()
                && doneCutting && !walkedPassedGate) {
            //Application.log("Died walking back");
            return State.TOGATEPURE;
        } else if (playingPC && ToastyPestGui.chkboxPureMode.isSelected()
                && !doneCutting && initialWalk) {
            return State.CUTTING;
        } else if (playingPC && ToastyPestGui.chkboxPureMode.isSelected()
                && doneCutting && walkedPassedGate) {
            //Application.log("Pure mode repairing barricades");
            return State.REPAIR;
        } else if ((voidK != null && startArea.contains(Players.getLocal()))
                || Widgets.getComponent(244, 6) != null
                || (voidK != null && middleArea.contains(Players.getLocal()))
                || (ToastyPestGui.chkboxPureMode.isSelected() && !doneCutting)) {
            if (ToastyPestGui.chkboxPureMode.isSelected()) {
                //Application.log("Games started walking to tree's");
                return State.GAMESTARTEDPUREMODE;
            } else {
                Application.log("Games started walking to first portal");
                return State.GAMESTARTED;
            }
        } else if (playingPC && (portalDead && !ToastyPestGui.chkboxPureMode.isSelected())) {
            //Application.log("Switching portals, current is dead");
            return State.NEXTPORTAL;
        } else if (playingPC
                && ((spinner != null && spinner.getLocation().distance(Players.getLocal().getLocation()) < 8)
                || (Portal != null && Portal.getLocation().distance(Players.getLocal().getLocation()) < 8)
                || (miscMobs != null && miscMobs.getLocation().distance(Players.getLocal().getLocation()) < 8))
                && !ToastyPestGui.chkboxPureMode.isSelected()) {
            //Application.log("Fighting");
            return State.FIGHTING;
        }
        Application.log("hmm..");
        return State.IDLE;
    }

    public void insideBoat() throws InterruptedException {
        if (Widgets.getComponent(407, 14) != null) {
            currentPoints = Integer.parseInt(Widgets.getComponent(407, 14).getText().substring(13));
            //Application.log("pointers meter");
        }
        /*
         if (!firstBoatMove && (insideBoat.contains(Players.getLocal())
         || insideBoatHigh.contains(Players.getLocal()) || insideBoatMed.contains(Players.getLocal()))) {
         //Application.log("First move");
         if (ToastyPestGui.rdbtnNovice.isSelected()) {
         if (Players.getLocal().isInArea(insideBoat)) {
         //client.moveMouseTo(new MainScreenTileDestination(client.getBot(),
         new Tile(random(2660, 2663), random(2638, 2643), 0)), false, true, false);
         firstBoatMove = true;
         }
         } else if (ToastyPestGui.rdbtnMedium.isSelected()) {
         if (Players.getLocal().isInArea(insideBoatMed)) {
         //client.moveMouseTo(new MainScreenTileDestination(client.getBot(),
         new Tile(random(2638, 2641), random(2642, 2647), 0)), false, true, false);
         firstBoatMove = true;
         }
         } else if (ToastyPestGui.rdbtnHard.isSelected()) {
         if (Players.getLocal().isInArea(insideBoatHigh)) {
         // client.moveMouseTo(new MainScreenTileDestination(client.getBot(),
         new Tile(random(2632, 2635), random(2649, 2654), 0)), false, true, false);
         firstBoatMove = true;
         }
         }
         }*/
        //Application.log("first moves done/ anti");
        if (Random.nextInt(1, 900) == 6) {
            Application.log("Rotations");
            Camera.setAngle(Random.nextInt(10, 290));
            sleep(Random.nextInt(500, 600));
            Mouse.move(Random.nextInt(0, 764), Random.nextInt(0, 500));
        } else if (Random.nextInt(1, 600) == 8) {
            Application.log("Move to skills");
            Tabs.SKILLS.open();
            sleep(Random.nextInt(1000, 1200));
            Tabs.INVENTORY.open();
        } else if (Random.nextInt(1, 500) == 5) {
            Application.log("Move to friends");
            Tabs.FRIEND_LIST.open();
            sleep(Random.nextInt(1000, 1200));
            Tabs.INVENTORY.open();
        }
        //Application.log("antis done");
    }

    public void outsideBoat() throws InterruptedException {
        if (wonGame) {
            if (Widgets.getComponent(243, 5) != null) {
                Application.log("Won game!");
                winCounter++;
                wonGame = false;
            }
        }
        prayerActivated = false;
        // Application.log("1");
        playingPC = false;
        portalDead = false;
        isFighting = false;
        fightingMisc = false;
        setPaths = false;
        doneCutting = false;
        initialWalk = false;
        walkedPassedGate = false;
        firstBoatMove = false;

        purpleWest = false;
        blueEast = false;
        yellowSouthE = false;
        redSouthW = false;
        //Application.log("10");

        plank = GameObjects.getNearest("Gangplank");
        //Application.log("11");
        if (plank != null) {
            plank.interact("Cross");
            // Application.log("13");
        }
    }

    public void gameStarted2(int portals) throws InterruptedException {
        playingPC = true;
        if (!setPaths) {
            sleep(Random.nextInt(1000, 1500));
            voidK = NPCs.getNearest("Squire");
            voidKx = voidK.getX();
            voidKy = voidK.getY();
            startArea = new Area(voidKx + 1, voidKy - 1, voidKx + 4, voidKy + 7);
            middleArea = new Area(voidKx - 12, voidKy - 22, voidKx + 15, voidKy - 1);
            eGate = new Area(voidKx + 13, voidKy - 16, voidKx + 15, voidKy - 12);
            wGate = new Area(voidKx - 12, voidKy - 17, voidKx - 10, voidKy - 12);
            sGate = new Area(voidKx, voidKy - 22, voidKx + 3, voidKy - 21);
            // pathing for begining portals
            toEastG = new int[][]{{voidKx + 5, voidKy - 5}, {voidKx + 15, voidKy - 14}};
            toEastP = new int[][]{{voidKx + 22, voidKy - 17}};
            toWestG = new int[][]{{voidKx - 5, voidKy - 6}, {voidKx - 12, voidKy - 15}};
            toWestP = new int[][]{{voidKx - 22, voidKy - 16}};
            toSouth = new int[][]{{voidKx + 2, voidKy - 13}, {voidKx + 2, voidKy - 22}};
            toSW = new int[][]{{voidKx - 6, voidKy - 32}};
            toSE = new int[][]{{voidKx + 11, voidKy - 33}};
            // switch portals
            switchSWToW = new int[][]{{voidKx - 10, voidKy - 32}, {voidKx - 13, voidKy - 24}, {voidKx - 20, voidKy - 15}};
            switchSEToSW = new int[][]{{voidKx + 14, voidKy - 32}, {voidKx + 2, voidKy - 36}, {voidKx - 9, voidKy - 34}};
            switchEToSE = new int[][]{{voidKx + 22, voidKy - 19}, {voidKx + 20, voidKy - 27}, {voidKx + 14, voidKy - 32}};
            setPaths = true;

        }
        prayerActivated = false;
        switch (portals) {
            //East
            case 1:
                //playingPC = true;
                if ((Players.getLocal().getX() == x && Players.getLocal().getY() == y)
                        || !eGate.contains(Players.getLocal())) {
                Application.log("Walking to East Gate");
                Camera.setAngle(Random.nextInt(278, 283));
                WalkAlongPath(toEastG, true);
                sleep(Random.nextInt(600, 800));
            }
                if (eGate.contains(Players.getLocal())) {
                    if (gate != null) {
                        if (gate.getId() == doorO1 || gate.getId() == doorO2
                                || gate.getId() == doorO3
                                || gate.getId() == doorO4
                                || gate.getId() == doorO5) {
                            Movement.walkTileMM(new Tile(voidKx + 22, voidKy - 17, 0));
                        } else {
                            gate.interact("Open");
                            sleep(Random.nextInt(200, 400));
                            Movement.walkTileMM(new Tile(voidKx + 22, voidKy - 17, 0));
                            //WalkAlongPath(toEastP, true);
                        }
                    }
                }
                break;
            //South-East
            case 2:
                //playingPC = true;
                if ((Players.getLocal().getX() == x && Players.getLocal().getY() == y)
                        || !sGate.contains(Players.getLocal())) {
                Application.log("Walking to South Gate");
                Camera.setAngle(Random.nextInt(178, 183));
                WalkAlongPath(toSouth, true);
                sleep(Random.nextInt(600, 800));
            }
                if (sGate.contains(Players.getLocal())) {
                    if (gate != null) {
                        if (gate.getId() == doorO1 || gate.getId() == doorO2
                                || gate.getId() == doorO3
                                || gate.getId() == doorO4
                                || gate.getId() == doorO5) {
                            Movement.walkTileMM(new Tile(voidKx + 11, voidKy - 33, 0));
                            //WalkAlongPath(toSE, true);
                        } else {
                            gate.interact("Open");
                            sleep(Random.nextInt(200, 400));
                            Movement.walkTileMM(new Tile(voidKx + 11, voidKy - 33, 0));
                            //WalkAlongPath(toSE, true);
                        }
                    }
                }
                break;
            //South-West
            case 3:
                //playingPC = true;
                if ((Players.getLocal().getX() == x && Players.getLocal().getY() == y)
                        || !sGate.contains(Players.getLocal())) {
                Application.log("Walking to South Gate");
                Camera.setAngle(Random.nextInt(178, 183));
                WalkAlongPath(toSouth, true);
                sleep(Random.nextInt(600, 800));
            }
                if (sGate.contains(Players.getLocal())) {
                    if (gate != null) {
                        if (gate.getId() == doorO1 || gate.getId() == doorO2
                                || gate.getId() == doorO3
                                || gate.getId() == doorO4
                                || gate.getId() == doorO5) {
                            Movement.walkTileMM(new Tile(voidKx - 6, voidKy - 32, 0));
                            //WalkAlongPath(toSW, true);
                        } else {
                            gate.interact("Open");
                            sleep(Random.nextInt(200, 400));
                            Movement.walkTileMM(new Tile(voidKx - 6, voidKy - 32, 0));
                            //WalkAlongPath(toSW, true);
                        }
                    }
                }
                break;
            //West
            case 4:
                //playingPC = true;
                if ((Players.getLocal().getX() == x && Players.getLocal().getY() == y)
                        || !wGate.contains(Players.getLocal())) {
                Application.log("Walking to West Gate");
                Camera.setAngle(Random.nextInt(88, 93));
                WalkAlongPath(toWestG, true);
                sleep(Random.nextInt(600, 800));
            }
                if (wGate.contains(Players.getLocal())) {
                    if (gate != null) {
                        if (gate.getId() == doorO1 || gate.getId() == doorO2
                                || gate.getId() == doorO3
                                || gate.getId() == doorO4
                                || gate.getId() == doorO5) {
                            Movement.walkTileMM(new Tile(voidKx - 22, voidKy - 16, 0));
                            //WalkAlongPath(toWestP, true);
                        } else {
                            gate.interact("Open");
                            sleep(Random.nextInt(200, 400));
                            Movement.walkTileMM(new Tile(voidKx - 22, voidKy - 16, 0));
                            //WalkAlongPath(toWestP, true);
                        }
                    }
                }
                break;
        }
    }

    public void bank() throws InterruptedException {
        if (wonGame) {
            if (Widgets.getComponent(243, 5) != null) {
                winCounter++;
                wonGame = false;
            }
        }
        if (bankArea != null
                && bankArea.contains(Players.getLocal()) && !Inventory.isFull()) {
            GameObject bankBox = GameObjects.getNearest(14367);
            if (Bank.isOpen()) {
                if (Bank.contains("Willow logs")) {
                    Bank.withdraw(1519, Bank.Amount.ALL);
                    sleep(Random.nextInt(800, 900));
                } else {
                    Application.log("Ran out of logs Shutting down!");
                    stop();
                }
            } else {
                if (bankBox != null) {
                    if (bankBox.isOnGameScreen()) {
                        bankBox.interact("Bank");
                        sleep(Random.nextInt(700, 900));
                        bank();
                    } else {
                        Camera.setAngle(Camera.getAngleTo(bankBox.getLocation()));
                    }
                }
            }
        } else if (!bankArea.contains(Players.getLocal()) && !Inventory.isFull()) {
            if (ToastyPestGui.rdbtnNovice.isSelected()) {
                WalkAlongPath(toBankNov, true);
            } else if (ToastyPestGui.rdbtnMedium.isSelected()) {
                WalkAlongPath(toBankMed, true);
            } else if (ToastyPestGui.rdbtnHard.isSelected()) {
                WalkAlongPath(toBankHard, true);
            }
        }
        if (Inventory.isFull()) {
            if (ToastyPestGui.rdbtnNovice.isSelected()) {
                WalkAlongPath(toBankNov, false);
            } else if (ToastyPestGui.rdbtnMedium.isSelected()) {
                WalkAlongPath(toBankMed, false);
            } else if (ToastyPestGui.rdbtnHard.isSelected()) {
                WalkAlongPath(toBankHard, false);
            }
            if (ToastyPestGui.rdbtnNovice.isSelected()) {
                if (outsideBoat.contains(Players.getLocal())) {
                    doneBanking = true;
                }
            } else if (ToastyPestGui.rdbtnMedium.isSelected()) {
                if (outsideBoatMed.contains(Players.getLocal())) {
                    doneBanking = true;
                }
            } else if (ToastyPestGui.rdbtnHard.isSelected()) {
                if (outsideBoatHigh.contains(Players.getLocal())) {
                    doneBanking = true;
                }
            }

        }
    }

    public void gameStartedPureRetry() throws InterruptedException {
        playingPC = true;
        if (!setPaths) {
            sleep(Random.nextInt(1000, 1500));
            voidK = NPCs.getNearest("Squire");
            voidKx = voidK.getX();
            voidKy = voidK.getY();
            startArea = new Area(voidKx + 1, voidKy - 1, voidKx + 4, voidKy + 7);
            middleArea = new Area(voidKx - 12, voidKy - 22, voidKx + 15, voidKy - 1);

            eGate = new Area(voidKx + 13, voidKy - 16, voidKx + 15, voidKy - 12);

            eastBar1 = new Area(voidKx + 17, voidKy - 18, voidKx + 20, voidKy - 13);
            eastBar2 = new Area(voidKx + 19, voidKy - 24, voidKx + 23, voidKy - 19);
            eastBar3 = new Area(voidKx + 19, voidKy - 37, voidKx + 23, voidKy - 32);
            eastBar4 = new Area(voidKx + 10, voidKy - 31, voidKx + 15, voidKy - 27);
            eastBar5 = new Area(voidKx, voidKy - 34, voidKx + 5, voidKy - 30);
            eastBar6 = new Area(voidKx - 9, voidKy - 31, voidKx - 4, voidKy - 27);

            // pathing
            toEastG = new int[][]{{voidKx + 5, voidKy - 5}, {voidKx + 15, voidKy - 14}};

            // Barriers
            toEastBar2 = new int[][]{{voidKx + 17, voidKy - 16}, {voidKx + 20, voidKy - 22}};
            toEastBar3 = new int[][]{{voidKx + 20, voidKy - 22}, {voidKx + 22, voidKy - 29}, {voidKx + 22, voidKy - 35}};
            toEastBar4 = new int[][]{{voidKx + 22, voidKy - 35}, {voidKx + 18, voidKy - 30}, {voidKx + 12, voidKy - 28}};
            toEastBar5 = new int[][]{{voidKx + 12, voidKy - 28}, {voidKx + 3, voidKy - 31}};
            toEastBar6 = new int[][]{{voidKx + 2, voidKy - 31}, {voidKx - 6, voidKy - 28}};

            pos1 = new Tile(voidKx - 11, voidKy + 7, 0);
            pos2 = new Tile(voidKx - 9, voidKy - 1, 0);
            pos3 = new Tile(voidKx - 9, voidKy - 11, 0);
            pos4 = new Tile(voidKx - 3, voidKy - 4, 0);
            pos5 = new Tile(voidKx + 11, voidKy - 1, 0);

            area1 = new Area(voidKx - 14, voidKy + 1, voidKx - 11, voidKy + 9);
            area2 = new Area(voidKx - 10, voidKy - 4, voidKx - 7, voidKy - 1);
            area3 = new Area(voidKx - 9, voidKy - 12, voidKx - 5, voidKy - 6);
            area4 = new Area(voidKx - 3, voidKy - 7, voidKx + 8, voidKy - 3);
            area5 = new Area(voidKx + 8, voidKy - 12, voidKx + 13, voidKy - 1);

            // Booleans
            doneCutting = false;
            initialWalk = false;

            area1Done = false;
            area2Done = false;
            area3Done = false;
            area4Done = false;
            area5Done = false;

            bar1Done = false;
            bar2Done = false;
            bar3Done = false;
            bar4Done = false;
            bar5Done = false;
            bar6Done = false;

            //Done
            walkedPassedGate = false;
            setPaths = true;
        }

        if (Inventory.isFull() || ToastyPestGui.chkboxBanking.isSelected()) {
            doneCutting = true;
        } else if (!Inventory.isFull() && (startArea.contains(Players.getLocal())
                || (!area1.contains(Players.getLocal()) && !initialWalk))) {
            Application.log("Walking to East Tree's");
            Movement.walkTileMM(pos1);
            sleep(Random.nextInt(1000, 1500));
            //tree = GameObjects.getNearest("Tree");
            initialWalk = true;
        }
    }

    public void cutting() throws InterruptedException {
        if (area1 != null && area1.contains(Players.getLocal())) {
            if (!Players.getLocal().isMoving() && Players.getLocal().getAnimation() == -1) {
                tree = GameObjects.getNearest("Tree");
                if (area1.contains(tree)) {
                    tree.interact("Chop down");
                    sleep(Random.nextInt(600, 700));
                } else if (!area1.contains(tree)) {
                    area1Done = true;
                    Movement.walkTileMM(pos2);
                }
            }
        } else if (area1Done && !area2Done) {
            if (!Players.getLocal().isMoving() && Players.getLocal().getAnimation() == -1) {
                tree = GameObjects.getNearest("Tree");
                if (area2.contains(tree)) {
                    tree.interact("Chop down");
                    sleep(Random.nextInt(600, 700));
                } else if (!area2.contains(tree)) {
                    area2Done = true;
                    Movement.walkTileMM(pos3);
                }
            }
        } else if (area2Done && !area3Done) {
            if (!Players.getLocal().isMoving() && Players.getLocal().getAnimation() == -1) {
                tree = GameObjects.getNearest("Tree");
                if (area3.contains(tree)) {
                    tree.interact("Chop down");
                    sleep(Random.nextInt(600, 700));
                } else if (!area3.contains(tree)) {
                    area3Done = true;
                    Movement.walkTileMM(pos4);
                }
            }
        } else if (area3Done && !area4Done) {
            if (!Players.getLocal().isMoving() && Players.getLocal().getAnimation() == -1) {
                tree = GameObjects.getNearest("Tree");
                if (area4.contains(tree)) {
                    tree.interact("Chop down");
                    sleep(Random.nextInt(600, 700));
                } else if (!area4.contains(tree)) {
                    area4Done = true;
                    Movement.walkTileMM(pos5);
                }
            }
        } else if (area4Done && !area5Done) {
            if (!Players.getLocal().isMoving() && Players.getLocal().getAnimation() == -1) {
                tree = GameObjects.getNearest("Tree");
                if (area5.contains(tree)) {
                    tree.interact("Chop down");
                    sleep(Random.nextInt(600, 700));
                } else if (tree == null || !area5.contains(tree)) {
                    area5Done = true;
                    doneCutting = true;
                }
            }
        }
    }

    public void walkToGatePure() throws InterruptedException {
        if (!middleArea.contains(Players.getLocal()) && !startArea.contains(Players.getLocal())) {
            walkedPassedGate = true;
        }
        if (eGate != null && eGate.contains(Players.getLocal()) && !walkedPassedGate) {
            if (gate != null) {
                if (gate.getId() == doorO1 || gate.getId() == doorO2
                        || gate.getId() == doorO3
                        || gate.getId() == doorO4
                        || gate.getId() == doorO5) {
                    Movement.walkTileMM(eastBar1.getCentralTile());
                    walkedPassedGate = true;
                } else {
                    gate.interact("Open");
                    sleep(Random.nextInt(400, 700));
                    if (gate.getId() == doorO1 || gate.getId() == doorO2
                            || gate.getId() == doorO3
                            || gate.getId() == doorO4
                            || gate.getId() == doorO5) {
                        Movement.walkTileMM(eastBar1.getCentralTile());
                        walkedPassedGate = true;
                    }
                }
            }

        } else if (!eGate.contains(Players.getLocal()) && !walkedPassedGate && !Players.getLocal().isMoving()) {
            Application.log("Didn't Movement.walkTileMM past gate and not near the gate");
            WalkAlongPath(toEastG, true);
        }
    }

    public void repairPure() throws InterruptedException {
        if (startArea.contains(Players.getLocal())) {
            walkedPassedGate = false;
        }
        if (!bar1Done) {
            if (Integer.parseInt(Widgets.getComponent(408, 11).getText()) >= 50) {
                sleep(Random.nextInt(1200, 2100));
            } else if (eastBar1 != null && eastBar1.contains(Players.getLocal())
                    && Integer.parseInt(Widgets.getComponent(408, 11).getText()) < 50) {
                fence = GameObjects.getNearest(Barricades);
                if (fence.isOnGameScreen() && fence.getLocation().isWalkable() && eastBar1.contains(fence)) {
                    fence.interact("Repair");
                    sleep(Random.nextInt(500, 600));
                    fence = GameObjects.getNearest(Barricades);
                } else if (!eastBar1.contains(fence)) {
                    bar1Done = true;
                    WalkAlongPath(toEastBar2, true);
                }
            } else if (eastBar1 != null && !eastBar1.contains(Players.getLocal())) {
                Movement.walkTileMM(eastBar1.getCentralTile());
            }

        } else if (!bar2Done) {
            if (Integer.parseInt(Widgets.getComponent(408, 11).getText()) >= 50) {
                sleep(Random.nextInt(1000, 2000));
            } else if (eastBar2 != null && eastBar2.contains(Players.getLocal())
                    && Integer.parseInt(Widgets.getComponent(408, 11).getText()) < 50) {
                fence = GameObjects.getNearest(Barricades);
                if (fence.isOnGameScreen() && fence.getLocation().isWalkable() && eastBar2.contains(fence)) {
                    fence.interact("Repair");
                    sleep(Random.nextInt(500, 600));
                    fence = GameObjects.getNearest(Barricades);
                } else if (!eastBar2.contains(fence)) {
                    bar2Done = true;
                    WalkAlongPath(toEastBar3, true);
                }
            } else if (eastBar2 != null && !eastBar2.contains(Players.getLocal())) {
                Movement.walkTileMM(eastBar2.getCentralTile());
            }

        } else if (!bar3Done) {
            if (Widgets.getComponent(408, 11).getText() != null
                    && Integer.parseInt(Widgets.getComponent(408, 11).getText()) >= 50) {
                sleep(Random.nextInt(1000, 1200));
            } else if (eastBar3 != null && eastBar3.contains(Players.getLocal())
                    && Integer.parseInt(Widgets.getComponent(408, 11).getText()) < 50) {
                fence = GameObjects.getNearest(Barricades);
                if (fence.isOnGameScreen() && fence.getLocation().isWalkable() && eastBar3.contains(fence)) {
                    fence.interact("Repair");
                    sleep(Random.nextInt(500, 600));
                    fence = GameObjects.getNearest(Barricades);
                } else if (!eastBar3.contains(fence)) {
                    bar3Done = true;
                    WalkAlongPath(toEastBar4, true);
                }
            } else if (!eastBar3.contains(Players.getLocal())) {
                WalkAlongPath(toEastBar3, true);
            }

        } else if (!bar4Done) {
            if (Integer.parseInt(Widgets.getComponent(408, 11).getText()) >= 50) {
                sleep(Random.nextInt(1000, 1200));
            } else if (eastBar4 != null && eastBar4.contains(Players.getLocal())
                    && Integer.parseInt(Widgets.getComponent(408, 11).getText()) < 50) {
                fence = GameObjects.getNearest(Barricades);
                if (fence.isOnGameScreen() && fence.getLocation().isWalkable() && eastBar4.contains(fence)) {
                    fence.interact("Repair");
                    sleep(Random.nextInt(500, 600));
                    fence = GameObjects.getNearest(Barricades);
                } else if (!eastBar4.contains(fence)) {
                    bar4Done = true;
                    WalkAlongPath(toEastBar5, true);
                }
            } else if (!eastBar4.contains(Players.getLocal())) {
                bar4Done = true;
                WalkAlongPath(toEastBar4, true);
            }

        } else if (!bar5Done) {
            if (Integer.parseInt(Widgets.getComponent(408, 11).getText()) >= 50) {
                sleep(Random.nextInt(1000, 2000));
            } else if (eastBar5 != null && eastBar5.contains(Players.getLocal())
                    && Integer.parseInt(Widgets.getComponent(408, 11).getText()) < 50) {
                fence = GameObjects.getNearest(Barricades);
                if (fence.isOnGameScreen() && fence.getLocation().isWalkable() && eastBar5.contains(fence)) {
                    fence.interact("Repair");
                    sleep(Random.nextInt(500, 600));
                    fence = GameObjects.getNearest(Barricades);
                } else if (!eastBar5.contains(fence)) {
                    bar5Done = true;
                    WalkAlongPath(toEastBar6, true);
                }
            } else if (eastBar5 != null && !eastBar5.contains(Players.getLocal())) {
                Movement.walkTileMM(eastBar5.getCentralTile());
            }

        } else if (!bar6Done) {
            if (Integer.parseInt(Widgets.getComponent(408, 11).getText()) >= 50) {
                sleep(Random.nextInt(1000, 2000));
            } else if (eastBar6 != null && eastBar6.contains(Players.getLocal())
                    && Integer.parseInt(Widgets.getComponent(408, 11).getText()) < 50) {
                fence = GameObjects.getNearest(Barricades);
                if (fence.isOnGameScreen() && fence.getLocation().isWalkable() && eastBar6.contains(fence)) {
                    fence.interact("Repair");
                    sleep(Random.nextInt(500, 600));
                    fence = GameObjects.getNearest(Barricades);
                } else if (!eastBar6.contains(fence)) {
                    bar6Done = true;
                }
            } else if (eastBar6 != null && !eastBar6.contains(Players.getLocal())) {
                Movement.walkTileMM(eastBar6.getCentralTile());
            }

        } else if (Integer.parseInt(Widgets.getComponent(408, 11).getText()) >= 50) {
            sleep(Random.nextInt(1000, 2000));
        }
    }

    public void nextPortal() throws InterruptedException {
        //east client.getInterface(408).getChild(13).getMessage() // w
        // client.getInterface(408).getChild(14).getMessage() e
        // client.getInterface(408).getChild(15).getMessage() se
        // client.getInterface(408).getChild(16).getMessage() sw
        brawler = NPCs.getNearest("Brawler");
        if (brawler != null && brawler.getLocation().distance(Players.getLocal().getLocation()) < 2.5) {
            if (brawler.getInteractingIndex() == -1) {
                brawler.interact("Attack");
            }
        } else if (playingPC) {
            if (Players.getLocal().getX() > (x + 14) && Widgets.getComponent(408, 14).getText().equalsIgnoreCase("0")) {
                Application.log("East to SE");
                //E To SE
                WalkAlongPath(switchEToSE, true);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }

            } else if (Players.getLocal().getX() > x && Players.getLocal().getX() <= (x + 14)
                    && !Widgets.getComponent(408, 14).getText().equalsIgnoreCase("0")) {
                //SE TO E
                WalkAlongPath(switchEToSE, false);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SE to East");

            } else if (Players.getLocal().getX() > x && Players.getLocal().getX() <= (x + 14)
                    && Widgets.getComponent(408, 14).getText().equalsIgnoreCase("0")
                    && Widgets.getComponent(408, 15).getText().equalsIgnoreCase("0")) {
                //SE TO SW
                WalkAlongPath(switchSEToSW, true);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SE to SW");

            } else if (Players.getLocal().getX() > (x - 14) && Players.getLocal().getX() <= x
                    && !Widgets.getComponent(408, 13).getText().equalsIgnoreCase("0")) {
                //SW TO W
                WalkAlongPath(switchSWToW, true);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SW to W");

            } else if (Players.getLocal().getX() > (x - 14) && Players.getLocal().getX() <= x
                    && Widgets.getComponent(408, 13).getText().equalsIgnoreCase("0")
                    && Widgets.getComponent(408, 16).getText().equalsIgnoreCase("0")) {
                //SW TO SE w e se sw 13 14 15 16
                WalkAlongPath(switchSEToSW, false);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SW to SE");

            } else if (Players.getLocal().getX() <= (x - 15)
                    && Widgets.getComponent(408, 13).getText().equalsIgnoreCase("0")) {
                //W TO SW
                WalkAlongPath(switchSWToW, false);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("W To SW");
            }
        }
    }

    public void nextPortalTest() throws InterruptedException {
        //east client.getInterface(408).getChild(13).getMessage() // w
        // client.getInterface(408).getChild(14).getMessage() e
        // client.getInterface(408).getChild(15).getMessage() se
        // client.getInterface(408).getChild(16).getMessage() sw
        brawler = NPCs.getNearest("Brawler");
        if (brawler != null && brawler.getLocation().distance(Players.getLocal().getLocation()) < 2.5) {
            if (getLastMovementTime()/1000 > 3) {
                brawler.interact("Attack");
                sleep(Random.nextInt(500, 600));
            }
        } else if (playingPC) {
            if (Widgets.getComponent(408, 14).getText().equalsIgnoreCase("0")
                    && !Widgets.getComponent(408, 15).getText().equalsIgnoreCase("0")) {
                Application.log("East to SE");
                //E To SE
                WalkAlongPath(switchEToSE, true);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }

            } else if (!Widgets.getComponent(408, 14).getText().equalsIgnoreCase("0")) {
                //SE TO E
                WalkAlongPath(switchEToSE, false);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SE to East");

            } else if (Widgets.getComponent(408, 14).getText().equalsIgnoreCase("0")
                    && Widgets.getComponent(408, 15).getText().equalsIgnoreCase("0")
                    && !Widgets.getComponent(408, 16).getText().equalsIgnoreCase("0")) {
                //SE TO SW
                WalkAlongPath(switchSEToSW, true);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SE to SW");

            } else if (!Widgets.getComponent(408, 13).getText().equalsIgnoreCase("0")) {
                //SW TO W
                WalkAlongPath(switchSWToW, true);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SW to W");

            } else if (Widgets.getComponent(408, 13).getText().equalsIgnoreCase("0")
                    && Widgets.getComponent(408, 16).getText().equalsIgnoreCase("0")) {
                //SW TO SE w e se sw 13 14 15 16
                WalkAlongPath(switchSEToSW, false);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("SW to SE");

            } else if (Widgets.getComponent(408, 13).getText().equalsIgnoreCase("0")) {
                //W TO SW
                WalkAlongPath(switchSWToW, false);
                if (NPCs.getNearest("Portal") != null) {
                    portalDead = false;
                }
                Application.log("W To SW");
            }
        }
    }

    public void fight() throws InterruptedException {
        NPC spin = NPCs.getNearest("Spinner");
        NPC port = NPCs.getNearest("Portal");
        NPC misc = NPCs.getNearest(mobs);
        NPC brawl = NPCs.getNearest("Brawler");
        Player p = Players.getLocal();
        if (p.getAnimation() != -1
                && (p.getInteracting() != null
                && (p.getInteracting().equals(port)
                || p.getInteracting().equals(spin) || p.getInteracting().equals(misc)
                || p.getInteracting().equals(brawl)))) {
            playerLastMoved = System.currentTimeMillis();
        }

        if (Players.getLocal().getAnimation() != deathAnim) {
            if (port != null) {
            } else {
                portalDead = true;
            }

            if (!isFighting) {
                if (spin != null && Math.abs(spin.getLocation().distance(Players.getLocal().getLocation())) < 9) {
                    if (spin.isOnGameScreen()) {
                        if (brawl != null && spin.getLocation().distance(brawl.getLocation()) < 1) {//error here
                            Application.log("Attacking Brawler that is in the way.");
                            brawl.interact("Attack");
                            isFighting = true;
                        } else {
                            Application.log("Attacking Spinners");
                            spin.interact("Attack");
                            isFighting = true;
                        }
                    } else {
                        Movement.walkTileMM(spin.getLocation());
                    }
                } else if (port != null && atkPort && ((voidKx - port.getX() == -26 && voidKy - port.getY() == 18 && blueEast)
                        || (voidKx - port.getX() == 26 && voidKy - port.getY() == 15 && purpleWest)
                        || (voidKx - port.getX() == -15 && voidKy - port.getY() == 36 && yellowSouthE)
                        || (voidKx - port.getX() == 9 && voidKy - port.getY() == 37 && redSouthW))) {
                    if (port.isOnGameScreen()) {
                        if (brawl != null && port.getLocation().distance(brawl.getLocation()) < 1) {//error here
                            Application.log("Attacking Brawler that is in the way.");
                            brawl.interact("Attack");
                            isFighting = true;
                        } else {
                            Application.log("Attacking Portal");
                            port.interact("Attack");
                            isFighting = true;
                        }
                    } else {
                        Movement.walkTileMM(port.getLocation());
                    }
                } else if (misc != null && Math.abs(misc.getLocation().distance(Players.getLocal().getLocation())) < 9) {
                    if (misc.isOnGameScreen()) {
                        Application.log("Attacking misc");
                        misc.interact("Attack");
                        fightingMisc = true;
                        isFighting = true;
                    } else {
                        Movement.walkTileMM(misc.getLocation());
                    }
                }
            } else {
                if (fightingMisc) {
                    if (spin != null) {
                        isFighting = false;
                        fightingMisc = false;
                    } else if (port != null && atkPort && ((voidKx - port.getX() == -26 && voidKy - port.getY() == 18 && blueEast)
                            || (voidKx - port.getX() == 26 && voidKy - port.getY() == 15 && purpleWest)
                            || (voidKx - port.getX() == -15 && voidKy - port.getY() == 36 && yellowSouthE)
                            || (voidKx - port.getX() == 9 && voidKy - port.getY() == 37 && redSouthW))) {
                        isFighting = false;
                        fightingMisc = false;
                    } else if (getLastMovementTime() >= weaponSpeed) {
                        playerLastMoved = System.currentTimeMillis();
                        isFighting = false;
                    }
                } else if (getLastMovementTime() >= weaponSpeed) {
                    playerLastMoved = System.currentTimeMillis();
                    isFighting = false;
                } else if (Players.getLocal().getInteractingIndex() == -1
                        && p.getInteracting() == null) {
                    isFighting = false;
                } else if ((p.getInteracting() != null && p.getInteracting().equals(Portal)) && isFighting && spinner != null
                        && Math.abs(spinner.getLocation().distance(Players.getLocal().getLocation())) < 9) {
                    isFighting = false;
                }
            }

            if (usingPrayer) {
                if (!prayerActivated) {
                    Tabs.PRAYER.open();
                    sleep(Random.nextInt(300, 400));
                    Widgets.getComponent(271, attPrayer).hover();
                    sleep(Random.nextInt(500, 600));
                    Mouse.click(true);
                    sleep(Random.nextInt(500, 600));
                    if (strPrayer != 0) {
                        Widgets.getComponent(271, strPrayer).hover();
                        sleep(Random.nextInt(500, 600));
                        Mouse.click(true);
                        sleep(Random.nextInt(500, 600));
                    }
                    Tabs.INVENTORY.open();
                    prayerActivated = true;
                }
            }
            if (usingSpec) {
                if (Combat.getSpecialAttackEnergy() >= specPerc) {
                    if (!(Combat.isSpecialAttackOn())) {
                        Tabs.COMBAT.open();
                        sleep(Random.nextInt(500, 600));
                        Mouse.move(specButton);
                        sleep(Random.nextInt(500, 600));
                        Mouse.click(true);
                        sleep(Random.nextInt(500, 600));
                        Tabs.INVENTORY.open();
                    }
                }
                specPerc = Random.nextInt(specTemp, 1000);
            }
        }
    }

    public void buyItems() throws InterruptedException {
        if (!ToastyPestGui.chkboxDontBuy.isSelected()) {
            if (insideBoat.contains(Players.getLocal())) {
                GameObject ladder = GameObjects.getNearest("Ladder");
                ladder.interact("Climb");
                sleep(Random.nextInt(800, 900));
            }
            if (outsideBoat.contains(Players.getLocal())) {
                Movement.walkTileMM(new Tile(2662, 2650, 0));
                sleep(Random.nextInt(2500, 2700));
            }

            NPC exchanger = NPCs.getNearest("Void Knight");
            exchanger.interact("Exchange");
            sleep(Random.nextInt(1200, 1500));
            if (ToastyPestGui.rdbtnBody.isSelected() || ToastyPestGui.rdbtnLegs.isSelected()
                    || ToastyPestGui.rdbtnGloves.isSelected() || ToastyPestGui.rdbtnMageHelm.isSelected()
                    || ToastyPestGui.rdbtnRHelm.isSelected() || ToastyPestGui.rdbtnMeleeHelm.isSelected()) {
                Mouse.move(exchangeBottom);
                sleep(Random.nextInt(400, 500));
                Mouse.click(true);
                sleep(Random.nextInt(900, 1000));
            }

            Widgets.getComponent(267, itemToBuy).hover();
            sleep(Random.nextInt(900, 1000));
            Mouse.click(true);
            sleep(Random.nextInt(900, 1000));
            Mouse.move(confirm);
            sleep(Random.nextInt(900, 1000));
            Mouse.click(true);
            sleep(Random.nextInt(900, 1000));
            if (ToastyPestGui.rdbtnNovice.isSelected()) {
                Movement.walkTileMM(new Tile(2657, 2639, 0));
                sleep(Random.nextInt(1500, 1800));
            } else if (ToastyPestGui.rdbtnMedium.isSelected()) {
                Movement.walkTileMM(new Tile(2650, 2646, 0));
                sleep(Random.nextInt(1500, 1800));
                Movement.walkTileMM(new Tile(2644, 2643, 0));
                sleep(Random.nextInt(1500, 1800));
            } else if (ToastyPestGui.rdbtnHard.isSelected()) {
                Movement.walkTileMM(new Tile(2650, 2646, 0));
                sleep(Random.nextInt(1500, 1800));
                Movement.walkTileMM(new Tile(2638, 2653, 0));
                sleep(Random.nextInt(1500, 1800));
            }
        } else {
            stop();
        }
    }

    //~~~~~~~~~~~~~~~~~~ HELPERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private boolean inTheWay(Player p, NPC m) {

        return false;
    }

    private long getLastMovementTime() {
        return System.currentTimeMillis() - playerLastMoved;
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
        //Application.A("Walking to point:" + destination);
        Movement.walkTileMM(new Tile(path[destination][0], path[destination][1]));

    }

    private int distanceToPoint(int pointX, int pointY) {
        return (int) Math.sqrt(Math
                .pow(Players.getLocal().getX() - pointX, 2)
                + Math.pow(Players.getLocal().getY() - pointY, 2));
    }

    long timeTnl(double xpTNL, double xpPH) {

        if (xpPH > 0) {
            long timeTNL = (long) ((xpTNL / xpPH) * 3600000.0D);
            return timeTNL;
        }
        return 0;
    }

    private String formatTime(long time) {
        int sec = (int) (time / 1000L), d = sec / 86400, h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
        return new StringBuilder()
                .append(d < 10 ? new StringBuilder().append("0").append(d)
                .toString() : Integer.valueOf(d))
                .append(":")
                .append(h < 10 ? new StringBuilder().append("0").append(h)
                .toString() : Integer.valueOf(h))
                .append(":")
                .append(m < 10 ? new StringBuilder().append("0").append(m)
                .toString() : Integer.valueOf(m))
                .append(":")
                .append(s < 10 ? new StringBuilder().append("0").append(s)
                .toString() : Integer.valueOf(s)).toString();
    }
}

package Test;

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
import org.excobot.game.api.methods.Calculations;
import org.excobot.game.api.methods.cache.Game;
import org.excobot.game.api.methods.cache.media.Widgets;
import org.excobot.game.api.methods.input.Keyboard;
import org.excobot.game.api.methods.input.Mouse;
import org.excobot.game.api.methods.media.Bank;
import org.excobot.game.api.methods.media.Bank.Amount;
import org.excobot.game.api.methods.media.animable.GameObjects;
import org.excobot.game.api.methods.media.animable.actor.NPCs;
import org.excobot.game.api.methods.media.animable.actor.Players;
import org.excobot.game.api.methods.scene.Movement;
import org.excobot.game.api.methods.tab.Combat;
import org.excobot.game.api.methods.tab.Inventory;
import org.excobot.game.api.methods.tab.Tabs;
import org.excobot.game.api.util.Random;
import static org.excobot.game.api.util.Time.sleep;
import org.excobot.game.api.util.impl.Filter;
import org.excobot.game.api.wrappers.Locatable;
import org.excobot.game.api.wrappers.cache.landscape.local.PathFinder;
import org.excobot.game.api.wrappers.media.animable.actor.Player;
import org.excobot.game.api.wrappers.media.animable.object.GameObject;
import org.excobot.game.api.wrappers.scene.Area;
import org.excobot.game.api.wrappers.scene.Path;
import org.excobot.game.api.wrappers.scene.Tile;

@Manifest(name = "Tester", authors = "Tester")
public class TestScript extends GameScript implements PaintListener {
    
    private PathWalker pathWalker;

    @Override
    public boolean start() {
        pathWalker = new PathWalker(Players.getLocal().getLocation(), new Tile(3211, 3222));
        return true;
    }

    @Override
    public int execute() throws InterruptedException {
        if(pathWalker != null && pathWalker.traverse()) {
			//destination reached
		}
        return 600;
    }

    @Override
    public void repaint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.drawString("A: " + Calculations.canReach(NPCs.getNearest("Chicken").getLocation()), 10, 300);
    }

    @Override
    public void onFinish() {
    }


}

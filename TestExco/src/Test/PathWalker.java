/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;
import org.excobot.bot.script.Condition;
import org.excobot.game.api.methods.Calculations;
import org.excobot.game.api.methods.cache.Game;
import org.excobot.game.api.methods.media.animable.GameObjects;
import org.excobot.game.api.methods.media.animable.actor.Players;
import org.excobot.game.api.methods.scene.Flags;
import org.excobot.game.api.methods.scene.Movement;
import org.excobot.game.api.util.Time;
import org.excobot.game.api.util.impl.Filter;
import org.excobot.game.api.wrappers.Locatable;
import org.excobot.game.api.wrappers.cache.landscape.local.PathFinder;
import org.excobot.game.api.wrappers.media.animable.object.GameObject;
import org.excobot.game.api.wrappers.scene.Tile;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 16/11/13
 * Time: 23:08
 */
public class PathWalker {

	private final List<GameObject> barriers = Collections.synchronizedList(new LinkedList<GameObject>());
	private final List<Tile> tilePath = Collections.synchronizedList(new LinkedList<Tile>());

	public PathWalker(Locatable startTile, Locatable end) {
		loadBarriers();
		int[][] collisionFlags = Game.getCollisionMaps()[startTile.getLocation().getPlane()].getFlags().clone();
		Tile[] tiles = new PathFinder(startTile.getLocation(), end.getLocation(), openFlags(collisionFlags)).getPath();
		if(tiles != null) {
			Collections.addAll(tilePath, tiles);
		}
	}

	public boolean traverse() throws InterruptedException {
		synchronized (tilePath) {
			for(int i = 0; i < tilePath.size(); i++) {
				Tile tile = tilePath.get(i);
				if(Movement.distance(Players.getLocal(), tile) < 1) {
					for(int i2 = 0; i2 < i; i2++) {
						tilePath.remove(0);
					}
				}
			}

			loadBarriers();
			if(tilePath.size() == 0)
				return true;
			else {
				final GameObject door = getNextObstacle();
				if(door != null && false) {
					if(door.isOnGameScreen()) {
						if(door.interact(door.getActions()[0])) {
							Time.sleep(new Condition() {
								public boolean validate() {
									loadBarriers();
									return getNextObstacle() == door;
								}
							}, 3000);
						}
						return false;
					} else {
						Movement.findPath(Players.getLocal(), door).traverse();
						return false;
					}
				} else {
					if(tilePath.size() == 1) {
						Tile end = tilePath.get(tilePath.size()-1);
						if(Movement.distance(Players.getLocal(), end) < 5)
							return true;
						else {
							Movement.findPath(Players.getLocal(), end).traverse();
							return false;
						}
					}
					return tilePath.size() == 0;
				}
			}
		}
	}

	private void loadBarriers() {
		barriers.clear();
		Collections.addAll(barriers, GameObjects.getLoaded(new Filter<GameObject>() {
			public boolean accept(final GameObject gameObject) {
				if(gameObject.getName() == null || gameObject.getName().equalsIgnoreCase("null"))
					return false;
				String name = gameObject.getName().toLowerCase();
				if(name.contains("door") || name.contains("gate")) {
					if(actionsContain(gameObject.getActions(), "open"))
						return true;
				}
				if(name.contains("wall")) {
					if(actionsContain(gameObject.getActions(), "push"))
						return true;
				}
				return false;
			}
		}));
	}

	private boolean actionsContain(final String[] actions, final String find) {
		return actions != null && Arrays.asList(actions).contains(find);
	}

	public GameObject getNextObstacle() {
		if(tilePath.size() == 0)
			return null;
		for (int i = 0; i < tilePath.size()-1; i++) {
			Tile tile = tilePath.get(i);
			for (GameObject door : barriers) {
				if (door.getModel() != null && door.getLocation().equals(tile)) {
					if(Calculations.canReach(tilePath.get(i+1)))
						continue;
					return door;
				}
			}
		}
		return null;
	}

	private int[][] openFlags(int[][] flags){
		for(GameObject door : barriers) {
			if(door == null)
				continue;
			int x = door.getX()-Game.getBaseX(), y = door.getY()-Game.getBaseY();
			if((flags[x][y] & Flags.WALL_NORTH) == Flags.WALL_NORTH)
				flags[x][y+1] = 0;
			if((flags[x][y] & Flags.WALL_SOUTH) == Flags.WALL_SOUTH)
				flags[x][y-1] = 0;
			if((flags[x][y] & Flags.WALL_EAST) == Flags.WALL_EAST)
				flags[x+1][y] = 0;
			if((flags[x][y] & Flags.WALL_WEST) == Flags.WALL_WEST)
				flags[x-1][y] = 0;
			flags[x][y] = 0;
		}
		return flags;
	}
}
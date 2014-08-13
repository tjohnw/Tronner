/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014. Tristan John Whitcher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tronner.servers.racing.players;

import com.tronner.dispatcher.Commands;
import com.tronner.parser.Parser;
import com.tronner.parser.ServerEventListener;
import com.tronner.servers.racing.logs.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Tronner - PlayerManager
 *
 * @author Tristan on 8/7/2014.
 */
public class PlayerManager extends ServerEventListener {

    private List<Player> players = new ArrayList<>();

    private String winner = "";

    public PlayerManager() {
        Parser.getInstance().reflectListeners(this);
    }

    public void addPlayer(Player racer) {
        if (!players.contains(racer))
            players.add(racer);
    }

    public void removePlayer(Player racer) {
        if (players.contains(racer)) {
            players.remove(racer);
        }
    }

    public void removePlayer(String playerID) {
        removePlayer(playerFromID(playerID));
    }

    public Player playerFromID(String playerID) {
        for (Player r : players) {
            if (r.getId().equals(playerID))
                return r;
        }
        return null;
    }

    public int playersAlive() {
        int alive = 0;
        for (Player p : players)
            if (p.isAlive())
                alive++;
        return alive;
    }

    public int playersFinished() {
        int finished = 0;
        for (Player p : players)
            if (p.isFinished())
                finished++;
        return finished;
    }

    public int playersRacing() {
        int racing = 0;
        for (Player p : players)
            if (!p.isFinished() && p.isAlive())
                racing++;
        return racing;
    }

    public void killAll() {
        for (Player p : players)
            Commands.KILL(p.getId());
    }

    @Override
    public void ROUND_COMMENCING() {
        winner = "";
        for (Player p : players) {
            p.setAlive(false);
            p.setFinished(false);
        }
    }

    @Override
    public void CYCLE_CREATED(String playerName, float xPosition, float yPosition, float xDirection, float yDirection) {

        Player p = playerFromID(playerName);
        if (p == null) {
            p = new Player(playerName);
            addPlayer(p);
            System.out.println("Player created without entering, was this because the script was started during gameplay?");
        }
        p.setAlive(true);
    }

    /**
     * Declares the round winner when all racing is done
     */
    public void declareWinner() {
        Commands.DECLARE_ROUND_WINNER(winner);
        Commands.CENTER_MESSAGE("Winner: " + winner + "                  ");
    }

    @Override
    public void TARGETZONE_PLAYER_ENTER(int globalID, float zoneX, float zoneY,
                                        String playerId, float playerX, float playerY, float playerXDir,
                                        float playerYDir, float time) {

        Player p = playerFromID(playerId);

        if(p == null)
            return;

        if ("".equals(winner)) {
            winner = playerId;
        }
        if (!p.isFinished())
            p.setFinished(true);
    }

    public void death(String player) {
        Player p = playerFromID(player);
        if (p != null)
            p.setAlive(false);
    }

    @Override
    public void DEATH_SUICIDE(String player) {
        death(player);
    }

    @Override
    public void DEATH_FRAG(String playerKilled, String killer) {
        death(playerKilled);
    }

    @Override
    public void DEATH_DEATHZONE(String player) {
        death(player);
    }

    @Override
    public void DEATH_RUBBERZONE(String player) {
        death(player);
    }

    @Override
    public void PLAYER_KILLED(String player, String ip, float x, float y, float what, float why) {
        death(player);
    }

    @Override
    public void PLAYER_RENAMED(String oldName, String newName, String ip, String displayName) {
        if(playerFromID(oldName) == null) {
            addPlayer(new Player(newName));
        } else {
            Player p = playerFromID(oldName);
            p.setId(newName);
        }
    }

    @Override
    public void PLAYER_ENTERED(String name, String ip, String displayName) {
        addPlayer(new Player(name));
    }

    @Override
    public void PLAYER_LEFT(String player, String ip) {
        removePlayer(player);
    }
}
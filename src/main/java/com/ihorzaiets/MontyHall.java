package com.ihorzaiets;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Monty Hall problem or paradox.
 * <p>
 * The best explanation I found is this:
 * <p>
 * If you pick the winning door and switch you always lose, but if you pick a loosing door and switch you always win.
 * Since Monty is forced to eliminate the other loosing doors from the game, but because you are more likely to pick a loosing
 * door in the first place, this means you are more likely to be in situation where switching wins you the game.
 * </p>
 * */
public class MontyHall {

    private static final boolean DO_PLAYER_SWITCH_CHOICE = true;
    private static final int NUMBER_OF_SIMULATIONS = 10000000;

    private static int playerDoorChoice;
    private static int winningDoor;
    private static boolean isPlayerWinning;

    public static void main(String[] args) {
        System.out.println("Do player switch choice: " + DO_PLAYER_SWITCH_CHOICE + "\n");
        runMontyHallSimulation(3);
        runMontyHallSimulation(100);
    }

    private static void runMontyHallSimulation(int numberOfDoors) {
        String formattedNumberOfSimulations = NumberFormat.getInstance(Locale.FRANCE).format(NUMBER_OF_SIMULATIONS);
        System.out.printf("Running %s Monty Hall simulations for %s doors.\n", formattedNumberOfSimulations, numberOfDoors);
        int numberOfWins = 0;
        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
            winningDoor = pickRandomDoor(numberOfDoors);
            playerDoorChoice = pickRandomDoor(numberOfDoors);
            offerChoiceSwitch();
            if (isPlayerWinning) numberOfWins++;
        }
        float percentageOfWin = (float) numberOfWins / (float) NUMBER_OF_SIMULATIONS * 100;
        System.out.printf("Player won in %s%% of simulations.\n\n", percentageOfWin);
    }

    private static void offerChoiceSwitch() {
        isPlayerWinning = playerDoorChoice == winningDoor;
        if (DO_PLAYER_SWITCH_CHOICE) isPlayerWinning = !isPlayerWinning;
    }

    private static int pickRandomDoor(int numberOfDoors) {
        return (int) (Math.random() * numberOfDoors) + 1;
    }
}

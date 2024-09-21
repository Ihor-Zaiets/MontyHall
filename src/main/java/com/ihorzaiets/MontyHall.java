package com.ihorzaiets;

import java.util.HashMap;

public class MontyHall {

    private static HashMap<Integer, Boolean> doorPriceMap = new HashMap<>();
    private static int playerDoorChoice;
    private static int winningDoor;
    private static boolean isPlayerWinning;
    private static float percentageOfWin;

    public static void main(String[] args) {
        runSimulationForMontyHall(3);
        runSimulationForMontyHall(100);
    }

    private static void runSimulationForMontyHall(int numberOfDoors) {
        System.out.printf("Running simulation for %s doors.\n", numberOfDoors);
        int numberOfSimulations = 1000000;
        int numberOfWins = 0;
        for (int i = 0; i < numberOfSimulations; i++) {
            createDoors(numberOfDoors);
            pickADoor();
            revealWrongDoors();
            offerChoiceSwitch();
            if (isPlayerWinning) numberOfWins++;
        }
        percentageOfWin = (float)numberOfWins / (float)numberOfSimulations * 100;
        System.out.printf("Player won in %s%% of simulations.\n", percentageOfWin);
    }



    private static void offerChoiceSwitch() {
        //always switch
        isPlayerWinning = !doorPriceMap.get(playerDoorChoice);
    }

    private static void revealWrongDoors() {
        int secondDoor = (int) (Math.random() * doorPriceMap.size()) + 1;
        while (secondDoor == playerDoorChoice) {
            secondDoor = (int)(Math.random() * doorPriceMap.size()) + 1;
        }
        if (doorPriceMap.get(playerDoorChoice)) {
            doorPriceMap = new HashMap<>();
            doorPriceMap.put(playerDoorChoice, true);
            doorPriceMap.put(secondDoor, false);
        } else {
            doorPriceMap = new HashMap<>();
            doorPriceMap.put(playerDoorChoice, false);
            doorPriceMap.put(winningDoor, true);
        }
    }

    private static void pickADoor() {
        playerDoorChoice = (int)(Math.random() * doorPriceMap.size()) + 1;
    }

    private static void createDoors(int numberOfDoors) {

        for (int i = 1; i <= numberOfDoors; i++) {
            doorPriceMap.put(i, false);
        }
        winningDoor = (int)(Math.random() * numberOfDoors) + 1;
        doorPriceMap.replace(winningDoor, true);
    }
}

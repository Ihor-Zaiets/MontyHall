# Monty Hall problem
## Installation guide

Download zip, it should work out of the box. You would also need valid java SDK. Project was written with java 21,
but should also work with older versions of java.

## Problem description

There are three doors. Behind one is prize and that is winning door. Another 2 doors have nothing and are losing doors.

1. You pick 1 door. You have a 1\3 (33%) chance to pick a winning door. 
2. Then host reveals one of the losing doors.
3. You are given a choice to pick the remaining door or to stick with already chosen.

It might seem that switching does not matter or gives you a higher chance of win like 50\50, but actually it's 2\3 (66%) 
to pick a winning door if you switch.

## Code

There was a lot of explaining from different people, but I decided to just implement this problem as algorithm,
run it, say, 10 millions times and see statistic.

I wrote the fastest and simplest way which came to my mind and then planned on optimization.
The implementation is pretty straightforward:

```java
private static void runMontyHallSimulation(int numberOfDoors) {
    String formattedNumberOfSimulations = NumberFormat.getInstance(Locale.FRANCE).format(NUMBER_OF_SIMULATIONS);
    System.out.printf("Running %s Monty Hall simulations for %s doors.\n", formattedNumberOfSimulations, numberOfDoors);
    int numberOfWins = 0;
    for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
        createDoors(numberOfDoors);
        pickADoor();
        revealWrongDoors();
        offerChoiceSwitch();
        if (isPlayerWinning) numberOfWins++;
    }
    float percentageOfWin = (float) numberOfWins / (float) NUMBER_OF_SIMULATIONS * 100;
    System.out.printf("Player won in %s%% of simulations.\n", percentageOfWin);
}
```

There was another example of this problem with 100 doors, where host reveals 98 losing doors. So I added support for 
number of doors argument.

I used HashMap for doors creation. Number of door as key and if it's winning or not as boolean value. 
Keys are saved starting from 1.

```java
private static void createDoors(int numberOfDoors) {
    for (int i = 1; i <= numberOfDoors; i++) {
        doorPrizeMap.put(i, false);
    }
    winningDoor = pickRandomDoor();
    doorPrizeMap.replace(winningDoor, true);
}
```

then 

```java
private static void pickADoor() {
    playerDoorChoice = pickRandomDoor();
}
```

and then revealing losing doors. As after revealing wrong doors there are always should be 2 doors, 1 winning and 1 losing,
I overwrote hashMap with new values of only 2 doors.

1. If player choose winning door, then left player choice and 1 random losing door
2. If player choose losing door, then left player choice and winning door

```java
private static void revealWrongDoors() {
    int secondDoor = pickRandomDoor();
    while (secondDoor == playerDoorChoice) {
        secondDoor = pickRandomDoor();
    }
    if (doorPrizeMap.get(playerDoorChoice)) {
        doorPrizeMap = new HashMap<>();
        doorPrizeMap.put(playerDoorChoice, true);
        doorPrizeMap.put(secondDoor, false);
    } else {
        doorPrizeMap = new HashMap<>();
        doorPrizeMap.put(playerDoorChoice, false);
        doorPrizeMap.put(winningDoor, true);
    }
}
```

As now there are always 1 winning and 1 losing doors, I checked if player chose winning door and if player decides to 
switch, then just switch the result.

```java
private static void offerChoiceSwitch() {
    isPlayerWinning = doorPrizeMap.get(playerDoorChoice);
    if (DO_PLAYER_SWITCH_CHOICE) isPlayerWinning = !isPlayerWinning;
}
```

Code returns:

```
Running 10 000 000 Monty Hall simulations for 3 doors.
Player won in 66.6651% of simulations.

Running 10 000 000 Monty Hall simulations for 100 doors.
Player won in 99.00329% of simulations.
```

## Optimization

Algorithm was running for about 10 seconds for 100 doors, so I decided to optimized it. Why it's interesting, is because 
I was making legit optimisations and algorithm was returning the same results, but after I finished optimisation,
the meaning of the code was quite different.

First thing, I got rid of HashMap, as it was the biggest bottleneck and I understood I don't need that. All I need is 
number of winning door and number of all doors, as any door except winning will be losing. 
So I replaced and deleted these methods

```java
createDoors(numberOfDoors);
pickADoor();
```

with this code

```java
winningDoor = pickRandomDoor(numberOfDoors);
playerDoorChoice = pickRandomDoor(numberOfDoors);
```

and changed method ```pickRandomDoor()``` as it was depending on hashMap size to this:

```java
private static int pickRandomDoor(int numberOfDoors) {
    return (int) (Math.random() * numberOfDoors) + 1;
}
```

which is basically the same.

I really didn't want to keep Hashmap at all as I was feeling I can replace it with simple int in ```revealWronDoors(int numberOfDoors)```.

So instead of creating a hashMap with 2 doors I kept the values in variables ```secondDoor``` and ```playerDoorChoice```
with the same logic 

1. If player choose winning door, then left player choice and 1 random losing door
2. If player choose losing door, then left player choice and winning door

```java
private static void revealWrongDoors(int numberOfDoors) {
    if (winningDoor == playerDoorChoice) {
        secondDoor = pickRandomDoor(numberOfDoors);
        while (secondDoor == playerDoorChoice) {
            secondDoor = pickRandomDoor(numberOfDoors);
        }
    } else {
        secondDoor = winningDoor;
    }
}
```

and last place where hashMap was used, i replaced 

```java
private static void offerChoiceSwitch() {
    isPlayerWinning = doorPrizeMap.get(playerDoorChoice);
    if (DO_PLAYER_SWITCH_CHOICE) isPlayerWinning = !isPlayerWinning;
}
```

with 

```java
private static void offerChoiceSwitch() {
    isPlayerWinning = playerDoorChoice == winningDoor;
    if (DO_PLAYER_SWITCH_CHOICE) isPlayerWinning = !isPlayerWinning;
}
```

but here i suddenly realised, that ```secondDoor``` variable just isn't used anywhere. And the whole purpose of 
```revealWrondDoors(int numberOfDoors)``` method is to set ```secondDoor``` variable value. So I delete variable and method.

## Unexpected results

At the end I was left with this
```java
for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
    winningDoor = pickRandomDoor(numberOfDoors);
    playerDoorChoice = pickRandomDoor(numberOfDoors);
    offerChoiceSwitch();
    if (isPlayerWinning) numberOfWins++;
}
```

and this code

```java
private static void offerChoiceSwitch() {
    isPlayerWinning = playerDoorChoice == winningDoor;
    if (DO_PLAYER_SWITCH_CHOICE) isPlayerWinning = !isPlayerWinning;
}
```

this basically means: "If player switches, then there are becomes 99 winning doors and 1 losing."(for 100 doors) which is 
absolutely not what I expected from description of this problem. Mind blowing, tbh.

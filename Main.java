import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;

class Game{
  public ArrayList<Player> players;
  public Deck deck;
  public Cards[] communityCards;
  public int pot;
  public int smallBlind;
  public int bigBlind;
  public Player playerSmallBlind;
  public Player playerBigBlind;
  public int[] playersTotalBets;
  Scanner scanner = new Scanner(System.in);

  public Game(int smallBlind){
    this.players = new ArrayList<Player>();
    this.deck = new Deck();
    this.communityCards = new Cards[5];
    this.pot = 0;
    this.smallBlind = smallBlind;
    this.bigBlind = smallBlind * 2;
    playerSetup();
    bettingRound(true);
    dealNextCommunityCard();
    bettingRound(false);
    dealNextCommunityCard();
    bettingRound(false);
    dealNextCommunityCard();
    bettingRound(false);
    findWinner();
    rotatePositions();
  }

  public void addPlayer(){
    System.out.println("Enter your name.");
    String name = scanner.nextLine();
    System.out.println("Enter starting balance in dollars.");
    int balance = scanner.nextInt();
    Cards[] heldCards = {deck.getNextCard(), deck.getNextCard()};
    System.out.println("Your cards are: " + heldCards[0] + ", " + heldCards[1]);
    players.add(new Player(name, balance, heldCards));
    scanner.nextLine();
  }

  public void rotatePositions(){
    int smallBlindIndex = this.players.indexOf(this.playerSmallBlind);
    int bigBlindIndex = this.players.indexOf(this.playerBigBlind);
    int tempIndex = smallBlindIndex;
    smallBlindIndex = bigBlindIndex;
    bigBlindIndex = tempIndex;
  }
  
  public void bettingRound(boolean isPreFlop){
    int startingPlayer;
    if(isPreFlop == true){
      startingPlayer = this.players.indexOf(this.playerSmallBlind);
    }
    else{
      startingPlayer = this.players.indexOf(this.playerSmallBlind);
    }
    int currentPlayer = startingPlayer;
    for(int i = 0; i < players.size(); i++){
      if(this.players.get(currentPlayer).isNotFolded == true){
        currentPlayer = individualBet(currentPlayer);
      }  
    }
    while(!areAllBetsEqual()){
      currentPlayer =individualBet(currentPlayer);
    }
  }

  public int individualBet(int currentPlayer){
    String checkOrCall = areAllBetsEqual() ? "check" : "call";
    System.out.println("The pot is $" + this.pot);
      System.out.println(this.players.get(currentPlayer).toString() + " Do you want to fold, " + checkOrCall + " or raise?");
      String answer = scanner.nextLine();
      if(answer.equals("fold")){
        fold(this.players.get(currentPlayer));
        System.out.println(this.players.get(currentPlayer).name + " lost");
        System.exit(0);
        
      }
      else if(answer.equals("call")){
        call(this.players.get(currentPlayer));
      }
      else if(answer.equals("raise")){
        raise(this.players.get(currentPlayer));
        scanner.nextLine();
      }
      currentPlayer++;
      if(currentPlayer == this.players.size()){
        currentPlayer = 0;
      }
    return currentPlayer;
  }
  
   public boolean areAllBetsEqual(){
    int highestBet = getHighestBet();
    for(int j = 0; j < this.playersTotalBets.length; j++){
      if(this.playersTotalBets[j] < highestBet && this.players.get(j).isNotFolded == true){
        return false;
      }
    }
    return true;
  }

  public int getHighestBet(){
    int highestBet = 0;
    for(int i = 0; i < this.playersTotalBets.length; i++){
      if(this.playersTotalBets[i] > highestBet && this.players.get(i).isNotFolded == true){
        highestBet = this.playersTotalBets[i];
      }
    }
    return highestBet;
  }
  
  public void fold(Player player){
    player.isNotFolded = false;
  }

  public void call(Player player){
    int highestBet = getHighestBet();
    int playersBetDifference = highestBet - (this.playersTotalBets[this.players.indexOf(player)]);
    player.reduceBalance(playersBetDifference);
    this.playersTotalBets[this.players.indexOf(player)] += playersBetDifference;
    updatePot();
  }

  public void raise(Player player){
    System.out.println(player.name + " How much do you want to raise");
    int raiseAmount = scanner.nextInt();
    call(player);
    player.reduceBalance(raiseAmount);
    this.playersTotalBets[this.players.indexOf(player)] += raiseAmount;
    updatePot();
  }

  public void dealNextCommunityCard(){
    if(this.communityCards[0] == null){
      this.communityCards[0] = this.deck.getNextCard();
      this.communityCards[1] = this.deck.getNextCard();
      this.communityCards[2] = this.deck.getNextCard();
      System.out.println("---------------------------------------------------------------------------------------------------------");
      System.out.println("Flop " + this.communityCards[0] + " " + this.communityCards[1] + " " + this.communityCards[2]);
      System.out.println("---------------------------------------------------------------------------------------------------------");
    }
    else if(this.communityCards[3] == null){
      this.communityCards[3] = this.deck.getNextCard();
      System.out.println("---------------------------------------------------------------------------------------------------------");
      System.out.println("Turn " + this.communityCards[0] + " " + this.communityCards[1] + " " + this.communityCards[2] + " " + this.communityCards[3]);
      System.out.println("---------------------------------------------------------------------------------------------------------");
    }
    else if(this.communityCards[4] == null){
      this.communityCards[4] = this.deck.getNextCard();
      System.out.println("---------------------------------------------------------------------------------------------------------");
      System.out.println("River " + this.communityCards[0] + " " + this.communityCards[1] + " " + this.communityCards[2] + " " + this.communityCards[3] + " " + this.communityCards[4]);
      System.out.println("---------------------------------------------------------------------------------------------------------");
    }
  }

  public void updatePot(){
    this.pot = 0;
    for(int i = 0; i < this.playersTotalBets.length; i++){
      this.pot += this.playersTotalBets[i]; 
    }
  }
    
  public void findWinner(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    winningPlayer = royalFlush();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }
    winningPlayer = straightFlush();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }
     /*winningPlayer = fourOfAKind();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }*/
     /*winningPlayer = fullHouse();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }*/
     winningPlayer = flush();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }
     winningPlayer = straight();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }
     /*winningPlayer = threeOfAKind();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }*/
     /*winningPlayer = twoPair();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }*/
     /*winningPlayer = onePair();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }*/
     winningPlayer = HighCard();
    if(winningPlayer.size() > 0){
      handleWinners(winningPlayer);
      return;
    }
  }

  public void handleWinners(ArrayList<Player> winningPlayer){
    int tempWinner = this.pot / winningPlayer.size();
      for(int i = 0; i < winningPlayer.size(); i++){
        winningPlayer.get(i).addBalance(tempWinner);
        System.out.println(winningPlayer.get(i).name + " won balance is now $" + winningPlayer.get(i).balance );
      }
    this.pot = 0;
  }
    
  public ArrayList<Player> HighCard(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    int[][] sortedPlayersHeldCards = new int[this.players.size()] [2];
    int highestCardColTwo = 0;
    int highestCardColOne = 0;
    boolean tie = false;
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        ArrayList<Integer> heldCards = new ArrayList<Integer>();
        heldCards.add(this.players.get(i).heldCards[0].value);
        heldCards.add(this.players.get(i).heldCards[1].value);
        Collections.sort(heldCards);
        
        if(heldCards.get(1) > highestCardColTwo){
          highestCardColTwo = heldCards.get(1);
          winningPlayer.clear();
          winningPlayer.add(this.players.get(i));
          sortedPlayersHeldCards = new int[this.players.size()] [2];
          sortedPlayersHeldCards[i][0] = i;
          sortedPlayersHeldCards[i][1] = heldCards.get(0);
        }
        else if(heldCards.get(1) == highestCardColTwo){
          winningPlayer.add(this.players.get(i));
          tie = true;
          sortedPlayersHeldCards[i][0] = i;
          sortedPlayersHeldCards[i][1] = heldCards.get(0);
        }
      }
    }
    if(tie == true){
      winningPlayer.clear();
      for(int k = 0; k < sortedPlayersHeldCards.length; k++){
        if(sortedPlayersHeldCards[k][1] > highestCardColOne){
          highestCardColOne = sortedPlayersHeldCards[k][1];
        }
      }
      for(int l = 0; l < sortedPlayersHeldCards.length; l++){
        if(sortedPlayersHeldCards[l][1] == highestCardColOne){
          winningPlayer.add(this.players.get(l));
        }
      }
    }
    return winningPlayer;
  }
    
  public ArrayList<Player> onePair(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        int [] allValues = new int[13];
      ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
      for(int j = 0; j < playerCardsAll.size(); j++){
        allValues[playerCardsAll.get(j).value - 2]++;
      }
      for(int k = 0; k < allValues.length; k++){
        if(allValues[k] >= 2 && !winningPlayer.equals(this.players.get(i))){
            winningPlayer.add(this.players.get(i));  
      }
        }
      }
    }
    return winningPlayer;
  }
    
  public ArrayList<Player> twoPair(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        int [] allValues = new int[13];
        int pairCount = 0;
      ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
      for(int j = 0; j < playerCardsAll.size(); j++){
        allValues[playerCardsAll.get(j).value - 2]++;
      }
      for(int k = 0; k < allValues.length; k++){
        if(allValues[k] >= 2){
          pairCount++;
          if(pairCount == 2){
            winningPlayer.add(this.players.get(i));
          }   
      }
        }
      }
    }
    return winningPlayer;
  }
    
  public ArrayList<Player> threeOfAKind(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        int [] allValues = new int[13];
      ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
      for(int j = 0; j < playerCardsAll.size(); j++){
        allValues[playerCardsAll.get(j).value - 2]++;
      }
      for(int k = 0; k < allValues.length; k++){
        if(allValues[k] >= 3){
          winningPlayer.add(this.players.get(i));
      }
        }
      }
    }
    return winningPlayer;
  }
    
  public ArrayList<Player> straight(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        ArrayList<Integer> playerCards = new ArrayList<Integer>();
        ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
        ArrayList<Integer> duplicatesRemoved = new ArrayList<Integer>();
        for(int k = 0; k < playerCards.size(); k++){
          if(!duplicatesRemoved.equals(playerCards.get(k))){
            duplicatesRemoved.add(playerCards.get(k));
          }
        }
        if(duplicatesRemoved.size() >= 5){
          if(duplicatesRemoved.get(1) == duplicatesRemoved.get(0) + 1){
            if(duplicatesRemoved.get(2) == duplicatesRemoved.get(1) + 1 && duplicatesRemoved.get(3) == duplicatesRemoved.get(2) + 1 && duplicatesRemoved.get(4) == duplicatesRemoved.get(3) + 1){
              winningPlayer.add(this.players.get(i)); 
            }
          }
        }
        if(duplicatesRemoved.size() >= 6){
          if(duplicatesRemoved.get(2) == duplicatesRemoved.get(1) + 1){
            if(duplicatesRemoved.get(3) == duplicatesRemoved.get(2) + 1 && duplicatesRemoved.get(4) == duplicatesRemoved.get(3) + 1 && duplicatesRemoved.get(5) == duplicatesRemoved.get(4) + 1){
              if(!winningPlayer.equals(this.players.get(i))){
                 winningPlayer.add(this.players.get(i)); 
              }
             
            }
          }
        }
        if(duplicatesRemoved.size() >= 7){
          if(duplicatesRemoved.get(3) == duplicatesRemoved.get(2) + 1){
            if(duplicatesRemoved.get(4) == duplicatesRemoved.get(3) + 1 && duplicatesRemoved.get(5) == duplicatesRemoved.get(4) + 1 && duplicatesRemoved.get(6) == duplicatesRemoved.get(5) + 1){
              if(!winningPlayer.equals(this.players.get(i))){
                 winningPlayer.add(this.players.get(i)); 
              } 
            }
          }
        }
      }
    }
    return winningPlayer;
  }
    
  public ArrayList<Player> flush(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    for(int i = 0; i < this.players.size(); i++){
    int hearts = 0;
    int diamonds = 0;
    int spades = 0;
    int clubs = 0;
      if(this.players.get(i).isNotFolded == true){
        ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
        for(int j = 0; j < playerCardsAll.size(); j++){
          if(playerCardsAll.get(j).suit.equals("Hearts")){
            hearts++;
          }
          if(playerCardsAll.get(j).suit.equals("Diamonds")){
            diamonds++;
          }
          if(playerCardsAll.get(j).suit.equals("Spades")){
            spades++;
          }
          if(playerCardsAll.get(j).suit.equals("Clubs")){
            clubs++;
          }
        }
        if(hearts >= 5 || diamonds >= 5 || spades >= 5 || clubs >= 5){
          winningPlayer.add(this.players.get(i));
        }
      }
    }
    return winningPlayer;
  }
    
  public ArrayList<Player> fullHouse(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    for(int i = 0; i < this.players.size(); i++){
    int hearts = 0;
    int diamonds = 0;
    int spades = 0;
    int clubs = 0;
      if(this.players.get(i).isNotFolded == true){
        int [] allValues = new int[13];
        boolean threeCardValue = false;
        boolean twoCardValue = false;
      ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
      for(int j = 0; j < playerCardsAll.size(); j++){
        allValues[playerCardsAll.get(j).value - 2]++;
        }
        for(int k = 0; k < allValues.length; k++){
          if(allValues[k] >= 3){
            threeCardValue = true;
          }
          else if(allValues[k] >= 2){
            twoCardValue = true;
          }
        }
        if(threeCardValue == true && twoCardValue == true){
          winningPlayer.add(this.players.get(i));
        }
      }
    }
    return winningPlayer;
  }

  public ArrayList<Player> fourOfAKind(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        int [] allValues = new int[13];
      ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
      for(int j = 0; j < playerCardsAll.size(); j++){
        allValues[playerCardsAll.get(j).value - 2]++;
      }
      for(int k = 0; k < allValues.length; k++){
        if(allValues[k] >= 4){
          winningPlayer.add(this.players.get(i));
      }
        }
      }
    }
    return winningPlayer;
  }
    
  public ArrayList<Player> straightFlush(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    int hearts = 0;
    int diamonds = 0;
    int spades = 0;
    int clubs = 0;
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        ArrayList<Integer> playerCards = new ArrayList<Integer>();
        ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
        for(int j = 0; j < playerCardsAll.size(); j++){
          if(playerCardsAll.get(j).suit.equals("Hearts")){
            hearts++;
          }
          if(playerCardsAll.get(j).suit.equals("Diamonds")){
            diamonds++;
          }
          if(playerCardsAll.get(j).suit.equals("Spades")){
            spades++;
          }
          if(playerCardsAll.get(j).suit.equals("Clubs")){
            clubs++;
          }
        }
        if(hearts >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Hearts");
        }
        else if(diamonds >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Diamonds");
        }
        else if(spades >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Spades");
        }
        else if(clubs >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Clubs");
        }
        ArrayList<Integer> duplicatesRemoved = new ArrayList<Integer>();
        for(int k = 0; k < playerCards.size(); k++){
          if(!duplicatesRemoved.equals(playerCards.get(k))){
            duplicatesRemoved.add(playerCards.get(k));
          }
        }
        if(duplicatesRemoved.size() >= 5){
          if(duplicatesRemoved.get(1) == duplicatesRemoved.get(0) + 1){
            if(duplicatesRemoved.get(2) == duplicatesRemoved.get(1) + 1 && duplicatesRemoved.get(3) == duplicatesRemoved.get(2) + 1 && duplicatesRemoved.get(4) == duplicatesRemoved.get(3) + 1){
              winningPlayer.add(this.players.get(i)); 
            }
          }
        }
        if(duplicatesRemoved.size() >= 6){
          if(duplicatesRemoved.get(2) == duplicatesRemoved.get(1) + 1){
            if(duplicatesRemoved.get(3) == duplicatesRemoved.get(2) + 1 && duplicatesRemoved.get(4) == duplicatesRemoved.get(3) + 1 && duplicatesRemoved.get(5) == duplicatesRemoved.get(4) + 1){
              if(!winningPlayer.equals(this.players.get(i))){
                 winningPlayer.add(this.players.get(i)); 
              }
             
            }
          }
        }
        if(duplicatesRemoved.size() >= 7){
          if(duplicatesRemoved.get(3) == duplicatesRemoved.get(2) + 1){
            if(duplicatesRemoved.get(4) == duplicatesRemoved.get(3) + 1 && duplicatesRemoved.get(5) == duplicatesRemoved.get(4) + 1 && duplicatesRemoved.get(6) == duplicatesRemoved.get(5) + 1){
              if(!winningPlayer.equals(this.players.get(i))){
                 winningPlayer.add(this.players.get(i)); 
              } 
            }
          }
        }
        }
      }
    return winningPlayer;
    }

  public ArrayList<Player> royalFlush(){
    ArrayList<Player> winningPlayer = new ArrayList<Player>();
    int hearts = 0;
    int diamonds = 0;
    int spades = 0;
    int clubs = 0;
    for(int i = 0; i < this.players.size(); i++){
      if(this.players.get(i).isNotFolded == true){
        ArrayList<Integer> playerCards = new ArrayList<Integer>();
        ArrayList<Cards> playerCardsAll = playerCardsAll(this.players.get(i));
        for(int j = 0; j < playerCardsAll.size(); j++){
          if(playerCardsAll.get(j).suit.equals("Hearts")){
            hearts++;
          }
          if(playerCardsAll.get(j).suit.equals("Diamonds")){
            diamonds++;
          }
          if(playerCardsAll.get(j).suit.equals("Spades")){
            spades++;
          }
          if(playerCardsAll.get(j).suit.equals("Clubs")){
            clubs++;
          }
        }
        if(hearts >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Hearts");
        }
        else if(diamonds >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Diamonds");
        }
        else if(spades >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Spades");
        }
        else if(clubs >= 5){
          playerCards = getValuesOfSuit(playerCardsAll, "Clubs");
        }
        if(this.players.get(i).equals(10) && this.players.get(i).equals(11) && this.players.get(i).equals(12) && this.players.get(i).equals(13) && this.players.get(i).equals(14)){
          winningPlayer.add(this.players.get(i));
        }
        }
      }
    return winningPlayer;
    }
    
  public ArrayList<Cards> playerCardsAll(Player player){
    ArrayList<Cards> result = new ArrayList<Cards>();
    for(int i = 0; i < this.communityCards.length; i++){
      result.add(this.communityCards[i]);
    }
    result.add(player.heldCards[0]);
    result.add(player.heldCards[1]);
    return result;
  }
    
  public ArrayList<Integer> getValuesOfSuit(ArrayList<Cards> playerCardsAll, String suit){
    ArrayList<Integer> result = new ArrayList<Integer>();
    for(int i = 0; i < playerCardsAll.size(); i++){
      if(playerCardsAll.get(i).suit.equals(suit)){
        result.add(playerCardsAll.get(i).value);
      }
    }
    return result;
  }
    
  public void playerSetup(){
    addPlayer();
    addPlayer();
    this.playerSmallBlind = players.get(0);
    this.playerBigBlind = players.get(1);
    this.playersTotalBets = new int[players.size()];
  }

  public void payOutBlinds(){
    this.playerSmallBlind.reduceBalance(this.smallBlind);
    this.playersTotalBets[this.players.indexOf(playerSmallBlind)] = this.smallBlind;
    this.playerBigBlind.reduceBalance(this.bigBlind);
    this.playersTotalBets[this.players.indexOf(playerBigBlind)] = this.bigBlind;
  }
}
class Cards{
  public int value;
  public String suit;
 
  public Cards(int value, String suit){
    this.value = value;
    this.suit = suit;
  }
 
  public String convertValueToName(){
    switch(this.value){
        case 1 : return "Ace";
        case 2 : return "Two";
        case 3 : return "Three";
        case 4 : return "Four";
        case 5 : return "Five";
        case 6 : return "Six";
        case 7 : return "Seven";
        case 8 : return "Eight";
        case 9 : return "Nine";
        case 10 : return "Ten";
        case 11 : return "Jack";
        case 12 : return "Queen";
        case 13 : return "King";
        case 14 : return "Ace";
    default: return "Value not valid.";
    }
  }
  public String toString(){
    return(convertValueToName() + " of " + this.suit);
  }
}

class Deck{
  public ArrayList<Cards> deck = new ArrayList<Cards>();

  public Deck(){
    reset();
    shuffle();
  }
 
  public void reset(){
    this.deck.clear();
    for(int i = 1; i < 14; i++){
      deck.add(new Cards(i, "Hearts"));
    }
    for(int i = 1; i < 14; i++){
      deck.add(new Cards(i, "Diamonds"));
    }
    for(int i = 1; i < 14; i++){
      deck.add(new Cards(i, "Spades"));
    }
    for(int i = 1; i < 14; i++){
      deck.add(new Cards(i, "Clubs"));
    }
  }

  public void shuffle(){
    ArrayList<Cards> tempDeck = new ArrayList<Cards>();
    while(this.deck.size() > 0){
      tempDeck.add(this.deck.remove(((int) (Math.random() * 100)) % this.deck.size()));
    }
    this.deck = tempDeck;
  }

  public Cards getNextCard(){
    return this.deck.remove(this.deck.size() - 1);
  }

  public int getRemainingCardCount(){
    return this.deck.size();
  }
}

class Player{
  public String name;
  public int balance;
  public Cards[] heldCards = new Cards[2];
  public boolean isNotFolded;

  public Player(String name, int balance, Cards[] heldCards){
    this.name = name;
    this.balance = balance;
    this.heldCards = heldCards;
    this.isNotFolded = true;
  }
 
  public int reduceBalance(int amount){
    return this.balance -= amount;
  }

  public int addBalance(int amount){
    return this.balance += amount;
  }
 
  public String toString(){
    return("Player " + this.name + " has " + this.balance + " dollars. Held cards are " + this.heldCards[0] + " " + this.heldCards[1] + ".");
  }
}

class Main {
  public static void main(String[] args) {
    Game poker = new Game(500);
  }
}
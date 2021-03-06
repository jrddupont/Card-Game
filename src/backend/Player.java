package backend;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Player {
	public PrintWriter writer;
	
	public static final int TRICKS = 0;
	public static final int CARD_TOTAL = 1;
	
	//	Game Data
	public ArrayList<Card> hand = new ArrayList<Card>();
	public int turn;	// Who's turn it is
	public Card[] playedCards;	// Cards on table
	public int[][] score;	// score[PLAYER_ONE][TRICKS] score[PLAYER_TWO][CARD_TOTAL] for example
	public int serverStatus = 0;	// Players connected, 3 means the game can start
	public int playerNumber;	// What player they are
	//	Game Data
	
	public Player(int playerNumber, PrintWriter writer){
		this.playerNumber = playerNumber;
		this.writer = writer;
		
		turn = -1;
		playedCards = new Card[3];
		score = new int[][] {{0,0},{0,0},{0,0}};
	}
	
	/**
	 * Checks to see if the player has a given card.
	 * @param cardNumber What card to check for.
	 * @return True if the player has the card
	 */
	public boolean hasCard(int cardNumber){
		for(Card c : hand){
			if(c.cardNumber == cardNumber){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks to see if the player has a given suit.
	 * @param suit What suit to check for.
	 * @return True if the player has the suit
	 */
	public boolean hasSuit(int suit){
		for(Card c : hand){
			if(c.getSuit() == suit){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets a string representing all player information in JSON format
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getPlayerPacket(){	// Used by the server to send user data to client
		JSONObject container = new JSONObject();
		container.put("turn", turn);	// Who's turn it is
		container.put("playerNumber", playerNumber);	// Player number (for verification)
		container.put("serverStatus", serverStatus);	// Server status (0, 1, or 2)
		container.put("hand", getHandString());	// Player's hand
		
		container.put("playedCards", getPlayedCardString());	// Cards on the table
		container.put("score", getScoreString());	// 3x2 array of scores
		
		return container.toJSONString();
	}
	
	/**
	 * Update all player data given a JSON string.
	 * @param data Entire player packet encoded in JSON
	 * @return True if data parsed successfully.
	 */
	public boolean updatePlayerData(String data){	// Used by the client to update player info
		
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(data);
		} catch (ParseException e) {
			return false;
		}
        JSONObject jsonData = (JSONObject)obj;
        
        turn = ((Long)jsonData.get("turn")).intValue();
        playerNumber = ((Long)jsonData.get("playerNumber")).intValue();
        serverStatus = ((Long)jsonData.get("serverStatus")).intValue();
        
        parseHandString((String)jsonData.get("hand"));
        parsePlayedCardString((String)jsonData.get("playedCards"));
        parseScoreString((String)jsonData.get("score"));
        return true;
	}
	
	/**
	 * Turn 2D score array into a string
	 * @return A string of score numbers separated by spaces and a colon
	 */
	private String getScoreString(){
		String output = "";
		for(int i = 0; i < score.length; i++){
			output += score[i][TRICKS] + " ";
		}
		output = output.trim() + ":";
		for(int i = 0; i < score.length; i++){
			output += score[i][CARD_TOTAL] + " ";
		}
		return output.trim();
	}
	
	/**
	 * Parse score string into 2D array
	 * @param inputScoreString String to parse
	 */
	private void  parseScoreString(String inputScoreString){
		String[] groups = inputScoreString.split(":");
		String[] tricks = groups[TRICKS].split(" ");
		String[] cardTotal = groups[CARD_TOTAL].split(" ");
		for(int i = 0; i < tricks.length; i++){
			score[i][TRICKS] = Integer.parseInt(tricks[i]);
		}
		for(int i = 0; i < cardTotal.length; i++){
			score[i][CARD_TOTAL] = Integer.parseInt(cardTotal[i]);
		}
	}
	
	/**
	 * Turns played cards array into a string of card numbers separated by spaces
	 * @return String representing card array
	 */
	private String getPlayedCardString(){
		String output = "";
		for(Card c : playedCards){
			if(c == null){
				output += -1 + " ";
			}else{
				output += c.cardNumber + " ";
			}
		}
		return output.trim();
	}
	
	/**
	 * Parse played cards string into an array
	 * @param inputPlayedCard String to parse
	 */
	private void parsePlayedCardString(String inputPlayedCard){
		String[] input = inputPlayedCard.split(" ");
		playedCards = new Card[3];
		for(int i = 0; i < input.length; i++){
			int card = Integer.parseInt(input[i]);
			if(card == -1){
				playedCards[i] = null;
			}else{
				playedCards[i] = new Card(Integer.parseInt(input[i]));
			}
			
		}
	}
	
	/**
	 * Turn hand arraylist into a string of numbers separated by spaces
	 * @return
	 */
	private String getHandString(){
		String output = "";
		for(Card c : hand){
			output += c.cardNumber + " ";
		}
		return output.trim();
	}
	/**
	 * Parse a string of integers into card objects 
	 * @param inputHand	String to parse
	 */
	private void parseHandString(String inputHand){
		hand = new ArrayList<Card>();
		if(inputHand.length() < 1){
			return;
		}
		String[] input = inputHand.split(" ");
		
		for(String s : input){
			hand.add(new Card(Integer.parseInt(s)));
		}
	}
	
	public String toString(){
		return "Player " + playerNumber;
	}

	/**
	 * Removes given card from players hand.
	 * @param chosenCard Card to remove.	
	 */
	public void removeCard(Card chosenCard) {
		Card cardToRemove = null;
		for(Card c : hand){
			if(c.equals(chosenCard)){
				cardToRemove = c;
			}
		}
		hand.remove(cardToRemove);
	}
}

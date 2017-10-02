package backend;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Card {
	
	int pos;
	
	BufferedImage faces = null; 
	
	public void loadImage() {
		try {
			faces = ImageIO.read(new File("cards.jpg"));
		}
		catch (IOException e){
			
		}
	}
	
	public Card(int pos) {
		this.pos = pos;
	}
	
	public Card() {
		pos = 0;
	}
	
	public int getSuit() {
		return (pos/13);
	}
	public String getSuitString() {
		switch(pos/13) {
		case 0: return "Clubs";
		case 1: return "Diamonds";
		case 2: return "Hearts";
		case 3: return "Spades";
		default: return "Error";
		}
	}
	public int getValue() {
		return (pos % 13 + 2);
	}
	public String getValString() {
		switch(pos % 13 + 2) {
		case 11: return "J";
		case 12: return "Q";
		case 13: return "K";
		case 14: return "A";
		default: return Integer.toString(pos % 13 + 2);
		}
	}
	public void setValue(int pos) {
		this.pos = pos;
	}
	/*
	public BufferedImage getCardImage() {
		
	}
	*/
}

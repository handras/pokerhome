package hu.bandris_1994.pokerhome;

import android.util.Log;
import android.util.StringBuilderPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by bandris_1994 on 2016.05.29..
 */
public class CardDeck {

    private ArrayList<Card> deck = new ArrayList<>(52);
    private ArrayList<Card> removed = new ArrayList<>();

    public CardDeck(){
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < Card.suits.values().length; i++) {
            for (int j = 0; j < Card.ranks.values().length; j++) {
                deck.add(new Card(Card.suits.values()[i], Card.ranks.values()[j]));
            }
            Collections.shuffle(deck, rand);
        }
        Collections.shuffle(deck);
    }

    public void Shuffle(long seed){
        deck.addAll(removed);
        removed.clear();
        Collections.shuffle(deck, new Random(seed));
    }

    public void Shuffle(){
        Shuffle(System.currentTimeMillis());
    }

    public Card Draw(){
        int i = new Random(System.nanoTime()).nextInt(deck.size());
        Card c = deck.remove(i);
        removed.add(c);
        return c;
    }

    public static String[] CardFileNames(){
        int ranks = Card.ranks.values().length;
        int suits = Card.suits.values().length;
        String[] names = new String[ranks*suits];
        for (int i = 0; i < suits; i++) {
            for (int j = 0; j < ranks; j++) {
                names[i*ranks+j]=new Card(Card.suits.values()[i], Card.ranks.values()[j]).getFilename();
            }
        }
        return names;
    }
}

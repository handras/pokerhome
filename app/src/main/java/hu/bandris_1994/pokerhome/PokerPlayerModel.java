package hu.bandris_1994.pokerhome;

/**
 * Created by bandris_1994 on 2016.06.01..
 */
public class PokerPlayerModel {
    private PokerTableModel table;
    private String name;
    private int coin;
    private Card card1, card2;

    public PokerPlayerModel(int cash, String name){
        coin = cash;
        this.name=name;
        card1=null;
        card2=null;
    }

    public void setTable(PokerTableModel t){
        table = t;
    }

    public String getName(){
        return name;
    }

    public void deal(Card c1, Card c2){
        card1=c1;
        card2=c2;
    }

    public void GiveMoney(int sum){
        coin += sum;
    }

    public int getCoin(){
        return coin;
    }

    public Card[] getCards(){
        return new Card[]{card1,card2};
    }

    public void act(){

    }
}

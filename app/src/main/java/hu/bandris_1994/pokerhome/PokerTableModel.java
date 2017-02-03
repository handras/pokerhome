package hu.bandris_1994.pokerhome;

import android.app.Application;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hu.bandris_1994.pokerhome.util.PokerRank;
import view.tableview;

/**
 * Created by bandris_1994 on 2016.06.01..
 */
public class PokerTableModel {
    private ArrayList<PokerPlayerModel> players = null;
    private CardDeck deck = null;
    private Card[] cards = null;
    private int pot = 0;
    private GameState state;
    private static PokerTableModel instance = null;
    private View[] views;
    private Thread GameThread;

    public final Object lock;
    public boolean ContinueRunning;

    enum GameState{
        idle, preFlop, preTurn, PreRiver
    }

    public static PokerTableModel getInstance(){
        if(instance==null){
            instance = new PokerTableModel();
        }
        return instance;
    }

    //Clears the table
    public void NewTable(){
        players =  new ArrayList<>();
        deck = new CardDeck();
        cards = new Card[5];
        state = GameState.preFlop;
    }

    private PokerTableModel(){
        lock=new Object();
        views = new View[2];
        NewTable();
    }

    //subscribes the view to notify changes
    public void Subscribe(View v){
        if(views[0]==null){
            views[0]=v;
        }else{
            views[1]=v;
        }
    }

    private void notifyViews(){
        if(views[0]!=null)
            views[0].getHandler().obtainMessage(tableview.Messages.TableStateChanged.ordinal()).sendToTarget();
        if(views[1]!=null)
            views[1].getHandler().obtainMessage(tableview.Messages.TableStateChanged.ordinal()).sendToTarget();
    }

    public void PlayerSit(PokerPlayerModel player){
        players.add(player);
        player.setTable(this);
    }

    private void WaitInteraction(){
        ContinueRunning=false;
        notifyViews();
        synchronized (lock){
            while (!ContinueRunning){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyViews();
    }

    public PokerPlayerModel getPlayer(int place){
        return players.get(place);
    }

    public Card[] getCards(){
        return cards;
    }

    public void Play() {
        GameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                GameLogic();
            }
        });
        GameThread.start();
    }

    boolean quitGame = false;
    public void GameLogic(){
        quitGame = false;
        state=GameState.preFlop;
        while (!quitGame){
            deck.Shuffle();
            for(PokerPlayerModel player: players){
                player.deal(deck.Draw(),deck.Draw());
            }
            WaitInteraction();
            boolean hasWinner = false;
            while(!hasWinner) {
                switch (state) {
                    case preFlop:
                        for (int i = 0; i < 3; i++) {
                            cards[i] = deck.Draw();
                        }
                        state = GameState.preTurn;
                        WaitInteraction();
                        break;
                    case preTurn:
                        cards[3] = deck.Draw();
                        state = GameState.PreRiver;
                        WaitInteraction();
                        break;
                    case PreRiver:
                        cards[4] = deck.Draw();
                        state = GameState.idle;
                        WaitInteraction();
                        break;
                    case idle:
                        //quit = true;
                        hasWinner = true;
                        for (int i = 0; i < 5; i++) {
                            cards[i]=null;
                        }
                        state=GameState.preFlop;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public String evaluate(Card[] playercards){

        Card[] allcards = new Card[cards.length + 2];
        System.arraycopy(cards, 0, allcards, 0, cards.length);
        System.arraycopy(playercards, 0, allcards, cards.length, playercards.length);

        Card[] evalcards = new Card[5];
        int p =0;
        PokerRank finalrank= new PokerRank(cards);
        //every 5 card out of 7
        for (int i = 0; i < 6; i++) {
            for (int j = i+1; j < 7; j++) {
                p=0;
                //5 card to evalcards from allcards
                for (int k = 0; k < 7; k++) {
                    if(k!=j && k!=i){
                        evalcards[p++] = allcards[k];
                    }
                }
                //get rank of 5 card handset
                PokerRank ranker = new PokerRank(evalcards);
                if(ranker.getRank() > finalrank.getRank()){
                    finalrank = ranker;
                }
            }
        }
        return finalrank.getDescription();
//        return finalrank.testdescription;
    }
}

package hu.bandris_1994.pokerhome;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * Created by bandris_1994 on 2016.05.29..
 */
public class Card {
    private suits suit;
    private ranks rank;
    private Bitmap bitmap;

    public Card(suits suit, ranks rank){
        this.suit=suit;
        this.rank=rank;
        this.bitmap=null;
    }

    public final static int ACE = 14;

    enum suits{
        diamonds, hearts, spades, clubs
    }
    enum ranks{
        _2, _3, _4, _5, _6, _7, _8, _9, _10, _jack, _queen, _king, _ace
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(rank.name() + " of " + suit.name());
        return builder.toString().substring(1);
    }

    public String getFilename(){
        StringBuilder builder = new StringBuilder();
         return builder.append("card" + rank.name().substring(1) + "of" + suit.name()).toString();
    }

    public Bitmap getBitmap(){
        if(bitmap==null){
            Resources res = MainActivity.getInstance().getResources();
            String context = MainActivity.getInstance().getApplicationContext().getPackageName();
            int id = res.getIdentifier(getFilename(), "drawable", context);
            bitmap = BitmapFactory.decodeResource(res, id);
        }
        return bitmap;
    }

    public byte getValue(){
        int i = 0;
        for (; ranks.values()[i] != rank ; i++) {
        }
        return (byte)(i+2);
    }

    public byte getSuit(){
        switch (suit){
            case spades:
                return 0;
            case clubs:
                return 3;
            case hearts:
                return 1;
            case diamonds:
                return 2;
        }
        return 0;
    }

}

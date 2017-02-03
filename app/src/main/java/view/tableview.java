package view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import hu.bandris_1994.pokerhome.Card;
import hu.bandris_1994.pokerhome.PokerPlayerModel;
import hu.bandris_1994.pokerhome.PokerTableModel;

/**
 * Created by bandris_1994 on 2016.06.02..
 */
public class tableview extends View {

    //paint object for drawing, initialized in the constructor
    Paint green = null;
    Paint red = null;
    Paint black = null;
    PokerTableModel table = null;
    //original ratio of card height/width
    static final float ratio = 1.452f;
    boolean TableViewActive=false;

    int margin=100;
    int marginChange=5;
    Rect CardsBoundingRect;
    boolean zoomToCards=false;

    //Zoom animation to cards
    Runnable zoomanimator = new Runnable() {
        @Override
        public void run() {
            boolean scheduleNewFrame = true;
            if(margin<100 && !zoomToCards) {
                margin += marginChange;
                if(margin>=100) {
                    margin = 100;
                    scheduleNewFrame = false;
                }
            }
            else if (margin>0 && zoomToCards){
                margin-=marginChange;
                if(margin<=0){
                    margin=0;
                    scheduleNewFrame=false;
                }
            }
            if (scheduleNewFrame) {
                postDelayed(this, 20);
            }
            invalidate();
        }
    };

    public tableview(Context context, AttributeSet attrs) {
        super(context, attrs);

        green=new Paint();
        green.setColor(Color.argb(255,45,120,35));
        green.setStyle(Paint.Style.FILL);

        red=new Paint();
        red.setColor(Color.argb(255,160, 25, 25));
        red.setStrokeWidth(5);
        red.setStyle(Paint.Style.STROKE);

        black = new Paint();
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);
        black.setTextSize(20);

        CardsBoundingRect=new Rect();

        table=PokerTableModel.getInstance();
        table.Subscribe(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), green);
        if (TableViewActive){
            DrawTableView(canvas);
        }else {
            DrawPlayerView(canvas);
        }
    }

    private void DrawCards(Canvas canvas, int left, int top, int right, int bottom){
        int width = right-left;
        int height = bottom-top;
        int picw = width/5;
        int pich = (int) (picw*ratio);
        if(pich>height){
            pich=height;
            picw=(int) (pich/ratio);
            left+=(width-5*picw)/2;
        }
        float cardFrameWidth = picw*0.04f;
        float cardOffSet = cardFrameWidth/2;
        red.setStrokeWidth(cardFrameWidth);
        Card[] cards=table.getCards();
        int cardtop = height/2-(pich/2)+top;
        int cardbottom = cardtop+pich;
        CardsBoundingRect.set(left, cardtop, left+5*picw, cardbottom);
        for (int i = 0; i < 5; i++) {
            RectF  rect = new RectF(i*picw+left, cardtop,(i+1)*picw+left, cardbottom);
            canvas.drawRoundRect(rect, cardFrameWidth, cardFrameWidth, red);
            if(cards[i]==null)
                continue;
            Bitmap pic1 = cards[i].getBitmap();
            pic1 = Bitmap.createScaledBitmap(pic1, Math.round(picw-cardFrameWidth+1), Math.round(pich-cardFrameWidth+1), false );
            canvas.drawBitmap(pic1, i*picw+left+cardOffSet, cardtop+cardOffSet, red);
        }
    }

    private void DrawTableView(Canvas canvas){
        DrawCards(canvas, margin, margin, getWidth()-margin, getHeight()-margin);
        //debug bounding box
        canvas.drawRect(margin, margin, getWidth()-margin, getHeight()-margin, black);
    }

    private void DrawPlayerView(Canvas canvas){
        PokerPlayerModel player = table.getPlayer(0);
        Card cards[] = player.getCards();

        Bitmap pic1 = cards[0].getBitmap();
        Bitmap pic2 = cards[1].getBitmap();

        int width = getWidth();
        int picw = width/2;
        int pich = (int)(picw*ratio);
        pic1 = Bitmap.createScaledBitmap(pic1, picw, pich, false );
        pic2 = Bitmap.createScaledBitmap(pic2, picw, pich, false );
        canvas.drawBitmap(pic1, 0, 50, red);
        canvas.drawBitmap(pic2, width/2, 50, red);

        cards = table.getCards();
        picw = width/5;
        pich = (int)(picw*ratio);
        for(int i=0; i<5; i++){
            if(cards[i]==null){
                continue;
            }
            pic1=cards[i].getBitmap();
            pic1 = Bitmap.createScaledBitmap(pic1, picw, pich, false );
            canvas.drawBitmap(pic1, picw*i, pich*3, red);
        }
        drawCoins(canvas,player.getCoin());
        canvas.drawText(player.getName(),40, 40, black);
        if(cards[4]!=null)
            canvas.drawText(table.evaluate(player.getCards()),40, getHeight()-55, black);
    }

    public void drawCoins(Canvas canvas, int money){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,14,143,230));
        paint.setStyle(Paint.Style.FILL);
        Paint black = new Paint();
        black.setColor(Color.argb(255, 0, 0, 0));
        int i=0;
        while (money > 0){
            int a = money % 10;
            canvas.drawCircle(getWidth()-i*61-15, getHeight()-32, 30, paint);
            canvas.drawText(Integer.toString(a),getWidth()-i*61-20,getHeight()-32, black);
            money/=10;
            i++;
        }
    }

    private boolean DownInCardPlace = false;
    private boolean Swiped=false;
    private int MoveStartX=0;
    private int MoveStartY=0;
    private static final int MOVETRESHOLD=10;
    @Override
    public boolean onTouchEvent(MotionEvent event){
        final int x = (int)event.getX();
        final int y = (int)event.getY();
        final int action= event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                MoveStartX=x;
                MoveStartY=y;
                Swiped=false;
                //touched on cards
                if(CardsBoundingRect.contains(x, y)){
                    DownInCardPlace=true;
                }
                return true;
            case MotionEvent.ACTION_UP:
                //it was not a swipe, but a tap
                if(!Swiped){
                    checkCode(dirs.tap);
                    //touch ended where it started: on cards, thats why DownInCardsPlace is true
                    if(DownInCardPlace) {
                        zoomToCards = !zoomToCards;
                        post(zoomanimator);
                        DownInCardPlace=false;
                        return true;
                    }else {
                        //touched outside of cards
                        synchronized (table.lock) {
                            table.ContinueRunning = true;
                            table.lock.notify();
                        }
                    }
                }
                Swiped=false;
                break;
            case MotionEvent.ACTION_MOVE:
                final int deltaX = x - MoveStartX;
                final int deltaY = y - MoveStartY;
                if((Math.abs(deltaX)<MOVETRESHOLD && Math.abs(deltaY)<MOVETRESHOLD) || Swiped) {
                    break;
                }
                //if moving, its not clicking on cards
                DownInCardPlace = false;
                //swipe right
                if(deltaX>5*MOVETRESHOLD){
                    checkCode(dirs.right);
                    Swiped=true;
                    TableViewActive=true;
                    invalidate();
                    return true;
                }
                //swipe left
                else if (deltaX<-5*MOVETRESHOLD){
                    checkCode(dirs.left);
                    Swiped=true;
                    TableViewActive=false;
                    invalidate();
                    return true;
                }
                //swipe up
                else if (deltaY<-5*MOVETRESHOLD){
                    checkCode(dirs.up);
                    Swiped=true;
                    return true;
                }
                //swipe down
                else if (deltaY>5*MOVETRESHOLD){
                    checkCode(dirs.down);
                    Swiped=true;
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
    public enum Messages{
        TableStateChanged
    }
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==Messages.TableStateChanged.ordinal()){
                invalidate();
            }
        }
    };

    @Override
    public Handler getHandler() {
        return mHandler;
    }


    private enum dirs{up,down,left,right,tap};
    private final dirs[] code ={
            dirs.up,dirs.up,
            dirs.down,dirs.down,
            dirs.left,dirs.right,
            dirs.left,dirs.right,
            dirs.tap,dirs.tap
    };
    private int codeProgress=0;
    private final int len=code.length;
    private void checkCode(dirs d){
        if(d==code[codeProgress]){
            codeProgress++;
        }
        else {
            codeProgress=0;
        }
        if(codeProgress==len){
            codeProgress=0;
            Log.d("konami","code success");
        }
    }
}

package ryan.musicplayerproject.Utility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.ArrayList;
import ryan.musicplayerproject.Model.LyricContent;

/**
 * All the Lyric Function From Internet Learning
 * @link http://blog.csdn.net/iwanghang/article/details/51388896
 */
public class LyricView extends TextView {
    private float width;
    private float height;
    // use to draw current Lyric
    private Paint currentPaint;
    // use to draw not current Lyric
    private Paint notCurrentPaint;
    // use to draw no Lyric
    private Paint noPaint;
    private float textHeight = 70;
    private float textSize = 45;
    // Index of the current Lyric
    private int index = 0;
    // Every LyricContent matches one Sentence in Lyric, whole List will be the Lyric for the song
    private ArrayList<LyricContent> myLyricList = null;

    /*
    Constructor
     */
    public LyricView(Context context){
        super(context);
        init();
    }

    public LyricView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }

    public LyricView(Context context,AttributeSet attributeSet,int defSytle){
        super(context,attributeSet,defSytle);
        init();
    }

    // Getter and Setter
    public void setIndex(int index){
        this.index = index;
    }

    public void setMyLyricList(ArrayList<LyricContent> lyricList){
        this.myLyricList = lyricList;
    }

    public ArrayList<LyricContent> getMyLyricList(){
        return this.myLyricList;
    }

    // Init the Draw of the Lyric
    private void init(){
        setFocusable(true);

        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);

        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);

        noPaint = new Paint();
        noPaint.setAntiAlias(true);
        noPaint.setTextAlign(Paint.Align.CENTER);

    }

    /*
    Using OnDraw method to draw the Lyric through lyricView.invalidate() method, through
    handler and Runnable to draw the Lyric to synchronize with the song
    */
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(canvas == null){
            return ;
        }

        // Set Color for Lyric
        currentPaint.setColor(Color.BLUE);
        notCurrentPaint.setColor(Color.BLACK);
        noPaint.setColor(Color.BLACK);

        // Set TextStyle and Size for current Sentence
        currentPaint.setTextSize(50);
        currentPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Set TextStyle and Size for notCurrent Sentence
        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.MONOSPACE);

        // Set TextStyle and Size for No Lyric
        noPaint.setTextSize(60);
        noPaint.setTypeface(Typeface.DEFAULT_BOLD);

        try{
            setText("");

            // Draw previous sentence
            float tempY = height / 2;
            for(int i = index - 1;i >= 0; i--){
                tempY -= textHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(),width/2,tempY,notCurrentPaint);
            }

            // Draw current sentence
            canvas.drawText(myLyricList.get(index).getLyricString(),width/2,height/2,currentPaint);

            // Draw after sentence
            tempY = height / 2;
            for(int i = index + 1;i < myLyricList.size(); i++){
                tempY += textHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(),width/2,tempY,notCurrentPaint);
            }

        }

        // Draw No Lyrics
        catch(Exception e){
            canvas.drawText("No Lyrics Found!",width/2,height/2,noPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w,int h,int oldW,int oldH){
        super.onSizeChanged(w,h,oldW,oldH);
        this.width = w;
        this.height = h;
    }
}
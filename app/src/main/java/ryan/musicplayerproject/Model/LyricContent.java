package ryan.musicplayerproject.Model;

/**
 * All the Lyric Function From Internet Learning
 * @link http://blog.csdn.net/iwanghang/article/details/51388896
 */
public class LyricContent {
    // Content in the current sentence
    private String lyricString;
    // Time of the current sentence
    private int lyricTime;

    /*
    All Getter and Setter
     */

    public String getLyricString(){
        return this.lyricString;
    }

    public void setLyricString(String str){
        this.lyricString = str;
    }

    public int getLyricTime(){
        return this.lyricTime;
    }

    public void setLyricTime(int time){
        this.lyricTime = time;
    }
}

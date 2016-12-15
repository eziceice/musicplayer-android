package ryan.musicplayerproject.Model;

/**
 * All the Lyric Function From Internet Learning
 * @link http://blog.csdn.net/iwanghang/article/details/51388896
 */
public class Lyric {
    // Variable in Lyric Object
    private String lyricName;
    private String lyricPath;

    // Constructor
    public Lyric(String lyricName, String lyricPath) {
        this.lyricName = lyricName;
        this.lyricPath = lyricPath;
    }
    /*
     All Getter And Setter
    */
    public String getLyricName() {
        return lyricName;
    }

    public void setLyricName(String lyricName) {
        this.lyricName = lyricName;
    }

    public String getLyricPath() {
        return lyricPath;
    }

    public void setLyricPath(String lyricPath) {
        this.lyricPath = lyricPath;
    }
}

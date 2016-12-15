package ryan.musicplayerproject.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ryan.musicplayerproject.Model.LyricContent;

/**
 * All the Lyric Function From Internet Learning
 * @link http://blog.csdn.net/iwanghang/article/details/51388896
 */
public class LyricProgress {

    private ArrayList<LyricContent> lyricList;
    private LyricContent myLyricContent;

    // Constructor
    public LyricProgress(){
        myLyricContent = new LyricContent();
        lyricList = new ArrayList<LyricContent>();
    }

    // Read the Lyrics from the file
    public String readLyric(String path){
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(path);
        try{
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis,"GBK");
            BufferedReader br = new BufferedReader(isr);
            String s= "";
            /*
            Clean the Lyric File
            */
            while((s = br.readLine()) != null){

                // Remove [] from the Lyric
                s = s.replace("[","");
                s = s.replace("]","@");

                // Remove Time Label Using Regular Expression
                s = s.replaceAll("<[0-9]{3,5}>","");
                // Split the Lyric by @
                String spiltLrcData[] = s.split("@");

                if(spiltLrcData.length > 1){
                    // Get one sentence and one time from the Lyric File and Add to the LyricContent Object
                    myLyricContent.setLyricString(spiltLrcData[1]);
                    int lycTime = time2Str(spiltLrcData[0]);
                    myLyricContent.setLyricTime(lycTime);
                    // Add the Object to the LyricList
                    lyricList.add(myLyricContent);
                    // Remove the Current Sentence and New One LyricContent to use
                    myLyricContent = new LyricContent();
                }
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            stringBuilder.append("No Lyrics Found!");
        }
        catch(IOException e){
            e.printStackTrace();
            stringBuilder.append("No Lyrics Found!");
        } finally {
            return stringBuilder.toString();
        }
    }

    // Change The Time in the Lyric File to Int
    public int time2Str(String timeStr){
        // Clean the String
        timeStr = timeStr.replace(":",".");
        timeStr = timeStr.replace(".","@");
        // Split the String by @
        String timeData[] = timeStr.split("@");
        // Get Min, Sec and MillSec
        int min = Integer.parseInt(timeData[0]);
        int sec = Integer.parseInt(timeData[1]);
        int millSec = Integer.parseInt(timeData[2]);
        int currentTime = (min * 60 + sec) * 1000 + millSec * 10;
        return currentTime;
    }

    public ArrayList<LyricContent> getLyricList(){
        return this.lyricList;
    }
}

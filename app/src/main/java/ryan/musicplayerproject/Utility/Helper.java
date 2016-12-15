package ryan.musicplayerproject.Utility;

/**
 * Created by EleMeNt on 4/05/16.
 */
/**
 * All the Icon Download From StackOverFlow
 * @link http://stackoverflow.com/questions/9027317/
 */
public class Helper {
    /**
     * Function to convert milliseconds time to
     * Time Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTime(long milliseconds){
        String finalTimeString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int)(milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimeString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimeString = finalTimeString + minutes + ":" + secondsString;

        // return time string
        return finalTimeString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration elapsed time of song
     * @param totalDuration length of song
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage;

        if (currentDuration == 0 && totalDuration == 0){
            return 0;
        }
        else {
            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // calculating percentage
            percentage = (((double) currentSeconds) / totalSeconds) * 100;

            // return percentage
            return percentage.intValue();
        }
    }

    /**
     * Function to change progress to time
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTime(int progress, int totalDuration) {
        int currentDuration;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}

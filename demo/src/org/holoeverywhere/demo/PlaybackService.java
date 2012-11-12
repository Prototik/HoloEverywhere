
package org.holoeverywhere.demo;

import android.content.Context;
import android.media.MediaPlayer;

public class PlaybackService {
    private static boolean ignore = false;
    private static MediaPlayer mediaPlayer;
    private static int seek = 0;

    public static void ignore() {
        ignore = true;
    }

    public static void onCreate() {
        if (!disable && seek == -1) {
            seek = 0;
        }
    }

    public static void onPause() {
        if (ignore) {
            return;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                seek = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            } else {
                seek = -1;
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void onResume(Context context) {
        if (ignore) {
            ignore = false;
            return;
        }
        if (disable || seek < 0) {
            return;
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.winter_dawn);
        mediaPlayer.setLooping(true);
        if (seek > 0) {
            mediaPlayer.seekTo(seek);
        }
        mediaPlayer.start();
    }

    private PlaybackService() {
    }

    private static boolean disable = false;

    public static boolean isDisable() {
        return disable;
    }

    public static void disable() {
        disable = true;
        ignore = false;
        onPause();
        ignore = true;
        seek = -1;
    }
}

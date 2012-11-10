
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
        if (seek == -1) {
            seek = 0;
        }
    }

    public static void onDestroy() {
        if (ignore) {
            return;
        }
        // seek = 0;
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
        if (seek < 0) {
            return;
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.winter_dawn);
        if (seek > 0) {
            mediaPlayer.seekTo(seek);
        }
        mediaPlayer.start();
    }

    public static void restart(Context context) {
        seek = 0;
        onResume(context);
    }

    private PlaybackService() {
    }
}

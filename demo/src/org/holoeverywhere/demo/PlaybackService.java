
package org.holoeverywhere.demo;

import org.holoeverywhere.app.Application;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

public class PlaybackService {
    private static final Handler HANDLER = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            PlaybackService.handleMessage(msg.what, msg.arg1);
            return true;
        }
    });
    private static boolean mDisable = false;
    private static MediaPlayer mPlayer;
    private static int mSeek = -1;
    private static final int T_PAUSE = 1, T_PLAY = 2;

    private static void handleMessage(int what, int res) {
        switch (what) {
            case T_PAUSE:
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mSeek = mPlayer.getCurrentPosition();
                        mPlayer.stop();
                    }
                    mPlayer.release();
                    mPlayer = null;
                }
                break;
            case T_PLAY:
                if (mPlayer != null && mPlayer.isPlaying()) {
                    return;
                }
                mPlayer = MediaPlayer.create(Application.getLastInstance(), res);
                if (mSeek > 0) {
                    mPlayer.seekTo(mSeek);
                }
                mPlayer.start();
                break;
        }
    }

    public static boolean isDisable() {
        return mDisable;
    }

    public static void pause() {
        HANDLER.removeMessages(T_PLAY);
        Message message = Message.obtain(HANDLER, T_PAUSE);
        HANDLER.sendMessageDelayed(message, 400);
    }

    public static void play() {
        HANDLER.removeMessages(T_PAUSE);
        Message message = Message.obtain(HANDLER, T_PLAY);
        message.arg1 = R.raw.winter_dawn;
        message.sendToTarget();
    }

    private PlaybackService() {
    }
}

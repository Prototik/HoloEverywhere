
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
    private static MediaPlayer mPlayer;
    private static int mSeek = -1;
    private static final int T_PAUSE = 1, T_PLAY = 2, T_STOP = 3;

    private static void handleMessage(int what, int res) {
        switch (what) {
            case T_PAUSE:
            case T_STOP:
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mSeek = what == T_PAUSE ? mPlayer.getCurrentPosition() : -1;
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
                mPlayer.setLooping(true);
                mPlayer.start();
                break;
        }
    }

    public static void pause(boolean stop) {
        HANDLER.removeMessages(T_PLAY);
        HANDLER.removeMessages(T_STOP);
        HANDLER.removeMessages(T_PAUSE);
        if (stop) {
            HANDLER.sendMessage(Message.obtain(HANDLER, T_STOP));
        } else {
            HANDLER.sendMessageDelayed(Message.obtain(HANDLER, T_PAUSE), 400);
        }
    }

    public static void play() {
        HANDLER.removeMessages(T_PAUSE);
        HANDLER.removeMessages(T_STOP);
        Message message = Message.obtain(HANDLER, T_PLAY);
        message.arg1 = R.raw.winter_dawn;
        message.sendToTarget();
    }

    private PlaybackService() {
    }
}

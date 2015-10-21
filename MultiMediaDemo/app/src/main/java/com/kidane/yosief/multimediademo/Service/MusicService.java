package com.kidane.yosief.multimediademo.Service;

import java.util.ArrayList;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.kidane.yosief.multimediademo.Model.Song;

/**
 * * @author Kidane Yosief
 * @version 10/20/2015
 * @since 1.0
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    String LOGCAT = "playhere";
    //binder
    private final IBinder musicBind = new MusicBinder();

    public void onCreate(){
        //create the service
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }
    public void playSong(){

        player.stop();
        //get song
        Song playSong = songs.get(songPosn);
        Log.i(LOGCAT, (playSong.toString()));
//get id
        long currSong = playSong.getID();
        Log.i(LOGCAT, Long.toString(currSong));
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        Log.i(LOGCAT, trackUri.toString());
        player.start();
        /*int me = player.getDuration()%(1000*60*60)/ (1000*60);
        Log.i(LOGCAT, me+" min");*/
        try{
            player.setDataSource(getApplicationContext(), trackUri);
           // Log.i(LOGCAT, trackUri.toString());
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
       player.prepareAsync();
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }


}
package com.sun.music61.screen.service;

import com.sun.music61.data.model.Track;
import java.util.List;

public interface BasePlayTrackService {
    void startTrack();
    void changeTrack(Track track);
    void pauseTrack();
    void stopTrack();
    void seek(int milliseconds);
    long getDuration();
    long getCurrentDuration();
    int getState();
    void setTracks(List<Track> tracks);
    List<Track> getTracks();
    void removeTrack(Track track);
}

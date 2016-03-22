package com.example.drdc_admin.moverioapp.interfaces;

/**
 * interface for ContentActivity and stepImage/VideoFragment to communicate
 */
public interface Communicator {
// https://www.youtube.com/watch?v=fvG20PAUdcU&index=10&list=PLonJJ3BVjZW4lMlpHgL7UNQSGMERcDzHo

    public void putImgFragment();
    public void putVideoFragment();

    public void openMenu();
    public void closeMenu();
    public void handleGesture(String gesture);
    public void handleFingersSpread();
    public void moveUporDown(String upordown);


//    public void playNextVideo();
//    public void playPrevVideo();
}

package com.example.drdc_admin.moverioapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.drdc_admin.moverioapp.Constants;
import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.interfaces.Communicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class stepVideoFragment extends Fragment {

    private String fileName;
    private static final String fileNameArg = "fileNameWithoutExt";
    private static final String TAG = "stepImageFragment";
    private VideoView videoView;
    private static int playPosition;
    private MediaController mediaControls;
    private TextView tv_gesture;
    private int skipSeconds = 5;
    long timeNewGesture;
    public stepVideoFragment() {
        // Required empty public constructor
    }

    public void handleGesture(String gesture) {
        playPosition = videoView.getCurrentPosition();
//        Log.i(TAG, "handleGesture playPosition = " + playPosition);

        switch (gesture) {
            case Constants.MYO_FINGERSPEREAD:
                videoView.pause();
                Communicator comm = (Communicator) getActivity();
                comm.openMenu();

                break;
            case Constants.MYO_FIST:
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    play();
                }
                break;

            case Constants.MYO_WAVEIN:
                playPosition = playPosition - (skipSeconds * 1000);
                play();
                break;
            case Constants.MYO_WAVEOUT:
                playPosition = playPosition + (skipSeconds * 1000);
                play();
                break;


        }

        // inform the user which gesture has been made except "rest"
        if (!gesture.equals(Constants.MYO_REST)) {

            timeNewGesture = System.currentTimeMillis();
            tv_gesture.setText(gesture);
            tv_gesture.setVisibility(View.VISIBLE);

            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // fade out image?
                }

                @Override
                public void onFinish() {
                    long now = System.currentTimeMillis();
                    // hide textview if the user did not make any gesture in the last x second
                    if ((now - timeNewGesture) >= (1 * 1000)) {
                        tv_gesture.setVisibility(View.INVISIBLE);
                    }
                }
            }.start();
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param videoFileName Parameter 1.
     * @return A new instance of fragment stepImageFragment.
     */
    public static stepVideoFragment newInstance(String videoFileName) {
        stepVideoFragment fragment = new stepVideoFragment();
        Bundle args = new Bundle();
        args.putString(fileNameArg, videoFileName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            fileName = getArguments().getString(fileNameArg);
        }
        mediaControls = new MediaController(getActivity());

        tv_gesture = (TextView) getActivity().findViewById(R.id.gestureOnVideo);

        videoView = (VideoView) getActivity().findViewById(R.id.videoView);
        videoView.setMediaController(mediaControls);

        // TODO overall text instruction
        playFile(fileName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_video, container, false);
    }

    public void play() {
//        Log.i(TAG, "play() playPosition = " + playPosition);

        videoView.seekTo(playPosition);
        videoView.start();
    }

    public void playFile(String fileNameWithoutExt) {

//        Log.i(TAG, "playFile() playPosition = " + playPosition);

        String path = "/storage/sdcard1/airplane/" + fileNameWithoutExt + ".mp4";
        videoView.setVideoPath(path);
        videoView.seekTo(playPosition);
        videoView.start();
    }

    public void resume() {
        play();
    }

    public void replay() {
        playPosition = 0;
        play();
    }

    public void setFileName(String newFile) {
        fileName = newFile;
    }

    public void playNext() {
//        Log.i(TAG, "currentFileName = " + currentFileName);

        // extract nondigits from the file
        int currentStepNum = Integer.parseInt(fileName.replaceAll("[^0-9]", ""));
//        Log.i(TAG, "currentStepNum = " + currentStepNum);

        // change "airplanestep01" to "airplanestep02"
        String chars = fileName.replaceAll("[0-9]", "");
        int newStep = currentStepNum + 1;

        // TODO check if newStep > total num of steps

        String newFileName;
        if (newStep < 10) {
            newFileName = chars + 0 + newStep;
        } else {
            newFileName = chars + newStep;

        }

        // update the variable currentFileName
        fileName = newFileName;

        playPosition = 0;
        playFile(fileName);


    }

    public void playPrev() {
//        Log.i(TAG, "currentFileName = " + currentFileName);

        // extract nondigits from the file
        int currentStepNum = Integer.parseInt(fileName.replaceAll("[^0-9]", ""));
//        Log.i(TAG, "currentStepNum = " + currentStepNum);

        // change "airplanestep01" to "airplanestep02"
        String chars = fileName.replaceAll("[0-9]", "");
        int newStep = currentStepNum - 1;
        if (newStep < 1) {
            newStep = 1;
        }

        String newFileName;
        if (newStep < 10) {
            newFileName = chars + 0 + newStep;
        } else {
            newFileName = chars + newStep;

        }
        // update the variable currentFileName
        fileName = newFileName;

        playPosition = 0;
        playFile(fileName);


    }

}

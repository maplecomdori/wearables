package com.example.drdc_admin.moverioapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drdc_admin.moverioapp.Constants;
import com.example.drdc_admin.moverioapp.R;
import com.example.drdc_admin.moverioapp.activities.ContentActivity;
import com.example.drdc_admin.moverioapp.interfaces.Communicator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link stepImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link stepImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class stepImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String fileNameArg = "fileNameWithoutExt";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "stepImageFragment";


    // TODO: Rename and change types of parameters
    private ImageView imgView;
    private TextView textView;
    private String fileName;

    private OnFragmentInteractionListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imgFileName Parameter 1.
     * @return A new instance of fragment stepImageFragment.
     */
    public static stepImageFragment newInstance(String imgFileName) {
        stepImageFragment fragment = new stepImageFragment();
        Bundle args = new Bundle();
        args.putString(fileNameArg, imgFileName);
        fragment.setArguments(args);
        return fragment;
    }

    public stepImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imgView = (ImageView) getActivity().findViewById(R.id.iv_stepImage);
        textView = (TextView) getActivity().findViewById(R.id.tv_stepImage);
        setImage(fileName);
        setText(fileName);

    }



    public void setImage(String imgFileNameWithoutExt) {
//        String uriPath = Constants.sdCardDirectory + imgFileNameWithoutExt + ".png";
        String uriPath = ContentActivity.pathToContentFolder + imgFileNameWithoutExt + ".png";
        imgView.setImageURI(Uri.parse(uriPath));

    }

    public void setText(String imgFileNameWithoutExt) {

        // read the text file containing instruction for the current step
        try {
            String instruction = null;
//            FileReader fileReader = new FileReader(Constants.sdCardDirectory + imgFileNameWithoutExt + ".txt");
            FileReader fileReader = new FileReader(ContentActivity.pathToContentFolder + imgFileNameWithoutExt + ".txt");

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            instruction = bufferedReader.readLine();
            Log.i(TAG, "Instruction = " + instruction);
            textView.setText(instruction);

            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void alert(String msg) {
        // alert the user to make a fist to go to
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(R.string.dialogueMsg);
        builder.setMessage(msg);
        builder.setTitle("Welcome");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked OK button
//            }
//        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            @Override
            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();
        // dismiss after some time

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            fileName = getArguments().getString(fileNameArg);
        }
//        Log.e(TAG, "onCreate currentFileName = " + fileName);

    }

    @Override
    public void onResume() {
        super.onResume();


//        String uriPath = Constants.sdCardDirectory + fileName + ".png";
//        String uriPath = ContentActivity.pathToContentFolder + fileName + ".png";
//        imgView.setImageURI(Uri.parse(uriPath));

        setImage(fileName);

//        Log.e(TAG, "onResume currentFileName = " + fileName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_image, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void handleGesture(String gesture) {
        Communicator communicator = (Communicator) getActivity();
//            Log.e(TAG, "iFrag handleGesture currentFileName = " + fileName);

        switch (gesture) {
            case Constants.MYO_FINGERSPEREAD:

//                communicator.openMenu();
                communicator.handleFingersSpread();
//                communicator.handleGesture(gesture);
                break;
            case Constants.MYO_FIST:
                communicator.closeMenu();
                communicator.putVideoFragment();
                break;
            case Constants.MYO_WAVEOUT:
//                Log.e(TAG, "MYO_WAVEOUT in imgFrag");
                // nothing to do
                break;
            case Constants.MYO_WAVEIN:
//                Log.e(TAG, "MYO_WAVEIN in imgFrag");
                // nothing to do
                break;
        }


    }
}
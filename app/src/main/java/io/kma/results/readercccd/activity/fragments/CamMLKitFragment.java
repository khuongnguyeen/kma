/*
 * Copyright (C) 2020 Atos Spain SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kma.results.readercccd.activity.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jmrtd.lds.icao.MRZInfo;

import io.kma.results.readercccd.mlkit.OcrMrzDetectorProcessor;
import io.kma.results.readercccd.mlkit.VisionProcessorBase;
import io.kma.results.readercccd.task.OcrReaderTask;
import io.fotoapparat.Fotoapparat;
//import io.fotoapparat.characteristic.LensPosition;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.view.CameraView;


import io.kma.results.readercccd.R;

import static io.fotoapparat.selector.SelectorsKt.firstAvailable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link CamMLKitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */ //* {@link CamMLKitFragment.OnFragmentInteractionListener} interface
public class CamMLKitFragment extends Fragment
{
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "CamMLKitFragment";

    protected Fotoapparat fotoapparat = null;
    protected CameraView cameraView = null;
    //private OcrMrzDetectorProcessor frameProcessor;
    CustomFrameProcessor frameProcessor;
    private OcrMrzDetectorProcessor ocrFrameProcessor;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public CamMLKitFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CamMLKitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CamMLKitFragment newInstance(String param1, String param2) {
        CamMLKitFragment fragment = new CamMLKitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        System.out.println("En CamMLKitFragment.newInstance");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("En CamMLKitFragment.onCreateView");
        View view  = inflater.inflate(R.layout.fragment_cam_mlkit, container, false);

        //camera_ocr_preview
        cameraView = view.findViewById(R.id.camera_ocr_preview);
        mStatusBar= view.findViewById(R.id.status_view_bottom);
        mStatusRead = view.findViewById(R.id.status_view_top);

        buildCamera(cameraView);
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //MRZUtil.cleanStorage();//[REVIEW]
        ocrFrameProcessor =  new OcrMrzDetectorProcessor();
        //camera2Manager.startCamera();
        fotoapparat.start();
    }

    @Override
    public void onPause()
    {
        fotoapparat.stop();
        //camera2Manager.stopCamera();
        ocrFrameProcessor.stop();
        super.onPause();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");

        FragmentActivity activity = getActivity();
        if (activity instanceof CamMLKitFragmentListener) {
            camMLKitFragmentListener = (CamMLKitFragmentListener) activity;
        }
    }

    @Override
    public void onDetach()
    {
        camMLKitFragmentListener = null;
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    ////
    //
    //      Camera
    //
    ////
    private void buildCamera(CameraView cameraView)
    {
        frameProcessor = new CustomFrameProcessor();
        fotoapparat = Fotoapparat
                .with(this.getActivity())
                .into(cameraView)
                .previewScaleType(ScaleType.CenterCrop)
                .photoResolution(ResolutionSelectorsKt.highestResolution())
                .lensPosition(LensPositionSelectorsKt.back())
                .focusMode(firstAvailable(FocusModeSelectorsKt.continuousFocusPicture(), FocusModeSelectorsKt.autoFocus(), FocusModeSelectorsKt.fixed()))// (optional) use the first focus mode which is supported by device
                .frameProcessor(frameProcessor)// (optional) receives each frame from preview stream
                .build();
    }



    ////////////////////////////////////////////////////////////////////////////////////////
    //
    //        Permissions
    //
    ////////////////////////////////////////////////////////////////////////////////////////

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new CamMLKitFragment.ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                CamMLKitFragment.ErrorDialog.newInstance(getString(R.string.permission_camera_rationale))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    //        Dialogs UI
    //
    ////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static CamMLKitFragment.ErrorDialog newInstance(String message) {
            CamMLKitFragment.ErrorDialog dialog = new CamMLKitFragment.ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_camera_rationale)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    //        Listener
    //
    ////////////////////////////////////////////////////////////////////////////////////////

    public interface CamMLKitFragmentListener {
        void onPassportRead(MRZInfo mrzInfo);
        void onError();
    }


    private boolean isDecoding = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private CamMLKitFragmentListener camMLKitFragmentListener;
    private TextView mStatusBar;
    private TextView mStatusRead;

    private class CustomFrameProcessor implements FrameProcessor
    {

        @Override
        public void process(Frame frame)
        {
            if(!isDecoding) {
                isDecoding = true;
                //Bitmap bitmap= createBitmap(frame);
                OcrReaderTask ocrReaderTask = new OcrReaderTask(getContext().getApplicationContext(),
                        ocrFrameProcessor,
                        frame,
                        new VisionProcessorBase.OcrListener() {
                            @Override
                            public void onMRZRead(final MRZInfo mrzInfo, final long timeRequired)
                            {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
//                                            mStatusRead.setText(getString(R.string.status_bar_ocr, mrzInfo.getDocumentNumber(), mrzInfo.getDateOfBirth(), mrzInfo.getDateOfExpiry()));
//                                            mStatusBar.setText(getString(R.string.status_bar_success, timeRequired));
//                                            mStatusBar.setTextColor(getResources().getColor(R.color.status_text));
                                            if(camMLKitFragmentListener!=null){
                                                camMLKitFragmentListener.onPassportRead(mrzInfo);
                                            }

                                        }catch (IllegalStateException e){
                                            //The fragment is destroyed
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onMRZReadFailure(final long timeRequired) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
//                                            mStatusBar.setText(getString(R.string.status_bar_failure, timeRequired));
//                                            mStatusBar.setTextColor(Color.RED);
//                                            mStatusRead.setText("");
                                        }catch (IllegalStateException e){
                                            //The fragment is destroyed
                                        }
                                    }
                                });

                                isDecoding = false;
                            }

                            @Override
                            public void onFailure(Exception e, long timeRequired) {
                                isDecoding = false;
                                e.printStackTrace();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(camMLKitFragmentListener!=null){
                                            camMLKitFragmentListener.onError();
                                        }
                                    }
                                });
                            }
                        });
                ocrReaderTask.execute();


            }
        }

    }

//    private Bitmap createBitmap(Frame frame)
//    {
//    }


}

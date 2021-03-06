package org.huntingtonrobotics.frcrecyclerushpitscouter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Created by 2015H_000 on 1/6/2015.
 */
public class TeamFragment extends Fragment {
    private static final String TAG = "TeamListFragment";
    public static final String EXTRA_TEAM_ID = "org.huntingtonrobotics.frcrecyclerush.team_id";

    private static final int REQUEST_PHOTO =1;
    private static final String DIALOG_IMAGE = "image";

    private Team mTeam;
    private EditText mTeamNum;
    private EditText mScoutName;
    private ImageButton mPhotoButton;
    private ImageButton mPhotoSaveButton;
    private ImageView mPhotoView;
    private ImageView mPhotoSaveView;

    //mech
    private CheckBox mMechLitterInserter;
    private CheckBox mMechLitterPusher;

    private CheckBox mMechToteFeeder;

    private CheckBox mMechContainerFlipper;
    private CheckBox mMechContainerStepRemover;

    //auto
    private TextView mAuto;
    private ImageView mAutoField;
    private EditText mAutoProgs;
    private CheckBox mAutoMove;
    private EditText mAutoTotes;
    private EditText mAutoContainers;
    //private EditText mAutoHeight;     //Deleted from auto
    private CheckBox mAutoMoveTote;
    private RadioGroup mAutoPos;

    //teleop
    private TextView mTele;
    private ImageView mTeleField;
    private EditText mTeleTotes;
    private EditText mTeleContainer;
    private EditText mTelePlaceLitter;
    private EditText mTelePlaceTotes;
    private EditText mTeleCarryTotes;
    private EditText mTeleCoopSet;
    private CheckBox mTeleCoopStack;

    private RadioGroup mTeleStackingDirection;
    private RadioGroup mTelePlatform;
    private RadioGroup mTeleHumanStation;

    private CheckBox mTeleFlipTote;
    private CheckBox mTeleRemoveContainer;
    private CheckBox mTelePickUpLitter;
    private CheckBox mTeleMoveLitter;

    private EditText mComments;
    private Drawable drawable;


    //fragment is created
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //retrieve the extra and fetch the team
        UUID teamId = (UUID)getArguments().getSerializable(EXTRA_TEAM_ID);
        mTeam = TeamLab.get(getActivity()).getTeam(teamId);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setHasOptionsMenu(true);
    }
    //---fragment is created


    //inflates the view
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.fragment_team, parent, false);

        //turn on Up button

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            //no parent no carret
            if (NavUtils.getParentActivityName(getActivity())==null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        //PUT TEAM INFO VIEWS HERE

        //Team Number Edit Text
        mTeamNum = (EditText)v.findViewById(R.id.teamNum);
        mTeamNum.setText(""+mTeam.getTeamNum());
        mTeamNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setTeamNum(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setTeamNum to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setTeamNum(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        //Scout name edit text
        mScoutName = (EditText)v.findViewById(R.id.scoutName);
        mScoutName.setText(""+mTeam.getScoutName());
        mScoutName.setEnabled(false);

        //photo button
        mPhotoButton = (ImageButton)v.findViewById(R.id.team_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getActivity(), RobotCameraActivity.class);
                Log.d(TAG,"Photo button clicked");
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        //photo button
        mPhotoSaveButton = (ImageButton)v.findViewById(R.id.team_imageButtonSave);
        mPhotoSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    BitmapDrawable drawable = (BitmapDrawable) mPhotoSaveView.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "" + mTeam.getTeamNum(), mComments.getText().toString());
                    Toast.makeText(getActivity().getApplicationContext(), "Photo saved to the bottom of your camera roll.", Toast.LENGTH_LONG).show();
                }catch (NullPointerException npe){
                    Toast.makeText(getActivity().getApplicationContext(), "That's a null pointer ghost rider. Is there a picture in the image view to save?", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //photo view
        mPhotoView = (ImageView)v.findViewById(R.id.team_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Photo p = mTeam.getPhoto();
                if(p==null){
                    return;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });

        mPhotoSaveView = (ImageView)v.findViewById(R.id.team_imageViewSave);
        mPhotoSaveView.setVisibility(View.GONE);

        //if camera is not avilable, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                || (Build.VERSION.SDK_INT>= Build.VERSION_CODES.GINGERBREAD&& Camera.getNumberOfCameras() > 0);
        if(!hasACamera){
            mPhotoButton.setEnabled(false);
        }

        //MECH VIEW

        mMechLitterInserter = (CheckBox)v.findViewById(R.id.mechLitterInserter);
        mMechLitterInserter.setChecked(mTeam.isMechLitterInserter());
        mMechLitterInserter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTeam.setMechLitterInserter(true);
                } else if (!isChecked) {
                    mTeam.setMechLitterInserter(false);
                } else {

                }
            }
        });


        mMechLitterPusher = (CheckBox)v.findViewById(R.id.mechLitterPusher);
        mMechLitterPusher.setChecked(mTeam.isMechLitterPusher());
        mMechLitterPusher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTeam.setMechLitterPusher(true);
                } else if (!isChecked) {
                    mTeam.setMechLitterPusher(false);
                } else {

                }
            }
        });


        mMechToteFeeder = (CheckBox)v.findViewById(R.id.mechToteFeeder);
        mMechToteFeeder.setChecked(mTeam.isMechToteFeeder());
        mMechToteFeeder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTeam.setMechToteFeeder(true);
                } else if (!isChecked) {
                    mTeam.setMechToteFeeder(false);
                } else {

                }
            }
        });


        mMechContainerFlipper = (CheckBox)v.findViewById(R.id.mechContainerFlipper);
        mMechContainerFlipper.setChecked(mTeam.isMechContainerFlipper());
        mMechContainerFlipper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTeam.setMechContainerFlipper(true);
                } else if (!isChecked) {
                    mTeam.setMechContainerFlipper(false);
                } else {

                }
            }
        });




        //PUT AUTO VIEWS HERE
        mAuto = (TextView)v.findViewById(R.id.auto);
        mAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mAutoField.isShown())){
                    mAutoField.setVisibility(View.VISIBLE);
                }else{
                    mAutoField.setVisibility(View.GONE);
                }
            }
        });

        mAutoField = (ImageView)v.findViewById(R.id.autoImageViewField);
        mAutoField.setVisibility(View.GONE);


        mAutoProgs = (EditText)v.findViewById(R.id.auto_programs);
        mAutoProgs.setText(""+mTeam.getAutoProgs());
        mAutoProgs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setAutoProgs(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoProgs to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setAutoProgs(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mAutoMove = (CheckBox)v.findViewById(R.id.auto_move);
        mAutoMove.setChecked(mTeam.isAutoMove());
        mAutoMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTeam.setAutoMove(true);
                } else if (!isChecked) {
                    mTeam.setAutoMove(false);
                } else {

                }
            }
        });


        mAutoTotes = (EditText)v.findViewById(R.id.auto_totes);
        //set filter to only allow numbers 0-3
        mAutoTotes.setFilters(new InputFilter[]{new InputFilterMinMax(0, 3, getActivity().getApplicationContext())});
        mAutoTotes.setText(""+mTeam.getAutoTotes());
        mAutoTotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setAutoTotes(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoTotes to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setAutoTotes(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mAutoContainers = (EditText)v.findViewById(R.id.auto_containers);
        //set filter to only allow numbers 0-3
        mAutoContainers.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 3,getActivity().getApplicationContext())});
        mAutoContainers.setText(""+mTeam.getAutoContainers());
        mAutoContainers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setAutoContainers(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setAutoContainers(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mAutoMoveTote = (CheckBox)v.findViewById(R.id.auto_move_tote_stack);
        mAutoMoveTote.setChecked(mTeam.isAutoMoveTote());
        mAutoMoveTote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    mTeam.setAutoMoveTote(true);
                }else if (!isChecked){
                    mTeam.setAutoMoveTote(false);
                }else{

                }
            }
        });


        mAutoPos = (RadioGroup)v.findViewById(R.id.startPosRadioGroup);
        int ap = mTeam.getAutoPos();
        switch (ap) {
            case (1):
                mAutoPos.check(R.id.autoStartPos1);
                break;
            case (2):
                mAutoPos.check(R.id.autoStartPos2);
                break;
            case (3):
                mAutoPos.check(R.id.autoStartPos3);
                break;
            case (0):
                mAutoPos.check(R.id.autoStartPosA);
                break;
        }
        mAutoPos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                switch (checkedId) {
                    case R.id.autoStartPos1:
                        mTeam.setAutoPos(1);
                        break;
                    case R.id.autoStartPos2:
                        mTeam.setAutoPos(2);
                        break;
                    case R.id.autoStartPos3:
                        mTeam.setAutoPos(3);
                        break;
                    case R.id.autoStartPosA:
                        mTeam.setAutoPos(0);
                        break;

                }
            }

        });


        //Tele
        mTele = (TextView)v.findViewById(R.id.tele);
        mTele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mTeleField.isShown())){
                    mTeleField.setVisibility(View.VISIBLE);
                }else{
                    mTeleField.setVisibility(View.GONE);
                }
            }
        });


        mTeleField = (ImageView)v.findViewById(R.id.teleImageViewField);
        mTeleField.setVisibility(View.GONE);





        mTeleTotes = (EditText)v.findViewById(R.id.teleStackTotes);
        //set filter to only allow numbers 0-6
        mTeleTotes.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 6,getActivity().getApplicationContext())});
        mTeleTotes.setText(""+mTeam.getTeleTotes());
        mTeleTotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setTeleTotes(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setTeleTotes(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mTeleContainer = (EditText)v.findViewById(R.id.teleStackContainer);
        //set filter to only allow numbers 0-6
        mTeleContainer.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 6,getActivity().getApplicationContext())});
        mTeleContainer.setText(""+mTeam.getTeleContainer());
        mTeleContainer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setTeleContainer(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setTeleContainer(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mTelePlaceLitter = (EditText)v.findViewById(R.id.telePlaceLitter);
        //set filter to only allow numbers 0-6
        mTelePlaceLitter.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 6,getActivity().getApplicationContext())});
        mTelePlaceLitter.setText(""+mTeam.getTelePlaceLitter());
        mTelePlaceLitter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setTelePlaceLitter(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setTelePlaceLitter(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mTelePlaceTotes = (EditText)v.findViewById(R.id.teleStackToteStack);
        //set filter to only allow numbers 0-6
        mTelePlaceTotes.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 6,getActivity().getApplicationContext())});
        mTelePlaceTotes.setText(""+mTeam.getTelePlaceTotes());
        mTelePlaceTotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setTelePlaceTotes(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setTelePlaceTotes(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mTeleCarryTotes = (EditText)v.findViewById(R.id.teleCarryTotes);
        //set filter to only allow numbers 0-6
        mTeleCarryTotes.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 6,getActivity().getApplicationContext())});
        mTeleCarryTotes.setText(""+mTeam.getTeleCarryTotes());
        mTeleCarryTotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setTeleCarryTotes(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setTeleCarryTotes(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });


        mTeleCoopSet = (EditText)v.findViewById(R.id.teleCoopSet);
        //set filter to only allow numbers 0-3
        mTeleCoopSet.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 3,getActivity().getApplicationContext())});
        mTeleCoopSet.setText(""+mTeam.getTeleCoopSet());
        mTeleCoopSet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setTeleCoopSet(Integer.parseInt(s.toString()));
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setTeleCoopSet(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });

        mTeleCoopStack = (CheckBox)v.findViewById(R.id.teleCoopStack);
        //set filter to only allow numbers 0-6
        mTeleCoopStack.setChecked(mTeam.isTeleCoopStack());
        mTeleCoopStack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    mTeam.setTeleCoopStack(true);
                }else if (!isChecked){
                    mTeam.setTeleCoopStack(false);
                }else{

                }
            }
        });


        mTeleStackingDirection = (RadioGroup)v.findViewById(R.id.stackingDirectionRadioGroup);
        switch (mTeam.getTeleStackingDirection()) {
            case 1:
                mTeleStackingDirection.check(R.id.teleDirectionL);
                break;
            case 2:
                mTeleStackingDirection.check(R.id.teleDirectionS);
                break;
            case 0:
                mTeleStackingDirection.check(R.id.teleDNS);
                break;
        }
        mTeleStackingDirection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                switch (checkedId) {
                    case R.id.teleDirectionL:
                        mTeam.setTeleStackingDirection(1);
                        break;
                    case R.id.teleDirectionS:
                        mTeam.setTeleStackingDirection(2);
                        break;
                    case R.id.teleDNS:
                        mTeam.setTeleStackingDirection(0);
                        break;

                }
            }

        });


        mTelePlatform = (RadioGroup)v.findViewById(R.id.platformRadioGroup);
        switch (mTeam.getTelePlatform()) {
            case 1:
                mTelePlatform.check(R.id.telePlat1);
                break;
            case 2:
                mTelePlatform.check(R.id.telePlat2);
                break;
            case 0:
                mTelePlatform.check(R.id.telePlatA);
                break;
        }
        mTelePlatform.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                switch (checkedId) {
                    case R.id.telePlat1:
                        mTeam.setTelePlatform(1);
                        break;
                    case R.id.telePlat2:
                        mTeam.setTelePlatform(2);
                        break;
                    case R.id.telePlatA:
                        mTeam.setTelePlatform(0);
                        break;

                }
            }

        });


        mTeleHumanStation = (RadioGroup)v.findViewById(R.id.humanStationRadioGroup);
        switch (mTeam.getTeleHumanStation()) {
            case 1:
                mTeleHumanStation.check(R.id.teleHumanStation1);
                break;
            case 2:
                mTeleHumanStation.check(R.id.teleHumanStation2);
                break;
            case 0:
                mTeleHumanStation.check(R.id.teleHumanStationA);
                break;
        }
        mTeleHumanStation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                switch (checkedId) {
                    case R.id.teleHumanStation1:
                        mTeam.setTeleHumanStation(1);
                        break;
                    case R.id.teleHumanStation2:
                        mTeam.setTeleHumanStation(2);
                        break;
                    case R.id.teleHumanStationA:
                        mTeam.setTeleHumanStation(0);
                        break;

                }
            }

        });

        mTeleFlipTote = (CheckBox)v.findViewById(R.id.teleFlipTotes);
        mTeleFlipTote.setChecked(mTeam.isTeleFlipTote());
        mTeleFlipTote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    mTeam.setTeleFlipTote(true);
                }else if (!isChecked){
                    mTeam.setTeleFlipTote(false);
                }else{

                }
            }
        });

        mTeleRemoveContainer = (CheckBox)v.findViewById(R.id.teleRemoveContainer);
        mTeleRemoveContainer.setChecked(mTeam.isTeleRemoveContainer());
        mTeleRemoveContainer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    mTeam.setTeleRemoveContainer(true);
                }else if (!isChecked){
                    mTeam.setTeleRemoveContainer(false);
                }else{

                }
            }
        });

        mTelePickUpLitter = (CheckBox)v.findViewById(R.id.telePickUpLitter);
        mTelePickUpLitter.setChecked(mTeam.isTelePickUpLitter());
        mTelePickUpLitter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    mTeam.setTelePickUpLitter(true);
                }else if (!isChecked){
                    mTeam.setTelePickUpLitter(false);
                }else{

                }
            }
        });


        mTeleMoveLitter = (CheckBox)v.findViewById(R.id.teleMoveLitter);
        mTeleMoveLitter.setChecked(mTeam.isTeleMoveLitter());
        mTeleMoveLitter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    mTeam.setTeleMoveLitter(true);
                }else if (!isChecked){
                    mTeam.setTeleMoveLitter(false);
                }else{

                }
            }
        });

        
        //comments

        mComments = (EditText)v.findViewById(R.id.comments);
        mComments.setText(""+mTeam.getComments());
        mComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //left blank
            }

            //user changes text of auto progs
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //use try catch in case nothing is in text view
                try {
                    //saves text after converting CS to integer
                    mTeam.setComments(s.toString());
                } catch (Exception e) {
                    //exception is thrown so setAutoContainers to 0 so program can carry on
                    Log.d(TAG, "ERROR: " + e);
                    mTeam.setComments("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //also left blank
            }
        });

        return v;
    }
    //---inflates the view

    //shows the photo
    private void showPhoto(){
        //(Re)set the image buttons image based on your photo
        Photo p = mTeam.getPhoto();
        BitmapDrawable b = null;
        if (p != null){
            String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
        mPhotoSaveView.setImageDrawable(b);
    }
    //---shows the photo in photoview

    //Load the image
    @Override
    public void onStart(){
        super.onStart();
        showPhoto();
    }
    //---Load the image

    //unload the image
    @Override
    public void onStop(){
        super.onStop();
        //if (mPhotoView!=null) {
        //    PictureUtils.cleanImageView(mPhotoView);
        //}
        TeamLab.get(getActivity()).saveTeams();
    }
    //---unload the image

    //attach extra argument to team fragment
    public static TeamFragment newInstance(UUID teamId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TEAM_ID, teamId);

        TeamFragment fragment = new TeamFragment();
        fragment.setArguments(args);

        return fragment;
    }
    //---attach extra argument to team fragment


    //retrieve files from extras
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if (resultCode!= Activity.RESULT_OK) return;

        if(requestCode == REQUEST_PHOTO){
            //create a new Photo objet and attach it to the team
            String filename = data.getStringExtra(RobotCameraFragment.EXTRA_PHOTO_FILENAME);
            if(filename!=null){
                Log.i(TAG,"filename: " + filename);

                //handle a new photo
                Photo p = new Photo(filename);
                mTeam.setPhoto(p);
                showPhoto();
                Log.i(TAG, "Team: " + mTeam.getTeamNum() + "has a photo");
            }
        }
    }


    //saves teams to filesystem onPause()
    @Override
    public void onPause(){
        super.onPause();
        TeamLab.get(getActivity()).saveTeams();
    }
    //---saves teams to filesystem onPause()


    //Respond to menu items being clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity())!=null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //---Respond to menu items being clicked

    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }
}


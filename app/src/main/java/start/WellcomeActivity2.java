package start;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.app.barcodeclient3.R;

import main.MainApplication;
import startactivity.MainActivity;

/**
 * Created by userd088 on 30.03.2016.
 */
public class WellcomeActivity2 extends AppCompatActivity {



    static FragmentManager fragmentManager;



    FrameLayout frameLayout =null;

    static WellcomeFragment0 wellcomeFragment0;
    private final static  int MAX_FRAGMENTS = 4;

    Button buttonNext=null;
    Button buttonBack=null;

    private static int currentPosition =0;

    private boolean next=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager=getFragmentManager();
        setContentView(R.layout.wellcome2_activity_empty_fr);



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        buttonNext = (Button)findViewById(R.id.buttonNext);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition <MAX_FRAGMENTS) currentPosition +=1;
                else finish();
                next=true;
                jumpFragment();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition >0) currentPosition -=1;
                next=false;
                jumpFragment();
            }
        });

        buttonBack.setVisibility(View.GONE);

        //frameLayout = (FrameLayout)findViewById(R.id.set_frame_container);

        if(wellcomeFragment0==null) wellcomeFragment0 = new WellcomeFragment0();



        if (wellcomeFragment0 != null)
            fragmentManager.beginTransaction()
                    .replace(R.id.set_frame_container, wellcomeFragment0).commit();

        // wellcomeFragment0.setEmailEditListener(editEmailListener);
    }


    public static boolean autonom = true;


    private void jumpFragment()
    {
        Log.d("my", "fgagment # " + currentPosition);
        FragmentTransaction ft = fragmentManager.beginTransaction();
       // if(next) ft.setCustomAnimations(R.anim.left_input, R.anim.left_exit);
       // else ft.setCustomAnimations(R.anim.rigth_input, R.anim.rigth_exit);

        switch (currentPosition)
        {
            case 0:

                ft.replace(R.id.set_frame_container, wellcomeFragment0, "detailFragment");
                ft.commit();
                buttonBack.setVisibility(View.GONE);
            break;

            case 1:
                if(autonom){

                    MainActivity.OFFLINE_MODE=true;
                    MainApplication.dbHelper.insertOrReplaceOption("OFFLINE_MODE","1");

                    WellcomeFragment12 newFragment = new WellcomeFragment12();
                    ft.replace(R.id.set_frame_container, newFragment, "detailFragment");
                    ft.commit();
                    buttonBack.setVisibility(View.VISIBLE);
                    break;

                }else {

                    MainActivity.OFFLINE_MODE=false;
                    MainApplication.dbHelper.insertOrReplaceOption("OFFLINE_MODE","0");

                    WellcomeFragment1 newFragment = new WellcomeFragment1();
                    ft.replace(R.id.set_frame_container, newFragment, "detailFragment");
                    ft.commit();
                    buttonBack.setVisibility(View.VISIBLE);
                    break;
                }

            case 2:
                if(autonom){
                    WellcomeFragment4 newFragment4 = new WellcomeFragment4();
                    ft.replace(R.id.set_frame_container, newFragment4, "detailFragment");
                    ft.commit();
                    buttonBack.setVisibility(View.VISIBLE);
                    buttonNext.setText(getResources().getText(R.string.ready));
                    break;
                }else {
                    WellcomeFragment2 newFragment2 = new WellcomeFragment2();
                    ft.replace(R.id.set_frame_container, newFragment2, "detailFragment");
                    ft.commit();
                    buttonBack.setVisibility(View.VISIBLE);
                    break;
                }

            case 3:
                if(autonom){
                    finish();
                }else {
                    WellcomeFragment3 newFragment3 = new WellcomeFragment3();
                    ft.replace(R.id.set_frame_container, newFragment3, "detailFragment");
                    ft.commit();
                    buttonBack.setVisibility(View.VISIBLE);
                    buttonNext.setText(getResources().getText(R.string.next));
                    break;
                }

            case 4:
                WellcomeFragment4 newFragment4 = new WellcomeFragment4();
                ft.replace(R.id.set_frame_container, newFragment4, "detailFragment");
                ft.commit();
                buttonBack.setVisibility(View.VISIBLE);
                buttonNext.setText(getResources().getText(R.string.ready));
                break;


            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.set_frame_container, wellcomeFragment0).commit();
                break;
        }
    }





}

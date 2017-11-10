package start;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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



    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    FrameLayout frameLayout =null;


    private final static  int MAX_FRAGMENTS = 4;

    Button buttonNext=null;
    Button buttonBack=null;

    private static int currentPosition =0;

    private boolean next=true;

    WellcomeFragment0 wellcomeFragment0;
    BluetoothFragment4 bluetoothFragment4;
    PortableTerminalFragment12 portableTerminalFragment12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager=getFragmentManager();
        setContentView(R.layout.wellcome2_activity_empty_fr);



       /* ActionBar mainActionBar = getSupportActionBar();
        if (mainActionBar != null) {
            mainActionBar.hide();
        }*/

        buttonNext = (Button)findViewById(R.id.buttonNext);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition <MAX_FRAGMENTS) currentPosition +=1;
                else finish();
                next=true;

            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition >0) currentPosition -=1;
                next=false;

            }
        });

        buttonBack.setVisibility(View.GONE);

        mViewPager = (ViewPager) findViewById(R.id.container);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //frameLayout = (FrameLayout)findViewById(R.id.set_frame_container);

        if(wellcomeFragment0==null) wellcomeFragment0 = new WellcomeFragment0();
        bluetoothFragment4 = new BluetoothFragment4();
        portableTerminalFragment12 = new PortableTerminalFragment12();




    }


    public static boolean autonom = true;




    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ZalFragment (defined as a static inner class below).
           switch (position) {
               case 0:
                   return wellcomeFragment0;
               case 1:
                   return portableTerminalFragment12;
               case 2:
                   return bluetoothFragment4;

               default:
                   return wellcomeFragment0;
           }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }



}

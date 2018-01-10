package scanworkingactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.app.barcodeclient3.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import essences.Good;
import main.MainApplication;


public class TotalGoodsListAdapter extends BaseAdapter {


    int docId=-1;
    Context appContext;



    public TotalGoodsListAdapter()
    {

    }

    public TotalGoodsListAdapter(int docId, Context appContext)
    {
        this.docId=docId;
        this.appContext=appContext;
    }




    public static abstract class Row {}

    public static final class Section extends Row {
        public final String text;

        public Section(String text) {
            this.text = text;
        }
    }

    public static final class Item extends Row {
        public final String text;

        public Item(String text) {
            this.text = text;
        }
    }

    private List<Row> rows=new ArrayList<Row>();

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    HashMap<String, Good> goodsMap = new HashMap<>();

    public void setHashMap(HashMap<String, Good> authorsMap) {
        this.goodsMap = authorsMap;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) {
            return 1;
        } else {
            return 0;
        }
    }


    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view = convertView;

       // Log.d("my","Position = "+position);

        if (getItemViewType(position) == 0)
        {
           // Log.d("my","--position = "+position);

            // Item
            if (view == null) {

                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.row_item_total, parent, false);

            }

            Item item = (Item) getItem(position);
            Button textView = (Button) view.findViewById(R.id.bookStatusTextView);
            TextView letter = (TextView) view.findViewById(R.id.letterView);
            final EditText fcntEditText = (EditText) view.findViewById(R.id.fcntEditText);
            fcntEditText.setTag(item.text);
            if(docId>=0) fcntEditText.setVisibility(View.VISIBLE);
            else fcntEditText.setVisibility(View.GONE);

            if(!MainApplication.OFFLINE_MODE) fcntEditText.setVisibility(View.GONE);

            fcntEditText.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        EditText ed = (EditText) v;
                        String value=ed.getText().toString();




                                        String txt = ed.getTag().toString();
                                        Good good = goodsMap.get(txt);

                                        String goodName = good.getName();
                        Log.d("my","  name = "+goodName+" val="+value);



                        if(value.length()>0) {
                            try {
                                double cnt = Double.parseDouble(value);

                                if(MainApplication.OFFLINE_MODE){
                                    MainApplication.dbHelper.insertGoodsAcCnt(docId, good.getId(), cnt);
                                    Double tempOfflineAnsCnt= MainApplication.dbHelper.getGoodCountAcc(docId,good.getId());
                                    if(tempOfflineAnsCnt==cnt){
                                        MediaPlayer mp = MediaPlayer.create(appContext, R.raw.done);
                                        mp.start();
                                    }
                                    else{
                                        MediaPlayer mp = MediaPlayer.create(appContext, R.raw.error_sound);
                                        mp.start();
                                    }


                                }else {
                                   /* RequestInventoryDtEditCnt requestInventoryDtEditCnt = new RequestInventoryDtEditCnt(ScanWorkingActivity.this);
                                    ansJSON = requestInventoryDtEditCnt.sendEditInventoryDt(invId, goodId, cnt);

                                    //MainActivity.dbHelper.insertGoodsAcCnt(invId, goodId, cnt); ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    h2.post(readySendInvId);*/
                                }


                            } catch (Exception e) {
                            }

                        }





                        return true;
                    }
                    return false;
                }
            });



            if(position>0)
            {
                if(getItemViewType((position-1)) == 1)
                {
                   // Log.d("my","UUUUUU@@@@@ position = "+(position-1));
                    Section section = (Section) getItem((position-1));
                    if(section==null) Log.d("my","section=null");
                    if(letter==null) Log.d("my","letter=null");
                    letter.setText(section.text);
                    letter.setVisibility(View.GONE);//заглавн буква
                }
                else
                {
                    letter.setVisibility(View.GONE);
                }
            }

            textView.setText(item.text);

            Good good = goodsMap.get(item.text);
            double cnt = good.getFcnt();
            if(cnt>=0) fcntEditText.setText(""+cnt);

            textView.setBackground(view.getContext().getResources().getDrawable(R.drawable.crimea_nobord_button));///////

//            Log.d("my","uuuuu"+item.text);
            textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    Button bt = (Button) v;
                    String txt = bt.getText().toString();
                    Good good = goodsMap.get(txt);

                   /* String id = author.getId();
                    String url = author.getCover(Author.COVER_NORMAL);
                    Log.d("my","url="+url);
                    if(url.length()==0) url="";


                    Bundle arg = new Bundle();
                    arg.putString("id", id);
                    arg.putString("author", txt);
                    arg.putString("url", url);

                    Intent intent = new Intent(v.getContext(),
                            AuthorDetailsActivity.class);
                    intent.putExtras(arg);
                    v.getContext().startActivity(intent);*/




                }
            });







        } else { // Section

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.row_section, parent, false);
                view.setVisibility(View.VISIBLE);
            }

            Section section = (Section) getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.goodNameTW);
           /* EditText fcntEditText = (EditText) view.findViewById(R.id.fcntEditText);

            if(docId>=0) fcntEditText.setVisibility(View.VISIBLE);
            else  fcntEditText.setVisibility(View.GONE);*/

            String goodName= section.text;
            textView.setText(goodName);
            Good good = goodsMap.get(goodName);
            if(good!=null) {
                double fcnt = good.getFcnt();
              //  fcntEditText.setText(""+fcnt);
            }

        }
        // Log.d("my","s ---222");
        if(view==null) view = new View(parent.getContext());
        // Log.d("my","s ---223");
        return view;
    }

}
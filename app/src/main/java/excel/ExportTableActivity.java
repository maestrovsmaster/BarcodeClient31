package excel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.app.barcodeclient3.R;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by MaestroVS on 11.11.2017.
 */

public class ExportTableActivity extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export_table);

        Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.primary_dark));
        }

        list = (ListView) findViewById(R.id.list);

        View selectFileButton = findViewById(R.id.selectFileButton);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        chooseFilepath ="/storage/emulated/0/Создать папку/Товары2.xls";
        startExcelParser();
    }









    ///file chooser

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {


        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);



            try {
                startActivityForResult(intent,FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {

                Toast.makeText(this, "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }



        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");


            try {
                startActivityForResult(intent,FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {

                Toast.makeText(this, "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        }


    }

    String chooseFilepath="";
    Handler h0 = new Handler();
    ProgressDialog pd;
    Uri uri;

    ExcelParser excelParser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();
                    Log.d("my", "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        chooseFilepath = getPath(this, uri);
                        Log.d("my", "File Path: " + chooseFilepath);
                        // Get the file instance
                        // File file = new File(path);
                        // Initiate the upload

                        h0.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                pd = new ProgressDialog(ExportTableActivity.this);
                                pd.setTitle("Waiting");
                                pd.setMessage("Load goods...");
                                pd.setCancelable(true);
                                pd.show();
                             //   AndroidReadExcelActivity.ReadEXLfile readEXLfile = new AndroidReadExcelActivity.ReadEXLfile(chooseFilepath);
                             //   readEXLfile.start();

                                //startExcelParser(); ///!!!!!!!!!!!!!!!!!!!!!!!!!!
                            }
                        },500);

                    }catch (Exception e)
                    {

                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startExcelParser()
    {
        if(excelParser==null) excelParser = new ExcelParser(ExportTableActivity.this,chooseFilepath);
        excelParser.parseFile(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                Log.d("my","we obtain parsed data");
                if(pd!=null) pd.dismiss();
                Object obj = message.obj;
                if(obj==null) return false;
                final ArrayList<ArrayList<String>> xlsList = (ArrayList<ArrayList<String>>) obj;
              //  parseData(xlsList);
                h0.post(new Runnable() {
                    @Override
                    public void run() {
                        columnsCnt = excelParser.getMaxColumnsCnt();
                        Log.d("my","parse data, list size = "+xlsList.size()+" max columns "+columnsCnt);

                        adapter = new ExportTableAdapter(ExportTableActivity.this,R.layout.row_excel_adapter,xlsList);
                        list.setAdapter(adapter);
                    }
                });
                return false;
            }
        });
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    int columnsCnt=0;

    ExportTableAdapter adapter;

    private void parseData(ArrayList<ArrayList<String>>  xlsList){





    }


}

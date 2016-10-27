package excel;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barcodeclient3.R;
import com.opencsv.CSVReader;

import essences.Good;
import main.MainApplication;
import startactivity.MainActivity;

public class AndroidReadExcelActivity extends ListActivity implements OnClickListener {


    TextView countOfGoods;


    ImportGoodsListAdapter<Good> importGoodsListAdapter;
    ArrayList<Good> goodsList = new ArrayList<>();

    private static final String inExcelFileName = "import.xls";
    private static final String outExcelFileName = "export.xls";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.excel_example_activity);


        View writeExcelButton = findViewById(R.id.writeExcel);
        writeExcelButton.setOnClickListener(this);
        View readExcelButton = findViewById(R.id.readExcel);
        readExcelButton.setOnClickListener(this);


        countOfGoods = (TextView) findViewById(R.id.countOfGoods);

        Button chooseFile = (Button) findViewById(R.id.chooseFileBt);
        chooseFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button clearGoodsBt = (Button) findViewById(R.id.clearGoodsBt);
        clearGoodsBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AndroidReadExcelActivity.this);
                builder.setTitle("Delete all goods?");


// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainApplication.dbHelper.clear_AC_GOODS_CNT();
                        MainApplication.dbHelper.clearDic_Goods();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        ImageButton backButton =(ImageButton) findViewById(R.id.settingsBackBt);
        backButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                Log.d("my","finish");
                finish();

            }
        });


       /* h0.postAtTime(new Runnable() {
            @Override
            public void run() {
                readExcelFile(AndroidReadExcelActivity.this, inExcelFileName);
            }
        },500);*/
    }

    Handler h0 = new Handler();



    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.writeExcel:
                saveExcelFile(this, outExcelFileName);
                break;
            case R.id.readExcel:
                readExcelFile(this, inExcelFileName);
                break;
        }
    }


    private static boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!FileLoader.isExternalStorageAvailable() || FileLoader.isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Item Number");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Quantity");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Price");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));




        success = FileLoader.writeExcel(wb, fileName);

        return success;
    }

    private  void readExcelFile(Context context, String filename) {


        Log.d("my","filename = "+filename);

        if(filename==null){
            Toast.makeText(AndroidReadExcelActivity.this, getString(R.string.cant_open_file),Toast.LENGTH_SHORT);
            return;
        }

        if (!FileLoader.isExternalStorageAvailable() || FileLoader.isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        if(filename.contains(".xls")){

        try {
            Log.d("my", "myCell ++ xls");
            // Creating Input Stream
            // File file = FileLoader.getExcelFileFromSD(filename);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            File file = FileLoader.getExcelFileWithPathFromSD(filename);
            FileInputStream myInput = new FileInputStream(file);
            if (myInput == null) Log.d("my", "myInput is null");
            else Log.d("my", "myInput ok!");

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            Log.d("my", "getSheets =" + myWorkBook.getActiveSheetIndex());

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            //int n_id

            MainApplication.dbHelper.clear_AC_GOODS_CNT();
            MainApplication.dbHelper.clearDic_Goods();
            MainApplication.dbHelper.clearDic_Goods_GRP();


            int iid = 0;
            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                int num = myRow.getRowNum();

                int cnt = myRow.getLastCellNum();
                Log.d("my", " num =" + num + " last = " + cnt);
                String id = "";
                String name = "";
                String article = "";
                String unit = "";
                String barcode = "";


                Iterator<Cell> cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    int index = myCell.getColumnIndex();
                  //  Log.d("my", "myCell = " + myCell + " column = " + index);

                    switch (index) {
                        case 0:
                            id = myCell.toString();
                            break;
                        case 1:
                            name = myCell.toString();
                            break;
                        case 2:
                            article = myCell.toString();
                            break;
                        case 3:
                            unit = myCell.toString();
                            break;
                        case 4:

                            barcode = myCell.toString();
                            try {
                                double bc = Double.parseDouble(barcode);
                                int bb = (int) bc;
                                barcode = Integer.toString(bb);
                            } catch (Exception e) {
                            }
                            break;
                    }
                }

                if (num > 0) {
                    Log.d("my", ">>>>>" + name + " ");
                    if (barcode.length() > 0 && article.length() == 0) {
                        article = barcode;
                    }

                    if (name.length() > 0 && article.length() > 0 && unit.length() > 0 && id.length() > 0) {



                        if (name.contains("'")) {
                            name = name.replaceAll("'", "\''");
                            Log.d("my", "replacer: " + name);
                        }


                        Good good = new Good(iid, 0, name, unit, article);
                        ArrayList<String> barcs = new ArrayList<>();
                        barcs.add(barcode);
                        good.setBarcodes(barcs);

                        Log.d("my", "***************good: " + good.toString());
                        goodsList.add(good);
                        iid = MainApplication.dbHelper.getMaxGoodId()+1;
                        MainApplication.dbHelper.writeGood(iid, 0, name, article, unit, barcode, 0);
                    }
                }

                iid++;

            }
            MainApplication.dbHelper.writeGoodGRP(0,0,"Excel");

             importGoodsListAdapter = new ImportGoodsListAdapter<>(AndroidReadExcelActivity.this, R.layout.row_excel_good, goodsList);
             setListAdapter(importGoodsListAdapter);
            countOfGoods.setText("" + goodsList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("my","err open="+e.toString());

            AlertDialog alertDialog = new AlertDialog.Builder(AndroidReadExcelActivity.this).create();
            alertDialog.setTitle(getString(R.string.error));
            alertDialog.setMessage(getString(R.string.cant_open_file));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
////////////////////////////////////////////////////////////////////////

        }
        if(filename.contains(".csv"))
        {
        Log.d("my","import csv");

            readCsv(AndroidReadExcelActivity.this,filename);

           // String filename = "C:\Book.csv";


            System.out.println("Starting to parse CSV file using opencsv");
            parseUsingOpenCSV(filename);

        }


        return;
    }


    private void parseUsingOpenCSV(String filename)
    {
        CSVReader reader;
        try
        {
            reader = new CSVReader(new FileReader(filename));
            String[] row;
            List<?> content = reader.readAll();

            int iid = 0;
            int first = 0;
            for (Object object : content)
            {
                row = (String[]) object;

                String id = "";
                String name = "";
                String article = "";
                String unit = "";
                String barcode = "";

                Log.d("my",">>>>>>>>"+ row.toString());
                for (int i = 0; i < row.length; i++)
                {
                    // display CSV values
                    System.out.println("Cell column index: " + i);
                    System.out.println("Cell Value: " + row[i]);
                    System.out.println("-------------");
                    Log.d("my","import csv ok = ");
                    Log.d("my"," = "+row[i]);

                    switch (i) {
                        case 0:
                            id = row[i].toString();
                            Log.d("my", ">id" + id);
                            break;
                        case 1:
                            name = row[i].toString();
                            Log.d("my", ">name" + name);
                            break;
                        case 2:
                            article = row[i].toString();
                            Log.d("my", ">article" + article);
                            break;
                        case 3:
                            unit = row[i].toString();
                            Log.d("my", ">unit" + unit);
                            break;
                        case 4:

                            barcode = row[i].toString();
                            try {
                                double bc = Double.parseDouble(barcode);
                                int bb = (int) bc;
                                barcode = Integer.toString(bb);
                                Log.d("my", ">barcode" + barcode);
                            } catch (Exception e) {
                            }
                            break;
                    }
                }

                if (row.length > 0) {
                    Log.d("my", ">>>>>YYYYY" + name + " ");
                    if (barcode.length() > 0 && article.length() == 0) {
                        article = barcode;
                    }
                    Log.d("my","ok 1");

                    if(first>0)
                    if (name.length() > 0 && article.length() > 0 && unit.length() > 0 && id.length() > 0) {

                        Log.d("my","ok 2");



                        if (name.contains("'")) {
                            name = name.replaceAll("'", "\''");
                            Log.d("my", "replacer: " + name);
                        }


                        Good good = new Good(iid, 0, name, unit, article);
                        ArrayList<String> barcs = new ArrayList<>();
                        barcs.add(barcode);
                        good.setBarcodes(barcs);

                        Log.d("my", "***************good: " + good.toString());
                        goodsList.add(good);
                        iid = MainApplication.dbHelper.getMaxGoodId()+1;
                        MainApplication.dbHelper.writeGood(iid, 0, name, article, unit, barcode, 0);
                    }
                }
                first++;
            }

            try {
                MainApplication.dbHelper.writeGoodGRP(0, 0, "Excel");
            }catch (Exception e){

            }

            importGoodsListAdapter = new ImportGoodsListAdapter<>(AndroidReadExcelActivity.this, R.layout.row_excel_good, goodsList);
            setListAdapter(importGoodsListAdapter);
            countOfGoods.setText("" + goodsList.size());
            Log.d("my"," success import from CSV!!!");
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e.getMessage());
        }
        catch (IOException e)
        {
            Log.d("my","import csv err2 = "+e.toString());
            System.err.println(e.getMessage());
        }
    }


    public final List<String[]> readCsv(Context context, String CSV_PATH) {
        List<String[]> questionList = new ArrayList<String[]>();


        AssetManager assetManager = context.getAssets();
        try {
            InputStreamReader is = new InputStreamReader(assetManager
                    .open(CSV_PATH));
            Log.d("my","import csv ok = ");

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {

            }
        }  catch (IOException e) {
        Log.d("my","import csv err = "+e.toString());
        e.printStackTrace();
    }


        /*AssetManager assetManager = context.getAssets();

        try {
            Log.d("my","import csv ok = ");
            InputStream csvStream = assetManager.open("content://com.estrongs.files/storage/sdcard0/BarcodeScanner/csvdocument.csv");//CSV_PATH);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                questionList.add(line);
            }
        } catch (IOException e) {
            Log.d("my","import csv err = "+e.toString());
            e.printStackTrace();
        }

        Log.d("my","List csv = "+questionList.toString());*/

        return questionList;
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




       /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("*//*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }*/
    }

    String chooseFilepath="";

    Uri uri;

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
                                readExcelFile(AndroidReadExcelActivity.this, chooseFilepath);
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



}
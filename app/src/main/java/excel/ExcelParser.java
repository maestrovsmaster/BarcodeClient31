package excel;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.app.barcodeclient3.R;
import com.opencsv.CSVReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import essences.Good;
import main.MainApplication;

/**
 * Created by MaestroVS on 11.11.2017.
 */

public class ExcelParser {


    String filename = "";
    Context context;

    public ExcelParser(Context context, String filename) {
        this.context = context;
        this.filename = filename;
    }

    Handler.Callback callback;

    ArrayList<Good> goodsList = new ArrayList<>();


    public void parseFile(@NonNull Handler.Callback callback) {
        this.callback = callback;

        ExecuteThread thread = new ExecuteThread();
        thread.start();
    }


    class ExecuteThread extends Thread {

        public void run() {
            Log.d("my", "filename = " + filename);

            if (filename == null) {
                callback.handleMessage(null);
                return;
            }

            if (!FileLoader.isExternalStorageAvailable() || FileLoader.isExternalStorageReadOnly()) {
                Log.w("FileUtils", "Storage not available or read only");
                callback.handleMessage(null);
                return;
            }

            if (filename.contains(".xls")) {
                ArrayList<ArrayList<String>> parseList = new ArrayList<>();
                parseList.addAll(parseXls());
                Message msg =  Message.obtain();
                msg.obj=parseList;
                callback.handleMessage(msg);

            }
            if (filename.contains(".csv")) {
                Log.d("my", "import csv");
                ArrayList<ArrayList<String>> parseList = new ArrayList<>();
                parseList.addAll(parseUsingOpenCSV());
                Message msg =  Message.obtain();
                msg.obj=parseList;
                callback.handleMessage(msg);

            }
            return;
        }


    }

    int maxColumnsCnt=0;

    public int getMaxColumnsCnt() {
        return maxColumnsCnt;
    }

    private ArrayList<ArrayList<String>> parseXls() {
        ArrayList<ArrayList<String>> xlsList = new ArrayList<>();

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


            int iid = 0;
            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                int num = myRow.getRowNum();

                int firstCellNum = myRow.getFirstCellNum();
                int lastCellNum = myRow.getLastCellNum();
                if(lastCellNum>maxColumnsCnt) maxColumnsCnt=lastCellNum;
                Log.d("my", " num =" + num +" first ="+firstCellNum+ " last = " + lastCellNum);
                String name = "";
                String[] cellsRow = new String[lastCellNum];
                for(int c=0;c<lastCellNum;c++) cellsRow[c]="";
              //  ArrayList<String> rowList = new ArrayList<>();
                Iterator<Cell> cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    int index = myCell.getColumnIndex();

                    //  Log.d("my", "myCell = " + myCell.toString() + " column = " + index);
                    name = myCell.toString();
                    if (name == null) name = "";
                   // rowList.add(name);
                    cellsRow[index]=name;
                }
                //xlsList.add(rowList);
                ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(cellsRow));
                xlsList.add(stringList);
                Log.d("my","stringList = "+stringList.toString());
            }
            // updateAdapterH.post();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("my", "err open=" + e.toString());


        }
        return xlsList;
    }


    Handler updateAdapterH = new Handler();


    private ArrayList<ArrayList<String>> parseUsingOpenCSV() {
        ArrayList<ArrayList<String>> xlsList = new ArrayList<>();
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(filename));
            String[] row;
            List<?> content = reader.readAll();


            ArrayList<String> rowList = new ArrayList<>();
            for (Object object : content) {
                row = (String[]) object;


                String name = "";
                Log.d("my", ">>>>>>>>" + row.toString());
                for (int i = 0; i < row.length; i++) {
                    // display CSV values

                    Log.d("my", "import csv ok = ");
                    Log.d("my", " = " + row[i]);

                    name = row[i].toString();
                    if (name == null) name = "";
                    rowList.add(name);

                }
                xlsList.add(rowList);

            }

            Log.d("my", " success import from CSV!!!");
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            Log.d("my", "import csv err2 = " + e.toString());
            System.err.println(e.getMessage());
        }

        return xlsList;
    }


}

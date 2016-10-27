package excel;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import essences.Good;

/**
 * Created by userd088 on 05.09.2016.
 */
public class CreateExportFile {

    /**
     *
     * @param goodsAccList
     * @param filename
     * @param id dock id
     * @return
     */
    public static boolean exportGoodsXLS(ArrayList<Good> goodsAccList, String filename, int id)
    {
        boolean success=false;

        Log.d("my", "accList size = " + goodsAccList.size());
        //setProgressBarIndeterminateVisibility(false);


        // check if available and not read only
        if (FileLoader.isExternalStorageAvailable() || !FileLoader.isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");


            //New Workbook
            Workbook wb = new HSSFWorkbook();

            Cell c = null;

            //Cell style for header row
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.LIME.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            //New Sheet
            Sheet sheet1 = null;
            sheet1 = wb.createSheet("inventory");

            // Generate column headings
            Row row = sheet1.createRow(0);

            c = row.createCell(0);
            c.setCellValue("inv_num");
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue("name");
            c.setCellStyle(cs);

            c = row.createCell(2);
            c.setCellValue("article");
            c.setCellStyle(cs);

            c = row.createCell(3);
            c.setCellValue("unit");
            c.setCellStyle(cs);

            c = row.createCell(4);
            c.setCellValue("barcode");
            c.setCellStyle(cs);

            c = row.createCell(5);
            c.setCellValue("quantity");
            c.setCellStyle(cs);

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));

            int r = 0;
            for (Good good : goodsAccList) {
                r++;
                Row row0 = sheet1.createRow(r);

                Cell c0 = row0.createCell(0);
                c0.setCellValue(id);

                c0 = row0.createCell(1);
                c0.setCellValue(good.getName());

                c0 = row0.createCell(2);
                c0.setCellValue(good.getArticle());

                c0 = row0.createCell(3);
                c0.setCellValue(good.getUnit());

                ArrayList<String> barcs = new ArrayList<>();
                barcs=good.getBarcodes();
                String barc = "";
                if (barcs.size() > 0) barc = barcs.get(0);
                c0 = row0.createCell(4);
                c0.setCellValue(barc);

                c0 = row0.createCell(5);
                c0.setCellValue(good.getFcnt());


            }


            success = FileLoader.writeExcel(wb,filename+".xls" );


        } else {
            //err read file
        }




        return success;
    }




    public static boolean exportGoodsCSV(ArrayList<Good> goodsAccList, String filename, int id)
    {
        boolean success=false;

        String filenm = Environment.getExternalStorageDirectory()
                + File.separator + FileLoader.DIR_NAME + File.separator + filename;

        File file = new File(filenm);
        try {
            file.createNewFile();
            success=true;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Log.d("my", "accList size = " + goodsAccList.size());
        // check if available and not read only
        if (FileLoader.isExternalStorageAvailable() || !FileLoader.isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");

            try {

                FileWriter fw = new FileWriter(filenm);






                ////////////////////////////////

                fw.append("inv_num");
                fw.append(',');

                fw.append("name");
                fw.append(',');

                fw.append("article");
                fw.append(',');

                fw.append("unit");
                fw.append(',');

                fw.append("barcode");
                fw.append(',');

                fw.append("quantity");
                fw.append(',');

                fw.append('\n');




                for (Good good : goodsAccList) {


                    fw.append(""+id);
                    fw.append(',');

                    fw.append(good.getName());
                    fw.append(',');


                    fw.append(good.getArticle());
                    fw.append(',');


                    fw.append(good.getUnit());
                    fw.append(',');

                    ArrayList<String> barcs = new ArrayList<>();
                    barcs=good.getBarcodes();
                    String barc = "";
                    if (barcs.size() > 0) barc = barcs.get(0);

                    fw.append(barc);
                    fw.append(',');


                    fw.append(""+good.getFcnt());
                    fw.append(',');


                    fw.append('\n');

                }



//////////////////////////
                // fw.flush();
                fw.close();

            } catch (Exception e) {
            }

        } else {
            //err read file
        }




        return success;
    }







}

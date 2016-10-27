package excel;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileLoader {

    public static final String DIR_NAME = "BarcodeScanner";
    private static final String inF = DIR_NAME +"\\in";
    private static final String outF = DIR_NAME +"\\out";


    private static FileLoader fileLoader =null;

    private FileLoader() {

    }

    public static FileLoader getFileLoader()
    {

        if(fileLoader ==null ) fileLoader =new FileLoader();
        return fileLoader;

    }


    public static  File getExcelFileWithPathFromSD(String fullfilename) {

       /* File folder = new File(Environment.getExternalStorageDirectory() + "/"+DIR_NAME);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }*/


        File f = new File(fullfilename);
        Log.d("my","import file = "+f.getAbsolutePath().toString());

        return f;
    }




    public static  File getExcelFileFromSD(String fname) {

        File folder = new File(Environment.getExternalStorageDirectory() + "/"+DIR_NAME);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }


            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + DIR_NAME + File.separator + fname);
        Log.d("my","import file = "+f.getAbsolutePath().toString());

        return f;
    }



    public static  boolean writeExcel(Workbook wb, String fileName) {
        boolean success = false;
        // Log.d("my","fname = "+name);

        File rootPath = new File(Environment.getExternalStorageDirectory(),
                DIR_NAME);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        File inPath = new File(Environment.getExternalStorageDirectory(),
                inF);
        if (!inPath.exists()) {
            inPath.mkdirs();
        }

        File outPath = new File(Environment.getExternalStorageDirectory(),
                outF);
        if (!outPath.exists()) {
            outPath.mkdirs();
        }



        // you can create a new file name "test.jpg" in sdcard folder.
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + DIR_NAME + File.separator + fileName);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Create a path where we will place our List of objects on external storage
        //File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }


        return success;

    }


    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}

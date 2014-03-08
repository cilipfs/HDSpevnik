package sk.suchac.hds.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Environment;

public class ExportHelper {
	
	public static final String EXPORT_DIR = "HDSpevnikExports";
	
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public static File getStorageDir() {
	    // Get the directory for the user's public pictures directory. 
	    File file = new File(Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DOWNLOADS), EXPORT_DIR);
	    if (!file.mkdirs()) {
	    	if (!file.exists()) {
	    		return null;
	    	}
	    }
	    return file;
	}
	
	public static boolean export(Activity activity, String text) {
		BufferedWriter out = null;
		try {
		
			File storageDir = null;
			if (ExportHelper.isExternalStorageWritable()) {
				storageDir = ExportHelper.getStorageDir();
			}
			if (storageDir == null) {
			    return false;
			}
			String storageFile = storageDir.toString() + File.separatorChar + activity.getTitle().toString() + ".txt";
			
			String[] lines = text.split(HtmlHelper.LINE_BRAKE);
			
			out = new BufferedWriter(new FileWriter(storageFile));
			
			for (String line : lines) {
				out.write(line);
				out.newLine();
			}
				
			return true;
		
		} catch (Exception e) {
		    return false;
		} finally {
			try{
		        if (out != null) {
		        	out.close( );
		        }
		    } catch (IOException e) {}
		}
	}
}

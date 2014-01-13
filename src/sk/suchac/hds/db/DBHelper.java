package sk.suchac.hds.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static String DB_NAME = "hdsdb";
	private static final int DB_VERSION = 8;

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
//		Log.i(DbHelper.class.getName(), "DbHelper()");
		this.myContext = context;
	}
	
	public void initialize() {
		try {
			createDataBase();
		} catch (IOException e) {
//			Log.w(DBHelper.class.getSimpleName(), "Create DB failed.");
		}
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {
//		Log.i(DbHelper.class.getName(), "createDataBase()");

		int dbVersion = checkDataBase();
//		Log.i(DBHelper.class.getSimpleName(), "old: " + dbVersion + " actual: " + DB_VERSION);
		
		if (dbVersion == DB_VERSION) {
//			Log.i(DBHelper.class.getSimpleName(), "DB exists");
		} else {
			if (dbVersion == -1) {
				// By calling this method and empty database will be created into
				// the default system path
				// of your application so we are gonna be able to overwrite that
				// database with our database.
				this.getReadableDatabase();
				this.close();
			}
			try {
				copyDataBase();
				openDataBaseToWrite().setVersion(DB_VERSION);
				close();

			} catch (IOException e) {
//				Log.e(DBHelper.class.getSimpleName(), "Error copying database");
				throw new Error("Error copying database");
			}
		}

	}

	private int checkDataBase() {
		SQLiteDatabase checkDB = null;
		int version = -1;

		try {
			checkDB = openDataBase();
		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			version = checkDB.getVersion();
			checkDB.close();
		}

		return version;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = myContext.getApplicationInfo().dataDir + "/databases/" + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String myPath = myContext.getApplicationInfo().dataDir + "/databases/" + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//		Log.i(DbHelper.class.getName(), "openDataBase()");
		return myDataBase;
	}
	
	public SQLiteDatabase openDataBaseToWrite() throws SQLException {
		String myPath = myContext.getApplicationInfo().dataDir + "/databases/" + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
//		Log.i(DbHelper.class.getName(), "openDataBase()");
		return myDataBase;
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}
	
	public boolean isOpen() {
		return myDataBase.isOpen();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}

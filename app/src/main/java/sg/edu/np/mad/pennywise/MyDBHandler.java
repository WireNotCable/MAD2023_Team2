package sg.edu.np.mad.pennywise;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyDBHandler extends SQLiteOpenHelper{

    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "account.db";
    public static  String ACCOUNTS= "ACCOUNTS";
    public static String COLUMN_USERNAME = "UserName";
    public static String COLUMN_PASSWORD = "Password";

    public MyDBHandler(Context context,String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + ACCOUNTS + "(" + COLUMN_USERNAME + "TEXT"+COLUMN_PASSWORD + "TEXT)";
        db.execSQL(CREATE_ACCOUNTS_TABLE);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion , int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS);
        onCreate(db);
    }

    public void addUser(User userData){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, userData.getUsername());
        values.put(COLUMN_PASSWORD, userData.getPassword());

        SQLiteDatabase db = this.getWritableDatabase(); // Create Connection
        db.insert(ACCOUNTS, null, values);//input values
        db.close();//close connection
    }
    public String getUID(String Email){
        String query = "SELECT UID FROM " + ACCOUNTS + "WHERE" + COLUMN_USERNAME + "=\"" + Email +"\"";
        SQLiteDatabase db = this.getWritableDatabase();// Open Connection
        Cursor cursor = db.rawQuery(query,null); //Executing query
        if (cursor.moveToFirst()){
            cursor.close();
            return cursor.getString(0);
        }
        else{
            return null;
        }
    }

    /*public User findUser(String Username)
    {
        String query = "SELECT * FROM " + ACCOUNTS + "WHERE " + COLUMN_USERNAME + "=\"" + Username +"\"";
        SQLiteDatabase db = this.getWritableDatabase();// Open Connection
        Cursor cursor = db.rawQuery(query,null); //Executing query
        User result = new User();
        if(cursor.moveToFirst()){
            result.setUsername(cursor.getString(0));
            result.setPassword(cursor.getString(1));
            cursor.close();
            return result;
        }
        else
        {
            return result = null;
        }

    }*/

}



package com.example.sample.database;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.example.sample.Node;
import com.example.sample.friendlist.FriendNode;

/**
 *
 * @author Kazuki Hasegawa
 * @version 2
 */
public final class YossipDatabaseOpenHelper extends SQLiteOpenHelper {
	/**
	 * コンテキストを保持
	 */
	private Context mContext;

	/**
	 * データベース名
	 */
	private final static String DB_NAME = "Yossip";

	/**
	 * データベースバージョン
	 */
	private final static int DB_VER = 1;

	/**
	 * テーブル名
	 */
	private final static String TABLE_NAME = "FavoriteNodeList";

	/**
	 * Columns
	 */
	private final static String[] COLUMNS = {"MACAddr", "UserName", "UserIcon", "UserProfile"};

	/**
	 *
	 * @param context
	 */
	public YossipDatabaseOpenHelper(Context context) {
		super(context,DB_NAME ,null, DB_VER);
		mContext = context;
	}

	/**
	 * 作成される場合
	 * execSqlを呼び出す
	 *
	 * @param db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			execSql(db, "sql/create");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * アップグレードされる場合
	 * execSqlを呼び出す
	 *
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			execSql(db, "sql/drop");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 渡されたassetsのパスにあるSQL文を実行する
	 *
	 * @param db
	 * @param assetsPath
	 * @param as
	 * @param sqlFiles[]
	 * @param fineName
	 * @param execStrings
	 * @param execStr
	 *
	 * @throws IOException
	 */
	private void execSql(SQLiteDatabase db, String assetPath) throws IOException {
		AssetManager as = mContext.getResources().getAssets();
		String sqlFiles[] = as.list(assetPath);
		for(String fileName :sqlFiles) {
			String execStrings = readFile(as.open(assetPath + "/" + fileName));
			for(String execStr : execStrings.split(";")) {
				db.execSQL(execStr);
			}
		}
	}

	/**
	 * 渡されたファイルパスのファイルの中身を
	 * 読み込み返す
	 *
	 * @param is
	 * @param br
	 * @param sb
	 * @param str
	 *
	 * @return sb.toString()
	 *
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private String readFile(InputStream is) throws IOException, UnsupportedEncodingException{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, "SJIS"));

			StringBuffer sb = new StringBuffer();
			String str;
			while((str = br.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} finally {
			if(br != null) {
				br.close();
			}
		}
	}

	/**
	 * SQLiteに登録されているデータを全て配列に取り出す
	 *
	 * @param list 追加するリスト
	 * @param db データベース変数
	 * @param c カーソル
	 * @param mac マックアドレス
	 * @param name ユーザ名
	 * @param icon ユーザアイコン
	 * @param profile ユーザプロフィール
	 */
    public final void onDataImport(List list) {
    	onQueryDataInDatabase(list);
    }

    /**
     * データベースに指定されたリストを書き込む(フレンドリスト)
     *
     * @param list
     * @param db
     * @param i
     * @param node
     * @param values
     */
    public final void onDataOutport(List list) throws SQLiteException  {
    	onDeleteDatabaseData();
    	onInsertDataIntoDatabase(list);
    }

    /**
     * データベース内の情報をすべて削除する
     */
	private void onDeleteDatabaseData() {
    	SQLiteDatabase db = null;
    	try {
    		db = getWritableDatabase();
    		db.delete(TABLE_NAME, null, null);
    	} catch(SQLException e) {
    		e.printStackTrace();
    	} finally {
    		if(db != null) {
    			db.close();
    		}
    	}
    }

	/**
	 * データベース内のデータを指定されたリストに挿入する
	 */
	private synchronized void onQueryDataInDatabase(List list) {
		SQLiteDatabase db = null;

    	try {
    		db = getReadableDatabase();
    		Cursor c = db.query(TABLE_NAME, COLUMNS, null, null, null, null, null, null);
    		if(c.moveToFirst()) {
    			while(c.getPosition() < c.getCount()) {
    				String mac = c.getString(c.getColumnIndex(COLUMNS[0]));
    				String name = c.getString(c.getColumnIndex(COLUMNS[1]));
    				Bitmap icon = byteArrayToBitmap(c.getBlob(c.getColumnIndex(COLUMNS[2])));
    				String profile = c.getString(c.getColumnIndex(COLUMNS[3]));

    				list.add(new FriendNode(new Node(mac, name, 1f, 1f, icon, profile)));

    				c.moveToNext();
    			}
    		}
    		c.close();
    	}
    	finally {
    		if(db != null) {
    			db.close();
    		}
    	}
	}

	/**
	 * データベースにデータをインサートする
	 */
	private synchronized void onInsertDataIntoDatabase(List list) {
		SQLiteDatabase db = null;

    	Iterator<FriendNode> i = list.iterator();
    	while(i.hasNext()) {
    		FriendNode node = i.next();
    		try {
    			db = getWritableDatabase();
				ContentValues values = new ContentValues();
    			values.put(COLUMNS[0], node.getMACAddr());
    			values.put(COLUMNS[1], node.getName());
    			values.put(COLUMNS[2], bitmapToByteArray(node.getIcon()));
    			values.put(COLUMNS[3], node.getProfile());
        		long r = db.insert(TABLE_NAME, null, values);
//        		if(r == -1) {
//        			System.out.println("インサートエラー");
//        		} else {
//        			System.out.println("成功");
//        		}
    		} finally {
    			if(db != null) {
    				db.close();
    			}
    		}
    	}
	}

	/**
	 * 渡されたリソースをbyte配列にして返す
	 *
	 * @param bitmap
	 * @param baos
	 *
	 * @return baos.toByteArray()
	 */
    public byte[] bitmapToByteArray(Bitmap bitmap) throws NullPointerException {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
    }

    /**
     * 渡されたbyte配列をリソースにして返す
     *
     * @param byteArray
     * @param options
     * @param bitmap
     *
     * @return bitmap
     */
    public Bitmap byteArrayToBitmap(byte[] byteArray) throws NullPointerException {
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

    	return bitmap;
    }
}
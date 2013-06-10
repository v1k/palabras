package com.mates120.myword;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class WordsDataSource {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allWordsColumns = {DatabaseHelper.COL_WORDS_ID, 
			DatabaseHelper.COL_WORDS_SOURCE, 
			DatabaseHelper.COL_WORDS_SEARCH_COUNTER, 
			DatabaseHelper.COL_WORDS_RATING};
	
	private String[] allLinksColumns = {DatabaseHelper.COL_LINKS_WORD,
			DatabaseHelper.COL_LINKS_VALUE};
	
	public WordsDataSource(Context context){
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public Word createWord(String word){
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COL_WORDS_SOURCE, word);
		values.put(DatabaseHelper.COL_WORDS_SEARCH_COUNTER, 0);
		values.put(DatabaseHelper.COL_WORDS_RATING, 0);
		long insertId = database.insert(DatabaseHelper.TABLE_WORDS, null, values);
		Cursor cursor = database.query(DatabaseHelper.TABLE_WORDS,
				allWordsColumns, DatabaseHelper.COL_WORDS_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		Word newWord = cursorToWord(cursor);
		cursor.close();
		return newWord;
	}
	
	public void deleteWord(Word word){
		long id = word.getId();
		System.out.println("Word deleted with id: " + id);
		database.delete(DatabaseHelper.TABLE_WORDS, 
				DatabaseHelper.COL_WORDS_ID + " = " + id, null);
	}
	
	public List<Word> getAllWords(){
		List<Word> words = new ArrayList<Word>();
		Cursor cursor = database.query(DatabaseHelper.TABLE_WORDS, allWordsColumns, 
				null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			Word word = cursorToWord(cursor);
			words.add(word);
			cursor.moveToNext();
		}
		cursor.close();
		return words;
	}
	
	private Word cursorToWord(Cursor cursor){
		Word word = new Word();
		word.setId(cursor.getLong(0));
		word.setSource(cursor.getString(1));
		word.setSearchCount(cursor.getInt(2));
		word.setRating(cursor.getInt(3));
		return word;
	}
}

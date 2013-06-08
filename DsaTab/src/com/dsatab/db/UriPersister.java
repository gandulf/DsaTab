package com.dsatab.db;

import android.net.Uri;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

public class UriPersister extends StringType {

	private static final UriPersister singleTon = new UriPersister();

	private UriPersister() {
		super(SqlType.STRING, new Class<?>[] { Uri.class });
	}

	public static UriPersister getSingleton() {
		return singleTon;
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
		Uri uri = (Uri) javaObject;
		if (uri == null) {
			return null;
		} else {
			return uri.toString();
		}
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
		return Uri.parse((String) sqlArg);
	}
}

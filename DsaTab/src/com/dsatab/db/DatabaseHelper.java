package com.dsatab.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dsatab.R;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.SpellInfo;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.xml.XmlParser;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something
	// appropriate for your app
	private static final String DATABASE_NAME = "dsatab.db";
	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 39;

	// the DAO object we use to access the SimpleData table
	private RuntimeExceptionDao<Item, UUID> itemRuntimeDao = null;

	private Map<Class<?>, RuntimeExceptionDao<?, Integer>> runtimeDaos = new HashMap<Class<?>, RuntimeExceptionDao<?, Integer>>(
			10);

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, ArtInfo.class);
			TableUtils.createTable(connectionSource, SpellInfo.class);

			TableUtils.createTable(connectionSource, Item.class);
			TableUtils.createTable(connectionSource, Weapon.class);
			TableUtils.createTable(connectionSource, Shield.class);
			TableUtils.createTable(connectionSource, DistanceWeapon.class);
			TableUtils.createTable(connectionSource, Armor.class);
			TableUtils.createTable(connectionSource, MiscSpecification.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

		XmlParser.fillArts();
		XmlParser.fillSpells();
		XmlParser.fillItems();

		Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate");
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, ArtInfo.class, true);
			TableUtils.dropTable(connectionSource, SpellInfo.class, true);

			TableUtils.dropTable(connectionSource, Item.class, true);
			TableUtils.dropTable(connectionSource, Weapon.class, true);
			TableUtils.dropTable(connectionSource, Shield.class, true);
			TableUtils.dropTable(connectionSource, DistanceWeapon.class, true);
			TableUtils.dropTable(connectionSource, Armor.class, true);
			TableUtils.dropTable(connectionSource, MiscSpecification.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Item, UUID> getItemDao() {
		if (itemRuntimeDao == null) {
			itemRuntimeDao = getRuntimeExceptionDao(Item.class);
		}
		return itemRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public <T> RuntimeExceptionDao<T, Integer> getRuntimeDao(Class<T> clazz) {

		@SuppressWarnings("unchecked")
		RuntimeExceptionDao<T, Integer> runtimeDao = (RuntimeExceptionDao<T, Integer>) runtimeDaos.get(clazz);

		if (runtimeDao == null) {
			runtimeDao = getRuntimeExceptionDao(clazz);
			runtimeDaos.put(clazz, runtimeDao);
		}
		return runtimeDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		itemRuntimeDao = null;
	}
}

package com.dsatab.xml;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.DsaTabApplication;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.SpellInfo;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * @author Gandulf
 * 
 */
public class DataManager {

	private static LruCache<String, Bitmap> mMemoryCache;

	private static SelectArg artNameArg, artGradeArg;
	private static PreparedQuery<ArtInfo> artNameQuery, artNameGradeQuery;

	private static SelectArg spellNameArg;
	private static PreparedQuery<SpellInfo> spellNameQuery;

	private static SelectArg itemNameArg;
	private static PreparedQuery<Item> itemNameQuery;

	public static void init(Context context) {

		// Get memory class of this device, exceeding this amount will throw an
		// OutOfMemory exception.
		final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = 1024 * 1024 * memClass / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in bytes rather than number
				// of items.
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};

	}

	private static void initArtQueries() {
		try {
			if (artNameArg == null || artNameQuery == null) {
				artNameArg = new SelectArg();
				artNameQuery = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
						.queryBuilder().where().eq("name", artNameArg).prepare();
			}

			if (artGradeArg == null || artNameArg == null || artNameQuery == null) {
				artGradeArg = new SelectArg();

				artNameGradeQuery = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
						.queryBuilder().where().eq("name", artNameArg).and().eq("grade", artGradeArg).prepare();
			}
		} catch (SQLException e) {
			Debug.error(e);
			BugSenseHandler.sendException(e);
		}

	}

	private static void initSpellQueries() {
		if (spellNameArg != null && spellNameQuery != null)
			return;

		try {
			spellNameArg = new SelectArg();

			spellNameQuery = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(SpellInfo.class)
					.queryBuilder().where().eq("name", spellNameArg).prepare();

		} catch (SQLException e) {
			Debug.error(e);
			BugSenseHandler.sendException(e);
		}

	}

	private static void initItemQueries() {
		if (itemNameArg != null && itemNameQuery != null)
			return;

		try {
			itemNameArg = new SelectArg();

			itemNameQuery = DsaTabApplication.getInstance().getDBHelper().getItemDao().queryBuilder().where()
					.eq("name", itemNameArg).prepare();
		} catch (SQLException e) {
			Debug.error(e);
			BugSenseHandler.sendException(e);
		}

	}

	private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	private static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public static List<String> getItemTypes() {
		RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();
		GenericRawResults<String[]> results = itemDao.queryRaw("select distinct(itemTypes) from Item;");
		Iterator<String[]> rowIter = results.iterator();

		Set<String> itemTypes = new HashSet<String>();

		while (rowIter.hasNext()) {
			String[] row = rowIter.next();
			String types = row[0];
			if (!TextUtils.isEmpty(types)) {
				for (String type : types.split(Item.ITEM_TYPES_SEP)) {
					if (!TextUtils.isEmpty(type))
						itemTypes.add(type);
				}
			}
		}

		List<String> result = new ArrayList<String>(itemTypes);
		Collections.sort(result);
		return result;

	}

	public static List<String> getItemCategories() {
		RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();
		GenericRawResults<String[]> results = itemDao.queryRaw("select distinct(category) from Item;");
		Iterator<String[]> rowIter = results.iterator();

		Set<String> itemTypes = new HashSet<String>();

		while (rowIter.hasNext()) {
			String[] row = rowIter.next();
			String cat = row[0];
			if (!TextUtils.isEmpty(cat)) {
				itemTypes.add(cat);
			}
		}

		List<String> result = new ArrayList<String>(itemTypes);
		Collections.sort(result);
		return result;

	}

	public static Cursor getItemsCursor(CharSequence nameConstraint, Collection<ItemType> itemTypes, String itemCategory) {

		try {

			RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();

			PreparedQuery<Item> query = null;

			QueryBuilder<Item, UUID> builder = itemDao.queryBuilder();

			if (TextUtils.isEmpty(nameConstraint) && (itemTypes == null || itemTypes.isEmpty())
					&& TextUtils.isEmpty(itemCategory)) {
				query = builder.prepare();
			} else {
				Where<Item, UUID> where = builder.where();

				if (!TextUtils.isEmpty(nameConstraint)) {
					where = where.like("name", nameConstraint + "%").and();
				}

				if (!TextUtils.isEmpty(itemCategory)) {
					where = where.eq("category", itemCategory).and();
				}

				if (itemTypes != null && !itemTypes.isEmpty()) {
					for (ItemType type : itemTypes) {
						where = where.like("itemTypes", "%;" + type.name() + ";%");
					}
					if (itemTypes.size() > 1) {
						where = where.or(itemTypes.size());
					}
				}
				query = where.prepare();
			}

			Cursor cursor = getCursor(query);

			return cursor;
		} catch (SQLException e) {
			Debug.error(e);
			BugSenseHandler.sendExceptionMessage(Debug.CATEGORY_DATABASE, "DataManager.getItemsCursor", e);
		}

		return null;

	}

	public static Cursor getCursor(PreparedQuery<?> query) {
		Cursor cursor = null;
		try {
			DatabaseConnection databaseConnection = DsaTabApplication.getInstance().getDBHelper().getConnectionSource()
					.getReadOnlyConnection();

			AndroidCompiledStatement compiledStatement = (AndroidCompiledStatement) query.compile(databaseConnection,
					StatementType.SELECT);
			cursor = compiledStatement.getCursor();
		} catch (SQLException e) {

			Debug.error(e);
		}
		return cursor;
	}

	public static int deleteItem(Item item) {
		RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();

		int result = itemDao.delete(item);
		for (ItemSpecification itemSpec : item.getSpecifications()) {
			RuntimeExceptionDao itemSpecDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(itemSpec.getClass());
			itemSpecDao.delete(itemSpec);
		}
		return result;
	}

	public static int updateItem(Item item) {
		RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();

		int result = itemDao.update(item);
		for (ItemSpecification itemSpec : item.getSpecifications()) {
			RuntimeExceptionDao itemSpecDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(itemSpec.getClass());
			itemSpecDao.createOrUpdate(itemSpec);
		}

		return result;
	}

	public static int createItem(Item item) {
		RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();
		int result = itemDao.create(item);

		for (ItemSpecification itemSpec : item.getSpecifications()) {
			RuntimeExceptionDao itemSpecDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(itemSpec.getClass());
			itemSpecDao.create(itemSpec);
		}
		itemDao.update(item);

		return result;
	}

	public static Bitmap getBitmap(File file, int suggestedSize) {
		if (file != null)
			return getBitmap(Uri.fromFile(file), suggestedSize);
		else
			return null;
	}

	public static Bitmap getBitmap(Uri uri, int suggestedSize) {
		Bitmap bitmap = getBitmapFromMemCache(uri.toString() + "x" + suggestedSize);
		if (bitmap == null) {
			bitmap = Util.decodeBitmap(uri, suggestedSize);
			addBitmapToMemoryCache(uri.toString() + "x" + suggestedSize, bitmap);
		}
		return bitmap;
	}

	public static Item getItemByCursor(Cursor cursor) {
		String _id = cursor.getString(cursor.getColumnIndex("_id"));
		return DataManager.getItemById(UUID.fromString(_id));
	}

	public static Item getItemById(UUID itemId) {
		RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();
		Item item = itemDao.queryForId(itemId);
		return item;
	}

	public static SpellInfo getSpellByName(String name) {
		initSpellQueries();
		spellNameArg.setValue(name);
		return DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(SpellInfo.class)
				.queryForFirst(spellNameQuery);
	}

	public static ArtInfo getArtByName(String name) {
		initArtQueries();
		artNameArg.setValue(name);
		return DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class).queryForFirst(artNameQuery);
	}

	public static ArtInfo getArtByNameAndGrade(String name, String grade) {
		initArtQueries();
		artGradeArg.setValue(grade);
		artNameArg.setValue(name);

		ArtInfo info = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
				.queryForFirst(artNameGradeQuery);

		// if we find no art with grade, try without
		if (info == null) {
			info = getArtByName(name);
			if (info != null)
				Debug.warning("Art with grade could not be found using the one without grade: " + name);
		}

		return info;
	}

	public static Item getItemByName(String name) {
		initItemQueries();

		itemNameArg.setValue(name);
		return DsaTabApplication.getInstance().getDBHelper().getItemDao().queryForFirst(itemNameQuery);
	}

}

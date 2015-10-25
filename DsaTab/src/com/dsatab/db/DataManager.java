package com.dsatab.db;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.LruCache;

import com.dsatab.DsaTabApplication;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.SpellInfo;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Debug;
import com.gandulf.guilib.util.ResUtil;
import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author Gandulf
 */
public class DataManager {

    private static SelectArg artNameArg, artGradeArg, artNameLikeArg;
    private static PreparedQuery<ArtInfo> artNameQuery, artNameGradeQuery, artNameLikeQuery, artNameGradeLikeQuery;

    private static SelectArg spellNameArg;
    private static PreparedQuery<SpellInfo> spellNameQuery;

    private static SelectArg itemNameArg;
    private static PreparedQuery<Item> itemNameQuery;

    private static List<String> webInfos;
    private static String webInfoTemplate;
    private static LruCache<String, String> webInfo;

    public static String likify(String value, boolean fuzzy) {
        value = value.replace('*','%');

        if (!value.contains("&")) {
            if (fuzzy)
                value = "%"+value + "%";
            else
                value = value + "%";
        }

        return value;
    }

    private static void initArtQueries() {
        try {
            if (artNameArg == null)
                artNameArg = new SelectArg();
            if (artGradeArg == null)
                artGradeArg = new SelectArg();
            if (artNameLikeArg == null)
                artNameLikeArg = new SelectArg();

            if (artNameQuery == null) {
                artNameQuery = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
                        .queryBuilder().where().eq("name", artNameArg).prepare();
            }

            if (artNameGradeQuery == null) {
                artNameGradeQuery = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
                        .queryBuilder().where().eq("name", artNameArg).and().eq("grade", artGradeArg).prepare();
            }

            if (artNameLikeQuery == null) {
                artNameLikeQuery = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
                        .queryBuilder().where().like("name", artNameLikeArg).prepare();
            }

            if (artNameGradeLikeQuery == null) {
                artNameGradeLikeQuery = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
                        .queryBuilder().where().like("name", artNameLikeArg).and().eq("grade", artGradeArg).prepare();
            }

        } catch (SQLException e) {
            Debug.error(e);

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
        }

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

                int stmtCount=0;
                if (!TextUtils.isEmpty(nameConstraint)) {
                    String name = nameConstraint.toString();
                    if (name.contains("%")) {
                        where.like("name", nameConstraint);
                    } else {
                        where.like("name", nameConstraint + "%");
                    }
                    stmtCount++;
                }

                if (!TextUtils.isEmpty(itemCategory)) {
                    where.eq("category", itemCategory);
                    stmtCount++;
                }

                if (itemTypes != null && !itemTypes.isEmpty()) {

                    for (ItemType type : itemTypes) {
                        where.like("itemTypes", "%;" + type.name() + ";%");
                    }
                    if (itemTypes.size() > 1) {
                        where.or(itemTypes.size());
                    }
                    stmtCount++;
                }

                if (stmtCount > 1) {
                    where.and(stmtCount);
                }

                query = where.prepare();
            }

            Cursor cursor = getCursor(query);

            return cursor;
        } catch (SQLException e) {
            Debug.error(e);
        }

        return null;

    }

    private static Cursor getCursor(PreparedQuery<?> query) {
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

    public static int createOrUpdateItem(Item item) {
        RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();

        CreateOrUpdateStatus status = itemDao.createOrUpdate(item);
        int result = status.getNumLinesChanged();

        for (ItemSpecification itemSpec : item.getSpecifications()) {
            RuntimeExceptionDao itemSpecDao = DsaTabApplication.getInstance().getDBHelper()
                    .getRuntimeDao(itemSpec.getClass());
            itemSpecDao.createOrUpdate(itemSpec);
        }

        return result;
    }

    public static Item getItemByCursor(Cursor cursor) {
        String _id = cursor.getString(cursor.getColumnIndex("_id"));
        return DataManager.getItemById(UUID.fromString(_id));
    }

    public static Item getItemById(UUID itemId) {
        if (itemId != null) {
            RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();
            Item item = itemDao.queryForId(itemId);
            return item;
        } else {
            return null;
        }
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

    public static ArtInfo getArtLikeName(String name) {
        initArtQueries();
        artNameLikeArg.setValue("%" + name);
        return DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
                .queryForFirst(artNameLikeQuery);
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

    public static ArtInfo getArtLikeNameAndGrade(String name, String grade) {
        initArtQueries();
        artGradeArg.setValue(grade);
        artNameLikeArg.setValue(name);

        ArtInfo info = DsaTabApplication.getInstance().getDBHelper().getRuntimeDao(ArtInfo.class)
                .queryForFirst(artNameGradeLikeQuery);

        // if we find no art with grade, try without
        if (info == null) {
            info = getArtLikeName(name);
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

    public static List<String> getWebInfos(Context context) {
        if (webInfos == null) {
            webInfos = new ArrayList<String>();
            InputStream is = null;

            try {
                XPathFactory factory = XPathFactory.newInstance();
                XPath xPath = factory.newXPath();
                is = context.getResources().getAssets().open("data/webinfo.html");
                InputSource inputSource = new InputSource(is);

                NodeList shows = (NodeList) xPath.evaluate("//div[@class='card']", inputSource, XPathConstants.NODESET);

                for (int i = 0; i < shows.getLength(); i++) {
                    Element show = (Element) shows.item(i);
                    webInfos.add(show.getAttribute("id"));
                }

            } catch (XPathExpressionException e) {
                Debug.error(e);
            } catch (FileNotFoundException e) {
                Debug.error(e);
            } catch (IOException e) {
                Debug.error(e);

            } catch (TransformerFactoryConfigurationError e) {
                Debug.error(e);
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {

                }
            }
        }

        return webInfos;
    }

    public static String getWebInfo(Context context, String tag) {

        if (webInfo == null) {
            webInfo = new LruCache<String, String>(20);
        }
        if (webInfoTemplate == null) {
            webInfoTemplate = ResUtil.loadAssestToString("data/webinfo_template.html", context);
        }

        String data = webInfo.get(tag);

        if (data == null) {
            InputStream is = null;
            try {
                XPathFactory factory = XPathFactory.newInstance();
                XPath xPath = factory.newXPath();

                is = context.getResources().getAssets().open("data/webinfo.html");
                InputSource inputSource = new InputSource(is);

                Element show = (Element) xPath.evaluate("//div[@id='" + tag + "']", inputSource, XPathConstants.NODE);

                StringWriter writer = new StringWriter();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(new DOMSource(show), new StreamResult(writer));
                data = writer.toString();
                if (webInfoTemplate != null) {
                    data = webInfoTemplate.replace("${data}", data);
                }
                webInfo.put(tag, data);

            } catch (XPathExpressionException e) {
                Debug.error(e);
            } catch (FileNotFoundException e) {
                Debug.error(e);
            } catch (IOException e) {
                Debug.error(e);
            } catch (TransformerConfigurationException e) {
                Debug.error(e);
            } catch (TransformerFactoryConfigurationError e) {
                Debug.error(e);
            } catch (TransformerException e) {
                Debug.error(e);
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
            }
        }
        return data;
    }
}

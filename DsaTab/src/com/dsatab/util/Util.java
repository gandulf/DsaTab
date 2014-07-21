package com.dsatab.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.MenuItemCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatTalent;
import com.dsatab.data.JSONable;
import com.dsatab.data.Markable;
import com.dsatab.data.Probe;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.view.listener.EditListener;
import com.dsatab.view.listener.ProbeListener;

public class Util {

	private static final String DRAWABLE = "drawable";

	private static final String PLUS = "+";
	private static final String MINUS = "-";
	private static final String NULL = "null";

	private static final List<String> ROMANS = Arrays.asList("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII",
			"IX", "X");

	private static NumberFormat effectFormat = NumberFormat.getNumberInstance();
	static {
		effectFormat.setMaximumFractionDigits(1);
	}

	public static int getDrawableByName(String name) {
		return DsaTabApplication.getInstance().getResources()
				.getIdentifier(name, DRAWABLE, DsaTabApplication.getInstance().getPackageName());

	}

	public static LayerDrawable mergeDrawables(Context context, int drawable1, int drawable2) {
		Resources r = context.getResources();
		Drawable[] layers = new Drawable[2];
		layers[0] = r.getDrawable(drawable1);
		layers[1] = r.getDrawable(drawable2);
		LayerDrawable layerDrawable = new LayerDrawable(layers);
		return layerDrawable;
	}

	public static void pickImage(Activity activity, int action) {

		Uri targetUri = Media.EXTERNAL_CONTENT_URI;
		String folderPath = DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS).getAbsolutePath();
		String folderBucketId = Integer.toString(folderPath.toLowerCase(Locale.GERMAN).hashCode());

		targetUri = targetUri.buildUpon().appendQueryParameter(ImageColumns.BUCKET_ID, folderBucketId).build();

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setData(targetUri);

		activity.startActivityForResult(Intent.createChooser(photoPickerIntent, "Bild auswählen"), action);
	}

	public static File handleImagePick(Activity activity, String prefKey, Intent data) {

		Uri selectedImage = data.getData();
		if (selectedImage != null) {
			String[] filePathColumn = { MediaColumns.DATA, ImageColumns.BUCKET_ID };

			Cursor cursor = activity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

			if (cursor.moveToFirst()) {

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				// int bucketIndex = cursor.getColumnIndex(filePathColumn[1]);
				String filePath = cursor.getString(columnIndex);
				// String bucketId = cursor.getString(bucketIndex);

				cursor.close();
				if (filePath != null) {
					File file = new File(filePath);
					if (file.exists()) {
						return file;
					}
				}
			}
		} else {
			Debug.error("Intent returned from image pick did not containt uri data:" + data);
		}
		return null;
	}

	public static class FileNameComparator implements Comparator<File> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(File object1, File object2) {
			return object1.getName().compareToIgnoreCase(object2.getName());
		}
	};

	public static void hideKeyboard(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) DsaTabApplication.getInstance().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
		}
	}

	private static final String PLAIN_ASCII = "AaEeIiOoUu" // grave
			+ "AaEeIiOoUuYy" // acute
			+ "AaEeIiOoUuYy" // circumflex
			+ "AaOoNn" // tilde
			+ "AaEeIiOoUuYy" // umlaut
			+ "Aa" // ring
			+ "Cc" // cedilla
			+ "OoUu" // double acute
	;

	private static final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
			+ "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
			+ "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
			+ "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
			+ "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" + "\u00C5\u00E5"
			+ "\u00C7\u00E7" + "\u0150\u0151\u0170\u0171";

	// remove accentued from a string and replace with ascii equivalent
	public static String convertNonAscii(String s) {
		if (s == null)
			return null;
		StringBuilder sb = new StringBuilder();
		int n = s.length();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);

			switch (c) {
			case 'Ä':
				sb.append("Ae");
				break;
			case 'ä':
				sb.append("ae");
				break;
			case 'Ö':
				sb.append("Oe");
				break;
			case 'ö':
				sb.append("oe");
				break;
			case 'Ü':
				sb.append("Üe");
				break;
			case 'ü':
				sb.append("ue");
				break;
			case 'ß':
				sb.append("ss");
				break;
			default:
				int pos = UNICODE.indexOf(c);
				if (pos > -1) {
					sb.append(PLAIN_ASCII.charAt(pos));
				} else if (Character.isLetterOrDigit(c)) {
					sb.append(c);
				}
				break;
			}

		}
		return sb.toString();
	}

	public static Spanned getText(int resourceId, java.lang.Object... formatArgs) {
		return Html.fromHtml(String.format(
				Html.toHtml(new SpannedString(DsaTabApplication.getInstance().getText(resourceId))), formatArgs));
	}

	public static Bitmap decodeBitmap(final File f, final int suggestedSize) {
		if (f == null) {
			return null;
		}
		if (f.exists() == false) {
			return null;
		}

		return decodeBitmap(Uri.fromFile(f), suggestedSize);
	}

	public static Bitmap decodeBitmap(Uri uri, int maxImageSize) {
		try {

			Bitmap b = null;
			InputStream is = null;
			BufferedInputStream bis = null;

			// Decode image size
			BitmapFactory.Options o;
			try {
				is = DsaTabApplication.getInstance().getBaseContext().getContentResolver().openInputStream(uri);
				bis = new BufferedInputStream(is);

				o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(bis, null, o);
			} finally {
				if (is != null)
					is.close();
			}
			is = null;
			bis = null;

			// Find the correct scale value.It should be the power of 2
			final int requiredSize = maxImageSize;
			int widthTmp = o.outWidth, heightTmp = o.outHeight;
			int scale = 1;
			while (true) {
				if ((widthTmp / 2) < requiredSize || (heightTmp / 2) < requiredSize) {
					break;
				}
				widthTmp /= 2;
				heightTmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inDither = false;
			o2.inInputShareable = true;
			o2.inPurgeable = true;
			o2.inTempStorage = new byte[32 * 1024];

			try {
				is = DsaTabApplication.getInstance().getBaseContext().getContentResolver().openInputStream(uri);
				bis = new BufferedInputStream(is);
				b = BitmapFactory.decodeStream(bis, null, o2);
			} finally {
				if (is != null)
					is.close();
			}
			return b;

		} catch (final FileNotFoundException e) {
			Debug.warning("Could not find bitmap file for:" + uri);
			return null;
		} catch (final Throwable e) {
			Debug.error(e);
			System.gc();
			return null;
		}
	}

	public static String checkFileWriteAccess(File file) {
		String error = null;

		String MEDIA_MOUNTED = " Der häufigste Grund hierfür ist, dass die SD-Karte gerade vom PC verwendet wird. Trenne am besten das Kabel zwischen Smartphone und PC und versuche es erneut.";

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_BAD_REMOVAL.equals(state)) {
			// Memory Card was removed before it was unmounted
			error = "SD-Karte wurde entfernt ohne das die unmounted wurde.";
		} else if (Environment.MEDIA_CHECKING.equals(state)) {
			// Memory Card is present and being disk-checked
			error = "SD-Karte ist vorhanden, wird aber gerade üerprüft.";
		} else if (Environment.MEDIA_MOUNTED.equals(state)) {
			// fine
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// Memory Card is present and mounted with readonly access
			error = "SD-Karte ist nur mit Leseberechtigungen gemounted." + MEDIA_MOUNTED;
		} else if (Environment.MEDIA_NOFS.equals(state)) {
			// Memory Card is present but is blank or using unsupported file
			// system
			error = "SD-Karte ist vorhanden, aber leer oder mit einem nicht unterstützten Dateisystem formatiert.";
		} else if (Environment.MEDIA_REMOVED.equals(state)) {
			// Memory Card is not present
			error = "Keine SD-Karte vorhanden.";
		} else if (Environment.MEDIA_SHARED.equals(state)) {
			// Memory Card is present but shared via USB mass storage
			error = "SD-Karte ist vorhanden, wird aber derzeit über den USB Massespeicher geteilt." + MEDIA_MOUNTED;
		} else if (Environment.MEDIA_UNMOUNTABLE.equals(state)) {
			// Memory Card is present but cannot be mounted
			error = "SD-Karte ist vorhanden, kann aber nicht gemounted werden." + MEDIA_MOUNTED;
		} else if (Environment.MEDIA_UNMOUNTED.equals(state)) {
			// Memory Card is present but not mounted
			error = "SD-Karte ist vorhanden, derzeit aber nicht gemounted.";
		}

		if (error == null) {
			if (file.exists()) {
				if (!file.canWrite()) {
					error = "DsaTab erhielt keine Schreibrechte für folgende Datei:" + file.getAbsolutePath() + "."
							+ MEDIA_MOUNTED;
				}
			} else if (file.getParentFile().exists() && !file.getParentFile().canWrite()) {
				error = "DsaTab erhielt keine Schreibrechte für folgende Datei:" + file.getAbsolutePath() + ". "
						+ MEDIA_MOUNTED;
			}
		}

		return error;
	}

	public static void close(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Debug.error(e);
			}
		}
	}

	public static void close(Writer stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Debug.error(e);
			}
		}
	}

	public static void close(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Debug.error(e);
			}
		}
	}

	public static String join(String... strings) {
		StringBuilder sb = new StringBuilder();

		for (String s : strings) {
			if (s != null) {
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static float[] parseFloats(String s) {
		StringTokenizer st = new StringTokenizer(s, " ");
		int count = st.countTokens();
		float[] floats = new float[count];

		for (int i = 0; i < count; i++) {
			floats[i] = Float.parseFloat(st.nextToken());
		}
		return floats;
	}

	public static boolean notifyDatasetChanged(AdapterView<?> list) {
		Adapter adapter = list.getAdapter();
		if (adapter instanceof BaseAdapter) {
			BaseAdapter baseAdapter = (BaseAdapter) adapter;
			baseAdapter.notifyDataSetChanged();
			return true;
		} else {
			return false;
		}

	}

	public static String toString(float[] floats) {
		StringBuilder sb = new StringBuilder();
		for (float f : floats) {
			sb.append(Float.toString(f));
			sb.append(" ");
		}
		return sb.toString().trim();
	}

	public static Integer parseInteger(String s) throws NumberFormatException {

		if (s == null)
			return null;

		s = s.trim();

		if (s.length() == 0 || MINUS.equals(s) || NULL.equals(s))
			return null;
		// gracefull fallback frm float to int
		if (s.contains(".")) {
			Debug.warning("Parsing float as integer: " + s);
			s = s.substring(0, s.indexOf('.'));
		}
		Integer i;
		if (s.startsWith(PLUS))
			i = Integer.valueOf(s.substring(1));
		else
			i = Integer.valueOf(s);

		return i;
	}

	public static int parseInt(String s, int defaultValue) throws NumberFormatException {
		if (s == null)
			return defaultValue;

		s = s.trim();

		if (s.length() == 0 || MINUS.equals(s) || NULL.equals(s))
			return defaultValue;

		Integer i;
		if (s.startsWith(PLUS))
			i = Integer.valueOf(s.substring(1));
		else
			i = Integer.valueOf(s);

		return i;

	}

	public static int parseInt(String s) throws NumberFormatException {
		return parseInt(s, 0);
	}

	public static Long parseLong(String s) throws NumberFormatException {

		if (s == null)
			return null;

		s = s.trim();

		if (s.length() == 0 || MINUS.equals(s) || NULL.equals(s))
			return null;

		Long i;
		if (s.startsWith(PLUS))
			i = Long.valueOf(s.substring(1));
		else
			i = Long.valueOf(s);

		return i;
	}

	public static void applyRowStyle(Markable markable, View row, int position) {
		LevelListDrawable levelListDrawable;

		if (row.getBackground() instanceof LevelListDrawable) {
			levelListDrawable = (LevelListDrawable) row.getBackground();
		} else {
			row.setBackgroundResource(Util.getThemeResourceId(row.getContext(), R.attr.listItemBackground));
			levelListDrawable = (LevelListDrawable) row.getBackground();
		}

		int level = position % 2;
		if (markable.isFavorite())
			level += 2;
		else if (markable.isUnused())
			level += 4;

		levelListDrawable.setLevel(level);

	}

	public static void applyRowStyle(TableLayout tableLayout) {
		int rowIndex = 0;
		for (int i = 0; i < tableLayout.getChildCount(); i++) {
			TableRow row = (TableRow) tableLayout.getChildAt(i);
			if (row.getVisibility() != View.GONE) {
				applyRowStyle(row, rowIndex++);
			}
		}
	}

	public static void applyRowStyle(View row, int position) {
		if (row == null)
			return;

		LevelListDrawable levelListDrawable;

		if (row.getBackground() instanceof LevelListDrawable) {
			levelListDrawable = (LevelListDrawable) row.getBackground();
		} else {
			row.setBackgroundResource(Util.getThemeResourceId(row.getContext(), R.attr.listItemBackground));
			levelListDrawable = (LevelListDrawable) row.getBackground();
		}

		levelListDrawable.setLevel(position % 2);
	}

	public static void setVisibility(View view, boolean visible) {
		setVisibility(view, visible, null);
	}

	public static void setVisibility(View view, boolean visible, View expander) {
		if (visible && view.getVisibility() != View.VISIBLE) {

			if (view.getVisibility() == View.GONE && expander != null) {
				// weight of text5 is added to text1 if invisible
				((LinearLayout.LayoutParams) expander.getLayoutParams()).weight -= ((LinearLayout.LayoutParams) view
						.getLayoutParams()).weight;
			}

			view.setVisibility(View.VISIBLE);

		}

		if (!visible && view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.GONE);
			if (expander != null) {
				// weight of text5 is added to text1 if invisible
				((LinearLayout.LayoutParams) expander.getLayoutParams()).weight += ((LinearLayout.LayoutParams) view
						.getLayoutParams()).weight;
			}
		}
	}

	public static Double parseDouble(String s) {
		if (s == null)
			return null;

		s = s.trim();

		if (s.length() == 0 || MINUS.equals(s) || NULL.equals(s))
			return null;

		Double i;

		if (s.startsWith(PLUS)) {
			s = s.substring(1);
		}

		try {
			i = effectFormat.parse(s).doubleValue();
		} catch (ParseException e) {
			i = Double.valueOf(s);
		}

		return i;
	}

	public static Float parseFloat(String s) {
		return parseFloat(s, null);
	}

	public static Float parseFloat(String s, Float defaultValue) {
		if (s == null)
			return defaultValue;

		s = s.trim();

		if (s.length() == 0 || MINUS.equals(s) || NULL.equals(s))
			return defaultValue;

		Float i;

		if (s.startsWith(PLUS)) {
			s = s.substring(1);
		}

		try {
			i = effectFormat.parse(s).floatValue();
		} catch (ParseException e) {
			i = Float.valueOf(s);
		}

		return i;
	}

	public static void setTextColor(TextView tf, Value value, int modifier) {
		if (value.getValue() != null) {
			if (modifier < 0 || (value.getReferenceValue() != null && value.getValue() < value.getReferenceValue()))
				tf.setTextColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed));
			else if (modifier > 0
					|| (value.getReferenceValue() != null && value.getValue() > value.getReferenceValue()))
				tf.setTextColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen));
			else {
				tf.setTextColor(getThemeColors(tf.getContext(), android.R.attr.textColorPrimary));
			}
		} else {
			tf.setTextColor(getThemeColors(tf.getContext(), android.R.attr.textColorPrimary));
		}
	}

	public static int getThemeColors(Context context, int attr) {
		if (getThemeResourceId(context, attr) != 0)
			return context.getResources().getColor(getThemeResourceId(context, attr));
		else
			return 0;
	}

	public static int getThemeResourceId(Context context, int attr) {
		TypedValue typedvalueattr = new TypedValue();
		if (context.getTheme().resolveAttribute(attr, typedvalueattr, true)) {
			return typedvalueattr.resourceId;
		} else {
			return 0;
		}
	}

	public static void setTextColor(TextView tf, int modifier) {

		if (modifier == 0) {
			tf.setTextColor(getThemeColors(tf.getContext(), android.R.attr.textColorPrimary));
		} else if (modifier < 0)
			tf.setTextColor(tf.getResources().getColor(R.color.ValueRed));
		else if (modifier > 0)
			tf.setTextColor(tf.getResources().getColor(R.color.ValueGreen));

	}

	public static void setText(TextView tf, Value value) {
		setText(tf, value, null);
	}

	public static void setText(TextView tf, Value value, String prefix) {
		setText(tf, value != null ? value.getValue() : null, 0, prefix);
	}

	public static void setText(TextView tf, Value value, int modifier, String prefix) {
		setText(tf, value != null ? value.getValue() : null, modifier, prefix);
	}

	public static void setText(TextView tf, Integer value, int modifier, String prefix) {
		if (tf != null) {
			if (value != null) {

				value += modifier;

				if (prefix != null) {
					tf.setText(prefix);
					tf.append(Util.toString(value));
				} else
					tf.setText(Util.toString(value));
			} else {
				tf.setText(null);
			}
			setTextColor(tf, modifier);
		}
	}

	public static void setValue(AbstractBeing being, TextView tv, Attribute attribute, String prefix,
			boolean includeBe, ProbeListener probeListener, EditListener editListener) {
		if (attribute != null) {

			int modifier = being.getModifier(attribute, includeBe, true);
			Util.setText(tv, attribute, modifier, prefix);
			tv.setTag(attribute);

			if (!tv.isLongClickable()) {

				if (attribute.getType().probable()) {
					tv.setOnClickListener(probeListener);
				} else if (attribute.getType().editable()) {
					tv.setOnClickListener(editListener);
				}

				if (attribute.getType().editable())
					tv.setOnLongClickListener(editListener);
			}
		} else {
			tv.setText(null);
		}
	}

	public static void setLabel(View tv, AttributeType type, ProbeListener probeListener, EditListener editListener) {
		if (!tv.isLongClickable()) {
			if (type == AttributeType.Behinderung || type == AttributeType.Sozialstatus
					|| type == AttributeType.Magieresistenz) {
				tv.setOnClickListener(editListener);
			} else if (type.probable()) {
				tv.setOnClickListener(probeListener);
			}
			tv.setOnLongClickListener(editListener);
		}
		tv.setTag(type);
	}

	public static void appendValue(AbstractBeing being, StyleableSpannableStringBuilder title, AttributeType type) {

		Attribute attr = being.getAttribute(type);
		if (attr != null && attr.getValue() != null) {
			int modifier = being.getModifier(attr);

			int color = Color.TRANSPARENT;
			if (modifier < 0)
				color = DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed);
			else if (modifier > 0)
				color = DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen);

			title.append(" (");

			if (color != Color.TRANSPARENT)
				title.appendColor(color, Util.toString(attr.getValue() + modifier));
			else
				title.append(Util.toString(attr.getValue() + modifier));

			title.append(")");
		}

	}

	public static void appendValue(AbstractBeing being, StyleableSpannableStringBuilder title, Probe probe1,
			Probe probe2, boolean includeModifiers) {

		Integer value1 = null, value2 = null;

		if (probe1 != null)
			value1 = probe1.getValue();

		if (probe2 != null)
			value2 = probe2.getValue();

		if (value1 != null || value2 != null)
			title.append(" (");

		if (value1 != null) {
			int modifier = 0;
			int color = Color.TRANSPARENT;
			if (includeModifiers) {
				modifier = being.getModifier(probe1);

				if (modifier < 0)
					color = DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed);
				else if (modifier > 0)
					color = DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen);
			}
			if (color != Color.TRANSPARENT)
				title.appendColor(color, Util.toString(value1 + modifier));
			else
				title.append(Util.toString(value1 + modifier));
		}

		if (value2 != null) {

			if (value1 != null)
				title.append("/");

			int modifier = 0;
			int color = Color.TRANSPARENT;
			if (includeModifiers) {
				modifier = being.getModifier(probe2);

				if (modifier < 0)
					color = DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed);
				else if (modifier > 0)
					color = DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen);
			}

			if (color != Color.TRANSPARENT)
				title.appendColor(color, Util.toString(value2 + modifier));
			else
				title.append(Util.toString(value2 + modifier));
		}

		if (value1 != null || value2 != null)
			title.append(")");
	}

	/*
	 * public static double getProbePercentage(Probe probe, int modifier) {
	 * 
	 * double v = 0;
	 * 
	 * int t = 0; if (probe.getProbeBonus() != null) { t = probe.getProbeBonus() + modifier; } else t = modifier;
	 * 
	 * Debug.verbose("T=" + t);
	 * 
	 * // see Wege des Meisters page 170 if (t <= 0) { v = Math.min(20, probe.getProbeValue(0) + t); for (int i = 1; i <
	 * 3; i++) { v *= Math.min(20, probe.getProbeValue(i) + t); } } else {
	 * 
	 * v = Math.min(20, probe.getProbeValue(0)); for (int i = 1; i < 3; i++) { v *= Math.min(20,
	 * probe.getProbeValue(i)); }
	 * 
	 * // E i=1 - 3 for (int i = 0; i < 3; i++) {
	 * 
	 * int ti = Math.min(20 - probe.getProbeValue(i), t);
	 * 
	 * Debug.verbose("T" + i + "=" + ti); // E n=1 - Ti for (int n = 1; n <= ti; n++) { v += (Math.min(20,
	 * probe.getProbeValue(i % 3) - n) * Math.min(20, probe.getProbeValue((i + 1) % 3) - n)); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return v / 8000; }
	 */

	public static AttributeType[] splitProbeString(String probe) {
		if (probe == null)
			return null;

		probe = probe.trim();
		if (probe.startsWith("("))
			probe = probe.substring(1);

		if (probe.endsWith(")"))
			probe = probe.substring(0, probe.length() - 1);

		String[] probes = probe.split("/");

		AttributeType[] types = new AttributeType[probes.length];

		for (int i = 0; i < probes.length; i++) {
			types[i] = AttributeType.byCode(probes[i]);
		}
		return types;
	}

	public static String[] splitDistanceString(String distance) {
		if (distance == null)
			return null;
		distance = distance.trim();
		if (distance.startsWith("("))
			distance = distance.substring(1);

		if (distance.endsWith(")"))
			distance = distance.substring(0, distance.length() - 1);

		String[] distances = distance.split("/");

		return distances;
	}

	public static String toString(Integer wmAt) {
		if (wmAt != null)
			return Integer.toString(wmAt);
		else
			return MINUS;

	}

	public static String toString(Long wmAt) {
		if (wmAt != null)
			return Long.toString(wmAt);
		else
			return MINUS;

	}

	public static String toString(Double value) {
		if (value != null)
			return effectFormat.format(value);
		else
			return MINUS;
	}

	public static String toString(Float value) {
		if (value != null)
			return effectFormat.format(value);
		else
			return MINUS;
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String toProbe(Integer value) {
		if (value != null)
			return (value >= 0 ? "+" : "") + Integer.toString(value);
		else
			return "";

	}

	static class EquippedItemComparator implements Comparator<EquippedItem> {

		private static final List<String> SORT_ORDER = Arrays.asList("fk", "nk", "sc", "ja", "ru");

		@Override
		public int compare(EquippedItem object1, EquippedItem object2) {

			String name1 = object1.getName();
			String name2 = object2.getName();

			int index1 = SORT_ORDER.indexOf(name1.substring(0, 2));
			int index2 = SORT_ORDER.indexOf(name2.substring(0, 2));

			int compare = Integer.valueOf(index1).compareTo(index2) * 10000
					+ object1.getName().compareTo(object2.getName());
			return compare;
		}

	}

	static class ItemComparator implements Comparator<Item> {

		@Override
		public int compare(Item object1, Item object2) {

			TalentType type1 = null, type2 = null;
			String atype1 = null, atype2 = null;

			for (ItemSpecification itemSpecification : object1.getSpecifications()) {
				if (itemSpecification instanceof Weapon) {
					type1 = ((Weapon) itemSpecification).getTalentType();
					break;
				} else if (itemSpecification instanceof DistanceWeapon) {
					type1 = ((DistanceWeapon) itemSpecification).getTalentType();
					break;
				} else if (itemSpecification instanceof Armor) {
					atype1 = object1.getCategory();
					break;
				}
			}

			for (ItemSpecification itemSpecification : object2.getSpecifications()) {
				if (itemSpecification instanceof Weapon) {
					type2 = ((Weapon) itemSpecification).getTalentType();
					break;
				} else if (itemSpecification instanceof DistanceWeapon) {
					type2 = ((DistanceWeapon) itemSpecification).getTalentType();
					break;
				} else if (itemSpecification instanceof Armor) {
					atype2 = object1.getCategory();
					break;
				}
			}

			int compareType = 0;
			if (type1 != null && type2 != null)
				compareType = type1.compareTo(type2);

			if (atype1 != null && atype2 != null)
				compareType = atype1.compareTo(atype2);

			int compareName = object1.getName().compareTo(object2.getName());
			return compareType * 10000 + compareName;
		}
	}

	public static List<EquippedItem> sort(List<EquippedItem> equippedItems) {

		Collections.sort(equippedItems, new EquippedItemComparator());

		for (int i = 0; i < equippedItems.size(); i++) {

			EquippedItem equippedItem = equippedItems.get(i);

			if (equippedItem.getItemSpecification() instanceof Weapon && equippedItem.getSecondaryItem() != null) {
				EquippedItem secondaryEquippedItem = equippedItem.getSecondaryItem();

				if (secondaryEquippedItem.getItemSpecification() instanceof Shield
						|| (secondaryEquippedItem.getItemSpecification() instanceof Weapon && secondaryEquippedItem
								.getHand() == Hand.links)) {
					equippedItems.remove(secondaryEquippedItem);

					int index = equippedItems.indexOf(equippedItem);
					equippedItems.add(index + 1, secondaryEquippedItem);
				}
			}
		}

		return equippedItems;
	}

	public static void sortItems(List<Item> items) {
		Collections.sort(items, new ItemComparator());
	}

	public static CombatTalent getBest(List<CombatTalent> combatTalents) {
		CombatTalent result = null;
		int bestWeight = 0;
		int weight;
		for (CombatTalent combatTalent : combatTalents) {
			weight = 0;
			if (combatTalent.getAttack() != null && combatTalent.getAttack().getValue() != null)
				weight += combatTalent.getAttack().getValue();
			if (combatTalent.getDefense() != null && combatTalent.getDefense().getValue() != null)
				weight += combatTalent.getDefense().getValue();

			if (result == null || weight > bestWeight) {
				bestWeight = weight;
				result = combatTalent;
			}
		}

		return result;

	}

	/**
	 * @param next
	 * @return
	 */
	public static int gradeToInt(String next) {
		if (!TextUtils.isEmpty(next))
			return ROMANS.indexOf(next.toUpperCase(Locale.GERMAN));
		else
			return -1;
	}

	public static String intToGrade(int grade) {
		if (grade >= 0 && grade < ROMANS.size()) {
			return ROMANS.get(grade);
		} else {
			return Util.toString(grade);
		}
	}

	public static boolean equalsOrNull(Object o1, Object o2) {
		return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2));
	}

	public static String getValue(String value, String defaultValue) {
		if (TextUtils.isEmpty(value))
			return defaultValue;
		else
			return value;
	}

	public static Uri getUriForResourceId(int resId) {
		if (resId > 0) {
			Resources resources = DsaTabApplication.getInstance().getResources();
			return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId)
					+ '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId));
		} else {
			return null;
		}
	}

	public static void inflateAcceptAbortMenu(Context context, Menu menu) {

		MenuItem item = menu.add(Menu.NONE, R.id.option_accept, Menu.NONE, android.R.string.ok);
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM
				| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(context, R.attr.imgBarAccept));

		item = menu.add(Menu.NONE, R.id.option_cancel, Menu.NONE, android.R.string.cancel);
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM
				| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(context, R.attr.imgBarCancel));
	}

	public static InputStream openHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");

		HttpURLConnection httpConn = (HttpURLConnection) conn;
		httpConn.setAllowUserInteraction(false);
		httpConn.setInstanceFollowRedirects(true);
		httpConn.setReadTimeout(10000 /* milliseconds */);
		httpConn.setConnectTimeout(15000 /* milliseconds */);
		httpConn.setRequestMethod("GET");
		httpConn.connect();

		response = httpConn.getResponseCode();
		if (response == HttpURLConnection.HTTP_OK) {
			in = httpConn.getInputStream();
		}

		return in;
	}

	public static Bitmap retrieveBitmap(Context context, Intent data, int maxSize) {

		String filePath = retrieveBitmapPath(context, data);

		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				Bitmap yourSelectedImage = Util.decodeBitmap(file, maxSize);
				return yourSelectedImage;
			}
		}

		return null;
	}

	public static File saveBitmap(Bitmap pic, String photoName) {
		FileOutputStream fOut = null;
		try {
			fOut = DsaTabApplication.getInstance().openFileOutput(photoName, Context.MODE_PRIVATE);
			pic.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();

			File outputfile = DsaTabApplication.getInstance().getFileStreamPath(photoName);
			return outputfile;
		} catch (FileNotFoundException e) {
			Debug.error(e);
		} catch (IOException e) {
			Debug.error(e);
		} finally {
			if (fOut != null) {
				try {
					fOut.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static Uri retrieveBitmapUri(Context context, Intent data) {
		return data.getData();
	}

	public static String retrieveBitmapPath(Context context, Intent data) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaColumns.DATA };

		Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		}
		return null;
	}

	/**
	 * @param imageTextOverlay
	 * @return
	 */
	public static boolean parseBoolean(String value) {
		if (value == null)
			return false;
		else if ("1".equals(value))
			return true;
		else
			return Boolean.parseBoolean(value);

	}

	public static int getWidth(Activity activity) {
		// initialize the DisplayMetrics object
		DisplayMetrics deviceDisplayMetrics = new DisplayMetrics();

		// populate the DisplayMetrics object with the display characteristics
		activity.getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);

		// get the width and height
		return deviceDisplayMetrics.widthPixels;
	}

	public static int getHeight(Activity activity) {
		// initialize the DisplayMetrics object
		DisplayMetrics deviceDisplayMetrics = new DisplayMetrics();

		// populate the DisplayMetrics object with the display characteristics
		activity.getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);

		// get the width and height
		return deviceDisplayMetrics.heightPixels;
	}

	public static void putArray(JSONObject out, Collection<? extends JSONable> list, String name) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		int index = 0;
		Iterator<? extends JSONable> iter = list.iterator();
		while (iter.hasNext()) {
			JSONObject jsonObject = iter.next().toJSONObject();
			if (jsonObject != null) {
				jsonArray.put(index++, jsonObject);
			}
		}
		out.put(name, jsonArray);

	}

	public static void putStringArray(JSONObject out, Collection<String> list, String name) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		int index = 0;
		for (String value : list) {
			jsonArray.put(index++, value);
		}
		out.put(name, jsonArray);

	}

	public static void putEnumArray(JSONObject out, Collection<? extends Enum<?>> list, String name)
			throws JSONException {
		JSONArray jsonArray = new JSONArray();
		int index = 0;
		for (Enum<?> value : list) {
			jsonArray.put(index++, value.name());
		}
		out.put(name, jsonArray);
	}

	public void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static String slurp(final InputStream is, final int bufferSize) {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ex) {
			/* ... */
		} catch (IOException ex) {
			/* ... */
		}
		return out.toString();
	}

}

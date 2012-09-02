package com.WazaBe.HoloEverywhere;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class FontLoader {
	public enum HoloFont {
		ROBOTO_BOLD(R.raw.roboto_bold), ROBOTO_BOLD_ITALIC(
				R.raw.roboto_bolditalic), ROBOTO_ITALIC(R.raw.roboto_italic), ROBOTO_REGULAR(
				R.raw.roboto_regular, Build.VERSION.SDK_INT >= 11);

		private int font;
		private boolean ignore;

		private HoloFont(int font) {
			this(font, false);
		}

		private HoloFont(int font, boolean ignore) {
			this.font = font;
			this.ignore = ignore;
		}
	}

	private static final SparseArray<Typeface> fontArray = new SparseArray<Typeface>();

	@Deprecated
	private static final Map<String, Typeface> fontMapOld = new HashMap<String, Typeface>();

	@Deprecated
	public static final String ROBOTO_REGULAR = "Roboto-Regular.ttf";

	private static final String TAG = "FontLoader";

	public static View inflate(Context context, int res) {
		return inflate(context, res, null);
	}

	public static View inflate(Context context, int res, ViewGroup parent) {
		return loadFont(View.inflate(context, res, parent));
	}

	@Deprecated
	public static void loadFont(TextView view, String font) {
		if (Build.VERSION.SDK_INT >= 14 || view == null
				|| view.getContext() == null) {
			return;
		}
		Typeface typeface = loadTypeface(view.getContext(), font);
		if (typeface != null) {
			view.setTypeface(typeface);
		}
	}

	public static View loadFont(View view) {
		return loadFont(view, HoloFont.ROBOTO_REGULAR);
	}

	public static View loadFont(View view, HoloFont font) {
		if (font.ignore) {
			return view;
		}
		return loadFont(view, font.font);
	}

	@SuppressLint("NewApi")
	public static View loadFont(View view, int font) {
		if (view == null || view.getContext() == null
				|| view.getContext().isRestricted()) {
			Log.e(TAG, "View or context is invalid");
			return view;
		}
		if (fontArray.get(font) == null) {
			try {
				File file = new File(Environment.getDataDirectory(), "data/"
						+ view.getContext().getPackageName() + "/fonts");
				if (!file.exists()) {
					file.mkdirs();
				}
				file = new File(file, String.valueOf(font));
				if (file.exists()) {
					file.delete();
				}
				Resources res = view.getContext().getResources();
				InputStream is = res.openRawResource(font);
				OutputStream os = new FileOutputStream(file);
				byte[] buffer = new byte[8192];
				int read;
				while ((read = is.read(buffer)) > 0) {
					os.write(buffer, 0, read);
				}
				os.flush();
				os.close();
				is.close();
				fontArray.put(font, Typeface.createFromFile(file));
			} catch (Exception e) {
				Log.e(TAG, "Error of loading font", e);
			}
		}
		Typeface typeface = fontArray.get(font);
		if (typeface == null) {
			Log.v(TAG, "Font " + font + " not found in resources");
			return view;
		} else {
			return loadFont(view, typeface);
		}
	}

	public static View loadFont(View view, Typeface typeface) {
		if (view == null || view.getContext() == null
				|| view.getContext().isRestricted()) {
			return view;
		}
		if (typeface == null) {
			Log.v(TAG, "Font is null");
			return view;
		}
		try {
			((TextView) view).setTypeface(typeface);
		} catch (ClassCastException e) {
		}
		try {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				loadFont(group.getChildAt(i), typeface);
			}
		} catch (ClassCastException e) {
		}
		return view;
	}

	@Deprecated
	private static Typeface loadTypeface(Context ctx, String font) {
		if (!FontLoader.fontMapOld.containsKey(font)) {
			try {
				Typeface typeface = Typeface.createFromAsset(ctx.getAssets(),
						font);
				FontLoader.fontMapOld.put(font, typeface);
			} catch (Exception e) {
				Log.w("FontLoader", "Error loading font " + font
						+ " from assets. Error: " + e.getMessage());
			}
		}
		return FontLoader.fontMapOld.get(font);
	}

	private FontLoader() {
	}
}

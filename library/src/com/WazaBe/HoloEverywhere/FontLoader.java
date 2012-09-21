package com.WazaBe.HoloEverywhere;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class FontLoader {
	public static class HoloFont {
		public static final HoloFont ROBOTO = new HoloFont(-1);
		public static final HoloFont ROBOTO_BOLD = new HoloFont(
				R.raw.roboto_bold);
		public static final HoloFont ROBOTO_BOLD_ITALIC = new HoloFont(
				R.raw.roboto_bolditalic);
		public static final HoloFont ROBOTO_ITALIC = new HoloFont(
				R.raw.roboto_italic);
		public static final HoloFont ROBOTO_REGULAR = new HoloFont(
				R.raw.roboto_regular);

		protected final int font;
		protected final boolean ignore;

		private HoloFont(int font) {
			this(font, VERSION.SDK_INT >= 11);
		}

		private HoloFont(int font, boolean ignore) {
			this.font = font;
			this.ignore = ignore;
		}
	}

	private static final SparseArray<Typeface> fontArray = new SparseArray<Typeface>();
	private static final String TAG = "FontLoader";

	public static View apply(View view) {
		return applyDefaultStyles(view);
	}

	public static View apply(View view, HoloFont font) {
		if (font.ignore) {
			return view;
		}
		return apply(view, font.font);
	}

	@SuppressLint("NewApi")
	public static View apply(View view, int font) {
		if (view == null || view.getContext() == null
				|| view.getContext().isRestricted()) {
			Log.e(TAG, "View or context is invalid");
			return view;
		}
		if (font < 0) {
			return applyDefaultStyles(view);
		}
		Typeface typeface = loadTypeface(view.getContext(), font);
		if (typeface == null) {
			Log.v(TAG, "Font " + font + " not found in resources");
			return view;
		} else {
			return apply(view, typeface);
		}
	}

	public static View apply(View view, Typeface typeface) {
		if (view == null || view.getContext() == null
				|| view.getContext().isRestricted()) {
			return view;
		}
		if (typeface == null) {
			Log.v(TAG, "Font is null");
			return view;
		}
		if (view instanceof TextView) {
			((TextView) view).setTypeface(typeface);
		}
		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				apply(group.getChildAt(i), typeface);
			}
		}
		return view;
	}

	public static View applyDefaultStyles(View view) {
		if (view == null || view.getContext() == null
				|| view.getContext().isRestricted()) {
			return view;
		}
		if (view instanceof TextView) {
			TextView text = (TextView) view;
			Typeface typeface = text.getTypeface();
			if (typeface == null) {
				text.setTypeface(loadTypeface(view.getContext(),
						HoloFont.ROBOTO_REGULAR.font));
				return view;
			}
			HoloFont font;
			boolean isBold = typeface.isBold(), isItalic = typeface.isItalic();
			if (isBold && isItalic) {
				font = HoloFont.ROBOTO_BOLD_ITALIC;
			} else if (isBold && !isItalic) {
				font = HoloFont.ROBOTO_BOLD;
			} else if (!isBold && isItalic) {
				font = HoloFont.ROBOTO_ITALIC;
			} else {
				font = HoloFont.ROBOTO_REGULAR;
			}
			if (!font.ignore) {
				typeface = loadTypeface(view.getContext(), font.font);
				if (typeface != null) {
					text.setTypeface(typeface);
				}
			}
		}
		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				applyDefaultStyles(group.getChildAt(i));
			}
		}
		return view;
	}

	public static View inflate(Context context, int res) {
		return apply(LayoutInflater.inflate(context, res));
	}

	public static View inflate(Context context, int res, ViewGroup parent) {
		return apply(LayoutInflater.inflate(context, res, parent));
	}

	public static Typeface loadTypeface(Context context, int font) {
		if (fontArray.get(font) == null) {
			try {
				File file = new File(Environment.getDataDirectory(), "data/"
						+ context.getPackageName() + "/fonts");
				if (!file.exists()) {
					file.mkdirs();
				}
				file = new File(file, Integer.toHexString(font));
				if (file.exists()) {
					file.delete();
				}
				Resources res = context.getResources();
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
		return fontArray.get(font);
	}

	private FontLoader() {
	}
}

package android.support.v4.app;

import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.WazaBe.HoloEverywhere.SystemServiceManager.SuperSystemService;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class _HoloActivity extends Watson implements
		ContextMenuListener, SuperSystemService {
	protected final String TAG = getClass().getSimpleName();

	@Override
	public MenuInflater getSupportMenuInflater() {
		return null;
	}

	@Override
	public final boolean onContextItemSelected(android.view.MenuItem item) {
		return onContextItemSelected(new ContextMenuItemWrapper(item));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item instanceof ContextMenuItemWrapper) {
			return super.onContextItemSelected(((ContextMenuItemWrapper) item)
					.unwrap());
		}
		return false;
	}

	@Override
	public final void onContextMenuClosed(android.view.Menu menu) {
		if (menu instanceof android.view.ContextMenu) {
			onContextMenuClosed(new ContextMenuWrapper(
					(android.view.ContextMenu) menu));
		} else {
			Log.w(TAG, "onContextMenuClosed: menu is not ContextMenu instance");
			super.onContextMenuClosed(menu);
		}
	}

	@Override
	public void onContextMenuClosed(ContextMenu menu) {
		if (menu instanceof ContextMenuWrapper) {
			super.onContextMenuClosed(((ContextMenuWrapper) menu).unwrap());
		}
	}

	@Override
	public final void onCreateContextMenu(android.view.ContextMenu menu,
			View v, ContextMenuInfo menuInfo) {
		onCreateContextMenu(new ContextMenuWrapper(menu), v, menuInfo);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		if (menu instanceof ContextMenuWrapper) {
			super.onCreateContextMenu(((ContextMenuWrapper) menu).unwrap(),
					view, menuInfo);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public Object superGetSystemService(String name) {
		return super.getSystemService(name);
	}
}

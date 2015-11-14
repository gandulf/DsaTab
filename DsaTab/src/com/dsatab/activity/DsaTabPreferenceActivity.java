package com.dsatab.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.fragment.preference.BasePreferenceFragment;
import com.wnafee.vector.compat.ResourcesCompat;

import java.util.List;

public class DsaTabPreferenceActivity extends AppCompatPreferenceActivity implements BasePreferenceFragment.DsaTabSettings {

    private static class HeaderAdapter extends ArrayAdapter<Header> {
        private static class HeaderViewHolder {
            ImageView icon;
            TextView title;
            TextView summary;
        }

        private LayoutInflater mInflater;
        private int mLayoutResId;
        private boolean mRemoveIconIfEmpty;

        public HeaderAdapter(Context context, List<Header> objects, int layoutResId,
                             boolean removeIconBehavior) {
            super(context, 0, objects);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayoutResId = layoutResId;
            mRemoveIconIfEmpty = removeIconBehavior;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            View view;

            if (convertView == null) {
                view = mInflater.inflate(mLayoutResId, parent, false);
                holder = new HeaderViewHolder();
                holder.icon = (ImageView) view.findViewById(android.R.id.icon);
                holder.title = (TextView) view.findViewById(android.R.id.title);
                holder.summary = (TextView) view.findViewById(android.R.id.summary);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }

            // All view fields must be updated every time, because the view may be recycled
            Header header = getItem(position);
            if (mRemoveIconIfEmpty) {
                if (header.iconRes == 0) {
                    holder.icon.setVisibility(View.GONE);
                } else {
                    holder.icon.setVisibility(View.VISIBLE);
                    holder.icon.setImageDrawable(ResourcesCompat.getDrawable(getContext(), header.iconRes));
                }
            } else {
                holder.icon.setImageDrawable(ResourcesCompat.getDrawable(getContext(), header.iconRes));
            }
            holder.title.setText(header.getTitle(getContext().getResources()));
            CharSequence summary = header.getSummary(getContext().getResources());
            if (!TextUtils.isEmpty(summary)) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(summary);
            } else {
                holder.summary.setVisibility(View.GONE);
            }

            return view;
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomPreferencesTheme());
		super.onCreate(savedInstanceState);


	}

	/**
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(com.dsatab.R.xml.preferences_headers, target);

		setContentView(R.layout.main_preferences);

		ActionBar bar = getSupportActionBar();
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		bar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_OK);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected boolean isValidFragment(String fragmentName) {
		return fragmentName.startsWith(DsaTabApplication.getInstance().getPackageName());
	}

}
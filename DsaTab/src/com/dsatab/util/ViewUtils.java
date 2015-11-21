package com.dsatab.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dsatab.R;
import com.gandulf.guilib.util.ResUtil;
import com.wnafee.vector.compat.ResourcesCompat;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class ViewUtils {

    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    public static Drawable circleIcon(Context context, Uri drawableUri) {
        Drawable drawable = ResUtil.getDrawableByUri(context, drawableUri);
        if (drawable!=null) {
            drawable = drawable.mutate();
            int circleTint = Util.getThemeColors(context, R.attr.circleTint);
            android.support.v4.graphics.drawable.DrawableCompat.setTint(drawable,circleTint);
        }
        return drawable;
    }
    public static Drawable circleIcon(Context context, int drawableId) {
        Drawable drawable = ResourcesCompat.getDrawable(context, drawableId);
        if (drawable!=null) {
            drawable = drawable.mutate();
            int circleTint = Util.getThemeColors(context, R.attr.circleTint);
            android.support.v4.graphics.drawable.DrawableCompat.setTint(drawable, circleTint);
        }
        return drawable;
    }

    public static Drawable icon(Context context, MaterialDrawableBuilder.IconValue iconValue) {
        return toolbarIcon(context, iconValue);
    }


    public static Drawable toolbarIcon(Context context, MaterialDrawableBuilder.IconValue iconValue) {
        int iconColor = Util.getThemeColors(context, R.attr.colorControlNormal);
        return MaterialDrawableBuilder.with(context).setIcon(iconValue).setToActionbarSize().setColor(iconColor).build();
    }

    public static Drawable fabIcon(Context context, MaterialDrawableBuilder.IconValue iconValue) {
        int iconColor = Util.getThemeColors(context, R.attr.colorControlNormal);
        return MaterialDrawableBuilder.with(context).setIcon(iconValue).setToActionbarSize().setColor(iconColor).build();
    }

    public static void menuIcon(Context context, Menu menu, int menuItemId, MaterialDrawableBuilder.IconValue iconValue) {
        MenuItem item = menu.findItem(menuItemId);
        if (item != null && item.getIcon() == null) {
            item.setIcon(ViewUtils.toolbarIcon(context, iconValue));
        }
    }

    public static void snackbar(Activity actitivity, int textId, int duration, Object... arguments) {
        snackbar(actitivity, actitivity.getString(textId, arguments), duration);
    }

    public static void snackbar(Activity actitivity, CharSequence text) {
        snackbar(actitivity, text, Snackbar.LENGTH_SHORT);
    }

    public static void snackbar(Activity actitivity, CharSequence text, int duration) {
        if (actitivity != null) {
            View rootView = actitivity.getWindow().getDecorView().getRootView();
            View contentView = rootView.findViewById(android.R.id.content);
            Snackbar.make(contentView !=null ? contentView: rootView, text, duration).show();
        } else {
            com.gandulf.guilib.util.Debug.warning(text.toString());
        }
    }

    public static void snackbar(Fragment fragment, int textId, int duration, Object... arguments) {
        snackbar(fragment, fragment.getResources().getString(textId, arguments), duration);
    }

    public static void snackbar(Fragment actitivity, CharSequence text, int duration) {
        if (actitivity != null) {
            View contentView = actitivity.getView().findViewById(android.R.id.content);
            Snackbar.make(contentView !=null ? contentView: actitivity.getView(), text, duration).show();
        } else {
            com.gandulf.guilib.util.Debug.warning(text.toString());
        }
    }

    /**
     *
     * @param menuItem
     * @param enabled
     * @return true if state changed, else false
     */
    public static boolean menuIconState(MenuItem menuItem, boolean enabled) {
        if (menuItem == null)
            return false;

        boolean changed = menuItem.isEnabled() != enabled;
        menuItem.setEnabled(enabled);
        Drawable icon = menuItem.getIcon();
        if (icon != null) {
            if (enabled)
                icon.setAlpha(255);
            else
                icon.setAlpha(100);
        }

        return changed;
    }

    public static void menuIcons(Context context, Menu menu) {
        ViewUtils.menuIcon(context, menu, R.id.option_about, MaterialDrawableBuilder.IconValue.INFORMATION_OUTLINE);
        ViewUtils.menuIcon(context, menu, R.id.option_donate, MaterialDrawableBuilder.IconValue.GIFT);

        ViewUtils.menuIcon(context, menu, R.id.option_move, MaterialDrawableBuilder.IconValue.SWAP_HORIZONTAL);
        ViewUtils.menuIcon(context, menu, R.id.option_equipped, MaterialDrawableBuilder.IconValue.HANGER);

        ViewUtils.menuIcon(context, menu, R.id.option_add, MaterialDrawableBuilder.IconValue.PLUS_CIRCLE);

        ViewUtils.menuIcon(context, menu, R.id.option_mark, MaterialDrawableBuilder.IconValue.STAR);

        ViewUtils.menuIcon(context, menu, R.id.option_assign_hunting, MaterialDrawableBuilder.IconValue.STAR);

        ViewUtils.menuIcon(context, menu, R.id.option_mark_favorite, MaterialDrawableBuilder.IconValue.STAR);
        ViewUtils.menuIcon(context, menu, R.id.option_unmark, MaterialDrawableBuilder.IconValue.CLOSE_CIRCLE_OUTLINE);
        ViewUtils.menuIcon(context, menu, R.id.option_mark_unused, MaterialDrawableBuilder.IconValue.STAR_OUTLINE);

        ViewUtils.menuIcon(context, menu, R.id.option_edit, MaterialDrawableBuilder.IconValue.PENCIL);


        ViewUtils.menuIcon(context, menu, R.id.option_unassign, MaterialDrawableBuilder.IconValue.CLOSE_CIRCLE_OUTLINE);

        ViewUtils.menuIcon(context, menu, R.id.option_delete, MaterialDrawableBuilder.IconValue.DELETE);

        ViewUtils.menuIcon(context, menu, R.id.option_cancel, MaterialDrawableBuilder.IconValue.CLOSE);
        ViewUtils.menuIcon(context, menu, R.id.option_ok, MaterialDrawableBuilder.IconValue.CHECK);

        ViewUtils.menuIcon(context, menu, R.id.option_refresh, MaterialDrawableBuilder.IconValue.REFRESH);
        ViewUtils.menuIcon(context, menu, R.id.option_view, MaterialDrawableBuilder.IconValue.EYE);
        ViewUtils.menuIcon(context, menu, R.id.option_pick_image, MaterialDrawableBuilder.IconValue.EYE);

        ViewUtils.menuIcon(context, menu, R.id.option_assign_secondary, MaterialDrawableBuilder.IconValue.SHARE_VARIANT);
        ViewUtils.menuIcon(context, menu, R.id.option_select_version, MaterialDrawableBuilder.IconValue.SHARE_VARIANT);
        ViewUtils.menuIcon(context, menu, R.id.option_select_talent, MaterialDrawableBuilder.IconValue.SHARE_VARIANT);

        ViewUtils.menuIcon(context, menu, R.id.option_take_photo, MaterialDrawableBuilder.IconValue.CAMERA);


        ViewUtils.menuIcon(context, menu, R.id.option_documents_choose, MaterialDrawableBuilder.IconValue.FILE_DOCUMENT);

        ViewUtils.menuIcon(context, menu, R.id.option_connect, MaterialDrawableBuilder.IconValue.CLOUD);

        ViewUtils.menuIcon(context, menu, R.id.option_load_example_heroes, MaterialDrawableBuilder.IconValue.ACCOUNT_STAR_VARIANT);
        ViewUtils.menuIcon(context, menu, R.id.option_pick_avatar, MaterialDrawableBuilder.IconValue.ACCOUNT_BOX);
        ViewUtils.menuIcon(context, menu, R.id.option_download_avatars, MaterialDrawableBuilder.IconValue.ACCOUNT_NETWORK);

        ViewUtils.menuIcon(context, menu, R.id.option_filter, MaterialDrawableBuilder.IconValue.FILTER);
        ViewUtils.menuIcon(context, menu, R.id.option_note_filter, MaterialDrawableBuilder.IconValue.FILTER);

        ViewUtils.menuIcon(context, menu, R.id.option_save_hero, MaterialDrawableBuilder.IconValue.CONTENT_SAVE);

        ViewUtils.menuIcon(context, menu, R.id.option_search, MaterialDrawableBuilder.IconValue.MAGNIFY);

        ViewUtils.menuIcon(context, menu, R.id.option_itemgrid_type_list, MaterialDrawableBuilder.IconValue.VIEW_LIST);
        ViewUtils.menuIcon(context, menu, R.id.option_itemgrid_type_grid, MaterialDrawableBuilder.IconValue.VIEW_GRID);

        ViewUtils.menuIcon(context, menu, R.id.option_take_hit, MaterialDrawableBuilder.IconValue.HEART_BROKEN);
        ViewUtils.menuIcon(context, menu, R.id.option_list_items, MaterialDrawableBuilder.IconValue.TSHIRT_CREW);

        ViewUtils.menuIcon(context, menu, R.id.option_settings, MaterialDrawableBuilder.IconValue.SETTINGS);

    }
}

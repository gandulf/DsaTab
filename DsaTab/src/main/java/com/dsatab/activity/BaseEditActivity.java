package com.dsatab.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dsatab.R;
import com.dsatab.fragment.EditFragment;
import com.gandulf.guilib.util.Debug;

public class BaseEditActivity extends BaseActivity {

    public static final String EDIT_FRAGMENT_CLASS = "editFragmentClass";
    public static final String EDIT_TITLE = "editTitle";

    private Fragment fragment;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_blank);

        if (getIntent() != null || getIntent().hasExtra(EDIT_TITLE)) {
            setToolbarTitle(getIntent().getStringExtra(EDIT_TITLE));
        }

        try {

            if (getIntent() == null || !getIntent().hasExtra(EDIT_FRAGMENT_CLASS)) {
                throw new IllegalArgumentException("Called BaseEditActivity without EDIT_FRAGMENT_CLASS in intent: "
                        + getIntent());
            }

            fragment = getSupportFragmentManager().findFragmentById(R.id.content);

            Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) getIntent()
                    .getExtras().getSerializable(EDIT_FRAGMENT_CLASS);

            if (fragment != null && fragmentClass.isAssignableFrom(fragment.getClass())) {
                // all ok keep current fragment
            } else {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (fragment != null) {
                    ft.remove(fragment);
                }

                fragment = fragmentClass.newInstance();
                fragment.setArguments(getIntent().getExtras());
                ft.add(R.id.content, fragment);
                ft.commit();

            }
        } catch (InstantiationException e) {
            Debug.error(e);
            setResult(RESULT_CANCELED);
            finish();
            return;
        } catch (IllegalAccessException e) {
            Debug.error(e);
            setResult(RESULT_CANCELED);
            finish();
            return;
        }


        if (getIntent() != null) {
            if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
                inflateDone();
            } else {
                inflateSaveAndDiscard();
            }
        } else {
            inflateDone();
        }
    }

    public void inflateSaveAndDiscard() {
        super.inflateSaveAndDiscard(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public void inflateDone() {
        View.OnClickListener discardListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        };
        super.inflateDone(discardListener);
    }

    protected void cancel() {
        if (fragment instanceof EditFragment) {
            ((EditFragment) fragment).cancel();
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    protected boolean save() {
        Bundle data = null;
        if (fragment instanceof EditFragment) {
            data = ((EditFragment) fragment).accept();
        }
        if (data != null) {
            Intent intent = new Intent();
            intent.putExtras(data);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else {
            return false;
        }
    }


}

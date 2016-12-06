package com.dsatab.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import pl.tajchert.nammu.Nammu;


public class DsaTabIntro extends AppIntro {

    public static final String KEY_APP_INTRO = "APP_INTRO";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(first_fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest

        addSlide(AppIntroFragment.newInstance("Ein Held wird geboren...", "Kurzanleitung wie du deine Helden mit DsaTab nutzen kannst.", R.drawable.ai_dsatab, ResourcesCompat.getColor(getResources(), R.color.blue_grey_200, getTheme())));

        addSlide(AppIntroFragment.newInstance("www.helden-software.de", "Mit DsaTab selbst kannst du keine neuen Helden erstellen, zum Erstellen benutze bitte die Helden-Software (www.helden-software.de) auf deinem PC", R.drawable.ai_heldensoftware, ResourcesCompat.getColor(getResources(), R.color.blue_grey_300, getTheme())));

        addSlide(AppIntroFragment.newInstance("Xml-Export", "Exportiere dort dann deinen Helden im Xml-Format", R.drawable.ai_xmlexport, ResourcesCompat.getColor(getResources(), R.color.blue_grey_400, getTheme())));

        addSlide(AppIntroFragment.newInstance("Daten Austausch I", "Nun musst du die Xml-Datei auf dein Smartphone kopieren", R.drawable.ai_filetransfer, ResourcesCompat.getColor(getResources(), R.color.blue_grey_500, getTheme())));
        if (!Nammu.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
        }

        addSlide(AppIntroFragment.newInstance("Daten Austausch USB", "Du kannst die Datei entweder direkt mittels USB-Kabel nach \"" + DsaTabApplication.getExternalHeroPath() + "\" kopieren, oder ...", R.drawable.ai_usb, ResourcesCompat.getColor(getResources(), R.color.blue_grey_500, getTheme())));

        addSlide(AppIntroFragment.newInstance("Daten Austausch Cloud", "Lege ein Verzeichnis \"dsatab\" in deinem Dropbox oder Drive Stammverzeichnis an und kopiere die Datei hinein.", R.drawable.ai_cloudsync, ResourcesCompat.getColor(getResources(), R.color.blue_grey_500, getTheme())));
        if (!Nammu.hasPermission(this, Manifest.permission.INTERNET)) {
            askForPermissions(new String[]{Manifest.permission.INTERNET}, 6);
        }

        addSlide(AppIntroFragment.newInstance("Daten Austausch Cloud", "Danach musst du noch DsaTab mit deinem Account synchonisieren.", R.drawable.ai_cloudsync, ResourcesCompat.getColor(getResources(), R.color.blue_grey_600, getTheme())));

        // OPTIONAL METHODS
        // Override bar/separator color
        setBarColor(ResourcesCompat.getColor(getResources(), R.color.blue_grey_500, getTheme()));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        setImmersiveMode(true);
        // Hide Skip/Done button
        //showSkipButton(false);
        //showDoneButton(false);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        SharedPreferences.Editor edit = DsaTabApplication.getPreferences().edit();
        edit.putBoolean(KEY_APP_INTRO, false);
        edit.apply();

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        SharedPreferences.Editor edit = DsaTabApplication.getPreferences().edit();
        edit.putBoolean(KEY_APP_INTRO, false);
        edit.apply();

        finish();
    }
}

package com.dsatab.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.bingzer.android.driven.AbsRemoteFile;
import com.bingzer.android.driven.AbsStorageProvider;
import com.bingzer.android.driven.Credential;
import com.bingzer.android.driven.DefaultUserInfo;
import com.bingzer.android.driven.DrivenException;
import com.bingzer.android.driven.LocalFile;
import com.bingzer.android.driven.Permission;
import com.bingzer.android.driven.RemoteFile;
import com.bingzer.android.driven.Result;
import com.bingzer.android.driven.StorageProvider;
import com.bingzer.android.driven.UserInfo;
import com.bingzer.android.driven.contracts.Search;
import com.bingzer.android.driven.contracts.SharedWithMe;
import com.bingzer.android.driven.contracts.Sharing;
import com.bingzer.android.driven.contracts.Trashed;
import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.util.Util;

import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ganymedes on 21.11.2015.
 */
public class HeldenAustauschProvider extends AbsStorageProvider {

    class HeldenAustauschUserInfo extends DefaultUserInfo {

        HeldenAustauschUserInfo(String token){
            name = token;
            displayName = token;
        }

    }

    class HeldenAustauschRemoteFile extends AbsRemoteFile {

        private String name;
        private String heldenid;
        private String heldenKey;
        private String lastChange;

        protected HeldenAustauschRemoteFile(StorageProvider provider, String heldenid, String heldenKey,String name, String lastChange) {
            super(provider);

            this.name = name;
            this.heldenid = heldenid;
            this.heldenKey = heldenKey;
            this.lastChange = lastChange;
        }

        @Override
        public String getId() {
            return heldenid;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public String getName() {
            return name + HeroFileInfo.HERO_FILE_EXTENSION;
        }

        @Override
        public String getType() {
            return "text/xml";
        }

        @Override
        public String getDownloadUrl() {
            return null;
        }

        @Override
        public boolean hasDetails() {
            return false;
        }

        @Override
        public boolean fetchDetails() {
            return false;
        }

        @Override
        public RemoteFile create(String name) {
            return getStorageProvider().create(this, name);
        }

        @Override
        public RemoteFile create(LocalFile content) {
            return getStorageProvider().create(this, content);
        }

        @Override
        public RemoteFile get(String name) {
            return getStorageProvider().get(this, name);
        }

        @Override
        public List<RemoteFile> list() {
            return getStorageProvider().list(this);
        }

        @Override
        public boolean download(LocalFile local) {
            return getStorageProvider().download(this, local);
        }

        @Override
        public boolean upload(LocalFile local) {
            return consume(getStorageProvider().update(this, local));
        }

        @Override
        public String share(String user) {
            return getStorageProvider().getSharing().share(this, user);
        }

        @Override
        public String share(String user, int kind) {
            return getStorageProvider().getSharing().share(this, user, kind);
        }

        @Override
        public boolean delete() {
            return getStorageProvider().delete(getId());
        }

        @Override
        public boolean rename(String name) {
            return false;
        }

        private boolean consume(RemoteFile remoteFile){
            if(!(remoteFile instanceof HeldenAustauschRemoteFile))
                return false;
            HeldenAustauschRemoteFile other = (HeldenAustauschRemoteFile) remoteFile;

            this.heldenid = other.heldenid;
            this.heldenKey = other.heldenKey;
            this.name = other.name;
            this.lastChange = other.lastChange;
            return true;
        }
    }


    private static final Result<DrivenException> SUCCESS = new Result(true,null);

    private UserInfo userInfo;

    private List<RemoteFile> cachedRemoteFiles;


    public HeldenAustauschProvider() {


    }
    @Override
    public UserInfo getUserInfo() {
        if(!isAuthenticated()) throw new DrivenException("Driven API is not yet authenticated. Call authenticate() first");
        return userInfo;
    }

    @Override
    public boolean isAuthenticated() {
        return userInfo!=null;
    }

    @Override
    public Result<DrivenException> clearSavedCredential(Context context) {

        Credential credential = new Credential(context);
        credential.clear(getName());

        final SharedPreferences preferences = DsaTabApplication.getPreferences();
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN);
        edit.apply();
        return SUCCESS;
    }

    public void syncAuthentication(Context context) {
        String token = getToken();
        if (TextUtils.isEmpty(token))
            clearSavedCredential(context);
        else {
            Credential credential = new Credential(context);
            credential.setAccountName(token);
            authenticate(credential);
        }
    }

    @Override
    public Result<DrivenException> authenticate(Credential credential) {
        Result<DrivenException> result = new Result<DrivenException>(false);
        try {
            if(credential == null) throw new DrivenException(new IllegalArgumentException("credential cannot be null"));

            if(credential.hasSavedCredential(getName())){
                credential.read(getName());
            }

            final SharedPreferences preferences = DsaTabApplication.getPreferences();
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, credential.getAccountName());
            edit.apply();

            userInfo = new HeldenAustauschUserInfo(credential.getAccountName());

            Log.i(getName(), "HeldenAustausch successfully authenticated by Token: " + credential.getAccountName());

            credential.save(getName());
        }
        catch (Exception e) {
            Log.i(getName(), "Driven API failed to authenticate");
            Log.e(getName(), "Exception:", e);
            result.setException(new DrivenException(e));
        }
        return result;
    }

    @Override
    public boolean exists(String name) {
        return false;
    }

    @Override
    public boolean exists(RemoteFile parent, String name) {
        return false;
    }

    @Override
    public Permission getPermission(RemoteFile remoteFile) {
        return null;
    }

    @Override
    public RemoteFile get(RemoteFile parent, String name) {
        return null;
    }

    @Override
    public RemoteFile get(String name) {
        return null;
    }

    @Override
    public RemoteFile id(String id) {
        if (TextUtils.isEmpty(id))
            return null;

        if (cachedRemoteFiles != null) {
            for (RemoteFile file : cachedRemoteFiles) {
                if (id.equals(file.getId()))
                    return file;
            }
        }

        List<RemoteFile> files = list();
        for (RemoteFile file : files) {
            if (id.equals(file.getId()))
                return file;
        }
        return null;
    }

    @Override
    public RemoteFile getDetails(RemoteFile remoteFile) {
        return null;
    }

    @Override
    public List<RemoteFile> list() {
        if(!isAuthenticated()) throw new DrivenException("Driven API is not yet authenticated. Call authenticate() first");

        List<RemoteFile> files = new ArrayList<>();

        try {
            // HeldenListe anfordern
            String stringHeldenliste = Helper.postRequest(getToken(), "action", "listhelden");

            Document d = Helper.string2Doc(stringHeldenliste);

            // Anzahl der Helden bestimmen
            int anzahl = Helper.getDaten(d, "/helden/held").getLength();
            // Die Namen der Helden anzeigen

            for (int i = 1; i <= anzahl; i++) {
                String name = Helper.getDatenAsString(d, "/helden/held[" + i + "]/name");
                String heldenid = Helper.getDatenAsString(d, "/helden/held[" + i + "]/heldenid");
                String heldenKey = Helper.getDatenAsString(d, "/helden/held[" + i + "]/heldenkey");
                String lastChange = Helper.getDatenAsString(d, "/helden/held[" + i + "]/heldlastchange");

                HeldenAustauschRemoteFile remoteFile = new HeldenAustauschRemoteFile(this, heldenid, heldenKey, name, lastChange);
                files.add(remoteFile);
            }
        }catch (Exception e) {
            return null;
        }
        cachedRemoteFiles = files;

        return files;
    }

    @Override
    public List<RemoteFile> list(RemoteFile parent) {
        return list();
    }

    @Override
    public RemoteFile create(String name) {
        return null;
    }

    @Override
    public RemoteFile create(LocalFile local) {
        return null;
    }

    @Override
    public RemoteFile create(RemoteFile parent, String name) {
        return null;
    }

    @Override
    public RemoteFile create(RemoteFile parent, LocalFile local) {
        return null;
    }

    @Override
    public RemoteFile update(RemoteFile remoteFile, LocalFile content) {
        return null;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    private String getToken() {
        return DsaTabApplication.getPreferences().getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");
    }

    @Override
    public boolean download(RemoteFile remoteFile, LocalFile local) {
        if(!isAuthenticated()) throw new DrivenException("Driven API is not yet authenticated. Call authenticate() first");

        // nur für die Testphase, bis ein gültiges Zertifikate vorhanden ist
        Helper.disableSSLCheck();

        BufferedWriter bufferedOutputStream = null;
        try {
            String stringheld = Helper.postRequest(getToken(), "action", "returnheld", "format", "heldenxml", "heldenid",
                    remoteFile.getId());

            // Create a file output stream
            FileOutputStream out = new FileOutputStream(local.getFile());

            if (out == null) {
                throw new IOException("Unable to open outputstream: " + remoteFile);
            }
            bufferedOutputStream = new BufferedWriter(new OutputStreamWriter(out));
            bufferedOutputStream.write(stringheld);

            // Flush and close the buffers
            bufferedOutputStream.flush();
            return true;
        } catch (Exception e) {
            throw new DrivenException(e);
        } finally {
            Util.close(bufferedOutputStream);
        }
    }

    @Override
    public Search getSearch() {
        return null;
    }

    @Override
    public SharedWithMe getShared() {
        return null;
    }

    @Override
    public Sharing getSharing() {
        return null;
    }

    @Override
    public Trashed getTrashed() {
        return null;
    }

    @Override
    public String getName() {
        return "HeldenAustausch";
    }
}

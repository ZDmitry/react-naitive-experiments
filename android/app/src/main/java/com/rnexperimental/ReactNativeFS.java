package com.rnexperimental;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.Iterator;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;


public class ReactNativeFS extends ReactContextBaseJavaModule {

    static final String TAG = "ReactNativeFS";

    private Map<String, Object>    _storage  = new HashMap<>();
    private Map<String, ReactTask> _cmdQueue = new HashMap<>();


    ReactNativeFS(final ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        final Context context = this.getReactApplicationContext();

        final Map<String, Object> directories = new HashMap<>();

        directories.put("applicationDirectory", "file:///android_asset/");
        directories.put("applicationStorageDirectory", context.getFilesDir().getParentFile().getAbsolutePath());
        directories.put("dataDirectory",  context.getFilesDir().getAbsolutePath());
        directories.put("cacheDirectory", context.getCacheDir().getAbsolutePath());

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                directories.put("externalApplicationStorageDirectory", context.getExternalFilesDir(null).getParentFile().getAbsolutePath());
                directories.put("externalDataDirectory",  context.getExternalFilesDir(null).getAbsolutePath());
                directories.put("externalCacheDirectory", context.getExternalCacheDir().getAbsolutePath());
                directories.put("externalRootDirectory",  Environment.getExternalStorageDirectory().getAbsolutePath());
            } catch (Throwable e) {

            }
        }

        constants.put("DIRECTORY", directories);
        return constants;
    }

    @Override
    protected void finalize() throws Throwable {
        _destroy();
        _cmdQueue.clear();
        super.finalize();
    }

    void _destroy() {
        Iterator it = _storage.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            try {
                // clean
            } catch(Throwable e) {
                // ...
            } finally {
                it.remove();
            }
        }
    }

    @Override
    public String getName() {
        return ReactNativeFS.TAG;
    }

    private String _newObject(Object obj) {
        UUID uuid = UUID.randomUUID();
        _storage.put(uuid.toString(), obj);
        return uuid.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> T _getObject(final String uuid) throws Exception {
        if (!_storage.containsKey(uuid)) {
            throw new ReactException("ENULLPTR", "No such object with key \"" + uuid + "\"");
        }
        return (T) _storage.get(uuid);
    }

    private Boolean _delObject(final String uuid) {
        if (_storage.containsKey(uuid)) {
            _storage.remove(uuid);
            return true;
        }
        return false;
    }


}

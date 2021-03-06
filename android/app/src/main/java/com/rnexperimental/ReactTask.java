package com.rnexperimental;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;


abstract class ReactTask extends AsyncTask<Object, Void, WritableMap> {

    private String TAG = "ReactTask";

    private Callback     _callback;
    private ReactContext _context;

    ReactTask(ReactContext context, Callback callback) {
        _callback = callback;
        _context  = context;
    }

    @Override
    protected WritableMap doInBackground(Object... params) {
        return this.start();
    }

    abstract Object run()  throws Exception;
    abstract void finish();

    WritableMap start() {
        WritableMap jsResult;
        try {
            Object result = this.run();
            jsResult = this._returnJSResult(result);
        } catch (Exception e) {
            String      message = e.getMessage();
            WritableMap details = Arguments.createMap();

            if (e instanceof ReactException) {
                ReactException re = (ReactException)e;
                details = re.getDetails();
            }

            Log.d(TAG, message);
            jsResult = this._raiseJSException("ERNINT", message, details);
        } finally {
            if (!isCancelled()) {
                finish();
            }
        }

        return jsResult;
    }

    @Override
    protected void onPostExecute(final WritableMap result) {
        if (!_context.getCatalystInstance().isDestroyed()) {
            _callback.invoke(result);
        }
    }

    @Override
    protected void onCancelled() {
        if (!_context.getCatalystInstance().isDestroyed()) {
            WritableMap msg = this._raiseJSException("ERNINT", "Interrupted");
            _callback.invoke(msg);
        }
    }

    ReactTask startAsync() {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return this;
    }

    WritableMap _successResult(Boolean success) {
        WritableMap successMap = Arguments.createMap();
        successMap.putBoolean("success", success);
        return successMap;
    }

    WritableMap _raiseJSException(String code, String message) {
        return  _raiseJSException(code, message, null);
    }

    WritableMap _raiseJSException(String code, String message, WritableMap details) {
        if (details == null) {
            details = Arguments.createMap();
        }

        WritableMap errorMap = Arguments.createMap();

        errorMap.putString("code",    code);
        errorMap.putString("message", message);
        errorMap.putMap("details",    details);

        WritableMap resultMap = Arguments.createMap();
        resultMap.putMap("error", errorMap);

        return resultMap;
    }

    WritableMap _returnJSResult(Object result) {
        return ReactTask.writableMapFromObject("result", result);
    }

    static WritableMap writableMapFromObject(String key, Object obj) {
        WritableMap retval = Arguments.createMap();

        if (obj == null) {
            retval.putNull(key);
            return retval;
        }

        if (obj instanceof String) {
            retval.putString(key, (String)obj);
        } else if (obj instanceof Double) {
            retval.putDouble(key, (Double)obj);
        } else if (obj instanceof Boolean) {
            retval.putBoolean(key, (Boolean)obj);
        } else if (obj instanceof Integer) {
            retval.putInt(key, (Integer)obj);
        } else if (obj instanceof WritableMap) {
            retval.putMap(key, (WritableMap)obj);
        } else if (obj instanceof WritableArray) {
            retval.putArray(key, (WritableArray)obj);
        } else {
            retval.putNull(key);
        }

        return retval;
    }
}
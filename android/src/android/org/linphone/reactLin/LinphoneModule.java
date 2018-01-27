package org.linphone.reactLin;

import android.content.Intent;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import org.linphone.LinphoneLauncherActivity;

/**
 * Created by hue on 1/24/18.
 */

public class LinphoneModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactContext;

    public LinphoneModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

    }

    @Override
    public String getName() {
        return "Linphone";
    }

    @ReactMethod
    public void show(final Promise promise)
    {

        try{
            promise.resolve("success message");
            ReactApplicationContext context = getReactApplicationContext();
            Intent intent = new Intent(context, LinphoneLauncherActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            promise.reject("error","this is a error message");
        }

    }
    
}

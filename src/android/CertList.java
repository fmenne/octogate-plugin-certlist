package de.octogate.plugin;

import android.content.Intent;
import android.content.res.AssetManager;
import android.security.KeyChain;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Enumeration;
import java.security.cert.X509Certificate;

public class CertList extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("get_certlist")) {
            this.getCertList(callbackContext);
            return true;
        } else if (action.equals("install")) {
            this.installCert(callbackContext);
            return true;
        }

        return false;
    }

    private void getCertList(CallbackContext callbackContext) {
        JSONArray list = new JSONArray();

        try {
            KeyStore ks = KeyStore.getInstance("AndroidCAStore");
            ks.load(null);

            Enumeration<String> aliases = ks.aliases();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                X509Certificate cert = (X509Certificate)ks.getCertificate(alias);
                list.put(cert.getIssuerDN().getName());
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

        callbackContext.success(list);
    }

    private void installCert(CallbackContext callbackContext) {
        AssetManager assets = this.cordova.getActivity().getAssets();
        try {
            //String[] lst = assets.list("www/crt/OctoGateCA.crt");
            InputStream certStream =  assets.open("www/crt/OctoGateCA.crt");

            byte[] cert = new byte[certStream.available()];
            certStream.read(cert);

            Intent intent = KeyChain.createInstallIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(KeyChain.EXTRA_NAME, "OctoGate");
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, cert);
			Context ctx=this.cordova.getActivity().getApplicationContext();
			ctx.startActivity(intent);	
            callbackContext.success();

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }
}
package com.saksham.app;

import android.content.Intent;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Payment")
public class PaymentPlugin extends Plugin {
    private static final int PAYMENT_REQUEST_CODE = 1001;
    private PluginCall savedCall;

    @PluginMethod
    public void startPayment(PluginCall call) {
        String amount = call.getString("amount");
        if (amount == null) {
            call.reject("Amount is required");
            return;
        }

        savedCall = call;
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra("amount", amount);
        startActivityForResult(call, intent, PAYMENT_REQUEST_CODE);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYMENT_REQUEST_CODE && savedCall != null) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                JSObject result = new JSObject();
                result.put("success", true);
                savedCall.resolve(result);
            } else {
                savedCall.reject("Payment was cancelled");
            }
            savedCall = null;
        }
    }
} 
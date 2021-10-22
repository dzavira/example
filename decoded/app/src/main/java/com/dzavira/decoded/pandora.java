package com.dzavira.decoded;

import android.Manifest;
import android.app.Activity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

class pandora {
    Activity activity;
    pandora(Activity aktifitas){
        activity = aktifitas;
    }

    void beri_akses() {
       Dexter.withActivity(activity)
               .withPermission(Manifest.permission.CAMERA)
               .withListener(new PermissionListener() {
                   @Override
                   public void onPermissionGranted(PermissionGrantedResponse response) {
                       //  Toast.makeText(getApplicationContext(), "Permission Granted ", Toast.LENGTH_LONG).show();
                   }

                   @Override
                   public void onPermissionDenied(PermissionDeniedResponse response) {
               //        Toast.makeText(this, "Enable Permission Guys", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                   }
               }).check();
   }


}

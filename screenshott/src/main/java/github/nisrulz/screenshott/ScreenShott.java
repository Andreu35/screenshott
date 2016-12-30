/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.screenshott;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ScreenShott {
    private AppCompatActivity activity;
    private ProgressDialog dialog;

    public ScreenShott(AppCompatActivity activity) {
        this.activity = activity;
    }

    public Bitmap takeScreenShotOfRootView(View root_view, String filename) {
        root_view.setDrawingCacheEnabled(true);
        Bitmap bitmap = root_view.getDrawingCache();
        saveScreenshot(bitmap, filename);
        root_view.destroyDrawingCache();
        return bitmap;
    }

    private void saveScreenshot(Bitmap bmp, String filename) {
        // Added ProgressDialog if device is slow
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Saving...please wait.");
        dialog.show();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/YOUR_DIR");
        try {
            // Verify if Directory or File exists.
            if(!file.exists()) {
                file.mkdirs();
            }
            File fileSaved = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/YOUR_DIR/" + filename + "_" + System.currentTimeMillis()+ ".jpg");
            fileSaved.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(fileSaved);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
            
            // Action Media Scanner to reload gallery inmediatelly.
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(fileSaved.getPath());
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);

            dialog.cancel();
            Toast.makeText(activity, "Screnshot saved succefully", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

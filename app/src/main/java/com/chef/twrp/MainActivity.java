package com.chef.twrp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;

public class MainActivity extends Activity {
	
	private static final String twrpvs980 = "e1775da8be4afc492304050280a42dab";
	private static final String twrpvs9802873 = "/sdcard/vs980twrp2873.zip";
	
	private String downloadfolder = "/storage/emulated/0/";
	//private String test;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		downloadfolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
		
		try {
			setupRawRes(this);
		} catch (FileNotFoundException e) {}

//		Integer[] myraw;
//		myraw = new Integer[1];
//		myraw[0] = R.raw.twrp0885vs980bumpblastagatorsigned;
//		//myraw[1] = R.raw.twrp2873vs980bumpblastagatorsigned;
//		try {
//			createFile("test.zip", this, myraw);
//		} catch (IOException e) {}
    }
	
	public void dltwrp(View view) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("test");
		pd.show();
		Ion.with(this).load("http://www.gregtwallace.com/_private/scripts/download/budgetvm.php?file=twrp-3.0.2-c-vs980-bump-blastagator-signed.zip&type=twrp&version=2016.04.17_(3.0.2-c)")
			.progressDialog(pd)
			.progress(new ProgressCallback() {

				@Override
				public void onProgress(long downloaded, long total) {
				}
			})
			.write(new File(twrpvs9802873))
			.setCallback(new FutureCallback<File>() {
				@Override
				public void onCompleted(Exception e, File file) {
					pd.cancel();
					try {
						if(MD5Checker.checkMD5(twrpvs980, twrpvs9802873)) {
							Toast.makeText(MainActivity.this, downloadfolder, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_LONG).show();
						}
					} catch (Exception ee) {}
				}
			});
	}
	
	public void installtwrp(View v) {
		Runtime run = Runtime.getRuntime();
		Process p = null;
		String SDCARD = "/storage/emulated/0/update.zip";
		//String SDCARD = "/mnt/sdcard/vs980twrp2873.zip";
		DataOutputStream out = null;
		try{
			//Toast.makeText(MainActivity.this, "echo '--update_package=(\""+SDCARD+"\");' > /cache/recovery/extendedcommand\n", Toast.LENGTH_LONG).show();
			p = run.exec("su");
			out = new DataOutputStream(p.getOutputStream());
			//out.writeBytes("adb shell\n");
//			out.writeBytes("echo '--update_package=(\""+SDCARD+"\");' > /cache/recovery/extendedcommand\n");
		//	out.writeBytes("echo '--update_package=(\""+SDCARD+"\");' > /cache/recovery/openrecoveryscript\n");
//			out.writeBytes("echo '--update_package=(\""+SDCARD+"\");' > /cache/recovery/openrecoveryscript\n");
			out.writeBytes("echo '--update_package=/sdcard/vs980twrp2873.zip' >> /cache/recovery/command\n");
			//out.writeBytes("install " + SDCARD + " > /cache/recovery/openrecoveryscript\n");
			out.writeBytes("reboot recovery\n"); // testing
			out.flush();
		}catch(Exception e){
			Log.e("FLASH", "Unable to reboot into recovery mode:", e);
			e.printStackTrace();

		}
	}
	
	public static void setupRawRes(final Context context) throws FileNotFoundException {

		String twrpvs980beta = "TWRPvs980Beta";
		String twrpvs980 = "TWRPvs980";
		
		File filebeta = new File(context.getFilesDir(), twrpvs980beta + ".zip");
		final OutputStream outputStream = new FileOutputStream(filebeta);
		try {
			createRaw(context, outputStream, R.raw.twrp0885vs980bumpblastagatorsigned);
		} catch (IOException e) {}

		File file = new File(context.getFilesDir(), twrpvs980 + ".zip");
		final OutputStream outputStream2 = new FileOutputStream(file);
		try {
			createRaw(context, outputStream2, R.raw.twrp2873vs980bumpblastagatorsigned);
		} catch (IOException e) {}

		
	}
	
	public static void createRaw(Context c, OutputStream outputStream, int resource) throws IOException {
		
		final Resources resources = c.getResources();
		InputStream inputStream = resources.openRawResource(resource);
		final byte[] largeBuffer = new byte[1024 * 4];
		int totalBytes = 0;
		int bytesRead = 0;
		
		while ((bytesRead = inputStream.read(largeBuffer)) > 0) {
			if (largeBuffer.length == bytesRead) {
				outputStream.write(largeBuffer);
			} else {
				final byte[] shortBuffer = new byte[bytesRead];
				System.arraycopy(largeBuffer, 0, shortBuffer, 0, bytesRead);
				outputStream.write(shortBuffer);
			}
			totalBytes += bytesRead;
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
	}
	
}

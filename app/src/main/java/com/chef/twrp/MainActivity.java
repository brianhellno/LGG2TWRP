package com.chef.twrp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {
	
	private static final String twrpvs980 = "f0740943f0f55ee3517eaeb38c8ca2d3";
	private static final String twrpvs9802873 = "/sdcard/vs980twrp2873.zip";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
    }
	
	public void dltwrp(View view) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("test");
		pd.show();
		Ion.with(this).load("https://p-def3.pcloud.com/cBZyagwMZohBzgZZZTN0id7Z2ZZRaVZkZAI7lZnXZOZk7Z8ZiXZTkZzXZz7ZLXZYkZn7ZPkZgXZ1XZcx4LZNGq9fCI3spjNOGekBrAeE5miAtLX/twrp-2.8.7.3-vs980-bump-blastagator-signed.zip")
			.progressDialog(pd)
			.progress(new ProgressCallback() {

				@Override
				public void onProgress(long p1, long p2) {
					// TODO: Implement this method
				}
			})
			.write(new File(twrpvs9802873))
			.setCallback(new FutureCallback<File>() {
				@Override
				public void onCompleted(Exception e, File file) {
					// download done...
					// do stuff with the File or error
					pd.cancel();
					try {
						//if(twrpvs980.compareTo(createChecksum("/sdcard/boom.zip").toString()) == 0){
						if(checkMD5(twrpvs980, twrpvs9802873)) {
							Toast.makeText(MainActivity.this, "Success!!!", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MainActivity.this, createChecksum("/sdcard/boom.zip").toString(), Toast.LENGTH_LONG).show();
						}
					} catch (Exception e2) {}
				}
			});
	}
	
	public void installtwrp(View v) {
		Runtime run = Runtime.getRuntime();
		Process p = null;
		String SDCARD = "/storage/emulated/0/vs980twrp2873.zip";
		DataOutputStream out = null;
		try{
			p = run.exec("su");
			out = new DataOutputStream(p.getOutputStream());
			// out.writeBytes("echo 'install_zip(\""+ SDCARD+"\");'" +" > /cache/recovery/openrecoveryscript\n");
			out.writeBytes("adb shell\n");
			out.writeBytes("echo '--update_package=(\""+SDCARD+"\");' > /cache/recovery/extendedcommand\n");
			out.writeBytes("reboot recovery\n"); // testing
			out.flush();

		}catch(Exception e){
			Log.e("FLASH", "Unable to reboot into recovery mode:", e);
			e.printStackTrace();

		}
	}
	
	public static boolean checkMD5(String md5, String fileName) {
		if (md5 == null || md5 == "" || fileName == null) {
			return false;
		}
		String calculatedDigest = calculateMD5(fileName);
		if (calculatedDigest == null) {
			return false;
		}
		return calculatedDigest.equalsIgnoreCase(md5);
	}

	public static String calculateMD5(String fileName) {
		File updateFile = new File(fileName);
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		InputStream is = null;
		try {
			is = new FileInputStream(updateFile);
		} catch (FileNotFoundException e) {
			return null;
		}
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			// Fill to 32 chars
			output = String.format("%32s", output).replace(' ', '0');
			return output;
		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(
					"Unable to close input stream for MD5 calculation", e);
			}
		}
	}
	
	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis =  new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}
}

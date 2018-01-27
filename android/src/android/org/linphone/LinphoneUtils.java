package org.linphone;

/*
SoftVolume.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.linphone.core.DialPlan;
import org.linphone.core.LinphoneAccountCreator;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.video.capture.hwconf.Hacks;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Helpers.
 */
public final class LinphoneUtils {
	private static Context context = null;

	private LinphoneUtils(){}

	//private static final String sipAddressRegExp = "^(sip:)?(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}(:[0-9]{2,5})?$";
	//private static final String strictSipAddressRegExp = "^sip:(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}$";

	public static boolean isSipAddress(String numberOrAddress) {
		try {
			LinphoneCoreFactory.instance().createLinphoneAddress(numberOrAddress);
			return true;
		} catch (LinphoneCoreException e) {
			return false;
		}
	}



	public static String getAddressDisplayName(LinphoneAddress address){
		if(address.getDisplayName() != null) {
			return address.getDisplayName();
		} else {
			if(address.getUserName() != null){
				return address.getUserName();
			} else {
				return address.asStringUriOnly();
			}
		}
	}

	public static String getUsernameFromAddress(String address) {
		if (address.contains("sip:"))
			address = address.replace("sip:", "");

		if (address.contains("@"))
			address = address.split("@")[0];

		return address;
	}

	public static boolean onKeyBackGoHome(Activity activity, int keyCode, KeyEvent event) {
		if (!(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
			return false; // continue
		}

		activity.startActivity(new Intent()
			.setAction(Intent.ACTION_MAIN)
			.addCategory(Intent.CATEGORY_HOME));
		return true;
	}


	static boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}

		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
				cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	public static boolean onKeyVolumeAdjust(int keyCode) {
		if (!((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
				&& (Hacks.needSoftvolume())|| Build.VERSION.SDK_INT >= 15)) {
			return false; // continue
		}

		if (!LinphoneService.isReady()) {
			Log.i("Couldn't change softvolume has service is not running");
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			LinphoneManager.getInstance().adjustVolume(1);
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			LinphoneManager.getInstance().adjustVolume(-1);
		}
		return true;
	}


	public static final List<LinphoneCall> getLinphoneCalls(LinphoneCore lc) {
		// return a modifiable list
		return new ArrayList<LinphoneCall>(Arrays.asList(lc.getCalls()));
	}


	public static final List<LinphoneCall> getCallsInState(LinphoneCore lc, Collection<State> states) {
		List<LinphoneCall> foundCalls = new ArrayList<LinphoneCall>();
		for (LinphoneCall call : getLinphoneCalls(lc)) {
			if (states.contains(call.getState())) {
				foundCalls.add(call);
			}
		}
		return foundCalls;
	}
	public static final List<LinphoneCall> getRunningOrPausedCalls(LinphoneCore lc) {
		return getCallsInState(lc, Arrays.asList(
				State.Paused,
				State.PausedByRemote,
				State.StreamsRunning));
	}

	public static final int countConferenceCalls(LinphoneCore lc) {
		int count = lc.getConferenceSize();
		if (lc.isInConference()) count--;
		return count;
	}

	public static void setVisibility(View v, int id, boolean visible) {
		v.findViewById(id).setVisibility(visible ? VISIBLE : GONE);
	}
	public static void setVisibility(View v, boolean visible) {
		v.setVisibility(visible ? VISIBLE : GONE);
	}


	public static boolean isCallRunning(LinphoneCall call)
	{
		if (call == null) {
			return false;
		}

		LinphoneCall.State state = call.getState();

		return state == LinphoneCall.State.Connected ||
				state == LinphoneCall.State.CallUpdating ||
				state == LinphoneCall.State.CallUpdatedByRemote ||
				state == LinphoneCall.State.StreamsRunning ||
				state == LinphoneCall.State.Resuming;
	}

	public static boolean isCallEstablished(LinphoneCall call) {
		if (call == null) {
			return false;
		}

		LinphoneCall.State state = call.getState();

		return isCallRunning(call) ||
				state == LinphoneCall.State.Paused ||
				state == LinphoneCall.State.PausedByRemote ||
				state == LinphoneCall.State.Pausing;
	}

	public static boolean isHighBandwidthConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && isConnectionFast(info.getType(),info.getSubtype()));
    }

	private static boolean isConnectionFast(int type, int subType){
		if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            	return false;
            }
		}
        //in doubt, assume connection is good.
        return true;
    }



	public static void recursiveFileRemoval(File root) {
		if (!root.delete()) {
			if (root.isDirectory()) {
				File[] files = root.listFiles();
		        if (files != null) {
		            for (File f : files) {
		            	recursiveFileRemoval(f);
		            }
		        }
			}
		}
	}

	public static String getDisplayableUsernameFromAddress(String sipAddress) {
		String username = sipAddress;
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc == null) return username;

		if (username.startsWith("sip:")) {
			username = username.substring(4);
		}

		if (username.contains("@")) {
			String domain = username.split("@")[1];
			LinphoneProxyConfig lpc = lc.getDefaultProxyConfig();
			if (lpc != null) {
				if (domain.equals(lpc.getDomain())) {
					return username.split("@")[0];
				}
			} else {
				if (domain.equals(LinphoneManager.getInstance().getContext().getString(R.string.default_domain))) {
					return username.split("@")[0];
				}
			}
		}
		return username;
	}

	private static Context getContext() {
		if (context == null && LinphoneManager.isInstanciated())
			context = LinphoneManager.getInstance().getContext();
		return context;
	}

	public static void displayError(boolean isOk, TextView error, String errorText) {
		if (isOk) {
			error.setVisibility(View.INVISIBLE);
			error.setText("");
		} else {
			error.setVisibility(View.VISIBLE);
			error.setText(errorText);
		}
	}



	public static String errorForEmailStatus(LinphoneAccountCreator.EmailCheck status) {
		Context ctxt = getContext();
		if (ctxt != null) {
			if (status.equals(LinphoneAccountCreator.EmailCheck.InvalidCharacters)
					|| status.equals(LinphoneAccountCreator.EmailCheck.Malformed))
				return ctxt.getString(R.string.invalid_email);
		}
		return null;
	}

	public static String errorForUsernameStatus(LinphoneAccountCreator.UsernameCheck status) {
		Context ctxt = getContext();
		if (ctxt != null) {
			if (status.equals(LinphoneAccountCreator.UsernameCheck.InvalidCharacters))
				return ctxt.getString(R.string.invalid_username);
			if (status.equals(LinphoneAccountCreator.UsernameCheck.TooShort))
				return ctxt.getString(R.string.username_too_short);
			if (status.equals(LinphoneAccountCreator.UsernameCheck.TooLong))
				return ctxt.getString(R.string.username_too_long);
			if (status.equals(LinphoneAccountCreator.UsernameCheck.Invalid))
				return ctxt.getString(R.string.username_invalid_size);
			if (status.equals(LinphoneAccountCreator.UsernameCheck.InvalidCharacters))
				return ctxt.getString(R.string.invalid_display_name);
		}
		return null;
	}

	public static String errorForPasswordStatus(LinphoneAccountCreator.PasswordCheck status) {
		Context ctxt = getContext();
		if (ctxt != null) {
			if (status.equals(LinphoneAccountCreator.PasswordCheck.TooShort))
				return ctxt.getString(R.string.password_too_short);
			if (status.equals(LinphoneAccountCreator.PasswordCheck.TooLong))
				return ctxt.getString(R.string.password_too_long);
		}
		return null;
	}

	public static String errorForRequestStatus(LinphoneAccountCreator.RequestStatus status) {
		Context ctxt = getContext();
		if (ctxt != null) {
			if (status.equals(LinphoneAccountCreator.RequestStatus.Failed))
				return ctxt.getString(R.string.request_failed);
			if (status.equals(LinphoneAccountCreator.RequestStatus.ErrorServer))
				return ctxt.getString(R.string.wizard_failed);
			if (status.equals(LinphoneAccountCreator.RequestStatus.AccountExist)
					|| status.equals(LinphoneAccountCreator.RequestStatus.AccountExistWithAlias))
				return ctxt.getString(R.string.account_already_exist);
			if (status.equals(LinphoneAccountCreator.RequestStatus.AliasIsAccount)
					|| status.equals(LinphoneAccountCreator.RequestStatus.AliasExist))
				return ctxt.getString(R.string.assistant_phone_number_unavailable);
			if (status.equals(LinphoneAccountCreator.RequestStatus.AccountNotExist))
				return ctxt.getString(R.string.assistant_error_bad_credentials);
			if (status.equals(LinphoneAccountCreator.RequestStatus.AliasNotExist))
				return ctxt.getString(R.string.phone_number_not_exist);
			if (status.equals(LinphoneAccountCreator.RequestStatus.AliasNotExist)
					|| status.equals(LinphoneAccountCreator.RequestStatus.AccountNotActivated)
					|| status.equals(LinphoneAccountCreator.RequestStatus.AccountAlreadyActivated)
					|| status.equals(LinphoneAccountCreator.RequestStatus.AccountActivated)
					|| status.equals(LinphoneAccountCreator.RequestStatus.AccountNotCreated)
					|| status.equals(LinphoneAccountCreator.RequestStatus.Ok))
				return "";
		}
		return null;
	}

	public static String getCountryCode(EditText dialCode) {
		if(dialCode != null) {
			String code = dialCode.getText().toString();
			if(code != null && code.startsWith("+")) {
				code = code.substring(1);
			}
			return code;
		}
		return null;
	}

	public static void setCountry(DialPlan c, EditText dialCode, Button selectCountry, int countryCode) {
		if( c != null && dialCode != null && selectCountry != null) {
			dialCode.setText(c.getCountryCode());
			selectCountry.setText(c.getCountryName());
		} else {
			if(countryCode != -1){
				dialCode.setText("+" + countryCode);
			} else {
				dialCode.setText("+");
			}
		}
	}

	public static void displayErrorAlert(String msg, Context ctxt) {
		if (ctxt != null && msg != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
			builder.setMessage(msg)
					.setCancelable(false)
					.setNeutralButton(ctxt.getString(R.string.ok), null)
					.show();
		}
	}


	/************************************************************************************************
	 *							Picasa/Photos management workaround									*
	 ************************************************************************************************/

	public static String getFilePath(final Context context, final Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			String type = getTypeFromUri(uri, context);
			String result = getDataColumn(context, uri, null, null); //
			if (TextUtils.isEmpty(result))
				if (uri.getAuthority().contains("com.google.android") || uri.getAuthority().contains("com.android")) {
					try {
						File localFile = createFile(context, null, type);
						FileInputStream remoteFile = getSourceStream(context, uri);
						if(copyToFile(remoteFile, localFile))
							result = localFile.getAbsolutePath();
						remoteFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			return result;
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}


	private static String getTypeFromUri(Uri uri, Context context){
		ContentResolver cR = context.getContentResolver();
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String type = mime.getExtensionFromMimeType(cR.getType(uri));
		return type;
	}

	/**
	 * Copy data from a source stream to destFile.
	 * Return true if succeed, return false if failed.
	 */
	private static boolean copyToFile(InputStream inputStream, File destFile) {
		if (inputStream == null || destFile == null) return false;
		try {
			OutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static String getTimestamp() {
		try {
			return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
		} catch (RuntimeException e) {
			return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		}
	}

	public static File createFile(Context context, String imageFileName, String type) throws IOException {
		if (TextUtils.isEmpty(imageFileName))
			imageFileName = getTimestamp()+"."+type; // make random filename if you want.

		final File root;
		imageFileName = imageFileName;
		root = context.getExternalCacheDir();

		if (root != null && !root.exists())
			root.mkdirs();
		return new File(root, imageFileName);
	}


	public static FileInputStream getSourceStream(Context context, Uri u) throws FileNotFoundException {
		FileInputStream out = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ParcelFileDescriptor parcelFileDescriptor =
					context.getContentResolver().openFileDescriptor(u, "r");
			FileDescriptor fileDescriptor = null;
			if (parcelFileDescriptor != null) {
				fileDescriptor = parcelFileDescriptor.getFileDescriptor();
				out = new FileInputStream(fileDescriptor);
			}
		} else {
			out = (FileInputStream) context.getContentResolver().openInputStream(u);
		}
		return out;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	static String getDataColumn(Context context, Uri uri, String selection,
								String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return null;
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		String[] proj = {MediaStore.Images.Media.DATA};
		CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		if (cursor != null && cursor.moveToFirst()) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			String result = cursor.getString(column_index);
			cursor.close();
			return result;
		}
		return null;
	}

    public static String processContactUri(Context context, Uri contactUri){
		ContentResolver cr = context.getContentResolver();
        InputStream stream = null;
		if(cr !=null) {
			try {
				stream = cr.openInputStream(contactUri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if(stream != null) {
				StringBuffer fileContent = new StringBuffer("");
				int ch;
				try {
					while ((ch = stream.read()) != -1)
						fileContent.append((char) ch);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String data = new String(fileContent);
				return data;
			}
			return null;
		}
		return null;
    }

    public static String getContactNameFromVcard(String vcard){
		if(vcard != null) {
			String contactName = vcard.substring(vcard.indexOf("FN:") + 3);
			contactName = contactName.substring(0, contactName.indexOf("\n") - 1);
			contactName = contactName.replace(";", "");
			contactName = contactName.replace(" ", "");
			return contactName;
		}
		return null;
	}

    public static Uri createCvsFromString(String vcardString){
		String contactName = getContactNameFromVcard(vcardString);
        File vcfFile = new File(Environment.getExternalStorageDirectory(), contactName+".cvs");
        try {
            FileWriter fw = new FileWriter(vcfFile);
            fw.write(vcardString);
            fw.close();
            return Uri.fromFile(vcfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}


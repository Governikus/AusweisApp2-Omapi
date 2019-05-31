/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 *
 * Copyright 2017-2019 adesso AG
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence"); You may
 * not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the Licence for the
 * specific language governing permissions and limitations under the Licence.
 */

package com.governikus.ausweisapp2.omapi.impl;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.*;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import net.vx4.lib.omapi.Hex;
import net.vx4.lib.omapi.OMAPITP;

/**
 *  This class is a pretty "raw" stub to request a screen unlock to provide a fixed "secret" unlock key for the UICC
 *  applet. As the UICC is fixed in the mobile phone there is no real encryption need. But in general we want to enable
 *  a protocol similar to PACE (Password Authenticated Connection Establishment) for user authentication. As there are
 *  ideas to enable multiple identities on one or several (remember dual SIM phones) UICCs we stick to a fixed secret
 *  here until a reliable management of "CHANGE SECRET", recovery codes and hardware key storage using KeyGuard is
 *  defined and supported by the GUI. Also there are some compatibility quirks below we didn't want to throw away as
 *  long as test mobile phones still rely on them.
 *
 * @author kahlo
 */
public final class OmapiSecretFetcherImpl extends Activity
{
	private static final String LOG_TAG = "AusweisApp2.OMAPI";


	/**
	 *  Used to build and start the enclosing activity requiring device unlock.
	 */
	static final class Builder
	{
		private final Context context;

		/**
		 *
		 * @param context
		 */
		Builder(final Context context)
		{
			this.context = context;
		}


		/**
		 * Set the callback handler, which is created inline below.
		 *
		 * @param tp
		 */
		final void set(final OMAPITP tp)
		{
			System.out.println("Builder.set");
			System.out.println("tp = [" + tp + "]");

			if (tp != null && tp.getClass() == OMAPITP.class && tp.getClass().getProtectionDomain() == this.getClass().getProtectionDomain())
			{
				tp.setCallbackHandler(new OMAPITP.CallbackHandler(){
							@Override
							public byte[] getSecret(){
								System.out.println("Builder.getSecret");

								final Class< ? >[] context = new SecurityManager() {
									@Override
									public Class< ? >[] getClassContext(){
										return super.getClassContext();
									}
								}
								.getClassContext();

								System.out.println("context = " + context); // is null on Android
						        //System.out.println(context[2]);
						        //System.out.println(context[3]);

								SecurityException se = new SecurityException();
								if (!tp.getClass().getName().equals(se.getStackTrace()[2].getClassName()))
								{
									throw se;
								}
								final Context ctx = Builder.this.context;

						        // TODO: check for call-source

								final SharedPreferences pref = ctx.getSharedPreferences("IVID", MODE_PRIVATE);
								final String tmwRawData = pref.getString("rawData", null);

						        //						if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
						        //							Toast.makeText(ctx, "Android API Version >= 23 required.", Toast.LENGTH_LONG);
						        //							return null;
						        //						};

								final Object fpm = ctx.getSystemService(Context.FINGERPRINT_SERVICE);
								if (fpm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ((FingerprintManager) fpm).isHardwareDetected())
								{
						            // fingerprint auth
						            // ((FingerprintManager) fpm).authenticate();
									final FingerprintManager fingerprintManager = (FingerprintManager) fpm;
									if (!fingerprintManager.hasEnrolledFingerprints())
									{
						                // Toast.makeText(ctx, "No fingerprints enrolled.", Toast.LENGTH_LONG);
									}
									else
									{

									}
								}
								else
								{
						            // fallback to KeyGuard

								}

								final KeyguardManager kgm = (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);
								Class< ? extends Activity> clazz = OmapiSecretFetcherImpl.class;

								final Object[] res = new Object[1];
								if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || kgm.isDeviceSecure())
								{
									ctx.registerReceiver(new BroadcastReceiver() {
										@Override
										public void onReceive(Context context, Intent intent){
											System.out.println("OmapiImpl.onReceive");
											System.out.println("context = [" + context + "], intent = [" + intent + "]");
											synchronized (res){
												res[0] = ((Object[]) intent.getExtras().get("result"))[0];
												res.notifyAll();
											}
										}
									}, new IntentFilter(clazz.getName()));

									ctx.startActivity(new Intent(ctx, clazz).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("result", res));

									synchronized (res){
										try
										{
											res.wait();
										}
										catch (InterruptedException e)
										{
										}
									}

									if (res[0] == null)
									{
										System.out.println("RESULT: null - NEGATIVE");
										return null;
									}
									else
									{
										System.out.println("RESULT: " + res[0] + " - POSITIVE");

						                // TODO: use real secret
										final byte[] ulk = Hex.x("E813C3318BBA3958D5ABD2E1D0B3163A");

										return ulk;
									}
								}

								return null;
							}
						});
			}
		}


	}

	/**
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		System.out.println("OmapiActivity.onCreate");
		System.out.println("savedInstanceState = [" + savedInstanceState + "]");

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{
			setResult(RESULT_CANCELED, null);
			//			this.sendBroadcast(new Intent(this.getClass().getName()).putExtra("result", new Object[1]));

			this.sendBroadcast(new Intent(this.getClass().getName()).putExtra("result", new Object[] {""}));

			finish();
		}
		else
		{
			final KeyguardManager kgm = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
			final Intent i = kgm.createConfirmDeviceCredentialIntent("meID", "meID requires valid user authorization.");
			this.startActivityForResult(i, 1);
		}
	}


	/**
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		System.out.println("OmapiActivity.onActivityResult");
		System.out.println("requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

		switch (requestCode)
		{
			case 1:
				setResult(resultCode, data);
				if (resultCode == RESULT_OK)
				{
					this.sendBroadcast(new Intent(this.getClass().getName()).putExtra("result", new Object[] {""}));
				}
				finish();
				break;
		}

		this.sendBroadcast(new Intent(this.getClass().getName()).putExtra("result", new Object[1]));
		finish();
	}


}

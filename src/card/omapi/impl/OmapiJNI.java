/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

package com.governikus.ausweisapp2.omapi.impl;

import android.content.Context;
import android.util.Log;
import com.governikus.ausweisapp2.omapi.Omapi;
import com.governikus.ausweisapp2.omapi.OmapiCard;
import com.governikus.ausweisapp2.omapi.OmapiReader;
import net.vx4.lib.omapi.ArrayTool;
import net.vx4.lib.omapi.Hex;

/**
 * Factory for OMAPI Wrapper and simple JNI entry for the Qt code.
 */
public final class OmapiJNI
{
	private static final String LOG_TAG = "AusweisApp2.OMAPI";
	private static final OmapiJNI INSTANCE = new OmapiJNI();

	private Omapi mOmapi;
	private OmapiCard mCard;

	private OmapiJNI()
	{
	}


	private static final OmapiJNI get()
	{
		return INSTANCE;
	}


	/**
	 * Initialize this factory for later usage.
	 *
	 * @param context Android {@link Context} of application (service or activity)
	 * @return
	 */
	public static boolean init(final Context context)
	{
		return get()._init0(context);
	}


	/**
	 * Transmit data to first available and functional SE.
	 *
	 * @param apdu
	 * @return
	 */
	public static byte[] transmit(final byte[] apdu)
	{
		return get()._transmit0(apdu);
	}


	/**
	 * Execute CCID PACE with control message
	 *
	 * @param paceData
	 * @return
	 */
	public static byte[] paceControl(final byte[] paceData)
	{
		return get()._paceControl0(paceData);
	}


	/**
	 * Closes and destroys the currently allocated implementation instance.
	 */
	public static void close()
	{
		get()._close0();
	}


	/**
	 * Retrieve internal service provider object.
	 *
	 * @return instance of {@link Omapi}
	 */
	public static final Omapi getInstance()
	{
		return INSTANCE.mOmapi;
	}


	/*
	   // instance implementation
	 */

	/**
	 * @param context
	 * @return
	 */
	private final boolean _init0(final Context context)
	{
		Log.d(LOG_TAG, "init omapi jni");
		System.out.println("OmapiJNI.init");
		System.out.println("context = [" + context + "]");

		if (this.mOmapi == null)
		{
			this.mOmapi = new OmapiImpl(context);
			if (this.mOmapi.isAvailable())
			{
				this.mOmapi.init();
			}
		}

		for (final OmapiReader reader : this.mOmapi.getReader())
		{
			if ((this.mCard = reader.getCard()) != null)
			{
				return true;
			}
		}

		return false;
	}


	/**
	 * @param apdu
	 * @return
	 */
	private final byte[] _transmit0(final byte[] apdu)
	{
		Log.d(LOG_TAG, "transmit apdu with omapi jni");
		System.out.println("OmapiJNI.transmit");
		System.out.println("apdu = [" + Hex.x(apdu) + "]");

		if (this.mOmapi != null)
		{
			if (this.mCard == null)
			{
				System.out.println("OmapiJNI.transmit: check for readers.");
				for (final OmapiReader reader : this.mOmapi.getReader())
				{
					if ((this.mCard = reader.getCard()) != null)
					{
						break;
					}
				}
			}

			if (this.mCard != null)
			{
				return this.mCard.transmit(apdu);
			}
		}

		return null;
	}


	/**
	 * @param paceData
	 * @return
	 */
	private final byte[] _paceControl0(final byte[] paceData)
	{
		Log.d(LOG_TAG, "paceControl with omapi jni");
		System.out.println("OmapiJNI.paceControl");
		System.out.println("paceData = [" + paceData != null ? Hex.x(paceData) : "null" + "]");

		if (this.mOmapi != null)
		{
			if (this.mCard == null)
			{
				System.out.println("OmapiJNI.paceControl: check for readers.");
				for (final OmapiReader reader : this.mOmapi.getReader())
				{
					if ((this.mCard = reader.getCard()) != null)
					{
						break;
					}
				}
			}

			if (this.mCard != null)
			{
				if (paceData == null)   // use as short hand for get caps or destroy?
				{   // TODO: to be completed
				}

				// keep in mind: extended length format and little to big endian conversion
				final byte[] cmd = new byte[] {
					(byte) 0xFF, (byte) 0x9A, (byte) 0x04, paceData[0], (byte) 0x00, paceData[2], paceData[1]
				};
				final byte[] res = this.mCard.transmit(ArrayTool.concat(cmd, ArrayTool.sub(paceData, 3, paceData.length - 3)));

				return ArrayTool.sub(res, 0, res.length - 2);
			}
		}

		return null;
	}


	/**
	 *
	 */
	private final void _close0()
	{
		Log.d(LOG_TAG, "transmit apdu with omapi jni");
		try
		{
			this.mOmapi.close();
		}
		finally
		{
			this.mOmapi = null;
			this.mCard = null;
		}
	}


}

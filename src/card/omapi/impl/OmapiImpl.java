/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

package com.governikus.ausweisapp2.omapi.impl;

import android.content.Context;
import com.governikus.ausweisapp2.omapi.Omapi;
import com.governikus.ausweisapp2.omapi.OmapiError;
import com.governikus.ausweisapp2.omapi.OmapiReader;
import net.vx4.lib.omapi.Hex;
import net.vx4.lib.omapi.OMAPITP;
import org.simalliance.openmobileapi.Channel;
import org.simalliance.openmobileapi.Reader;
import org.simalliance.openmobileapi.Session;
import org.simalliance.openmobileapi.SEService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the main entry point into the wrapper and handels basic logic as selecting the applet instance on the
 * secure element.
 *
 * @author kahlo, 2018
 */
public class OmapiImpl implements Omapi, SEService.CallBack
{
	private final Context mContext;
	private final SEService seService;
	private final List<OmapiReader> readerList = new ArrayList<>();

	OmapiImpl(final Context context)
	{
		System.out.println("OmapiImpl.OmapiImpl");
		System.out.println("context = [" + context + "]");

		this.mContext = context;
		this.seService = new SEService(this.mContext, OmapiImpl.this);
		System.out.println("SEService0: " + OmapiImpl.this.seService + " connected? " + OmapiImpl.this.seService.isConnected());
	}


	private void log(final String msg)
	{
		System.out.println("OmapiImpl.log: " + msg);
	}


	private String toHex(final byte[] in)
	{
		return Hex.toString(in);
	}


	@Override
	public void serviceConnected(final SEService service)
	{
		System.out.println("OmapiImpl.serviceConnected");
		System.out.println("service = [" + service + "]");

		if (this.seService == service)
		{
			System.out.println("SEService1: " + this.seService + " connected? " + (this.seService == null ? "null" : this.seService.isConnected()));
			System.out.println("SEService2: " + service + " connected? " + (service == null ? "null" : service.isConnected()));
		}
		else
		{
			System.out.println("SHOULD NOT HAPPEN: ignoring callback from foreign service = [" + service + "]");
		}
	}


	@Override
	public boolean isAvailable()
	{
		System.out.println("OmapiImpl.isAvailable");

		for (int i = 0; i < 20 && this.seService != null && !this.seService.isConnected(); i++)
		{
			System.out.println("waiting for connect");
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		System.out.println("SEService*: " + this.seService + " connected? " + (this.seService == null ? "null" : this.seService.isConnected()));
		return this.seService != null && this.seService.isConnected();
	}


	@Override
	public OmapiError init()
	{
		if (!isAvailable())
		{
			return new OmapiError("No Secure Element API", "OpenMobile API not available.");
		}

		System.out.println("OmapiImpl.init");
		final Reader[] readers = this.seService.getReaders();
		log("readers: " + (readers != null ? readers.length : "NONE"));
		log("readers: " + (readers != null ? Arrays.asList(readers) : "NONE"));

		if (readers != null && readers.length > 0)
		{
			for (final Reader reader : readers)
			{
				try
				{
					log("Reader: " + reader.getName() + " - SE present? " + reader.isSecureElementPresent());
					if (reader.isSecureElementPresent())
					{
						final Session session = readers[0].openSession();

						log("ATR: " + toHex(session.getATR()));
						log("Create logical channel to D2760000930101 within the session...");

						final Channel channel = session.openLogicalChannel(Hex.fromString("D2760000930101"));
						if (channel != null)
						{
							log("Channel: " + channel + " / " + Hex.x(channel.getSelectResponse()));
							// TODO: check channel and status
							// 6F0E8407D276000093010185030300FF9000

							log("Send CIPHER ENABLE command");
							final byte[] respApdu = channel.transmit(Hex.fromString("80CE000000"));
							log("Response: " + Hex.toString(respApdu));
							// TODO: check type depending on supported algs and name of instance if multi-SE
							// 7C0C8004FFFFFFFF8104544553549000

							final OMAPITP tp = new OMAPITP(channel);
							new OmapiSecretFetcherImpl.Builder(this.mContext).set(tp);

							readerList.add(new OmapiReaderImpl(tp));
						}
					}
				}
				catch (final Exception e)
				{
					// don't fail if single reader doesn't work, print log
					//return new OmapiErrorImpl("Exception while connecting to readers", e.getMessage());
					e.printStackTrace();
				}
			}
		}
		else
		{
			return new OmapiError("No readers", "No readers available");
		}

		// no error
		return null;
	}


	@Override
	public boolean isEnabled()
	{
		System.out.println("OmapiImpl.isEnabled");
		return readerList.size() > 0;
	}


	@Override
	public List<OmapiReader> getReader()
	{
		System.out.println("OmapiImpl.getReader");
		System.out.println("readerList = [" + readerList + "]");

		return readerList;
	}


	@Override
	public void close()
	{
		for (final OmapiReader r : this.readerList)
		{
			if (r != null)
			{
				final Channel c = ((OmapiReaderImpl) r).getChannel();
				if (!c.isClosed())
				{
					c.getSession().closeChannels();
					c.getSession().close();
				}
			}
		}

		this.readerList.clear();
		// seService.shutdown();
	}


}

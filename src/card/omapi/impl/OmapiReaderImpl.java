/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

package com.governikus.ausweisapp2.omapi.impl;

import android.util.Log;
import com.governikus.ausweisapp2.omapi.OmapiCard;
import com.governikus.ausweisapp2.omapi.OmapiReader;
import net.vx4.lib.omapi.Hex;
import net.vx4.lib.omapi.OMAPITP;
import net.vx4.lib.omapi.TransportProvider;
import org.simalliance.openmobileapi.Channel;

/**
 * This implementation basically wraps away specifics of the underlying transport providers.
 *
 * @author kahlo
 */
public class OmapiReaderImpl implements OmapiReader
{
	private static final String LOG_TAG = "AusweisApp2.OMAPI";

	private final OMAPITP tp;

	public OmapiReaderImpl(final OMAPITP tp)
	{
		this.tp = tp;
		System.out.println("OmapiReaderImpl.OmapiReaderImpl");
		System.out.println("tp = [" + tp + "]");
	}


	Channel getChannel()
	{
		return (Channel) ((TransportProvider) tp.getParent()).getParent();
	}


	@Override
	public String getName()
	{
		System.out.println("OmapiReaderImpl.getName");
		return getChannel().getSession().getReader().getName();
	}


	@Override
	public OmapiCard getCard()
	{
		System.out.println("OmapiReaderImpl.getCard");

		return new OmapiCard() {
				   @Override
				   public byte[] transmit(final byte[] apdu)
				   {
					   Log.d(LOG_TAG, "transmit omapi");
					   System.out.println("OmapiCardImpl.transmit");
					   System.out.println("apdu = [" + Hex.toString(apdu) + "]");

					   if (tp != null)
					   {
						   byte[] rpdu = tp.process(apdu);
						   System.out.println("rpdu = [" + Hex.toString(rpdu) + "]");
						   return rpdu;
					   }
					   else
					   {
						   System.out.println("No TransportProvider.");
					   }

					   return null;
				   }

		};
	}


}

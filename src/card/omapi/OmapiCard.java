/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

package com.governikus.ausweisapp2.omapi;

/**
 *
 */
public interface OmapiCard
{
	/**
	 * Send an APDU to the card.
	 *
	 * @return Response apdu, null on error.
	 */
	byte[] transmit(final byte[] apdu);
}

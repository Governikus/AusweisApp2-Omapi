/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

package com.governikus.ausweisapp2.omapi;

/**
 *
 */
public interface OmapiReader
{
	/**
	 * Get name of reader.
	 */
	String getName();

	/**
	 * Get inserted card, null if not inserted.
	 */
	OmapiCard getCard();
}

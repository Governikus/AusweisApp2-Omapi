/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

package com.governikus.ausweisapp2.omapi;

/**
 *
 */
public class OmapiError
{
	private final String error, desc;

	/**
	 * Create error instance.
	 *
	 * @param error
	 * @param desc
	 */
	public OmapiError(final String error, final String desc)
	{
		this.error = error;
		this.desc = desc;
	}


	/**
	 * Get name of error.
	 *
	 * @return
	 */
	public String getName()
	{
		return error;
	}


	/**
	 * Get a human readable description of the error.
	 *
	 * @return
	 */
	public String getDescription()
	{
		return desc;
	}


}

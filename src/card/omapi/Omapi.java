/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

package com.governikus.ausweisapp2.omapi;

import java.io.Closeable;
import java.util.List;

import com.governikus.ausweisapp2.omapi.OmapiError;
import com.governikus.ausweisapp2.omapi.OmapiReader;

/**
 *
 */
public interface Omapi extends Closeable   // AutoClosable requires min. API level 19
{
	/**
	 * Initialize omapi layer.
	 * This must be called if isAvailable() = false
	 *
	 * @return Null on success, otherwise an error.
	 */
	OmapiError init();

	/**
	 * Check if omapi layer is available.
	 *
	 * @return True if omapi layer exists, otherwise false.
	 */
	boolean isAvailable();

	/**
	 * Check if omapi layer is enabled.
	 *
	 * @return True if omapi layer is enabled, otherwise false.
	 */
	boolean isEnabled();

	/**
	 * Get all enabled reader.
	 *
	 * @return List of reader, otherwise empty list.
	 */
	List<OmapiReader> getReader();

	/**
	 * Closes and destroys the currently allocated implementation instance.
	 */
	void close();
}

/*
 * \copyright Copyright (c) 2015-2019 Governikus GmbH & Co. KG, Germany
 */

#pragma once

#include "messages/BluetoothMessage.h"
#include "parameter/BluetoothMessageParameterCardReaderStatus.h"

namespace governikus
{

class BluetoothMessageTransferCardReaderStatusResponse
	: public BluetoothMessage
{
	public:
		BluetoothMessageTransferCardReaderStatusResponse();
		virtual ~BluetoothMessageTransferCardReaderStatusResponse();

		BluetoothStatusChange getStatusChange() const;
};

} // namespace governikus

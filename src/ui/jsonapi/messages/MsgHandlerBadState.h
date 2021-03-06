/*!
 * \brief Message BadState of JSON API.
 *
 * \copyright Copyright (c) 2016-2019 Governikus GmbH & Co. KG, Germany
 */

#pragma once

#include "MsgHandler.h"

namespace governikus
{

class MsgHandlerBadState
	: public MsgHandler
{
	public:
		MsgHandlerBadState(MsgCmdType pType = MsgCmdType::UNDEFINED);
};


} // namespace governikus

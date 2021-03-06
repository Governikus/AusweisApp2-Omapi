/*!
 * \copyright Copyright (c) 2014-2019 Governikus GmbH & Co. KG, Germany
 */

#pragma once

#include "context/AuthContext.h"
#include "states/AbstractGenericState.h"

namespace governikus
{

class StateEACAdditionalInputType
	: public AbstractGenericState<AuthContext>
{
	Q_OBJECT
	friend class StateBuilder;

	explicit StateEACAdditionalInputType(const QSharedPointer<WorkflowContext>& pContext);
	virtual void run() override;

	Q_SIGNALS:
		void fireSendDidAuthenticatResponse();
};

} // namespace governikus

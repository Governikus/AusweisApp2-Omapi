/*!
 * \brief Implementation of \ref Reader for Omapi.
 *
 * \copyright Copyright (c) 2015-2019 Governikus GmbH & Co. KG, Germany
 */

#pragma once

#include "OmapiCard.h"
#include "Reader.h"

#include <QScopedPointer>

namespace governikus
{

class OmapiReader
	: public ConnectableReader
{
	Q_OBJECT

	private:
		bool mConnected;
		QScopedPointer<OmapiCard> mOmapiCard;

	public:
		OmapiReader();
		virtual ~OmapiReader() override = default;

		virtual void connectReader() override;
		virtual void disconnectReader() override;

		virtual Card* getCard() const override;
		virtual CardEvent updateCard() override;
};

} // namespace governikus

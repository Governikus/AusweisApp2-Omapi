/*!
 * \brief Implementation of \ref Card for Omapi.
 *
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

#pragma once

#include "Card.h"

#ifdef Q_OS_ANDROID
#include <QAndroidJniEnvironment>
#include <QAndroidJniObject>
#include <QtAndroid>
#endif

namespace governikus
{
class OmapiCard
	: public Card
{
	Q_OBJECT

	private:
		bool mIsValid;
		bool mConnected;

#ifdef Q_OS_ANDROID
		QByteArray convert(const jbyteArray& pData);
		jbyteArray convert(const QByteArray& pData);
		QByteArray sendData(const QByteArray& pData);
#endif

	public:
		OmapiCard();
		virtual ~OmapiCard() override = default;

		bool isValid() const;

		virtual CardReturnCode connect() override;
		virtual CardReturnCode disconnect() override;
		virtual bool isConnected() override;

		virtual CardReturnCode transmit(const CommandApdu& pCmd, ResponseApdu& pRes) override;

		virtual CardReturnCode establishPaceChannel(PacePasswordId pPasswordId, const QByteArray& pChat, const QByteArray& pCertificateDescription, EstablishPaceChannelOutput& pChannelOutput, quint8 pTimeoutSeconds) override;

		virtual CardReturnCode destroyPaceChannel() override;
};

} // namespace governikus

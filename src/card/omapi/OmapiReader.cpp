/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

#include "CardConnectionWorker.h"
#include "OmapiReader.h"

#include <QLoggingCategory>


using namespace governikus;


Q_DECLARE_LOGGING_CATEGORY(card_omapi)


OmapiReader::OmapiReader()
	: ConnectableReader(ReaderManagerPlugInType::OMAPI, QStringLiteral("omapi"))
	, mConnected(false)
{
	mReaderInfo.setBasicReader(false);
	mReaderInfo.setConnected(true);
	QMetaObject::invokeMethod(this, &Reader::update, Qt::QueuedConnection);
}


void OmapiReader::connectReader()
{
	mConnected = true;

	qCDebug(card_omapi) << "create card";
	mOmapiCard.reset(new OmapiCard);
	QSharedPointer<CardConnectionWorker> cardConnection = createCardConnectionWorker();
	CardInfoFactory::create(cardConnection, mReaderInfo);
	Q_EMIT fireCardInserted(getName());
}


void OmapiReader::disconnectReader()
{
	mConnected = false;
	mOmapiCard.reset(new OmapiCard);
}


Card* OmapiReader::getCard() const
{
	return mOmapiCard.data();
}


Reader::CardEvent OmapiReader::updateCard()
{
	return Reader::CardEvent::NONE;
}

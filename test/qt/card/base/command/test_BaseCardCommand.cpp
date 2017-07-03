/*!
 * \brief Unit tests for \ref BaseCardCommand
 *
 * \copyright Copyright (c) 2015 Governikus GmbH & Co. KG
 */

#include "command/BaseCardCommand.h"

#include "MockReader.h"

#include <QtCore>
#include <QtTest>

using namespace governikus;

class BaseCardCommandDummy
	: public BaseCardCommand
{
	Q_OBJECT

	public:
		BaseCardCommandDummy(Reader* pReader)
			: BaseCardCommand(CardConnectionWorker::create(pReader))
		{

		}


		virtual void internalExecute()
		{
			mReturnCode = ReturnCode::OK;
		}


};

class test_BaseCardCommand
	: public QObject
{
	Q_OBJECT

	private Q_SLOTS:
		void commandDone()
		{
			MockReader reader("dummy reader");
			BaseCardCommandDummy command(&reader);
			QCOMPARE(command.getReturnCode(), ReturnCode::UNKNOWN);

			QSignalSpy spy(&command, &BaseCardCommand::commandDone);
			QMetaObject::invokeMethod(&command, "execute");

			QCOMPARE(spy.count(), 1);
			auto param = spy.takeFirst();
			QSharedPointer<BaseCardCommand> sharedCommand = param.at(0).value<QSharedPointer<BaseCardCommand> >();
			QCOMPARE(sharedCommand.data(), &command);
			QCOMPARE(command.getReturnCode(), ReturnCode::OK);
		}


		void checkRetryCounterAndPrepareForPaceNoCard()
		{
			MockReader reader("dummy reader");
			BaseCardCommandDummy command(&reader);
			QCOMPARE(command.checkRetryCounterAndPrepareForPace("test"), ReturnCode::NO_CARD);
		}


};

QTEST_GUILESS_MAIN(test_BaseCardCommand)
#include "test_BaseCardCommand.moc"
/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

#include "OmapiReaderManagerPlugIn.h"

#include <QLoggingCategory>

#ifdef Q_OS_ANDROID
#include <QAndroidJniEnvironment>
#include <QAndroidJniObject>
#include <QtAndroid>
#endif

using namespace governikus;


Q_DECLARE_LOGGING_CATEGORY(card_omapi)

OmapiReaderManagerPlugIn::OmapiReaderManagerPlugIn()
	: ReaderManagerPlugIn(ReaderManagerPlugInType::OMAPI, true, true)
	, mEnabled(false)
	, mReader(new OmapiReader)
{
	connect(mReader.data(), &OmapiReader::fireCardInserted, this, &OmapiReaderManagerPlugIn::fireCardInserted);
	connect(mReader.data(), &OmapiReader::fireCardRemoved, this, &OmapiReaderManagerPlugIn::fireCardRemoved);
	connect(mReader.data(), &OmapiReader::fireCardRetryCounterChanged, this, &OmapiReaderManagerPlugIn::fireCardRetryCounterChanged);
	connect(mReader.data(), &OmapiReader::fireReaderPropertiesUpdated, this, &OmapiReaderManagerPlugIn::fireReaderPropertiesUpdated);
	qCDebug(card_omapi) << "Add reader" << mReader->getName();
}


OmapiReaderManagerPlugIn::~OmapiReaderManagerPlugIn()
{
}


QList<Reader*> OmapiReaderManagerPlugIn::getReaders() const
{
	qCDebug(card_omapi) << "get my readers";
	if (mEnabled)
	{
		return QList<Reader*>({mReader.data()});
	}

	return QList<Reader*>();
}


void OmapiReaderManagerPlugIn::startScan(bool)
{
	qCDebug(card_omapi) << "scan me";
	mEnabled = true;
	mReader->connectReader();
	Q_EMIT fireReaderAdded(mReader->getName());
}


void OmapiReaderManagerPlugIn::stopScan()
{
	mEnabled = false;
	mReader->disconnectReader();
	Q_EMIT fireReaderRemoved(mReader->getName());
}


void OmapiReaderManagerPlugIn::init()
{
	ReaderManagerPlugIn::init();
#ifdef Q_OS_ANDROID
	qCDebug(card_omapi) << "omapi init";

	QAndroidJniEnvironment env;
	const QAndroidJniObject context(QtAndroid::androidContext());
	if (!context.isValid())
	{
		qCritical() << "Cannot determine android context.";
		return;
	}

	QAndroidJniObject::callStaticMethod<jboolean>("com/governikus/ausweisapp2/omapi/impl/OmapiJNI",
			"init",
			"(Landroid/content/Context;)Z",
			context.object<jobject>());

	if (env->ExceptionCheck())
	{
		qCritical() << "Cannot call OmapiJNI.init()";
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
#endif
}


void OmapiReaderManagerPlugIn::shutdown()
{
#ifdef Q_OS_ANDROID
	QAndroidJniEnvironment env;
	QAndroidJniObject::callStaticMethod<void>("com/governikus/ausweisapp2/omapi/impl/OmapiJNI", "close");
	if (env->ExceptionCheck())
	{
		qCritical() << "Cannot call OmapiJNI.close()";
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
#endif
}

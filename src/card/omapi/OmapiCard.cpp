/*!
 * \copyright Copyright (c) 2019 Governikus GmbH & Co. KG, Germany
 */

#include "OmapiCard.h"

#include "DestroyPaceChannel.h"
#include "EstablishPaceChannel.h"

#include <QLoggingCategory>

using namespace governikus;


Q_DECLARE_LOGGING_CATEGORY(card_omapi)


OmapiCard::OmapiCard()
	: Card()
	, mIsValid(true)
	, mConnected(false)
{
}


bool OmapiCard::isValid() const
{
	return mIsValid;
}


CardReturnCode OmapiCard::connect()
{
	mConnected = true;
	return CardReturnCode::OK;
}


CardReturnCode OmapiCard::disconnect()
{
	mConnected = false;
	return CardReturnCode::OK;
}


bool OmapiCard::isConnected()
{
	return mConnected;
}


#ifdef Q_OS_ANDROID
QByteArray OmapiCard::sendData(const QByteArray& pData)
{
	QByteArray ret;
	QAndroidJniEnvironment env;
	jbyteArray cmd = convert(pData);

	const QAndroidJniObject javaActivity(QtAndroid::androidActivity());
	if (!javaActivity.isValid())
	{
		qCritical() << "Cannot determine android activity";
		return ret;
	}

	const auto response = QAndroidJniObject::callStaticObjectMethod("com/governikus/ausweisapp2/omapi/impl/OmapiJNI",
			"transmit",
			"([B)[B",
			cmd);

	if (!response.isValid())
	{
		return ret;
	}

	ret = convert(static_cast<jbyteArray>(response.object()));
	if (env->ExceptionCheck())
	{
		qCritical() << "Cannot call Omapi.transmit()";
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
	env->DeleteLocalRef(cmd);

	return ret;
}


QByteArray OmapiCard::convert(const jbyteArray& pData)
{
	QAndroidJniEnvironment env;

	jsize size = env->GetArrayLength(pData);
	QVector<jbyte> buffer(size);
	env->GetByteArrayRegion(pData, 0, size, buffer.data());

	return QByteArray(reinterpret_cast<char*>(buffer.data()), buffer.size());
}


jbyteArray OmapiCard::convert(const QByteArray& pData)
{
	QAndroidJniEnvironment env;
	const int size = pData.size();
	const char* buffer = pData.constData();

	jbyteArray target = env->NewByteArray(size);
	jbyte* bytes = env->GetByteArrayElements(target, 0);
	for (int i = 0; i < size; ++i)
	{
		bytes[i] = static_cast<jbyte>(buffer[i]);
	}
	env->SetByteArrayRegion(target, 0, size, bytes);

	return target;
}


CardReturnCode OmapiCard::establishPaceChannel(PacePasswordId pPasswordId,
		const QByteArray& pChat,
		const QByteArray& pCertificateDescription,
		EstablishPaceChannelOutput& pChannelOutput, quint8 pTimeoutSeconds)
{
	Q_UNUSED(pTimeoutSeconds);

	EstablishPaceChannel builder;
	builder.setPasswordId(pPasswordId);
	builder.setChat(pChat);
	builder.setCertificateDescription(pCertificateDescription);

	QAndroidJniEnvironment env;
	jbyteArray paceData = convert(builder.createCommandData());

	const QAndroidJniObject javaActivity(QtAndroid::androidActivity());
	if (!javaActivity.isValid())
	{
		qCritical() << "Cannot determine android activity";
		return CardReturnCode::COMMAND_FAILED;
	}

	const auto response = QAndroidJniObject::callStaticObjectMethod("com/governikus/ausweisapp2/omapi/impl/OmapiJNI",
			"paceControl",
			"([B)[B",
			paceData);

	if (!response.isValid())
	{
		return CardReturnCode::COMMAND_FAILED;
	}

	if (env->ExceptionCheck())
	{
		qCritical() << "Cannot call Omapi.paceControl()";
		env->ExceptionDescribe();
		env->ExceptionClear();
	}

	env->DeleteLocalRef(paceData);
	paceData = static_cast<jbyteArray>(response.object());
	if (env->IsSameObject(paceData, NULL))
	{
		qCritical() << "Control to establish PACE channel failed";
		return CardReturnCode::COMMAND_FAILED;
	}

	pChannelOutput.parse(convert(paceData), pPasswordId);
	return pChannelOutput.getPaceReturnCode();
}


CardReturnCode OmapiCard::destroyPaceChannel()
{
	// TODO: to be completed, for now PACE channel is destroyed implicitly on establish()
	return CardReturnCode::OK;
}


#endif

CardReturnCode OmapiCard::transmit(const CommandApdu& pCmd, ResponseApdu& pRes)
{
#ifdef Q_OS_ANDROID
	const auto& data = pCmd.getBuffer();
	qCDebug(card_omapi) << "transmit to omapi:" << data;
	const auto& response = sendData(data);
	qCDebug(card_omapi) << "received from omapi:" << response;
	pRes.setBuffer(response);

#else
	Q_UNUSED(pCmd)
	Q_UNUSED(pRes)
#endif
	return CardReturnCode::OK;
}

/*!
 * \brief Implementation of \ref ReaderManagerPlugIn for Omapi on Android.
 *
 * \copyright Copyright (c) 2015-2019 Governikus GmbH & Co. KG, Germany
 */

#pragma once

#include "OmapiReader.h"
#include "ReaderManagerPlugIn.h"

#include <QScopedPointer>

namespace governikus
{

class OmapiReaderManagerPlugIn
	: public ReaderManagerPlugIn
{
	Q_OBJECT
	Q_PLUGIN_METADATA(IID "governikus.ReaderManagerPlugIn" FILE "metadata.json")
	Q_INTERFACES(governikus::ReaderManagerPlugIn)

	private:
		bool mEnabled;
		QScopedPointer<OmapiReader> mReader;

	public:
		OmapiReaderManagerPlugIn();
		virtual ~OmapiReaderManagerPlugIn() override;

		virtual QList<Reader*> getReaders() const override;

		virtual void startScan(bool pAutoConnect) override;
		virtual void stopScan() override;

		virtual void init() override;
		virtual void shutdown() override;
};

} // namespace governikus

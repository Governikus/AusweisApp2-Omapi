/*!
 * \brief Qt UI for the advise user to remove card step.
 *
 * \copyright Copyright (c) 2014-2019 Governikus GmbH & Co. KG, Germany
 */

#pragma once

#include "context/AuthContext.h"
#include "StepGui.h"

#include <QMessageBox>
#include <QSharedPointer>
#include <QTimer>

namespace governikus
{

class StepAdviseUserToRemoveCardGui
	: public StepGui
{
	Q_OBJECT

	public Q_SLOTS:
		void onReaderManagerSignal();

	public:
		StepAdviseUserToRemoveCardGui(QSharedPointer<WorkflowContext> pContext, QWidget* const pMainWidget);
		virtual ~StepAdviseUserToRemoveCardGui() override;

		virtual void activate() override;
		void closeActiveDialogs();

	private:
		QSharedPointer<WorkflowContext> mContext;
		QWidget* const mMainWidget;
		QPointer<QMessageBox> mMessageBox;
		QTimer mMessageTimeoutTimer;
};

} // namespace governikus

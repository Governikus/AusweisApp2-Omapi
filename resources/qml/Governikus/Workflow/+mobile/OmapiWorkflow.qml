import QtQuick 2.10
import QtQuick.Layouts 1.1

import Governikus.Global 1.0
import Governikus.TechnologyInfo 1.0
import Governikus.Type.ApplicationModel 1.0
import Governikus.Type.ReaderPlugIn 1.0
import Governikus.Type.NumberModel 1.0


Item {
	id: baseItem
	signal requestPluginType(int pReaderPlugInType)	
	property int waitingFor: 0
	clip: true

	ProgressIndicator {
		id: progressIndicator
		anchors.left: parent.left
		anchors.top: parent.top
		anchors.right: parent.right
		height: parent.height / 2
		imageIconSource: "qrc:///images/icon_omapi.svg"
		imagePhoneSource: "qrc:///images/phone_omapi.svg"
		state: baseItem.waitingFor === Workflow.WaitingFor.Reader ? "off" : "one"
	}

	TechnologyInfo {
		id: technologyInfo

		anchors.left: parent.left
		anchors.leftMargin: Utils.dp(5)
		anchors.right: parent.right
		anchors.rightMargin: anchors.leftMargin
		anchors.top: progressIndicator.bottom
		anchors.bottom: technologySwitch.top
		clip: true

		enableButtonVisible: baseItem.waitingFor !== Workflow.WaitingFor.Reader
		enableButtonText: qsTr("Continue") + settingsModel.translationTrigger
		enableText: (enableButtonVisible ? qsTr("Please confirm the usage of your mobile eID.") : "") + settingsModel.translationTrigger
		onEnableClicked: workflowModel.continueWorkflow()
		titleText: qsTr("No mobile eID found") + settingsModel.translationTrigger
		subTitleText: qsTr("Please ensure that a SIM card is inserted and a suitable mobile eID is stored there.") + settingsModel.translationTrigger
	}

	TechnologySwitch {
		id: technologySwitch
		anchors.left: parent.left
		anchors.right: parent.right
		anchors.bottom: parent.bottom
		selectedTechnology: ReaderPlugIn.OMAPI
		onRequestPluginType: parent.requestPluginType(pReaderPlugInType)
	}
}

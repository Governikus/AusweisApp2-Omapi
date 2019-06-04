import QtQuick 2.10

import Governikus.Global 1.0
import Governikus.Type.ReaderPlugIn 1.0

Rectangle {
	id: baseItem
	height: technologyRow.height
	color: Constants.background_color

	signal requestPluginType(int pReaderPlugInType)

	property int selectedTechnology

	Row {
		id: technologyRow
		spacing: Utils.dp(20)
		anchors.bottom: parent.bottom
		anchors.horizontalCenter: parent.horizontalCenter

		TechnologySwitchButton {
			buttonActive: selectedTechnology !== ReaderPlugIn.NFC
			onClicked: baseItem.requestPluginType(ReaderPlugIn.NFC)
			imageSource: "qrc:///images/icon_nfc.svg"
			text: qsTr("NFC") + settingsModel.translationTrigger
		}

		TechnologySwitchButton {
			buttonActive: selectedTechnology !== ReaderPlugIn.REMOTE
			onClicked: baseItem.requestPluginType(ReaderPlugIn.REMOTE)
			imageSource: "qrc:///images/icon_remote.svg"
			text: qsTr("WiFi") + settingsModel.translationTrigger
		}

		TechnologySwitchButton {
			buttonActive: selectedTechnology !== ReaderPlugIn.BLUETOOTH
			onClicked: baseItem.requestPluginType(ReaderPlugIn.BLUETOOTH)
			imageSource: "qrc:///images/icon_bluetooth.svg"
			text: qsTr("Bluetooth") + settingsModel.translationTrigger
		}

		TechnologySwitchButton {
			buttonActive: selectedTechnology !== ReaderPlugIn.OMAPI
			onClicked: baseItem.requestPluginType(ReaderPlugIn.OMAPI)
			imageSource: "qrc:///images/icon_omapi.svg"
			text: qsTr("meID") + settingsModel.translationTrigger
		}
	}
}

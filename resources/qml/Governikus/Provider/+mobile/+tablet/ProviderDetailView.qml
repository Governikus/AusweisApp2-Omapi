import QtQuick 2.10

import Governikus.Global 1.0
import Governikus.Provider 1.0
import Governikus.TitleBar 1.0
import Governikus.View 1.0

SectionPage {
	id: baseItem
	readonly property TitleBarAction leftTitleBarAction: TitleBarAction {
		state: "back"
		onClicked: {
			if (providerDetailsHistoryInfo.visible) {
				providerDetailsHistoryInfo.visible = false
			}
			else {
				firePop()
			}
		}
	}
	readonly property TitleBarAction headerTitleBarAction: TitleBarAction {
		text: historyModelItem && historyModelItem.subject ? historyModelItem.subject : provider.shortName
		font.bold: true
	}
	readonly property Item rightTitleBarAction: Item {}
	readonly property color titleBarColor: Category.displayColor(provider.category)
	readonly property real titleBarOpacity: 1

	property alias historyModelItem: provider.modelItem
	property alias providerModelItem: provider.modelItem
	ProviderModelItem {
		id: provider
	}


	content: Column {
		id: mainContent
		height: childrenRect.height + Constants.component_spacing
		width: baseItem.width

		Row {
			height: baseItem.height / 2
			width: parent.width

			Item {
				height: parent.height
				width: baseItem.width * 2 / 3
				anchors.top: parent.top

				Image {
					id: image
					source: provider.image
					asynchronous: true
					height: parent.height
					fillMode: Image.PreserveAspectFit
					anchors.left: parent.left
					anchors.verticalCenter: parent.verticalCenter
				}

				Image {
					height: parent.height
					width: height / 2
					anchors.right: image.right
					anchors.top: parent.top
					fillMode: Image.Stretch
					source: Category.gradientImageSource(provider.category)
				}

				Rectangle {
					anchors.left: image.right
					anchors.right: parent.right
					anchors.top: parent.top
					height: parent.height
					color: baseItem.titleBarColor
				}
			}

			Rectangle {
				height: parent.height
				width: baseItem.width / 3
				color: baseItem.titleBarColor

				ProviderContactInfo {
					color: baseItem.titleBarColor
					height: parent.height
					width: baseItem.width / 3 - Constants.component_spacing

					contactModel: provider.contactModel
				}
			}
		}

		Row {
			id: lowerRow
			height: Math.max(buttonBar.height + leftColumn.height, rightColumn.height) + 3 * Constants.pane_padding
			width: parent.width

			Item {
				height: 1
				width: lowerRow.width * 2 / 3

				ProviderDetailButtonBar {
					id: buttonBar
					selectedCategory: provider.category
					providerIcon: provider.icon
					address: provider.address
					titleBarColor: baseItem.titleBarColor
				}

				Pane {
					id: leftPane
					anchors.margins: Constants.component_spacing
					anchors.top: buttonBar.bottom
					height: lowerRow.height - (buttonBar.height + Constants.pane_padding)
				}

				ProviderDetailDescription {
					id: leftColumn
					anchors.margins: 2 * Constants.pane_padding
					anchors.top: buttonBar.bottom
					anchors.left: parent.left
					anchors.right: parent.right

					description: provider.longDescription
				}
			}

			Item {
				height: 1
				width: lowerRow.width / 3 - Constants.component_spacing

				Pane {
					id: rightPane
					anchors.topMargin: Constants.component_spacing
					anchors.top: parent.top
					height: lowerRow.height - Constants.pane_padding
				}

				ProviderDetailHistory {
					id: rightColumn
					anchors.topMargin: 2 * Constants.pane_padding
					anchors.leftMargin: Constants.pane_padding
					anchors.rightMargin: Constants.pane_padding
					anchors.top: parent.top
					anchors.left: parent.left
					anchors.right: parent.right

					openHistoryInfoFunc: baseItem.openHistoryInfoFunc
				}
			}
		}
	}

	property var openHistoryInfoFunc: function(entryInfo) {
		providerDetailsHistoryInfo.visible = true

		providerDetailsHistoryInfo.providerName = entryInfo['providerName']
		providerDetailsHistoryInfo.providerPostalAddress = entryInfo['providerPostalAddress']
		providerDetailsHistoryInfo.purposeText = entryInfo['purposeText']
		providerDetailsHistoryInfo.requestedDataText = entryInfo['requestedDataText']
		providerDetailsHistoryInfo.termsOfUsageText = entryInfo['termsOfUsageText']
	}

	ProviderDetailHistoryInfo {
		id: providerDetailsHistoryInfo

		height: parent.height
		width: parent.width

		anchors.top: baseItem.top
		anchors.left: baseItem.left

		visible: false
	}
}

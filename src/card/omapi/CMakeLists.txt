#####################################################################
# The ReaderManagerPlugin for OMAPI.
#
# This plugin is a wrapper for the Virtual eID on a SIM card.
# Supported: Android.
#####################################################################

ADD_PLATFORM_LIBRARY(AusweisAppCardOmapi)

TARGET_LINK_LIBRARIES(AusweisAppCardOmapi Qt5::Core AusweisAppGlobal AusweisAppCard)
TARGET_COMPILE_DEFINITIONS(AusweisAppCardOmapi PRIVATE QT_STATICPLUGIN)

IF(ANDROID)
	TARGET_LINK_LIBRARIES(AusweisAppCardOmapi Qt5::AndroidExtras)
ENDIF()

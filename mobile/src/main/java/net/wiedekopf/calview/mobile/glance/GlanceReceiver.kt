package net.wiedekopf.calview.mobile.glance

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class GlanceReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = GlanceWidget()

}
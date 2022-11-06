package info.nightscout.implementation.queue.commands

import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.interfaces.Profile
import info.nightscout.androidaps.interfaces.PumpSync
import info.nightscout.androidaps.queue.Callback
import info.nightscout.androidaps.queue.commands.Command
import info.nightscout.implementation.R
import info.nightscout.rx.logging.LTag

import javax.inject.Inject

class CommandTempBasalAbsolute(
    injector: HasAndroidInjector,
    private val absoluteRate: Double,
    private val durationInMinutes: Int,
    private val enforceNew: Boolean,
    private val profile: Profile,
    private val tbrType: PumpSync.TemporaryBasalType,
    callback: Callback?
) : Command(injector, CommandType.TEMPBASAL, callback) {

    @Inject lateinit var activePlugin: ActivePlugin

    override fun execute() {
        val r = activePlugin.activePump.setTempBasalAbsolute(absoluteRate, durationInMinutes, profile, enforceNew, tbrType)
        aapsLogger.debug(LTag.PUMPQUEUE, "Result rate: $absoluteRate durationInMinutes: $durationInMinutes success: ${r.success} enacted: ${r.enacted}")
        callback?.result(r)?.run()
    }

    override fun status(): String = rh.gs(R.string.temp_basal_absolute, absoluteRate, durationInMinutes)

    override fun log(): String = "TEMP BASAL $absoluteRate U/h $durationInMinutes min"
}
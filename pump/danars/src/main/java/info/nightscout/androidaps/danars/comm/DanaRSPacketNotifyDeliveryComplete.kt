package info.nightscout.androidaps.danars.comm

import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.dana.DanaPump
import info.nightscout.androidaps.danars.R
import info.nightscout.androidaps.danars.encryption.BleEncryption
import info.nightscout.androidaps.interfaces.ResourceHelper
import info.nightscout.androidaps.plugins.general.overview.events.EventOverviewBolusProgress
import info.nightscout.rx.bus.RxBus
import info.nightscout.rx.logging.LTag
import javax.inject.Inject
import kotlin.math.min

class DanaRSPacketNotifyDeliveryComplete(
    injector: HasAndroidInjector
) : DanaRSPacket(injector) {

    @Inject lateinit var rxBus: RxBus
    @Inject lateinit var rh: ResourceHelper
    @Inject lateinit var danaPump: DanaPump

    init {
        type = BleEncryption.DANAR_PACKET__TYPE_NOTIFY
        opCode = BleEncryption.DANAR_PACKET__OPCODE_NOTIFY__DELIVERY_COMPLETE
        aapsLogger.debug(LTag.PUMPCOMM, "New message")
    }

    override fun handleMessage(data: ByteArray) {
        val deliveredInsulin = byteArrayToInt(getBytes(data, DATA_START, 2)) / 100.0
        danaPump.bolusingTreatment?.insulin = deliveredInsulin
        val bolusingEvent = EventOverviewBolusProgress
        bolusingEvent.status = rh.gs(R.string.bolusdelivering, deliveredInsulin)
        bolusingEvent.t = danaPump.bolusingTreatment
        bolusingEvent.percent = min((deliveredInsulin / danaPump.bolusAmountToBeDelivered * 100).toInt(), 100)
        danaPump.bolusDone = true
        rxBus.send(bolusingEvent)
        aapsLogger.debug(LTag.PUMPCOMM, "Delivered insulin: $deliveredInsulin")
    }

    override val friendlyName: String = "NOTIFY__DELIVERY_COMPLETE"
}
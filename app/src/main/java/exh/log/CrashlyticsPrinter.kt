package exh.log

import com.elvishew.xlog.printer.Printer

// SY: Firebase disabled - logging to Crashlytics removed
class CrashlyticsPrinter(private val logLevel: Int) : Printer {
    override fun println(logLevel: Int, tag: String?, msg: String?) {
        // Firebase disabled, no-op
    }
}

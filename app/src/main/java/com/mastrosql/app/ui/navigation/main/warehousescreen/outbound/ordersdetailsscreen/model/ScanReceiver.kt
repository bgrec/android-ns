package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.ordersdetailsscreen.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast action that is sent when a barcode is scanned.
 */
const val ACTION_DATA_CODE_RECEIVED: String = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
private const val DATA = "data"
private const val SOURCE = "source_byte"

/**
 * Class that listens for the scan broadcast and calls the provided lambda with the scanned code.
 */
class ScanReceiver(private val onScanReceived: (String) -> Unit) : BroadcastReceiver() {

    /**
     * Called when a broadcast is received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve the scanned code as a String
        val scannedCode = intent.getStringExtra(DATA) ?: return
        onScanReceived(scannedCode)

//        // Retrieve the scanned code as a Byte array
//        val scannedCodeByteArray = intent.getByteArrayExtra(SOURCE) ?: return
    }
}

/**
 * Register the ScanReceiver to listen for the scan broadcast.
 *
 * https://developer.sunmi.com/docs/read/en-US/frmeghjk546
 */

/**
 * For Sumni L2s pro
 *A USB barcode scanner functions like a USB keyboard, which can only collect data. Two collection
 * methods are available (alternative, please set according to your needs. KeyEvent
 * is used by default):Method 1: KeyEvent. Use dispacthKeyEvent.
 *
 * Method 2: Broadcast. While using this mode, data cannot be filled onto the input box shown on
 * the App interface like using a keyboard. Please switch receive mode and collect data scanned
 * through broadcast following the instructions below.
 *
 * a. Switch Receive Mode:
 *
 * Method 1: “Settings”->change “Barcode Scanning and Keyboard”
 * into “Do Not Output” + “Broadcast Output”
 *
 * Method 2 (recommended): action:com.sunmi.scanner.ACTION_BAR_DEVICES_SETTING
 * <p>field descriptions:
 */


/**
 * 2.  Brocadcast introduce
 *
 * Listening broadcast："com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
 *
 * Field description:
 *
 * data: character data;
 *
 * source_byte: Byte array raw data (Excluding the basic settings such as End Character,
 *
 * CodeID, suffix and advanced settings, etc., requires version 2.3.1 or higher);
 *
 * Example：
 *
 * private static final String ACTION_DATA_CODE_RECEIVED =
 *  "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
 * private static final String DATA = "data";
 * private static final String SOURCE = "source_byte";
 *
 * private BroadcastReceiver receiver = new BroadcastReceiver() {
 *
 *         @Override
 *
 *         public void onReceive(Context context, Intent intent) {
 *
 *            String code = intent.getStringExtra(DATA);
 *
 * String arr = intent.getByteArrayExtra(SOURCE);
 * if (code != null && !code.isEmpty()) {
 *             mCode.setText(code);
 *             }
 *         }
 *     };
 * private void registerReceiver() {
 *
 *    IntentFilter filter = new IntentFilter();
 *
 *     filter.addAction(ACTION_DATA_CODE_RECEIVED);
 *     registerReceiver(receiver, filter);
 *     }
 *  */


/* For zebra https://github.com/darryncampbell/DataWedgeKotlin*/

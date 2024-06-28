@file:Suppress("KDocMissingDocumentation")

package com.mastrosql.app.scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

/**
 * Broadcast action that is sent when a barcode is scanned.
 */
@Suppress("KDocMissingDocumentation")
const val ACTION_DATA_CODE_RECEIVED_SUNMI: String = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
const val ACTION_DATA_CODE_RECEIVED_ZEBRA: String = "com.mastrosql.app.scanner.SCAN"
private const val DATA_SUNMI = "data"
private const val DATA_ZEBRA = "com.symbol.datawedge.data_string"

/**
 * Class that listens for the scan broadcast and calls the provided lambda with the scanned code.
 */
class ScanReceiver(private val onScanReceived: (String) -> Unit) : BroadcastReceiver() {

    /**
     * Called when a broadcast is received.
     */

    override fun onReceive(context: Context, intent: Intent) {

        // Retrieve the action of the intent
        val action = intent.action

        // Retrieve the scanned code as a String
        val scannedCode: String? = when (action) {
            // Retrieve the scanned code from the intent for Sunmi devices
            ACTION_DATA_CODE_RECEIVED_SUNMI -> intent.getStringExtra(DATA_SUNMI)

            // Retrieve the scanned code from the intent for Zebra devices
            ACTION_DATA_CODE_RECEIVED_ZEBRA -> intent.getStringExtra(DATA_ZEBRA)
            else -> null
        }
        scannedCode?.let {
            onScanReceived(it)
        }
    }

    /**
     * Register the ScanReceiver to listen for the scan broadcast.
     */
    fun register(context: Context) {

        val intentFilter = IntentFilter().apply {
            addAction(ACTION_DATA_CODE_RECEIVED_SUNMI)
            addAction(ACTION_DATA_CODE_RECEIVED_ZEBRA)
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        // Register the receiver to listen for the scan broadcast
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(this, intentFilter, Context.RECEIVER_EXPORTED)
        }
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


/**
 *  For zebra https://github.com/darryncampbell/DataWedgeKotlin
 *
 * Open the DataWedge app on the Zebra device.
 * Create a new profile or select an existing one.
 *      Create a Profile:
 *          example: "MastroSql app"
 *
 * Assign your app to the profile under the "Associated apps" section.
 *          example: "com.mastrosql.app.dev"
 * Configure Intent Output:
 *
 * Under the created profile, go to "Intent Output".
 * Enable "Intent Output".
 *  (Disable "Keystroke output" if enabled if you want to not use keyboard input)
 *
 * Set "Intent action" to a custom action string, e.g., "com.mastrosql.app.SCAN".
 * Do not specify a "Intent category".
 * Set "Intent delivery" to BROADCAST.
 * Configure Barcode Input:
 *
 * Under the profile, go to "Barcode Input".
 * Enable "Barcode Input" and configure it as needed.
 */

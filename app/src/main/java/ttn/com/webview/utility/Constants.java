package ttn.com.webview.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @description This class act as a Interface, providing static values.or constants
 */
public class Constants {

    public static String mJsonUrl = "http://appcontent.hotelquickly.com/en/1/android/index.json";

    public static final String userIdKey = "{userId}";
    public static final String appSecretKey = "{appSecretKey}";
    public static final String currencyCodeKey = "{currencyCode}";
    public static final String offerIdKey = "{offerId}";
    public static final String selectedVoucherKey = "{selectedVouchers}";

    public static final String userIdKeyValue = "276";
    public static final String appSecretKeyValue = "gvx32RFZLNGhmzYrfDCkb9jypTPa8Q";
    public static final String currencyCodeKeyValue = "USD";
    public static final String offerIdKeyValue = "10736598";
    public static final String selectedVoucherKeyValue = "[]";

    public static final String folderName = "WebViewProject";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}

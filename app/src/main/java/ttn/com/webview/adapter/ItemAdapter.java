package ttn.com.webview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import ttn.com.webview.R;
import ttn.com.webview.model.ParseJSONUrlData;

/**
 * @description Adapter class to bind webview names
 */
public class ItemAdapter extends ArrayAdapter<ParseJSONUrlData> {

    public ItemAdapter(Context context, ArrayList<ParseJSONUrlData> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParseJSONUrlData user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item_adapter, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.txt_webview_name);
        // Populate the data into the template view using the data object
        tvName.setText(user.getPageTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}
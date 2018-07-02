package teampanther.developers.easyextractor.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import teampanther.developers.easyextractor.R;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    public TextView titleD, valueD, shareD, copyD;
    public DeviceViewHolder (View itemView) {
        super(itemView);
        titleD = itemView.findViewById(R.id.device_title);
        valueD = itemView.findViewById(R.id.device_description);
        shareD = itemView.findViewById(R.id.device_share);
        copyD = itemView.findViewById(R.id.device_copy);
    }
}

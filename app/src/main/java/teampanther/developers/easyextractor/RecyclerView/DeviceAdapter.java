package teampanther.developers.easyextractor.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import teampanther.developers.easyextractor.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder>{
    private LinkedList<DeviceInfo> items;
    private Context context;

    public DeviceAdapter(LinkedList<DeviceInfo> items, Context context){
        this.context = context;
        this.items = items;
    }



    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_card_info, parent, false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, final int position) {
        holder.titleD.setText(items.get(position).getNombre());
        holder.valueD.setText(items.get(position).getDesc());
        holder.shareD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getClipInfo(items.get(holder.getAdapterPosition())));
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });
        holder.copyD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clip = ClipData.newPlainText("text", getClipInfo(items.get(holder.getAdapterPosition())));
                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
            }
        });
    }

    public String getClipInfo(DeviceInfo info){
        return info.getNombre()+": "+info.getDesc();
    }


    @Override
    public int getItemCount() {
        return items != null ? items.size(): 0;
    }
}

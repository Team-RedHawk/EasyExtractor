package teampanther.developers.easyextractor.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import teampanther.developers.easyextractor.R;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder>{
    Context context;
    LayoutInflater inflater;
    LinkedList<DeviceInfo> items;

    public DeviceAdapter(LinkedList<DeviceInfo> items){

        this.items = items;
    }



    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_card_info, parent, false);

        DeviceViewHolder viewHolder=new DeviceViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.titleD.setText(items.get(position).getNombre());
        holder.valueD.setText(items.get(position).getDesc());
        holder.shareD.setOnClickListener(sharelistener);
        holder.copyD.setOnClickListener(copylistener);
    }

    View.OnClickListener sharelistener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //overyde

        }
    };

    View.OnClickListener copylistener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //overryde
        }
    };

    @Override
    public int getItemCount() {
        return items != null ? items.size(): 0;
    }
}

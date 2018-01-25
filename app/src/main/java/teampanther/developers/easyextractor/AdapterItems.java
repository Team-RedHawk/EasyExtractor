package teampanther.developers.easyextractor;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

public class AdapterItems extends ArrayAdapter {

    private Context context;
    private ArrayList<Items> test;
    private int resource;
    private ArrayList<Integer> mSelection = new ArrayList<Integer>();

    public AdapterItems(Context context, int resource,ArrayList<Items> test) {
        super(context, resource, test);

        this.context = context;
        this.test = test;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vista = convertView;
        ViewHolder holder;
        if (vista == null){

            //Declaro objetos que representaran vista de archivos
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vista = inflater.inflate(resource,null);

            holder = new ViewHolder();
            holder.file = (ImageView)vista.findViewById(R.id.folder_file);
            holder.nameFile = (TextView)vista.findViewById(R.id.name_file);
            holder.size = (TextView)vista.findViewById(R.id.sizeFile);
            holder.data = (TextView)vista.findViewById(R.id.metaData);
            holder.card = (CardView) vista.findViewById(R.id.cardView_list);

            vista.setTag(holder);

        }else{

            holder = (ViewHolder)vista.getTag();
        }

        holder.file.setImageResource(test.get(position).getImage());
        holder.nameFile.setText(test.get(position).getName());
        holder.size.setText(test.get(position).getSize());
        holder.data.setText(test.get(position).getMetadata());

        holder.card.setBackgroundColor(getContext().getResources().getColor(
                android.R.color.transparent));

        if (mSelection.contains(position)) {
            holder.card.setBackgroundColor(getContext().getResources().getColor(
                    android.R.color.tab_indicator_text)); // color when selected
        }
        return vista;
    }

    public void setNewSelection(int position) {
        mSelection.add(position);
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getCurrentCheckedPosition() {
        return mSelection;
    }

    public void removeSelection(int position) {
        mSelection.remove(Integer.valueOf(position));
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new ArrayList<Integer>();
        notifyDataSetChanged();
    }

    public int getSelectionCount() {
        return mSelection.size();
    }

    static class ViewHolder{

        ImageView file;
        TextView nameFile;
        TextView size;
        TextView data;
        CardView card;
    }

    public void removeAll(ArrayList<Integer> files) {
        ArrayList<Items> items= new ArrayList<>();
        for (int i=0; i<files.size();i++){
            items.add(test.get(files.get(i)));
        }
        for (int i=0; i<files.size(); i++){
            test.remove(items.get(i));
        }
        notifyDataSetChanged();
    }

    public void refreshEvents(ArrayList<Items> nuevos) {
        this.test = null;
        this.test= nuevos;
        notifyDataSetChanged();
    }
}


package com.teampanther.easyextractor;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterItems extends ArrayAdapter {

    Context context;
    Items[] items;
    int resource;

    public AdapterItems(Context context, int resource, Items[] items) {
        super(context, resource, items);

        this.context = context;
        this.items = items;
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

            vista.setTag(holder);

        }else{

            holder = (ViewHolder)vista.getTag();
        }

        holder.file.setImageResource(items[position].getImage());
        holder.nameFile.setText(items[position].getName());

        return vista;
    }

    static class ViewHolder{

        ImageView file;
        TextView nameFile;
    }
}

package com.earningfever.punjabidj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<String> songsNameList;
    private ArrayList<String> arraylist;

    public SearchListViewAdapter(Context context, List<String> animalNamesList) {
        mContext = context;
        this.songsNameList = animalNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(animalNamesList);
    }

    public class ViewHolder {
        TextView name;
    }

    public void addItems(ArrayList<String> postItems) {
        arraylist.addAll(postItems);
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return songsNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return songsNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.searching_item_rom, null);
            holder.name = view.findViewById(R.id.searchingSongName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(songsNameList.get(position));
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        songsNameList.clear();
        if (charText.length() == 0) {
            songsNameList.addAll(arraylist);
        } else {
            for (String wp : arraylist) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    songsNameList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

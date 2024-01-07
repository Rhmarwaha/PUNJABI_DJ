package com.earningfever.punjabidj;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomSingerPaginationAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private ArrayList<SingerModel> dataArrayList;
    private Context context;


    public CustomSingerPaginationAdapter(Context context, ArrayList<SingerModel> dataArrayList) {
        this.context = context;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.singers_item_row, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
        holder.onBind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingerSongsActivity.class);
                intent.putExtra("singer_details", dataArrayList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == dataArrayList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return dataArrayList == null ? 0 : dataArrayList.size();
    }

    public void addItems(ArrayList<SingerModel> postItems) {
        dataArrayList.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        dataArrayList.add(new SingerModel());
        notifyItemInserted(dataArrayList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataArrayList.size() - 1;
        SingerModel item = getItem(position);
        if (item != null) {
            dataArrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        dataArrayList.clear();
        notifyDataSetChanged();
    }

    SingerModel getItem(int position) {
        return dataArrayList.get(position);
    }


    //view holder
    public class ViewHolder extends BaseViewHolder {

        ImageView singer_image;
        TextView singer_name;

        ViewHolder(View itemView) {
            super(itemView);

            singer_image = itemView.findViewById(R.id.singer_image);
            singer_name = itemView.findViewById(R.id.singer_name);

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            SingerModel item = dataArrayList.get(position);
            singer_name.setText(item.getName());
            Picasso.get().load(item.getImageLink()).placeholder(R.drawable.image_error).error(R.drawable.image_error).fit().into(singer_image);


        }

        @Override
        protected void clear() {

        }
    }

    //Progress Holder
    public class ProgressHolder extends BaseViewHolder {
        ProgressBar progressBar;

        ProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        protected void clear() {
        }
    }
}

package com.earningfever.punjabidj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomVideoPlayerPaginationAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private ArrayList<Song> dataArrayList;
    private Context context;

    private DatabaseHelper db;




    public CustomVideoPlayerPaginationAdapter(Context context,ArrayList<Song> dataArrayList){
        this.context = context;
        this.dataArrayList = dataArrayList;
        db = new DatabaseHelper(context);
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new CustomVideoPlayerPaginationAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false));
            case VIEW_TYPE_LOADING:
                return new CustomVideoPlayerPaginationAdapter.ProgressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
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
                ((VideoPlayerActivity) context).onClickCalled(dataArrayList.get(position).getSongLinkId(),position);
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

    public void addItems(ArrayList<Song> postItems) {
        dataArrayList.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        dataArrayList.add(new Song());
        notifyItemInserted(dataArrayList.size() - 1);
    }
    public void removeLoading() {
        isLoaderVisible = false;
        int position = dataArrayList.size() - 1;
        Song item = getItem(position);
        if (item != null) {
            dataArrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        dataArrayList.clear();
        notifyDataSetChanged();
    }

    Song getItem(int position) {
        return dataArrayList.get(position);
    }



    //view holder
    public class ViewHolder extends BaseViewHolder{

        ImageView song_thumbnail;
        TextView song_name;
        Button song_favorite_button;

        ViewHolder(View itemView){
            super(itemView);

            song_thumbnail = itemView.findViewById(R.id.song_thumbnail);
            song_name = itemView.findViewById(R.id.song_name);
            song_favorite_button = itemView.findViewById(R.id.song_favorite_button);

            song_favorite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Song song = dataArrayList.get(position);
                    dataArrayList.get(position).setFavorite(song.isFavorite()? false:true);
                    song_favorite_button.setBackgroundResource(song.isFavorite()? R.drawable.favorite:R.drawable.favorite_border );
                    if(!song.isFavorite()){
                        db.deleteSong(song);
                        Toast.makeText(context, "Removed From Favourites", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.insertSong(song);
                    Toast.makeText(context, "Added To Favourites", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            Song item = dataArrayList.get(position);
            song_name.setText(item.getSongName());
            Picasso.get().load(item.getSongImageLink()).placeholder(R.drawable.image_error).error(R.drawable.image_error).fit().into(song_thumbnail);
            song_favorite_button.setBackgroundResource(item.isFavorite()? R.drawable.favorite:R.drawable.favorite_border );

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

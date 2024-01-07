package com.earningfever.punjabidj;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class CustomFavoriteAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    Context context;
    private ArrayList<Song> dataArrayList;
    private DatabaseHelper db;

    public CustomFavoriteAdapter(Context context , ArrayList<Song> dataArrayList) {

        this.context = context;
        this.dataArrayList = dataArrayList;
        this.db = new DatabaseHelper(context);
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomFavoriteAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
        holder.onBind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,VideoPlayerActivity.class);
                Song song = dataArrayList.get(position);
                intent.putExtra("song",  song);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
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

}

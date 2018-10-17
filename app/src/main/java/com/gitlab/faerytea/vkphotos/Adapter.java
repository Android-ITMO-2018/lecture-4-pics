package com.gitlab.faerytea.vkphotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Adapter extends RecyclerView.Adapter<Adapter.VH> {
    private final Bitmap[] data;

    public Adapter(int size) {
        this.data = new Bitmap[size];
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        vh.bind(data[i]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public void setElement(int pos, byte[] data) {
        this.data[pos] = data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
        notifyItemChanged(pos);
    }

    class VH extends RecyclerView.ViewHolder {
        final ProgressBar progress;
        final ImageView pic;

        VH(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress);
            pic = itemView.findViewById(R.id.pic);
        }

        void bind(Bitmap data) {
            if (data == null) {
                progress.setVisibility(View.VISIBLE);
                pic.setVisibility(View.GONE);
            } else {
                progress.setVisibility(View.GONE);
                pic.setVisibility(View.VISIBLE);
                pic.setImageBitmap(data);
            }
        }
    }
}

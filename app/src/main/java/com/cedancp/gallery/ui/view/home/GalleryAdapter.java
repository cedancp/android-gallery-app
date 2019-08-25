package com.cedancp.gallery.ui.view.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cedancp.gallery.R;
import com.cedancp.gallery.databinding.ImageItemBinding;
import com.cedancp.gallery.ui.model.ImageResponse;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<ImageItemViewHolder> {

    private List<ImageResponse> images;

    public GalleryAdapter(List<ImageResponse> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageItemBinding imageItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
            R.layout.image_item, parent, false);

        return new ImageItemViewHolder(imageItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemViewHolder holder, int position) {
        holder.imageItemBinding.setImage(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void updateImages(List<ImageResponse> imagesUpdate) {
        this.images = imagesUpdate;
    }
}

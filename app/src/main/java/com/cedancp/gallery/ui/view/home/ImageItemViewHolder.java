package com.cedancp.gallery.ui.view.home;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cedancp.gallery.databinding.ImageItemBinding;

public class ImageItemViewHolder extends RecyclerView.ViewHolder {

    public ImageItemBinding imageItemBinding;

    public ImageItemViewHolder(@NonNull ImageItemBinding imageItemBinding) {
        super(imageItemBinding.getRoot());

        this.imageItemBinding = imageItemBinding;
    }
}

package com.cedancp.gallery.ui.view.home;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cedancp.gallery.R;
import com.cedancp.gallery.databinding.ImageItemBinding;

public class ImageItemViewHolder extends RecyclerView.ViewHolder {

    public ImageItemBinding imageItemBinding;

    public ImageItemViewHolder(@NonNull ImageItemBinding imageItemBinding) {
        super(imageItemBinding.getRoot());

        this.imageItemBinding = imageItemBinding;

//        imageItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.imageDetailFragment);
//            }
//        });
    }
}

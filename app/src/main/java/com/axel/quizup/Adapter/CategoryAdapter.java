package com.axel.quizup.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.axel.quizup.Model.Category;
import com.axel.quizup.R;
import com.axel.quizup.Ui.GameActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getCategoryName());
        holder.categoryDescription.setText(category.getDescription());
        Glide.with(holder.itemView.getContext())
                .load(category.getImage())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.visibility_off)
                .into(holder.categoryImage);

        // Set image and description if needed

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GameActivity.class);
            intent.putExtra("categoryName", category.getCategoryName()); // Pass the correct category name
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        ImageView categoryImage;
        TextView categoryDescription;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryName);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryDescription = itemView.findViewById(R.id.categorydDescription);
        }
    }
}

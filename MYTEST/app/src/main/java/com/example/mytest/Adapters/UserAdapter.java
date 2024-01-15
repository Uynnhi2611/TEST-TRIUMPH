package com.example.mytest.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.Models.ProfileModel;
import com.example.mytest.R;

import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<ProfileModel> userList;

    public UserAdapter(List<ProfileModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ProfileModel user = userList.get(position);
        holder.stt.setText(String.valueOf(position+1));
        holder.tenNguoiDung.setText(user.getName());
        holder.email.setText(user.getEmail());

        // Check if phone number is empty
        String phoneNumber = user.getPhone();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            holder.sdt.setText("_");
        } else {
            holder.sdt.setText(phoneNumber);
        }

        holder.ngayTao.setText(user.getNgayTao());
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView stt, tenNguoiDung, email, sdt, ngayTao;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            stt = itemView.findViewById(R.id.stt);
            tenNguoiDung = itemView.findViewById(R.id.tenNguoiDung);
            email = itemView.findViewById(R.id.email);
            sdt = itemView.findViewById(R.id.sdt);
            ngayTao = itemView.findViewById(R.id.ngayTao);
        }
    }

}

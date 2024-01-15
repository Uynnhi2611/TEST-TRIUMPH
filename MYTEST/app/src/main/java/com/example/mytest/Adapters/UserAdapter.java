package com.example.mytest.Adapters;

import static com.example.mytest.DbQuery.g_usersData;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.DbQuery;
import com.example.mytest.Models.ProfileModel;
import com.example.mytest.MyCompleteListener;
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
            holder.sdt.setText("-");
        } else {
            holder.sdt.setText(phoneNumber);
        }

        holder.ngayTao.setText(user.getNgayTao());
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView stt, tenNguoiDung, email, sdt, ngayTao;
        ImageView deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            stt = itemView.findViewById(R.id.stt);
            tenNguoiDung = itemView.findViewById(R.id.tenNguoiDung);
            email = itemView.findViewById(R.id.email);
            sdt = itemView.findViewById(R.id.sdt);
            ngayTao = itemView.findViewById(R.id.ngayTao);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String userId = g_usersData.get(position).getUserId();

                    // Tạo một AlertDialog
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete User")
                            .setMessage("Do you want to delete this user?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Xóa người dùng khi người dùng nhấn "Yes"
                                    DbQuery.deleteUser(userId, new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            g_usersData.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure() {
                                            // Xử lý lỗi
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

        }
    }


}

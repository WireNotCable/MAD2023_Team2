package sg.edu.np.mad.pennywise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.FriendViewHolder>{
    private ArrayList<FriendClass> friendList;
    private Context context;

    public UsersRecyclerAdapter(ArrayList<FriendClass> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendcardview,parent,false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.name.setText(friendList.get(position).getName());
        holder.email.setText(friendList.get(position).getEmail());
        holder.contact.setText(friendList.get(position).getContact());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
    public void filterList(ArrayList<FriendClass> list){
        friendList = list;
        notifyDataSetChanged();
    }
    public class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView name,email,contact;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.FriendName);
            email = itemView.findViewById(R.id.FriendEmail);
            contact = itemView.findViewById(R.id.FriendContact);

        }
    }

}
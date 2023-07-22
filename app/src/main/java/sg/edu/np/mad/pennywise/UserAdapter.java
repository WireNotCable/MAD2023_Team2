package sg.edu.np.mad.pennywise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder>{
    private ArrayList<User> userList,filteredList;
    private Context context;
    private OnUserClickListener listener;

    public UserAdapter(ArrayList<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        this.filteredList = new ArrayList<User>(userList);
    }
    public interface OnUserClickListener {
        void onUserClick(User user);
    }
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendcardview,parent,false);
        return new UserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapterViewHolder holder, int position) {
        holder.profile.setImageResource(R.drawable.person_icon);
        holder.name.setText(filteredList.get(position).getName());
        holder.contact.setText(filteredList.get(position).getNumber());

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView name,contact;
        private ImageView profile;
        private CardView card;
        public UserAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.contactname);
            contact = itemView.findViewById(R.id.contactcontact);
            profile = itemView.findViewById(R.id.imageView2);
            card = itemView.findViewById(R.id.contactcardview);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        User clickedUser = filteredList.get(position);
                        if (listener != null) {
                            listener.onUserClick(clickedUser);
                        }
                    }
                }});

        }
    }
    public void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (User user : userList) {
                if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
}

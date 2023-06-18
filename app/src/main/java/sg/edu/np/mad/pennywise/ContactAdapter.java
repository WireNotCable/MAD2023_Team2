package sg.edu.np.mad.pennywise;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    private List<Contact> contactList;

    public ContactAdapter(@NonNull Context context, @NonNull ArrayList<Contact> contactList) {
        super(context,0, contactList);
        this.contactList = new ArrayList<>(contactList);
    }
    @NonNull
    @Override
    public Filter getFilter(){
        return contactFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.dropdown_item_contact,parent,false
            );
        }
        TextView contactName = convertView.findViewById(R.id.contactNameTextView);
        TextView contactNumber = convertView.findViewById(R.id.contactNumberTextView);
        Contact contact = getItem(position);
        if (contact != null){
            contactName.setText(contact.getName());
            contactNumber.setText(contact.getPhoneNumber());
        }

        return convertView;
    }

    private Filter contactFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Contact> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(contactList);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Contact item : contactList){
                    String phoneNumber = item.getPhoneNumber();
                    String contactName = item.getName();
                    if (phoneNumber.toLowerCase().contains(filterPattern) || contactName.toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                addAll((ArrayList<Contact>)results.values);
            }
            notifyDataSetChanged();
        }
        @Override
        public CharSequence convertResultToString(Object resultValue){
            return ((Contact) resultValue).getName()+","+((Contact) resultValue).getPhoneNumber();
        }
    };
}

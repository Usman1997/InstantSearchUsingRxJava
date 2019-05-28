package com.example.hp.instantsearch.adapter;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hp.instantsearch.R;
import com.example.hp.instantsearch.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactAdapterFilterable extends RecyclerView.Adapter<ContactAdapterFilterable.ViewHolder> implements Filterable {
    private Context context;
    private List<Contact> contactList;
    private List<Contact> contactListFiltered;
    private ContactAdapterListener listener;



    public ContactAdapterFilterable(Context context,List<Contact> contactList,ContactAdapterListener listener){
        this.context = context;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ContactAdapterFilterable.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int postion) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapterFilterable.ViewHolder holder, int position) {
        final Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

        Glide.with(context)
                .load(contact.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    contactListFiltered = contactList;
                }else{
                    List<Contact> filteredList = new ArrayList<>();
                    for(Contact row:contactList){
                        if(row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)){
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults  = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactListFiltered = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, phone;
        public ImageView thumbnail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            thumbnail = itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactList.get(getAdapterPosition()));
                }
            });
        }
    }


    public interface ContactAdapterListener{
        void onContactSelected(Contact contact);
    }
}

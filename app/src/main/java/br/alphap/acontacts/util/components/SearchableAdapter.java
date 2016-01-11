package br.alphap.acontacts.util.components;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.alphap.acontacts.R;
import br.alphap.acontacts.io.database.ADatabaseManager;
import br.alphap.acontacts.util.PersonalContact;
import br.alphap.acontacts.util.components.PersonalContactAdapter;

/**
 * Created by danielbt on 09/12/15.
 */
public final class SearchableAdapter extends PersonalContactAdapter {

    private String query;

    public SearchableAdapter(Context context, ADatabaseManager db) {
        super(context, db);
    }

    public SearchableAdapter(Context context, ADatabaseManager db, String query) {
        this(context, db);
        search(query);
    }

    private void search(String query) {
        databaseManager.queryData();

        List<PersonalContact> listSearched = databaseManager.getData();
        List<PersonalContact> oldList = new ArrayList<>(listSearched);

        databaseManager.clearData();
        databaseManager.setQueryData(false);

        for (int i = 0; i < oldList.size(); i++) {
            PersonalContact contact = oldList.get(i);

            if (contact.getName().toLowerCase().startsWith(query.toLowerCase()) ||
                    contact.getName().toLowerCase().contains(query.toLowerCase()) ||
                    contact.getName().toLowerCase().endsWith(query.toLowerCase()) ||
                    contact.getPhone().startsWith(query) || contact.getPhone().contains(query) ||
                            contact.getPhone().endsWith(query)) {
                listSearched.add(contact);
            }

            notifyItemChanged(i);
        }

        this.query = query;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (databaseManager.isEmpty()) {
            viewType = PCVHE;
        } else {
            viewType = PCVH;
        }


        return viewType;
    }

    @Override
    public int getItemCount() {
        return databaseManager.isEmpty() ? PCVHE : databaseManager.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == PCVH) {
            View view = layoutInflater.inflate(R.layout.card_item_personal, parent, false);
            viewHolder = new PersonalContactVH(view);
        } else if (viewType == PCVHE) {
            View view = layoutInflater.inflate(R.layout.card_empty_result, parent, false);
            viewHolder = new ContactEmptyVH(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PersonalContactVH && !databaseManager.isEmpty()) {
            PersonalContactVH holder = (PersonalContactVH) viewHolder;
            PersonalContact contact = databaseManager.get(position);

            if (contact.getImageAsBitmap() != null) {
                holder.imageViewPersonal.setImageBitmap(contact.getImageAsBitmap());
            } else {
                holder.imageViewPersonal.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.personal_image));
            }

            if (contact.getName() != null && !contact.getName().equals("")) {
                holder.textViewName.setText(contact.getName());
            } else {
                holder.textViewName.setText(getContext().getResources().getString(R.string.abc_info_cardview_textview_name));
            }

            if (contact.getPhone() != null) {
                String[] types = getContext().getResources().getStringArray(R.array.spinnerTypes);
                holder.textViewPhone.setText(types[contact.getContactType()] + ": " + contact.getPhone());
            }
        }
    }

    @Override
    public void replaceItemOnList(int position, PersonalContact contact, boolean withId) {
        super.replaceItemOnList(position, contact, withId);
        search(query);
    }

    @Override
    public void removeItemOnList(int position, boolean withId) {
        super.removeItemOnList(position, withId);
        search(query);
    }

    public void searchOnList(String search) {
        search(search);
    }

    public List<PersonalContact> getResult() {
        return databaseManager.getData();
    }

    public boolean isResultEmpty() {
        return databaseManager.isEmpty();
    }


}
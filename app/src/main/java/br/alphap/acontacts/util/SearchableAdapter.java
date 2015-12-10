package br.alphap.acontacts.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.alphap.acontacts.R;

/**
 * Created by danielbt on 09/12/15.
 */
public final class SearchableAdapter extends PersonalContactAdapter {

    private List<PersonalContact> list;
    private List<PersonalContact> listSearched;

    public SearchableAdapter(Context context, List<PersonalContact> list) {
        super(context);
        this.list = list;
        listSearched = new ArrayList<>();
    }

    public SearchableAdapter(Context context, List<PersonalContact> list, String query) {
        this(context, list);
        search(query);
    }

    private void search(String query) {
        listSearched.clear();

        for (int i = 0; i < list.size(); i++) {
            PersonalContact contact = list.get(i);

            if (contact.getName().toLowerCase().startsWith(query.toLowerCase()) ||
                    contact.getName().toLowerCase().contains(query.toLowerCase()) ||
                    contact.getName().toLowerCase().endsWith(query.toLowerCase())) {
                listSearched.add(list.get(i));
            } else if (contact.getPhone().startsWith(query) || contact.getPhone().contains(query) ||
                    contact.getPhone().endsWith(query)) {
                listSearched.add(list.get(i));
            }

            if (query != null) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (listSearched.isEmpty() && position == 0) {
            viewType = PCVHE;
        } else {
            viewType = PCVH;
        }


        return viewType;
    }

    @Override
    public int getItemCount() {
        return listSearched.isEmpty() ? PCVHE : listSearched.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == PCVH) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.card_item_personal, parent, false);
            viewHolder = new PersonalContactVH(view);
        } else if (viewType == PCVHE) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.card_empty_result, parent, false);
            viewHolder = new ContactEmptyVH(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PersonalContactVH && !listSearched.isEmpty()) {
            final PersonalContactVH holder = (PersonalContactVH) viewHolder;
            final PersonalContact contact = listSearched.get(position);

            if (contact.getImageData() != null) {
                holder.imageViewPersonal.setImageBitmap(contact.getImageData());
            } else {
                holder.imageViewPersonal.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.personal_image));
            }

            if (contact.getName() != null && !contact.getName().equals("")) {
                holder.textViewName.setText(contact.getName());
            } else {
                holder.textViewName.setText(getContext().getResources().getString(R.string.abc_info_cardview_textview_name));
            }

            if (contact.getPhone() != null || !contact.getPhone().equals("")) {
                String[] types = getContext().getResources().getStringArray(R.array.spinnerTypes);
                holder.textViewPhone.setText(types[contact.getContactType()] + ": " + contact.getPhone());
            }
        }
    }

    public List<PersonalContact> searchOnList(String search) {
        search(search);
        return listSearched;
    }

    public List<PersonalContact> getResult() {
        return listSearched;
    }

    public boolean isResultEmpty() {
        return listSearched.isEmpty();
    }


}
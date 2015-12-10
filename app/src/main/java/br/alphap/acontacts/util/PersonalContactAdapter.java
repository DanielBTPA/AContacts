package br.alphap.acontacts.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.alphap.acontacts.R;
import br.alphap.acontacts.io.database.ADatabaseManager;

/**
 * Created by Daniel on 27/10/2015.
 */
public class PersonalContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private ADatabaseManager databaseManager;

    private OnItemClickListenerProvider listener;
    private OnCardMenuItemListener menuItemClickListener;


    protected static final int PCVH = 0;
    protected static final int PCVHE = 1;

    public PersonalContactAdapter(Context context, ADatabaseManager databaseManager) {
        this.context = context;
        this.databaseManager = databaseManager;
    }

    protected PersonalContactAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }


    public void setOnItemClickListenerProvider(OnItemClickListenerProvider listener) {
        this.listener = listener;
    }

    public void setOnCardMenuItemClickListener(OnCardMenuItemListener listener) {
        this.menuItemClickListener = listener;
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
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

        if (viewType == PCVH) {
            View view = LayoutInflater.from(context).inflate(R.layout.card_item_personal, parent, false);
            holder = new PersonalContactVH(view);
        } else if (viewType == PCVHE) {
            View view = LayoutInflater.from(context).inflate(R.layout.card_empty_mensage, parent, false);
            holder = new ContactEmptyVH(view);
        }

        return holder;
    }

    @Override
    public int getItemCount() {
        return databaseManager.isEmpty() ? PCVHE : databaseManager.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PersonalContactVH) {
            final PersonalContactVH holder = (PersonalContactVH) viewHolder;
            final PersonalContact contact = databaseManager.get(position);

            if (contact.getImageData() != null) {
                holder.imageViewPersonal.setImageBitmap(contact.getImageData());
            } else {
                holder.imageViewPersonal.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.personal_image));
            }

            if (contact.getName() != null && !contact.getName().equals("")) {
                holder.textViewName.setText(contact.getName());
            } else {
                holder.textViewName.setText(context.getResources().getString(R.string.abc_info_cardview_textview_name));
            }

            if (contact.getPhone() != null || !contact.getPhone().equals("")) {
                String[] types = context.getResources().getStringArray(R.array.spinnerTypes);
                holder.textViewPhone.setText(types[contact.getContactType()] + ": " + contact.getPhone());
            }
        }
    }


    protected class ContactEmptyVH extends RecyclerView.ViewHolder {

        public ContactEmptyVH(View itemView) {
            super(itemView);
        }
    }

    protected class PersonalContactVH extends RecyclerView.ViewHolder {
        public ImageView imageViewPersonal;
        public TextView textViewName;
        public TextView textViewPhone;
        public Toolbar tbBottom;

        public PersonalContactVH(final View itemView) {
            super(itemView);

            imageViewPersonal = (ImageView) itemView.findViewById(R.id.idIvImagePersonalItem);

            textViewName = (TextView) itemView.findViewById(R.id.idTvNamePesonalItem);
            textViewPhone = (TextView) itemView.findViewById(R.id.idTvPhonePesonalItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (listener != null) {
                       listener.onClickItem(v, getAdapterPosition());
                   }
                }
            });

            tbBottom = (Toolbar) itemView.findViewById(R.id.idTbCardPersonal);
            tbBottom.inflateMenu(R.menu.menu_card_action);
            tbBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return menuItemClickListener.onItemSelected(item, itemView, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListenerProvider {
        public void onClickItem(View v, int position);
    }

    public interface OnCardMenuItemListener {
        public boolean onItemSelected(MenuItem item, View view, int position);

    }
}

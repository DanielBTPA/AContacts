package br.alphap.acontacts.util;

import android.content.Context;
import android.graphics.Bitmap;
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

/**
 * Created by Daniel on 27/10/2015.
 */
public class PersonalContactAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private PersonalContactList list;
    private OnItemClickListenerProvider listener;
    private OnCardMenuItemListener menuItemClickListener;


    private static final int PCVH = 0;
    private static final int PCVHE = 1;

    public PersonalContactAdapter(Context context, PersonalContactList list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListenerProvider(OnItemClickListenerProvider listener) {
        this.listener = listener;
    }

    public void setOnCardMenuItemClickListener(OnCardMenuItemListener listener) {
        this.menuItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewtype = 0;

        if (list.isEmpty()) {
            viewtype = PCVHE;
        } else {
            viewtype = PCVH;
        }


        return viewtype;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;

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
        return list.isEmpty() ? PCVHE : list.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (!list.isEmpty()) {

            PersonalContactVH holder = (PersonalContactVH) viewHolder;
            final PersonalContact contact = list.getContact(position);

            if (contact.getImageData() != null) {
                final ImageView imageView = holder.imageViewPersonal;
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = View.MeasureSpec.getSize(imageView.getMeasuredWidth());
                        int height = View.MeasureSpec.getSize(imageView.getMeasuredHeight());

                        Bitmap newBitmap = Bitmap.createScaledBitmap(contact.getImageData(), width, height, true);
                        imageView.setImageBitmap(newBitmap);
                    }
                });

            }

            if (contact.getName() != null && !contact.getName().equals("")) {
                holder.textViewName.setText(contact.getName());
            }

            if (contact.getPhone() != null || !contact.getPhone().equals("")) {
                PersonalContact p = list.getContact(holder.getAdapterPosition());
                String[] types = context.getResources().getStringArray(R.array.spinnerTypes);

                holder.textViewPhone.setText(types[p.getContactType()] + ": " + contact.getPhone());
            }

        }
    }

    protected class ContactEmptyVH extends ViewHolder {

        public ContactEmptyVH(View itemView) {
            super(itemView);
        }
    }

    protected class PersonalContactVH extends ViewHolder {

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
                    listener.onClickItem(v, getAdapterPosition());
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

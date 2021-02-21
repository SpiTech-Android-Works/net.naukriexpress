package app.spitech.ui.chat.group.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.ConvertTo;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;
import app.spitech.models.DataBin;
import app.spitech.ui.chat.group.GroupDetails;


public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AppSession session;
    String is_admin="0";
    GroupDetails parent;

    public GroupMemberAdapter(Context context1, ArrayList<DataBin> list1, String is_admin, GroupDetails parent) {
        this.parent=parent;
        this.is_admin = is_admin;
        this.context = context1;
        this.list = list1;
        this.session = new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_user_row, parent, false);

        return new GroupMemberAdapter.ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        holder.name.setText(ConvertTo.toTitleCase(data.getName()));
        holder.photo_name.setText(SpiTech.getInstance().getNamedPhoto(data.getName()));
        if (Validation.isNotEmpty(data.getImage())) {
            String url = AppConfig.mediaCustomer + data.getImage();
            SpiTech.getInstance().loadImage(context, url, holder.photo);
            holder.photo_name.setText("");
        }

        if (data.getIsAdmin().equalsIgnoreCase("1")) {
            holder.is_admin.setVisibility(View.VISIBLE);
        }else{
            holder.is_admin.setVisibility(View.GONE);
        }

        if(is_admin.equalsIgnoreCase("1")){
            holder.btnAction.setVisibility(View.VISIBLE);
        }else{
            holder.btnAction.setVisibility(View.GONE);
        }
        //-----------------Popup Menu Begin------------------
        PopupMenu popupMenu = new PopupMenu(context, holder.btnAction);
        popupMenu.inflate(R.menu.context_menu_group_details);
        MenuItem update_admin_menu = popupMenu.getMenu().findItem(R.id.action_update_admin);
        if (data.getIsAdmin().equalsIgnoreCase("1")) {
            update_admin_menu.setTitle("Remove from Admin");
        } else {
            update_admin_menu.setTitle("Promote to Admin");
        }

        MenuItem remove_menu = popupMenu.getMenu().findItem(R.id.action_remove_from_group);
        if (data.getUserId().equalsIgnoreCase(session.getUserId())) {
            remove_menu.setTitle("Left from Group");
            update_admin_menu.setTitle("Left from Admin");
        } else {
            remove_menu.setTitle("Remove from Group");
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_update_admin:
                    if (data.getIsAdmin().equalsIgnoreCase("1")) {
                        String []args={"update_group_admin",data.getRowId(),"0"};
                        parent.onAdapterItemClick(args); // Remove Group Admin
                    }else{
                        String []args={"update_group_admin",data.getRowId(),"1"};
                        parent.onAdapterItemClick(args); // Promote To Group Admin
                    }
                    break;
                case R.id.action_remove_from_group:
                    String []args={"remove_member",data.getRowId(),"1"};
                    parent.onAdapterItemClick(args); // Remove Member
                    break;
            }
            return false;
        });

        holder.btnAction.setOnClickListener(view -> {
            popupMenu.show();
            //-----------------Popup Menu End------------------
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, photo_name, is_admin;
        public CircleImageView photo;
        public ImageView btnAction;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            is_admin = itemView.findViewById(R.id.is_admin);
            photo_name = itemView.findViewById(R.id.photo_name);
            photo = itemView.findViewById(R.id.photo);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

    }

}
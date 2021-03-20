package aarnav100.developer.readers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import aarnav100.developer.readers.Classes.Person;
import aarnav100.developer.readers.R;

/**
 * Created by aarnavjindal on 15/07/17.
 */

public class FriendAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Person> friends;

    public FriendAdapter(Context context, ArrayList<Person> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Person getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Person friend=getItem(position);
        FriendHolder holder;
        if(convertView==null)
        {
            LayoutInflater li=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=li.inflate(R.layout.friend_view,null);
            holder=new FriendHolder();
            holder.img=(de.hdodenhof.circleimageview.CircleImageView)convertView.findViewById(R.id.fdImg);
            holder.name=(TextView) convertView.findViewById(R.id.fdName);
            holder.place=(TextView)convertView.findViewById(R.id.fdPlace);
            holder.prefernce=(TextView)convertView.findViewById(R.id.fdPreference);
            convertView.setTag(holder);
        }
        else
            holder=(FriendHolder)convertView.getTag();

        Glide.with(context).load(friend.getImageUrl()).placeholder(R.drawable.download).into(holder.img);
        holder.name.setText(friend.getName());
        holder.place.setText(friend.getLocation());
        holder.prefernce.setText(friend.getPreference());

        return convertView;
    }

    private class FriendHolder
    {
        private de.hdodenhof.circleimageview.CircleImageView img;
        private TextView name,place,prefernce;
    }
}

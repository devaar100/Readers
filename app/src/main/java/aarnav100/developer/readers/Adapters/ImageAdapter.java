package aarnav100.developer.readers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import aarnav100.developer.readers.Classes.Person;
import aarnav100.developer.readers.R;

/**
 * Created by aarnavjindal on 16/07/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;
    private Gson gson=new Gson();

    public ImageAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        LayoutInflater li=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String[] links;
            v=li.inflate(R.layout.friend_grid_item_view,null);
            links=getItem(position).split("=",2);
        ImageView img=(ImageView)v.findViewById(R.id.bookImg);
        Glide.with(context).load(links[1].trim()).into(img);
        final String[] finalLinks = links;
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("user/profile/"+ finalLinks[0]);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            JsonObject obj2 = gson.fromJson(gson.toJson(dataSnapshot.getValue()), JsonObject.class);
                            JsonArray array = obj2.getAsJsonArray("preferences");
                            Integer[] prefer = new Integer[6];
                            for (int i = 0; i < array.size(); i++)
                                prefer[i] = Integer.valueOf(array.get(i).getAsString());
                            final Person per = gson.fromJson(obj2, Person.class);
                            per.setPreference(prefer);
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                            View dialogView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.friend_dialog_layout, null);
                            dialogBuilder.setView(dialogView);
                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.getWindow().getDecorView().getBackground().setAlpha(0);

                            ImageView back,add;
                            de.hdodenhof.circleimageview.CircleImageView frnd_img;
                            TextView tv1,tv2,tv3,tv4,tv5,tv6;
                            TextView name,place,desc;

                            back=(ImageView)dialogView.findViewById(R.id.back);
                            add=(ImageView)dialogView.findViewById(R.id.add);
                            add.setImageResource(R.drawable.bin);

                            frnd_img=(de.hdodenhof.circleimageview.CircleImageView)dialogView.findViewById(R.id.edit_img);
                            tv1=(TextView)dialogView.findViewById(R.id.tv1);
                            tv2=(TextView)dialogView.findViewById(R.id.tv2);
                            tv3=(TextView)dialogView.findViewById(R.id.tv3);
                            tv4=(TextView)dialogView.findViewById(R.id.tv4);
                            tv5=(TextView)dialogView.findViewById(R.id.tv5);
                            tv6=(TextView)dialogView.findViewById(R.id.tv6);
                            name=(TextView)dialogView.findViewById(R.id.dialogName);
                            place=(TextView)dialogView.findViewById(R.id.dialogPlace);
                            desc=(TextView)dialogView.findViewById(R.id.dialogDesc);

                            tv1.setText(String.valueOf(per.getPreferenceArray()[0]));
                            tv2.setText(String.valueOf(per.getPreferenceArray()[1]));
                            tv3.setText(String.valueOf(per.getPreferenceArray()[2]));
                            tv4.setText(String.valueOf(per.getPreferenceArray()[3]));
                            tv5.setText(String.valueOf(per.getPreferenceArray()[4]));
                            tv6.setText(String.valueOf(per.getPreferenceArray()[5]));
                            name.setText(per.getName());
                            place.setText(per.getLocation());
                            desc.setText(per.getDescription());

                            ImageView fb_img=(ImageView)dialogView.findViewById(R.id.fb_edit);
                            ImageView q_img=(ImageView)dialogView.findViewById(R.id.q_edit);
                            ImageView t_img=(ImageView)dialogView.findViewById(R.id.t_edit);
                            ImageView g_img=(ImageView)dialogView.findViewById(R.id.g_edit);

                            final String[] urls=new String[4];
                            JsonArray array2 = obj2.getAsJsonArray("urls");
                            for (int i = 0; i < array2.size(); i++)
                                urls[i] = array2.get(i).getAsString().trim();
                            View.OnClickListener ocl2=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url="";
                                    switch (v.getId())
                                    {
                                        case R.id.fb_edit:
                                            url=urls[0];
                                            break;
                                        case R.id.g_edit:
                                            url=urls[1];
                                            break;
                                        case R.id.t_edit:
                                            url=urls[2];
                                            break;
                                        case R.id.q_edit:
                                            url=urls[3];
                                            break;

                                    }
                                    if(Patterns.WEB_URL.matcher(url).matches())
                                    {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                                        context.startActivity(browserIntent);
                                    }
                                    else
                                        Toast.makeText(context,"Not provided", Toast.LENGTH_SHORT).show();
                                }
                            };
                            fb_img.setOnClickListener(ocl2);
                            q_img.setOnClickListener(ocl2);
                            g_img.setOnClickListener(ocl2);
                            t_img.setOnClickListener(ocl2);
                            Glide.with(context).load(per.getImageUrl()).placeholder(R.drawable.download).into(frnd_img);
                            View.OnClickListener ocl=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(v.getId()==R.id.back)
                                        alertDialog.dismiss();
                                    else{
                                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("user/friends/"+user.getUid()+"/"+finalLinks[0]);
                                        ref.removeValue();
                                        notifyDataSetChanged();
                                        alertDialog.dismiss();
                                    }
                                }
                            };
                            back.setOnClickListener(ocl);
                            add.setOnClickListener(ocl);
                            alertDialog.show();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("TAG",databaseError.getDetails());
                        }
                    });
                }
        });
        return v;
    }
}

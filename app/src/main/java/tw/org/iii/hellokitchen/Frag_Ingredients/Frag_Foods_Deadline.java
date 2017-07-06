package tw.org.iii.hellokitchen.Frag_Ingredients;



import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;


import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tw.org.iii.hellokitchen.Entity.Ingredients;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.MyDBHelper;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Foods_Deadline#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Foods_Deadline extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SwipeMenuListView.OnMenuItemClickListener lv_OnMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    // delete
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("確定刪除該食材嗎?")
                            .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // 確認
                                    //TODO UPDATE the model here
                                    myDBHelper = MyDBHelper.getInstance(getActivity());
                                    db = myDBHelper.getWritableDatabase();
                                    db.delete("tingredients","ingredients_id = "+indegredientList.get(position).get_id(),null);
                                    indegredientList.remove(indegredientList.get(position));
                                    myAdapter.notifyDataSetChanged();
                                    myAdapter = new CustomAdapter_Ingredients(getActivity());
                                    lv.setAdapter(myAdapter);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // 取消

                                }
                            });
                    AlertDialog about_dialog = builder.create();
                    about_dialog.show();
                    break;
            }
            return false;
        }
    };


    public Frag_Foods_Deadline() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Foods_Deadline.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Foods_Deadline newInstance(String param1, String param2) {
        Frag_Foods_Deadline fragment = new Frag_Foods_Deadline();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag__foods__deadline, container, false);
        lv = (SwipeMenuListView) v.findViewById(R.id.indegredient_list);
        indegredientList = new ArrayList<>();
        myDBHelper = MyDBHelper.getInstance(getActivity());
        db = myDBHelper.getWritableDatabase();
        Query();
        myAdapter = new CustomAdapter_Ingredients(getActivity());
        lv.setAdapter(myAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        lv.setMenuCreator(creator);

        lv.setOnMenuItemClickListener(lv_OnMenuItemClickListener);

        return v;
    }

    public class CustomAdapter_Ingredients extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;

        public CustomAdapter_Ingredients(Context context)
        {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return indegredientList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            Ingredients i = indegredientList.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null)
            {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.layout_list_of_indegredients, null);
                viewHolder.txt_name = (TextView) convertView.findViewById(R.id.indegredient_name);
                viewHolder.txt_endDate = (TextView) convertView.findViewById(R.id.indegredient_deadDate);
                viewHolder.txt_dayLeft = (TextView) convertView.findViewById(R.id.indegredient_dayLeft);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.txt_name.setText(i.getName());
            viewHolder.txt_dayLeft.setText(String.valueOf(i.getDay()));
            viewHolder.txt_endDate.setText(i.getDeadDate());
            return convertView;
        }
    }
    class ViewHolder
    {
        private TextView txt_name;
        private TextView txt_endDate;
        private TextView txt_dayLeft;


    }



    public void Query()
    {
        Cursor cursor = db.query("tingredients", null, null, null, null, null, null);

        while (cursor.moveToNext())
        {
            String _id = cursor.getString(0);
            String name = cursor.getString(1);
            String startDate = cursor.getString(2);
            String endDate = cursor.getString(3);
            String amount = cursor.getString(4);
            String member = cursor.getString(5);
            Ingredients i  = new Ingredients(_id, name, startDate, endDate,amount,member);
            indegredientList.add(i);
        }
        Collections.sort(indegredientList, new Comparator<Ingredients>()
        {
            @Override
            public int compare(Ingredients i2, Ingredients i1)
            {
                return  i2.getTime().compareTo(i1.getTime());
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    List<Ingredients> indegredientList;
    MyDBHelper myDBHelper;
    SQLiteDatabase db;
    CustomAdapter_Ingredients myAdapter;
    SwipeMenuListView lv ;

}

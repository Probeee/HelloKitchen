package tw.org.iii.hellokitchen;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Recipe_Container#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Recipe_Container extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Frag_Recipe_Container() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Recipe_Container.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Recipe_Container newInstance(String param1, String param2) {
        Frag_Recipe_Container fragment = new Frag_Recipe_Container();
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
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag__recipe__container, container, false);
        lv = (ListView) v.findViewById(R.id.listview_recipe);
        myAdapter = new CustomAdapter_Recipe(getActivity());
        lv.setAdapter(myAdapter);

        return v;
    }

    public class CustomAdapter_Recipe extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;
        public CustomAdapter_Recipe(Context context)
        {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder_Recipe viewHolder = null;
            if (convertView == null)
            {
                viewHolder = new ViewHolder_Recipe();
                convertView = inflater.inflate(R.layout.layout_list_of_recipe, null);
                viewHolder.txt_recipe_name = (TextView) convertView.findViewById(R.id.txtRecipeName);
                viewHolder.txt_producer_name = (TextView) convertView.findViewById(R.id.txtProducerName);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder_Recipe) convertView.getTag();
            }
            return convertView;
        }
    }
    class ViewHolder_Recipe
    {
        private TextView txt_recipe_name;
        private TextView txt_producer_name;
    }

    List<Recipes> recipesList;
    CustomAdapter_Recipe myAdapter;
    ListView lv ;
}

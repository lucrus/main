/**
 *
 */
package com.lucrus.main.components;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucrus.main.R;
import com.lucrus.main.fontawesome.TextAwesome;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luca.russo
 */
public class TypefacedSimpleAdapter extends ArrayAdapter<Map<String, Object>> {
    private Context context;
    private int mLayout;
    private String[] mFrom;
    private int[] mTo;
    private boolean mStyle;
    private List<Map<String, Object>> data, filtered;

    /**
     * @param context
     * @param data
     * @param resource
     * @param from
     * @param to
     */
    public TypefacedSimpleAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, 0, new ArrayList<Map<String, Object>>());
        this.context = context;
        this.mLayout = resource;
        this.mFrom = from;
        this.mTo = to;
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data = new ArrayList<>();
        this.data.addAll(data);
        this.filtered = data;
        mStyle = true;
    }

    public void setNoStyle() {
        mStyle = false;
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public Map<String, Object> getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return filtered.get(position).hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(mLayout, parent, false);
            holder = new ViewHolder(mTo.length);
            for (int i = 0; i < mTo.length; i++) {
                holder.views[i] = v.findViewById(mTo[i]);
            }
            holder.ivFrecciaLista = (TextAwesome) v.findViewById(R.id.ivFrecciaLista);
            holder.ivFaIcon = (TextAwesome) v.findViewById(R.id.ivFaIcon);
            holder.ivIcon = (ImageView) v.findViewById(R.id.ivSimpleIcon);
            v.setTag(holder);
            holder.ivFaIcon.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            holder.ivFrecciaLista.setVisibility(View.GONE);
            for (View vv : holder.views) {
                if (vv instanceof TextView) {
                    ((TextView) vv).setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }
            }
        } else {
            holder = (ViewHolder) v.getTag();
        }

        Map<String, Object> item = getItem(position);
        for (View view : holder.views) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(null);
                view.setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < mTo.length; i++) {
            View view = holder.views[i];
            if (view instanceof TextView) {
                Object obj = item.get(mFrom[i]);
                if (obj == null || obj.toString().trim().length() == 0) {
                    view.setVisibility(View.GONE);
                } else {
                    TextView tv = (TextView) view;
                    tv.setText(item.get(mFrom[i]).toString());
                    if (item.get("color") != null) {
                        try {
                            int color = Integer.parseInt("" + item.get("color"));
                            tv.setTextColor(color);
                            holder.ivFrecciaLista.setTextColor(color);
                        } catch (Exception e) {
                        }
                    }
                }
            } else if (view instanceof ImageView) {
                final ImageView iv = (ImageView) view;
                iv.setVisibility(View.VISIBLE);
                final Object obj = item.get(mFrom[i]);
                try {
                    ((ImageView) view).setImageResource((Integer) obj);
                } catch (Throwable e) {
                }
                Object obj2 = obj;
                if (mFrom[i].equalsIgnoreCase("icon")) {
                    String tt = (String) item.get("iconUrl");
                    if (tt != null && tt.trim().length() > 0) {
                        obj2 = tt;
                    }
                }

                if (obj2 instanceof String && obj2 != null && ((String) obj2).toLowerCase().startsWith("http")) {
                    Picasso.with(context).load((String) obj2).into(iv, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            if (obj instanceof Integer && obj != null) {
                                ((ImageView) iv).setImageResource((Integer) obj);
                            }
                        }
                    });
                } else if (obj instanceof Integer) {
                    iv.setImageResource((Integer) obj);
                }
            }
        }


        Integer badge = (Integer) item.get("badge");
        if (badge != null && badge > 0) {
            BadgeView bv = new BadgeView(context, v.findViewById(R.id.ivFrecciaLista));
            bv.setText("" + badge);
            bv.setTextColor(Color.WHITE);
            bv.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
            //int marginH = (int)(-3.0f*Utility.getDensity((Activity)context));
            //int marginV = (int)(3.0f*Utility.getDensity((Activity)context));
            //bv.setBadgeMargin(marginH, marginV);
            bv.show();
        }

        Integer faIcon = (Integer) item.get("icon-fa");
        if (faIcon != null) {
            holder.ivFaIcon.setText(faIcon);
            holder.ivFaIcon.setVisibility(View.VISIBLE);
            holder.ivIcon.setVisibility(View.GONE);
        } else {
            holder.ivFaIcon.setVisibility(View.GONE);
        }
        return v;
    }

    static class ViewHolder {
        View[] views;
        TextAwesome ivFrecciaLista, ivFaIcon;
        ImageView ivIcon;


        public ViewHolder(int size) {
            super();
            views = new View[size];
        }
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults fr = new FilterResults();
                if (constraint != null) {
                    List<Map<String, Object>> res = new ArrayList<>();
                    if (constraint.length() == 0) {
                        res = data;
                    } else {
                        for (Map<String, Object> e : data) {
                            for (Object o : e.values()) {
                                if (o != null && o.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                    res.add(e);
                                    break;
                                }
                            }
                        }
                    }
                    fr.count = res.size();
                    fr.values = res;
                }
                return fr;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<Map<String, Object>> res = (List<Map<String, Object>>) results.values;
                filtered.clear();
                clear();
                if (res != null && res.size() > 0) {
                    filtered.addAll(res);
                }
                notifyDataSetChanged();
            }
        };
    }
}

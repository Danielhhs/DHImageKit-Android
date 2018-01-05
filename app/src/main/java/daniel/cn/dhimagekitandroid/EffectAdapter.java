package daniel.cn.dhimagekitandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageEffectType;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;

/**
 * Created by huanghongsen on 2018/1/5.
 */

public class EffectAdapter extends BaseAdapter {
    private Context context;
    private List<DHImageEffectType> filterTypeList;
    private LayoutInflater mInflater;

    public EffectAdapter(Context context, List<DHImageEffectType> filterTypes) {
        this.context = context;
        this.filterTypeList = filterTypes;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return filterTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return filterTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = (View)mInflater.inflate(R.layout.filter_list_row, parent, false);

        DHImageEffectType filterType = filterTypeList.get(position);

        TextView textView = (TextView)rowView.findViewById(R.id.filter_name_text);
        textView.setText(filterType.getName());

        return rowView;
    }
}

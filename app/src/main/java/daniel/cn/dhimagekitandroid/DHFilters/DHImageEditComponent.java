package daniel.cn.dhimagekitandroid.DHFilters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public enum DHImageEditComponent {

    Brightness("亮度", 1, -0.25f, 0.25f, 0.f),
    Contrast("对比度", 2, 0.7f, 1.3f, 1.f);

    private DHImageEditComponent(String name, int index, float minValue, float maxValue, float initialValue) {
        this.name = name;
        this.index = index;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initialValue = initialValue;
    }

    private String name;
    private int index;
    private float minValue;
    private float maxValue;
    private float initialValue;

    public  String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getInitialValue() {
        return initialValue;
    }

    private Map<String, Object> itemsMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.name);
        return map;
    }

    public static List<DHImageEditComponent> allComponents() {
        DHImageEditComponent []components = {Brightness, Contrast};
        return Arrays.asList(components);
    }

    public static List<Map<String, Object>> componentNames() {
        ArrayList<Map<String, Object>> components = new ArrayList<>();
        for (DHImageEditComponent component : DHImageEditComponent.allComponents()) {
            components.add(component.itemsMap());
        }
        return components;
    }
}

package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

import android.support.annotation.NonNull;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHImagePoint implements Comparable {
    public float x;
    public float y;

    public DHImagePoint() {
        this.x = 0.f;
        this.y = 0.f;
    }

    public DHImagePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        DHImagePoint anotherPoint = (DHImagePoint) o;
        if (this.x > anotherPoint.x) {
            return 1;
        } else if (this.x < anotherPoint.x) {
            return -1;
        } else {
            return 0;
        }
    }
}
